package io.r2mo.io.local.operation;

import io.r2mo.function.Fn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lang : 2025-09-06
 */
class MetaIs {

    static boolean isHidden(final String fullPath) {
        final Path path = Paths.get(fullPath);
        return Fn.jvmOr(() -> Files.exists(path) && Files.isHidden(path));
    }

    static boolean isReadOnly(final String fullPath) {
        final Path path = Paths.get(fullPath);
        return Files.exists(path) && !Files.isWritable(path);
    }

    static boolean isExist(final String fullPath) {
        final Path path = Paths.get(fullPath);
        return Files.exists(path);
    }

    static boolean isDirectory(final String fullPath) {
        final Path path = Paths.get(fullPath);
        return Files.isDirectory(path);
    }

    static boolean isFile(final String fullPath) {
        final Path path = Paths.get(fullPath);
        return Files.isRegularFile(path);
    }

    static boolean isEmpty(final String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return true; // 空文件名认为是空
        }

        try {
            final Path path = Paths.get(fullPath);

            // 检查路径是否存在
            if (!Files.exists(path)) {
                return true; // 路径不存在，认为是空
            }

            // 如果是目录
            if (Files.isDirectory(path)) {
                // 检查目录是否为空（没有子文件和子目录）
                try (final var stream = Files.list(path)) {
                    return stream.findFirst().isEmpty();
                }
            }

            // 如果是文件，检查文件大小是否为0
            if (Files.isRegularFile(path)) {
                return Files.size(path) == 0;
            }

            // 其他类型的文件（如符号链接等），认为是非空
            return false;

        } catch (final Exception e) {
            // 发生异常（如权限问题）也认为是空
            return true;
        }
    }
}
