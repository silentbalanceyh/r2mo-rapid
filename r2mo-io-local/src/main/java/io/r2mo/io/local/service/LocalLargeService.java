package io.r2mo.io.local.service;

import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.function.Fn;
import io.r2mo.io.component.node.StoreInit;
import io.r2mo.io.local.transfer.TransDownload;
import io.r2mo.io.local.transfer.TransUpload;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferLargeService;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._404NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 大文件分块传输服务实现
 * 支持大文件的分块上传、下载、断点续传和分块合并
 *
 * @author lang : 2025-09-16
 */
@Slf4j
class LocalLargeService extends AbstractTransferService implements TransferLargeService {

    // 存储分块上传的状态信息
    private static final Map<String, List<StoreChunk>> CHUNK_STORE = new ConcurrentHashMap<>();
    private static final Map<String, List<StoreChunk>> UPLOADED_CHUNKS = new ConcurrentHashMap<>();
    private static final Map<String, List<StoreChunk>> WAITING_CHUNKS = new ConcurrentHashMap<>();
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private final StoreInit<List<StoreChunk>> initializer;
    // 节点管理器实例
    private final NodeManager nm = NodeManager.of();

    LocalLargeService(final TransferTokenService token) {
        super(token);
        this.initializer = StoreInit.ofChunk();
    }

    @Override
    public TransferResponse initialize(final TransferRequest request) {
        this.verifyRequest(request);

        // 1. 创建传输令牌
        final TransferToken token = this.token.initialize(request);
        if (Objects.isNull(token)) {
            throw new _400BadRequestException("[ R2MO ] 令牌创建失败，请检查请求参数等相关数据！");
        }
        // 2. 初始化分块信息
        final List<StoreChunk> chunks = this.initializer.input(request);

        // 3. 存储分块信息到NodeManager (类型已匹配)
        this.nm.put(request.getNodeId(), chunks);

        // 4. 初始化分块状态
        this.initChunkList(token, chunks);

        // 5. 创建响应
        final TransferResponse response = this.initializer.output(chunks);
        response.setCountChunk((long) chunks.size());
        response.setToken(token.getToken());
        response.setSize(chunks.get(0).getTotalSize());
        return response;
    }

    private void initChunkList(final TransferToken token, final List<StoreChunk> chunks) {
        final String tokenId = token.getToken();
        CHUNK_STORE.put(tokenId, new ArrayList<>(chunks));
        UPLOADED_CHUNKS.put(tokenId, new ArrayList<>());
        WAITING_CHUNKS.put(tokenId, new ArrayList<>(chunks));
    }

    @Override
    public TransferResult runUpload(final String token, final InputStream fileData, final int index) {
        final List<StoreChunk> chunks = this.findStoreChunks(token);
        final StoreChunk targetChunk = this.findChunk(chunks, index);

        if (targetChunk == null) {
            throw new _404NotFoundException("[ R2MO ] 指定的分块不存在: " + index);
        }

        // 执行分块上传
        final boolean uploadSuccess = TransUpload.of().write(targetChunk.getStorePath(), fileData);

        if (uploadSuccess) {
            // 更新分块状态
            this.updateChunkStatus(token, targetChunk, true);
            return TransferResult.SUCCESS;
        }

        return TransferResult.FAILURE;
    }

    @Override
    public Binary runDownload(final String token, final int index) {
        final List<StoreChunk> chunks = this.findStoreChunks(token);
        final StoreChunk targetChunk = this.findChunk(chunks, index);

        if (targetChunk == null) {
            throw new _404NotFoundException("[ R2MO ] 指定的分块不存在: " + index);
        }


        // 执行分块下载
        return TransDownload.of().read(targetChunk.getStorePath(), FileRange.of(targetChunk.getByteFrom(), targetChunk.getByteTo()));
    }

    @Override
    public Binary runDownload(final String filename, final FileRange range) {
        return TransDownload.of().read(filename, range);
    }

    @Override
    public List<StoreChunk> dataUploaded(final String token) {
        return UPLOADED_CHUNKS.getOrDefault(token, Collections.emptyList());
    }

    @Override
    public List<StoreChunk> dataWaiting(final String token) {
        return WAITING_CHUNKS.getOrDefault(token, Collections.emptyList());
    }


    @Override
    public List<StoreChunk> data(final String token) {
        return this.findStoreChunks(token);
    }

    @Override
    public List<StoreChunk> data(final UUID id) {
        final List<StoreChunk> chunks = this.nm.find(id);
        return chunks != null ? chunks : Collections.emptyList();
    }

    @Override
    public TransferResult cancel(final String token) {
        try {
            // 清理分块状态
            this.cleanToken(token);

            // 撤销令牌
            this.token.runRevoke(token);
            return TransferResult.SUCCESS;
        } catch (final Exception e) {
            log.error("[ R2MO ] 取消传输失败: token={}", token, e);
            return TransferResult.FAILURE;
        }
    }


    @Override
    public TransferResult complete(final String token) {
        // 1. 验证所有分块的完整性
        this.validChunks(token);
        // 2.执行分块合并逻辑
        this.mergeChunks(token);
        // 3. 清理状态
        this.cleanToken(token);
        // 4. 完成令牌
        this.token.runRevoke(token);
        // 5.删除分片文件
        this.rmChunk(token);
        return TransferResult.SUCCESS;
    }

    private void cleanToken(final String token) {
        CHUNK_STORE.remove(token);
        UPLOADED_CHUNKS.remove(token);
        WAITING_CHUNKS.remove(token);
        log.info("[ R2MO ] 清理token完成：{}", token);
    }

    /**
     * 删除分片
     *
     * @param token token
     */
    private void rmChunk(final String token) {
        final List<StoreChunk> allChunks = this.findStoreChunks(token);
        for (final StoreChunk sc : allChunks) {
            TransUpload.of().rm(sc);
        }
        log.info("[ R2MO ] 文件传输成功，清理分片完成token：{}", token);
    }


    /**
     * 合并所有分块为一个完整文件
     */
    private void mergeChunks(final String token) {
        final List<StoreChunk> allChunks = this.findStoreChunks(token);
        // 获取最终文件路径
        final Path finalFilePath = this.finalFilePath(token, allChunks);

        // 确保目标目录存在
        Fn.jvmOr(() -> Files.createDirectories(finalFilePath.getParent()));

        // 按分块索引排序，确保按正确顺序合并
        final List<StoreChunk> sortedChunks = allChunks.stream()
                .sorted(Comparator.comparingLong(StoreChunk::getIndex))
                .toList();
        // 执行合并操作
        try (final OutputStream outputStream = Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            for (final StoreChunk chunk : sortedChunks) {
                final Binary chunkData = TransDownload.of().read(chunk.getStorePath());
                if (chunkData == null) {
                    log.error("[ R2MO ] 读取分块失败，无法合并: chunkId={}", chunk.getId());
                    throw new _400BadRequestException("[ R2MO ] 读取分块失败，无法合并: chunkId=" + chunk.getId());
                }
                // 将分块数据写入最终文件
                outputStream.write(chunkData.stream().readAllBytes());
                log.debug("[ R2MO ] 已合并分块: {} -> {}", chunk.getId(), finalFilePath);
            }
            outputStream.flush();
            log.info("[ R2MO ] 文件合并完成: path={}, 总大小: {} bytes", finalFilePath, Files.size(finalFilePath));
        } catch (final IOException e) {
            log.error("[ R2MO ] 合并文件时发生IO异常: path={}", finalFilePath, e);
            throw new _400BadRequestException("[ R2MO ] 合并文件时发生IO异常: path=" + finalFilePath);
        }
    }

    /**
     * 根据令牌和分块信息确定最终合并文件的路径
     */
    private Path finalFilePath(final String token, final List<StoreChunk> chunks) {
        final TransferToken tokenInfo = this.token.runValidate(token);
        if (tokenInfo == null || tokenInfo.getRef() == null) {
            log.error("[ R2MO ] 无法确定最终文件路径：token={}", token);
            throw new _400BadRequestException("[ R2MO ] 无法确定最终文件路径：token=" + token);
        }
        final String fileName = tokenInfo.getRef().refId().toString() + "-" + chunks.get(0).getFullFileName();
        return Paths.get(chunks.get(0).getStorePath(), fileName);
    }

    /**
     * 验证所有分块的完整性
     */
    private void validChunks(final String token) {
        final List<StoreChunk> allChunks = this.findStoreChunks(token);
        final List<StoreChunk> uploadedChunks = UPLOADED_CHUNKS.getOrDefault(token, Collections.emptyList());

        // 1. 检查是否所有分块都已上传
        if (uploadedChunks.size() != allChunks.size()) {
            throw new _400BadRequestException("[ R2MO ] 还有分块未完成上传，无法完成传输。已完成: " + uploadedChunks.size() + "/" + allChunks.size());
        }

        //
        for (final StoreChunk chunk : allChunks) {
            try {
                final Binary data = TransDownload.of().read(chunk.getStorePath());
                if (data == null || data.length() != chunk.getSize()) {
                    log.warn("[ R2MO ] 分块大小校验失败: chunkId={}, 期望: {}, 实际: {}",
                            chunk.getId(), chunk.getSize(), (data != null ? data.length() : "null"));
                }

                // 哈希校验（如果chunk中有存储期望的哈希值）
                if (chunk.getChecksum() != null && !chunk.getChecksum().isEmpty()) {
                    final String actualHash = this.calculateHash(data);
                    if (!chunk.getChecksum().equals(actualHash)) {
                        log.warn("[ R2MO ] 分块哈希校验失败: chunkId={}", chunk.getId());
                    }
                }

            } catch (final Exception e) {
                log.warn("[ R2MO ] 校验分块时发生异常: chunkId={}", chunk.getId(), e);
            }
        }
        log.info("[ R2MO ] 完成验证： token={}, 分块数：{}", token, uploadedChunks.size());
    }

    /**
     * 计算数据的SHA-256哈希值
     */
    private String calculateHash(final Binary data) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashBytes = digest.digest(Fn.jvmOr(data.stream()::readAllBytes));
            return this.bytesToHex(hashBytes);
        } catch (final NoSuchAlgorithmException e) {
            log.error("[ R2MO ] 哈希算法不可用", e);
            return null;
        }
    }

    private String bytesToHex(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            final int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 根据令牌查找分块信息
     */
    private List<StoreChunk> findStoreChunks(final String token) {
        // 1. 验证令牌
        final TransferToken tokenVerified = this.token.runValidate(token);
        if (Objects.isNull(tokenVerified)) {
            throw new _404NotFoundException("[ R2MO ] 令牌无效或已过期: " + token);
        }

        // 2. 从令牌中获取 nodeId
        final Ref ref = tokenVerified.getRef();
        if (Objects.isNull(ref) || Objects.isNull(ref.refId())) {
            throw new _404NotFoundException("[ R2MO ] 令牌关联的资源对象无效: " + token);
        }

        // 3. 根据 nodeId 获取分块信息
        final List<StoreChunk> chunks = this.nm.find(ref.refId());
        if (Objects.isNull(chunks)) {
            throw new _404NotFoundException("[ R2MO ] 分块信息不存在: " + ref.refId());
        }

        return chunks;
    }

    /**
     * 查找特定的分块
     */
    private StoreChunk findChunk(final List<StoreChunk> chunks, final int index) {
        return chunks.stream()
                .filter(chunk -> chunk.getIndex().equals(index))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新分块状态
     */
    private void updateChunkStatus(final String token, final StoreChunk chunk, final boolean uploaded) {
        final List<StoreChunk> uploadedList = UPLOADED_CHUNKS.getOrDefault(token, new ArrayList<>());
        final List<StoreChunk> waitingList = WAITING_CHUNKS.getOrDefault(token, new ArrayList<>());

        if (uploaded) {
            // 添加到已上传列表，从等待列表中移除
            uploadedList.add(chunk);
            waitingList.removeIf(c -> c.getId().equals(chunk.getId()));
        } else {
            // 添加到等待列表，从已上传列表中移除
            waitingList.add(chunk);
            uploadedList.removeIf(c -> c.getId().equals(chunk.getId()));
        }

        UPLOADED_CHUNKS.put(token, uploadedList);
        WAITING_CHUNKS.put(token, waitingList);
    }

//    /**
//     * 检查分块是否在指定范围内
//     */
//    private boolean isChunkInRange(final StoreChunk chunk, final FileRange range) {
//        final long chunkStart = chunk.getIndex();
//        final long chunkEnd = chunkStart + chunk.getSize();
//        final long rangeStart = range.getStart();
//        final long rangeEnd = range.getEnd();
//
//        return (chunkStart >= rangeStart && chunkStart <= rangeEnd) ||
//                (chunkEnd >= rangeStart && chunkEnd <= rangeEnd) ||
//                (chunkStart <= rangeStart && chunkEnd >= rangeEnd);
//    }
}