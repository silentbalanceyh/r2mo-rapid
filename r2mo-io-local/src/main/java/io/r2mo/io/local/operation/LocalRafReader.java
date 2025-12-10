package io.r2mo.io.local.operation;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HProgressor;
import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.io.local.transfer.ChecksumUtil;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 打包多个文件为 ZIP 压缩流
 *
 * @author lang
 * @since 2025-09-20
 */
@Slf4j
class LocalRafReader {

    static Binary inBinary(final String filename,
                           final FileRange fileRange,
                           final HProgressor progressRef) {

        if (StrUtil.isBlank(filename)) {
            log.warn("[ R2MO ] 文件读取参数无效:filename = {}", filename);
            return null;
        }

        // 1.构造路径
        final Path filePath = Paths.get(filename);
        // 2. 校验文件存在性
        if (!Files.exists(filePath)) {
            log.warn("[ R2MO ] 文件不存在:path = {}", filePath);
            return null;

        }

        try {
            // 2. 计算校验和
            final InputStream checksumStream = FileRangeInputStream.getInputStream(
                filePath, fileRange.getStart(), fileRange.getEnd());
            final String checksum = ChecksumUtil.calculateChecksum(
                checksumStream, ChecksumUtil.HashAlgorithm.SHA256);
            checksumStream.close(); // 及时关闭流

            // 3. 创建实际数据流
            final InputStream dataStream = FileRangeInputStream.getInputStream(
                filePath, fileRange.getStart(), fileRange.getEnd());

            return new Binary(dataStream)
                .checksum(checksum)
                .length(fileRange.getLength());
        } catch (final IOException e) {
            System.err.println("下载分片失败：" + e.getMessage());
        }
        return null;

    }
}
