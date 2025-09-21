package io.r2mo.io.local.operation;

import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author lang : 2025-09-21
 */
@Slf4j
class MetaCommon {

    static String metaChecksum(final String path, final String algorithm) {
        try {
            final Path p = Paths.get(path);
            if (!Files.exists(p)) {
                return "";
            }
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            final byte[] bytes = Files.readAllBytes(p);
            final byte[] digest = md.digest(bytes);
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (final Exception e) {
            log.error("[ R2MO ] 计算文件哈希失败: path={}, algorithm={}, error={}", path, algorithm, e.getMessage());
            return "";
        }
    }

    static LocalDateTime metaModifiedAt(final String path) {
        try {
            final Path p = Paths.get(path);
            final BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
            final Instant instant = attrs.lastModifiedTime().toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (final IOException e) {
            log.error("[ R2MO ] 获取文件修改时间失败: path={}, error={}", path, e.getMessage());
            return null;
        }
    }

}
