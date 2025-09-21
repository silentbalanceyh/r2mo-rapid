package io.r2mo.io.local.operation;

import io.r2mo.function.Fn;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lang : 2025-09-21
 */
@Slf4j
class MetaFile {
    private static final String[] MULTI_EXT = {
        "tar.gz", "tar.bz2", "tar.xz", "tar.zst"
    };

    static String fileMime(final String path) {
        final Path pathObj = Paths.get(path);
        if (Files.isDirectory(pathObj)) {
            return "";
        }
        return Fn.jvmOr(() -> Files.probeContentType(pathObj));
    }

    static long fileSize(final String path) {
        final Path pathObj = Paths.get(path);
        if (Files.isDirectory(pathObj)) {
            return 0;
        }
        return Fn.jvmOr(() -> Files.size(pathObj), 0L);
    }

    static String fileName(final String path) {
        final Path p = Paths.get(path);
        return p.getFileName() != null ? p.getFileName().toString() : "";
    }

    static String fileDirectory(final String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        try {
            final Path p = Paths.get(path);
            // 如果是目录，直接返回空
            if (Files.isDirectory(p)) {
                return "";
            }
            final Path parent = p.getParent();
            return parent != null ? parent.toString() : "";
        } catch (final InvalidPathException e) {
            // ⚠️ 如果路径非法，返回空字符串
            log.error("[ R2MO ] 获取文件目录失败: path={}, error={}", path, e.getMessage());
            return "";
        }
    }

    static String fileExtension(final String path) {
        final String name = fileName(path).toLowerCase();

        // ✅ 先检查复合扩展名
        for (final String ext : MULTI_EXT) {
            if (name.endsWith("." + ext)) {
                return ext;
            }
        }

        // ❌ 否则回退到单一扩展名
        final int dot = name.lastIndexOf('.');
        return (dot >= 0 && dot < name.length() - 1) ? name.substring(dot + 1) : "";
    }
}
