package io.r2mo.io.local.operation;

import io.r2mo.base.io.HProgressor;
import io.r2mo.io.local.transfer.HHome;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Binary;
import io.r2mo.typed.exception.web._501NotSupportException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author lang : 2025-09-18
 */
@Slf4j
class LocalHighway {

    static String findHome() {
        final List<HHome> found = SPI.findMany(HHome.class);
        if (found.isEmpty()) {
            return "";
        }
        if (found.size() > 1) {
            throw new _501NotSupportException("[ R2MO ] 本地存储只能存在一个 Home 实现，当前发现多个，请检查 SPI 配置！");
        }
        return found.get(0).ioHome();
    }

    /**
     * 高性能文件读取方法
     *
     * @param filename         文件路径
     * @param progressCallback 进度回调（可为空）
     *
     * @return Binary (包含 InputStream 和 length)，失败返回 null
     */
    public static Binary read(final String filename, final HProgressor progressCallback) {
        if (filename == null || filename.isEmpty()) {
            log.warn("[ R2MO ] 文件读取参数无效: filename={}", filename);
            return null;
        }

        try {
            // 1. 构造路径
            final Path filePath = Paths.get(filename);

            // 2. 校验文件存在性
            if (!Files.exists(filePath)) {
                log.warn("[ R2MO ] 文件不存在: path={}", filename);
                return null;
            }

            // 3. 获取文件大小
            final long fileSize = Files.size(filePath);

            // 4. 打开原始 InputStream
            final InputStream inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);

            // 5. 初始回调
            if (progressCallback != null) {
                try {
                    progressCallback.onProgress(0L);
                } catch (final Exception ignored) {
                    
                }
            }

            // 6. 返回 Binary（长度截断为 int，超大文件可扩展为 long）
            return new Binary(inputStream).length(fileSize);

        } catch (final Exception e) {
            log.error("[ R2MO ] 文件读取失败: path={}", filename, e);
            if (progressCallback != null) {
                try {
                    progressCallback.onError(e);
                } catch (final Exception ignored) {
                }
            }
            return null;
        }
    }

    /**
     * 使用 NIO Channels 的超高性能文件写入方法（带回调）
     *
     * @param filename         文件名（完整路径）
     * @param in               输入流
     * @param progressCallback 进度回调（可为空）
     *
     * @return 是否写入成功
     */
    public static boolean write(final String filename, final InputStream in, final HProgressor progressCallback) {
        if (filename == null || filename.isEmpty() || in == null) {
            log.warn("[ R2MO ] 文件写入参数无效: filename={}, inputStream={}", filename, in);
            return false;
        }

        Path filePath = null;
        try {
            // 1. 构造文件路径
            filePath = Paths.get(filename);

            // 2. 确保存储目录存在
            Files.createDirectories(filePath.getParent());

            // 3. 执行 NIO 高性能写入
            return performNioWrite(filePath, in, progressCallback);

        } catch (final Exception e) {
            log.error("[ R2MO ] 文件写入失败: path={}", filename, e);
            // 回调错误处理
            if (progressCallback != null) {
                try {
                    progressCallback.onError(e);
                } catch (final Exception callbackEx) {
                    log.debug("[ R2MO ] 进度回调错误处理失败", callbackEx);
                }
            }

            // 清理可能创建的文件
            if (filePath != null) {
                try {
                    Files.deleteIfExists(filePath);
                } catch (final IOException deleteEx) {
                    log.debug("[ R2MO ] 清理临时文件失败: path={}", filename, deleteEx);
                }
            }
            return false;
        }
    }

    /**
     * 使用 NIO Channels 执行超高性能文件写入
     */
    private static boolean performNioWrite(final Path filePath,
                                           final InputStream inputStream,
                                           final HProgressor progressCallback) {
        final int bufferSize = 65536; // 64KB 缓冲区

        try (final ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
             final FileChannel fileChannel = FileChannel.open(
                 filePath,
                 StandardOpenOption.CREATE,
                 StandardOpenOption.TRUNCATE_EXISTING,
                 StandardOpenOption.WRITE,
                 StandardOpenOption.SYNC)) {

            final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
            long totalBytes = 0L;
            long lastCallbackBytes = 0L;
            final long callbackInterval = 1024 * 1024; // 每1MB回调一次
            int bytesRead;

            // 使用 Direct ByteBuffer 进行零拷贝传输
            while ((bytesRead = inputChannel.read(buffer)) != -1) {
                buffer.flip(); // 切换到读模式

                while (buffer.hasRemaining()) {
                    fileChannel.write(buffer);
                }

                totalBytes += bytesRead;

                // 进度回调
                if (progressCallback != null && totalBytes - lastCallbackBytes >= callbackInterval) {
                    try {
                        progressCallback.onProgress(totalBytes);
                        lastCallbackBytes = totalBytes;
                    } catch (final Exception callbackEx) {
                        log.debug("[ R2MO ] 进度回调执行失败", callbackEx);
                    }
                }

                buffer.clear(); // 清空缓冲区准备下次读取
            }

            // 强制刷新到磁盘
            fileChannel.force(true);

            // 最终进度回调
            if (progressCallback != null) {
                try {
                    progressCallback.onComplete(totalBytes);
                } catch (final Exception callbackEx) {
                    log.debug("[ R2MO ] 完成回调执行失败", callbackEx);
                }
            }

            log.debug("[ R2MO ] NIO 文件写入完成: path={}, size={} bytes", filePath, totalBytes);
            return true;

        } catch (final Exception e) {
            log.error("[ R2MO ] NIO 文件写入失败: path={}", filePath, e);
            // 回调错误处理
            if (progressCallback != null) {
                try {
                    progressCallback.onError(e);
                } catch (final Exception callbackEx) {
                    log.debug("[ R2MO ] 进度回调错误处理失败", callbackEx);
                }
            }
            return false;
        }
    }
}