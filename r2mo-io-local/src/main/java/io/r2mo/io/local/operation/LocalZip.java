package io.r2mo.io.local.operation;

import io.r2mo.base.io.HProgressor;
import io.r2mo.base.io.common.FileMem;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 打包多个文件为 ZIP 压缩流
 *
 * @author lang
 * @since 2025-09-20
 */
@Slf4j
class LocalZip {

    static Binary inBinary(final Set<String> files,
                           final Set<FileMem> memSet,
                           final HProgressor progressRef) {
        if ((files == null || files.isEmpty()) && (memSet == null || memSet.isEmpty())) {
            throw new IllegalArgumentException("[ R2MO ] 输入文件集合为空！");
        }

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final ZipOutputStream zos = new ZipOutputStream(baos)) {

            final byte[] buffer = new byte[8192];
            long totalBytes = 0;

            // 处理本地文件
            if (files != null) {
                for (final String filePath : files) {
                    final Path path = Paths.get(filePath);
                    if (!Files.exists(path)) {
                        log.warn("[ R2MO ] 文件不存在: {}", filePath);
                        continue;
                    }

                    log.info("[ R2MO ] 正在压缩本地文件: {}", filePath);
                    try (final InputStream in = Files.newInputStream(path)) {
                        final ZipEntry entry = new ZipEntry(path.getFileName().toString());
                        zos.putNextEntry(entry);

                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                            totalBytes += len;

                            if (progressRef != null) {
                                try {
                                    progressRef.onProgress(totalBytes);
                                } catch (final Exception e) {
                                    log.debug("[ R2MO ] 进度回调失败", e);
                                }
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }

            // 处理内存文件
            if (memSet != null) {
                for (final FileMem fileMem : memSet) {
                    final String name = fileMem.name();
                    final byte[] content = fileMem.content();

                    if (name == null || content == null) {
                        log.warn("[ R2MO ] 内存文件无效: name={}, content={}", name, content != null);
                        continue;
                    }

                    log.info("[ R2MO ] 正在压缩内存文件: {}", name);
                    final ZipEntry entry = new ZipEntry(name);
                    zos.putNextEntry(entry);
                    zos.write(content);
                    totalBytes += content.length;

                    if (progressRef != null) {
                        try {
                            progressRef.onProgress(totalBytes);
                        } catch (final Exception e) {
                            log.debug("[ R2MO ] 进度回调失败", e);
                        }
                    }
                    zos.closeEntry();
                }
            }

            zos.finish();
            zos.flush();

            final byte[] zipBytes = baos.toByteArray();
            final int length = zipBytes.length;
            log.info("[ R2MO ] 压缩完成，共 {} 字节", length);

            if (progressRef != null) {
                try {
                    progressRef.onComplete(length);
                } catch (final Exception e) {
                    log.debug("[ R2MO ] 完成回调失败", e);
                }
            }

            return new Binary(new ByteArrayInputStream(zipBytes)).length(length);

        } catch (final Exception e) {
            log.error("[ R2MO ] 压缩文件时发生错误", e);
            if (progressRef != null) {
                try {
                    progressRef.onError(e);
                } catch (final Exception ex) {
                    log.debug("[ R2MO ] 错误回调执行失败", ex);
                }
            }
            return null;
        }
    }
}
