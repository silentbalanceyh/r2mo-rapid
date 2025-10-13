package io.r2mo.io.local.transfer;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.common.RFS;
import io.r2mo.io.enums.TransferStatus;
import io.r2mo.io.modeling.TransferProgress;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分片上传工具类
 * 支持大文件的分片上传、断点续传、进度监控
 */
@Slf4j
public class ChunkUploader {

    private final RFS rfs;
    private final ExecutorService executorService;

    // 存储上传状态
    private final List<ChunkSession> activeSessions = new ArrayList<>();

    public ChunkUploader(final RFS rfs, final int threadPoolSize) {
        this.rfs = rfs;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * 创建分片上传会话
     */
    public ChunkSession createUploadSession(final UUID nodeId, final String token) {
        final ChunkSession session = new ChunkSession(nodeId, token);
        this.activeSessions.add(session);
        return session;
    }

    private TransferResult uploadChunk(final ChunkSession session, final InputStream chunkStream, final int index) {
        final TransferRequest request = this.createRequest(session, TransferType.UPLOAD);
        return this.rfs.ioUploadChunk(request, chunkStream, index);
    }


    /**
     * 开始分片上传
     */
    public void startUpload(final ChunkSession session, final InputStream chunkStream, final int index) {
        final TransferResult transferResult = this.uploadChunk(session, chunkStream, index);
        this.activeSessions.remove(session);
    }


    /**
     * 获取上传进度
     */
    public TransferProgress getUploadProgress(final String token, final ChunkSession session) {
        try {
            final List<StoreChunk> uploadedChunks = this.rfs.getUploadedChunks(token);
            final List<StoreChunk> allChunks = this.rfs.getAllChunks(token);

            final int uploadedCount = uploadedChunks.size();
            final int totalCount = allChunks.size();
            final double progress = totalCount > 0 ? (uploadedCount * 100.0 / totalCount) : 0;

            final long uploadedSize = uploadedChunks.stream().mapToLong(StoreChunk::getSize).sum();
            final long totalSize = allChunks.get(0).getTotalSize();
            return new TransferProgress(token, session.getSessionId(), uploadedSize, totalSize, progress, true, TransferStatus.TRANSFERRING, TransferType.UPLOAD, null, null, null, null);

        } catch (final Exception e) {
            log.error("获取上传进度失败: {}", token, e);
            return new TransferProgress(token, session.getSessionId(), null, null, null, true, null, TransferType.UPLOAD, null, null, e.getMessage(), e.getMessage());
        }
    }

    /**
     * 取消上传
     */
    public boolean cancelUpload(final String token) {
        try {
            final TransferResult result = this.rfs.cancelUpload(token);
            final boolean success = result == TransferResult.SUCCESS;

            if (success) {
                // 清理会话
                this.activeSessions.removeIf(session -> session.getToken().equals(token));
                log.info("上传已取消: {}", token);
            }

            return success;

        } catch (final Exception e) {
            log.error("取消上传失败: {}", token, e);
            return false;
        }
    }

    // ========== 私有方法 ==========

    private TransferRequest createRequest(final ChunkSession session, final TransferType type) {
        final TransferRequest request = new TransferRequest();
        request.setNodeId(session.getSessionId());
        request.setIsMultipart(true);
        request.setToken(session.getToken());
        request.setType(type);
        return request;
    }

    /**
     * 关闭上传器，释放资源
     */
    public void shutdown() {
        this.executorService.shutdown();
        log.info("分片上传器已关闭");
    }

    public void complete(final String token) {
        final TransferResult transferResult = this.rfs.completeUpload(token);
        log.info("[R2MO] complete transfer: {}", transferResult);
    }

    private ChunkSession findSessionByToken(final String token) {
        return this.activeSessions.stream()
                .filter(session -> session.getToken().equals(token))
                .findFirst()
                .orElse(null);
    }


//    /**
//     * 开始分片上传
//     */
//    public CompletableFuture<UploadResult> startUpload(final ChunkSession session) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                final Path filePath = Paths.get(session.getFilePath());
//
//                try (final RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
//                    for (int chunkIndex = 0; chunkIndex < session.getTotalChunks(); chunkIndex++) {
//                        if (session.isChunkUploaded(chunkIndex)) {
//                            continue; // 跳过已上传的分片
//                        }
//
//                        final long chunkStart = (long) chunkIndex * this.chunkSize;
//                        final int currentChunkSize = (int) Math.min(this.chunkSize, session.getFileSize() - chunkStart);
//
//                        // 读取分片数据
//                        final byte[] chunkData = this.readChunk(raf, chunkStart, currentChunkSize);
//
//                        // 创建分片信息
//                        final StoreChunk chunk = this.createChunk(chunkIndex, chunkStart, currentChunkSize);
//
//                        // 上传分片
//                        try (final InputStream chunkStream = new ByteArrayInputStream(chunkData)) {
//                            final TransferResult result = this.rfs.ioUpload(this.createChunkUploadRequest(session), chunkStream, chunk);
//
//                            if (result == TransferResult.SUCCESS) {
//                                session.markChunkUploaded(chunkIndex);
//                                log.debug("分片上传成功: {}-{}, 大小: {} bytes",
//                                        session.getFileName(), chunkIndex, currentChunkSize);
//                            } else {
//                                throw new RuntimeException("分片上传失败: " + chunkIndex);
//                            }
//                        }
//
//                        // 更新进度
//                        this.updateProgress(session);
//                    }
//                }
//
//                // 完成传输
//                final TransferResult finalResult = this.rfs.completeUpload(session.getToken());
//
//                final UploadResult uploadResult = new UploadResult(
//                        finalResult == TransferResult.SUCCESS,
//                        session.getFileName(),
//                        session.getFileSize(),
//                        session.getToken(),
//                        finalResult == TransferResult.SUCCESS ? "上传成功" : "上传失败"
//                );
//
//                // 清理会话
//                this.activeSessions.remove(session);
//
//                return uploadResult;
//
//            } catch (final Exception e) {
//                log.error("分片上传失败: {}", session.getFileName(), e);
//                throw new RuntimeException("分片上传失败: " + session.getFileName(), e);
//            }
//        }, this.executorService);
//    }

//    /**
//     * 恢复上传会话
//     */
//    public CompletableFuture<UploadResult> resumeUpload(final String token) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                // 获取已上传的分片信息
//                final List<StoreChunk> uploadedChunks = this.rfs.getUploadedChunks(token);
//                final List<StoreChunk> allChunks = this.rfs.getAllChunks(token);
//
//                // 查找对应的会话
//                final ChunkSession session = this.findSessionByToken(token);
//                if (session == null) {
//                    throw new RuntimeException("找不到对应的上传会话: " + token);
//                }
//
//                // 更新已上传的分片状态
//                for (final StoreChunk chunk : uploadedChunks) {
//                    session.markChunkUploaded(chunk.getIndex());
//                }
//
//                // 继续上传
//                return this.startUpload(session).get();
//
//            } catch (final Exception e) {
//                log.error("恢复上传失败: {}", token, e);
//                throw new RuntimeException("恢复上传失败: " + token, e);
//            }
//        }, this.executorService);
//    }


}