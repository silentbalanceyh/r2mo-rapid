package io.r2mo.io.local.operation;

import io.r2mo.function.Fn;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * 元数据检查工具类 (增强版)
 * 支持物理文件及 ClassPath/Jar 包内资源的元数据检查
 *
 * @author lang : 2025-09-06
 */
class MetaIs {

    // ========================================================================
    // Public API (签名保持不变)
    // ========================================================================

    static boolean isHidden(final String fullPath) {
        // 默认返回 false (找不到或是普通的)
        return probe(fullPath, false, path -> Fn.jvmOr(() -> Files.exists(path) && Files.isHidden(path)));
    }

    static boolean isReadOnly(final String fullPath) {
        // 默认返回 false
        return probe(fullPath, false, path -> Files.exists(path) && !Files.isWritable(path));
    }

    static boolean isExist(final String fullPath) {
        // 默认返回 false
        return probe(fullPath, false, Files::exists);
    }

    static boolean isDirectory(final String fullPath) {
        // 默认返回 false
        return probe(fullPath, false, Files::isDirectory);
    }

    static boolean isFile(final String fullPath) {
        // 默认返回 false
        return probe(fullPath, false, Files::isRegularFile);
    }

    static boolean isEmpty(final String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return true; // 空文件名认为是空
        }

        // 默认返回 true (如果路径不存在或发生异常，原逻辑认为是空)
        return probe(fullPath, true, path -> {
            try {
                // 1. 检查存在性
                if (!Files.exists(path)) {
                    return true;
                }

                // 2. 如果是目录，检查是否有内容
                if (Files.isDirectory(path)) {
                    try (final var stream = Files.list(path)) {
                        return stream.findFirst().isEmpty();
                    }
                }

                // 3. 如果是文件，检查大小
                if (Files.isRegularFile(path)) {
                    return Files.size(path) == 0;
                }

                // 4. 其他类型
                return false;

            } catch (final Exception e) {
                // 发生异常（如无权限读取流）视为空
                return true;
            }
        });
    }

    // ========================================================================
    // 核心通用逻辑：资源探测器
    // ========================================================================

    /**
     * 统一探测入口
     *
     * @param resourcePath 资源路径 (文件路径 或 ClassPath/Jar 路径)
     * @param defaultValue 如果解析失败或发生异常时的默认返回值
     * @param checker      基于 Path 对象的检查逻辑 (Files::exists, Files::isDirectory 等)
     *
     * @return 检查结果
     */
    private static boolean probe(final String resourcePath, final boolean defaultValue, final Predicate<Path> checker) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return defaultValue;
        }

        // 1. 优先：尝试直接作为物理路径处理
        // -----------------------------------------------------------
        try {
            final Path physicalPath = Paths.get(resourcePath);
            // 只有当物理文件真的存在时，才优先使用物理路径判断
            // 否则，如果物理路径不存在，我们应该给 ClassPath 一个机会
            if (Files.exists(physicalPath)) {
                return checker.test(physicalPath);
            }
        } catch (final Exception ignored) {
            // 路径非法等，忽略，继续尝试 URL
        }

        // 2. 其次：尝试通过 HStoreURL 解析 (ClassPath / Zip / Jar)
        // -----------------------------------------------------------
        final URL url = HStoreURL.toURL(resourcePath, MetaIs.class);
        if (url == null) {
            // 物理不存在，URL 解析也不成功，那么确实是不存在
            // 对于 isEmpty 来说，不存在=空(true)；对于 isExist 来说，不存在=false
            // 所以这里返回 defaultValue
            return defaultValue;
        }

        try {
            final URI uri = url.toURI();
            final String protocol = uri.getScheme();

            if ("file".equals(protocol)) {
                return checker.test(Paths.get(uri));
            } else if ("jar".equals(protocol)) {
                return probeInJar(uri, checker, defaultValue);
            }

        } catch (final Exception ignored) {
            // URI 转换异常等
        }

        return defaultValue;
    }

    /**
     * 在 Jar 包内部执行检查
     */
    private static boolean probeInJar(final URI jarUri, final Predicate<Path> checker, final boolean defaultValue) {
        final String spec = jarUri.toString();
        final int separator = spec.indexOf("!/");
        String internalPathStr = (separator == -1) ? "/" : spec.substring(separator + 1);
        if (internalPathStr.isBlank()) {
            internalPathStr = "/";
        }

        // 使用 try-with-resources 确保 FileSystem 关闭
        // 这一点至关重要，否则多次调用 isExist 会导致 Jar 文件被 JVM 锁定无法删除或更新
        try (final FileSystem fs = getSafeJarFileSystem(jarUri)) {
            final Path internalPath = fs.getPath(internalPathStr);
            return checker.test(internalPath);
        } catch (final Exception e) {
            // 创建 FS 失败或 Check 过程异常
            return defaultValue;
        }
    }

    /**
     * 安全获取 ZipFileSystem (复用 LocalReader 的逻辑)
     */
    private static FileSystem getSafeJarFileSystem(final URI uri) throws IOException {
        try {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (final FileSystemAlreadyExistsException e) {
            return FileSystems.getFileSystem(uri);
        }
    }
}