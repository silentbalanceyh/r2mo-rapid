package io.r2mo.io.local.transfer;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.common.RFS;
import io.r2mo.io.enums.TransferStatus;
import io.r2mo.io.modeling.TransferProgress;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分片下载工具类
 * 支持大文件的分片下载、断点续传、进度监控
 */

@Slf4j
public class ChunkDownloader {

    private final RFS rfs;
    private final ExecutorService executorService;

    public ChunkDownloader(final RFS rfs, final int threadPoolSize) {
        this.rfs = rfs;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    private List<StoreChunk> getSort(final List<StoreChunk> chunks) {
        // 按索引排序分片
        return chunks.stream()
                .sorted(Comparator.comparing(StoreChunk::getIndex)).toList();
    }

    /**
     * 分片下载全部文件
     */
    public CompletableFuture<DownloadResult> downloadFile(final String transferId, final String token) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始分片下载: {}, token: {}", transferId, token);

                // 1. 获取分片信息
                final List<StoreChunk> chunks = this.rfs.getAllChunks(token);
                if (chunks == null || chunks.isEmpty()) {
                    throw new RuntimeException("分片信息不存在: " + token);
                }

                // 2. 创建本地文件
                final Path localPath = Paths.get(chunks.get(0).getStorePath(), token);
                Files.createDirectories(localPath.getParent());

                // 3. 并行下载分片
                this.downloadChunksParallel(token, chunks, localPath);

                final long downloadedSize = Files.size(localPath);
                log.info("分片下载完成: {}, 大小: {} bytes", token, downloadedSize);

                return new DownloadResult(true, token, downloadedSize, localPath.toString(), "下载成功");

            } catch (final Exception e) {
                log.error("分片下载失败: {}", token, e);
                throw new RuntimeException("分片下载失败: " + token, e);
            }
        }, this.executorService);
    }

    private List<StoreChunk> getDownloadChunks(final String token) {
        return this.rfs.getAllChunks(token).stream().filter(StoreChunk::getDone).toList();
    }

    /**
     * 获取下载进度
     */
    public TransferProgress getDownloadProgress(final String token, final ChunkSession session) {
        try {
            final List<StoreChunk> allChunks = this.rfs.getAllChunks(token);
            final int totalChunks = allChunks.size();
            final int downloadedChunks = this.getDownloadChunks(token).size();
            final double progress = totalChunks > 0 ? (downloadedChunks * 100.0 / totalChunks) : 0;

            final long totalSize = allChunks.stream().mapToLong(StoreChunk::getSize).sum();
            final long downloadedSize = this.getDownloadChunks(token).stream().mapToLong(StoreChunk::getSize).sum();
            return new TransferProgress(token, session.getSessionId(), downloadedSize, totalSize, progress, true, TransferStatus.TRANSFERRING, TransferType.UPLOAD, null, null, null, null);
        } catch (final Exception e) {
            log.error("获取下载进度失败: {}", token, e);
            return new TransferProgress(token, session.getSessionId(), null, null, null, true, null, TransferType.UPLOAD, null, null, e.getMessage(), e.getMessage());
        }
    }

    private void downloadChunksParallel(final String token, final List<StoreChunk> chunks, final Path localPath) throws Exception {
        final List<StoreChunk> sortedChunks = this.getSort(chunks);
        // 创建临时文件用于分片下载
        final Path tempDir = localPath.getParent().resolve("temp_" + UUID.randomUUID());
        Files.createDirectories(tempDir);

        try {
            // 并行下载所有分片
            final List<CompletableFuture<Void>> downloadFutures = sortedChunks.stream()
                    .map(chunk -> CompletableFuture.runAsync(() -> {
                        try {
                            this.downloadSingleChunk(token, chunk, tempDir);
                        } catch (final Exception e) {
                            throw new RuntimeException("分片下载失败: " + chunk.getId(), e);
                        }
                    }, this.executorService)).toList();

            // 等待所有分片下载完成
            CompletableFuture.allOf(downloadFutures.toArray(new CompletableFuture[0])).get();

            // 合并分片
            this.mergeChunks(sortedChunks, tempDir, localPath);

        } finally {
            // 清理临时文件
            this.cleanupTempFiles(tempDir);
        }
    }

    private void downloadSingleChunk(final String token, final StoreChunk chunk, final Path tempDir) {
        try {
            final Binary chunkData = this.rfs.ioDownload(this.createChunkRequest(token), chunk.getIndex());
            if (chunkData == null) {
                throw new RuntimeException("分片下载返回空数据: " + chunk.getId());
            }

            // 保存分片到临时文件
            final Path chunkFile = tempDir.resolve(chunk.getId().toString());
            Files.write(chunkFile, chunkData.stream().readAllBytes(), StandardOpenOption.CREATE);
            chunk.setDone(true);
            log.debug("分片下载成功: {}, 大小: {}", chunk.getId(), chunkData.length());
        } catch (final Exception e) {
            log.error("分片下载异常: {}", chunk.getId(), e);
            throw new RuntimeException("分片下载失败", e);
        }
    }

    private void mergeChunks(final List<StoreChunk> chunks, final Path tempDir, final Path outputPath) throws IOException {
        final List<StoreChunk> sortedChunks = this.getSort(chunks);

        try (final var outputStream = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            for (final StoreChunk chunk : sortedChunks) {
                final Path chunkFile = tempDir.resolve(chunk.getId().toString());
                if (Files.exists(chunkFile)) {
                    final byte[] chunkData = Files.readAllBytes(chunkFile);
                    outputStream.write(chunkData);
                }
            }
        }
    }

    
    private TransferRequest createChunkRequest(final String token) {
        final TransferRequest request = new TransferRequest();
        request.setNodeId(UUID.randomUUID());
        request.setIsMultipart(true);
        request.setToken(token);
        return request;
    }

    private void cleanupTempFiles(final Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted((a, b) -> b.compareTo(a)) // 反向排序，先删除文件再删除目录
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (final IOException e) {
                                log.warn("删除临时文件失败: {}", path, e);
                            }
                        });
            }
        } catch (final IOException e) {
            log.warn("清理临时文件失败: {}", tempDir, e);
        }
    }

    /**
     * 关闭下载器，释放资源
     */
    public void shutdown() {
        this.executorService.shutdown();
        log.info("分片下载器已关闭");
    }


}