package io.r2mo.io.local.operation;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author lang : 2025-09-02
 */
@Slf4j
@SuppressWarnings("all")
class LocalReader {

    // ========================================================================
    // Public API
    // ========================================================================

    public static List<String> lsFiles(final String dirPath, final String keyword) {
        try {
            Path path = Paths.get(dirPath);
            if (isValidDirectory(path)) {
                return listFilesInternal(path, dirPath, keyword);
            }
        } catch (Exception ignored) {
            log.error(ignored.getMessage(), ignored);
        }

        List<String> result = tryListFromResource(dirPath, (root, originalInput) -> listFilesInternal(root, originalInput, keyword));
        if (result != null) {
            return result;
        }

        log.debug("[ R2MO ] 提供的路径不是一个目录或未找到资源：{}", dirPath);
        return new ArrayList<>();
    }

    public static List<String> lsFilesN(final String dirPath, final String keyword) {
        try {
            Path path = Paths.get(dirPath);
            if (isValidDirectory(path)) {
                return walkFilesInternal(path, dirPath, keyword);
            }
        } catch (Exception ignored) {
        }

        List<String> result = tryListFromResource(dirPath, (root, originalInput) -> walkFilesInternal(root, originalInput, keyword));
        if (result != null) {
            return result;
        }

        log.debug("[ R2MO ] 提供的路径不是一个目录或未找到资源：{}", dirPath);
        return new ArrayList<>();
    }

    public static List<String> lsDirs(final String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (isValidDirectory(path)) {
                return listDirsInternal(path, dirPath);
            }
        } catch (Exception ignored) {
        }

        List<String> result = tryListFromResource(dirPath, (root, originalInput) -> listDirsInternal(root, originalInput));
        if (result != null) {
            return result;
        }

        log.debug("[ R2MO ] 提供的路径不是一个目录或未找到资源：{}", dirPath);
        return new ArrayList<>();
    }

    public static List<String> lsDirsN(final String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (isValidDirectory(path)) {
                return walkDirsInternal(path, dirPath);
            }
        } catch (Exception ignored) {
        }

        List<String> result = tryListFromResource(dirPath, (root, originalInput) -> walkDirsInternal(root, originalInput));
        if (result != null) {
            return result;
        }

        log.debug("[ R2MO ] 提供的路径不是一个目录或未找到资源：{}", dirPath);
        return new ArrayList<>();
    }

    // ========================================================================
    // 核心逻辑下沉
    // ========================================================================

    private static boolean isValidDirectory(Path path) {
        return Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * [关键修正] 格式化路径
     * 1. 统一使用 "/" 作为分隔符，确保在 ClassPath/Jar 环境下路径有效。
     * 2. 避免在 Windows 环境下生成反斜杠，导致资源加载失败。
     */
    private static String formatResultPath(String originalInput, Path root, Path current) {
        // relativize 可能会根据 OS 返回不同的分隔符
        Path relativePath = root.relativize(current);
        // 强制转换为 String 并统一替换分隔符为 "/"
        String relativeStr = relativePath.toString().replace("\\", "/");

        // 确保 originalInput 也是干净的
        String cleanInput = originalInput.replace("\\", "/");

        if (cleanInput.endsWith("/")) {
            return cleanInput + relativeStr;
        } else {
            return cleanInput + "/" + relativeStr;
        }
    }

    private static List<String> listFilesInternal(Path root, String originalInput, String keyword) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            return StreamSupport.stream(stream.spliterator(), false)
                .peek(path -> log.trace("  - 发现项: {} (是否文件: {})", path.getFileName(), Files.isRegularFile(path))) // 调试日志
                .filter(Files::isRegularFile)
                .filter(path -> StrUtil.isEmpty(keyword) || path.getFileName().toString().contains(keyword))
                .map(path -> formatResultPath(originalInput, root, path))
                .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("[ R2MO ] 列出目录文件时发生错误：" + root, e);
            return new ArrayList<>();
        }
    }

    private static List<String> walkFilesInternal(Path root, String originalInput, String keyword) {
        final List<String> files = new ArrayList<>();
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    final String fileName = file.getFileName().toString();
                    if (StrUtil.isEmpty(keyword) || fileName.contains(keyword)) {
                        files.add(formatResultPath(originalInput, root, file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException e) {
            log.error("[ R2MO ] 遍历目录文件时发生错误：" + root, e);
        }
        return files;
    }

    private static List<String> listDirsInternal(Path root, String originalInput) {
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            return StreamSupport.stream(stream.spliterator(), false)
                .filter(Files::isDirectory)
                .map(path -> formatResultPath(originalInput, root, path))
                .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("[ R2MO ] 列出子目录时发生错误：" + root, e);
            return new ArrayList<>();
        }
    }

    private static List<String> walkDirsInternal(Path root, String originalInput) {
        final List<String> dirs = new ArrayList<>();
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!root.equals(dir)) {
                        dirs.add(formatResultPath(originalInput, root, dir));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException e) {
            log.error("[ R2MO ] 遍历子目录时发生错误：" + root, e);
        }
        return dirs;
    }

    // ========================================================================
    // ClassPath / Jar / Zip 处理逻辑
    // ========================================================================

    @FunctionalInterface
    private interface PathOperation {
        List<String> execute(Path root, String originalInput);
    }

    private static List<String> tryListFromResource(String resourcePath, PathOperation operation) {
        URL url = HStoreURL.toURL(resourcePath, LocalReader.class);
        if (url == null) {
            return null;
        }

        try {
            URI uri = url.toURI();
            String protocol = uri.getScheme();

            if ("file".equals(protocol)) {
                Path path = Paths.get(uri);
                if (isValidDirectory(path)) {
                    return operation.execute(path, resourcePath);
                }
            } else if ("jar".equals(protocol)) {
                return executeInJarFileSystem(uri, resourcePath, operation);
            }
        } catch (Exception e) {
            log.debug("[ R2MO ] 资源加载尝试失败: {}", resourcePath, e);
        }
        return null;
    }

    /**
     * [关键修正]
     * 修复了 FileSystem 关闭问题：
     * 1. 只有当我们新创建了 FileSystem 时，才负责关闭它。
     * 2. 如果 FileSystem 已经存在（被 Spring 或其他库占用），则复用且**不关闭**。
     */
    private static List<String> executeInJarFileSystem(URI jarUri, String originalInput, PathOperation operation) throws IOException {
        String spec = jarUri.toString();
        int separator = spec.indexOf("!/");
        String internalPathStr = (separator == -1) ? "/" : spec.substring(separator + 1);
        if (internalPathStr.isBlank()) internalPathStr = "/";

        FileSystem fs = null;
        boolean shouldClose = false;

        try {
            try {
                // 尝试创建新的 FS
                fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap());
                shouldClose = true; // 创建成功，标记为需要关闭
            } catch (FileSystemAlreadyExistsException e) {
                // 已存在，获取现有的
                fs = FileSystems.getFileSystem(jarUri);
                shouldClose = false; // 复用现有的，绝对不能关闭
            }

            if (fs == null) return null;

            Path root = fs.getPath(internalPathStr);
            // 注意：某些 ZIP 实现可能不包含纯目录条目，但 walkFileTree 通常能工作。
            // 这里为了 listFilesInternal (DirectoryStream) 正常工作，依然检查 isValidDirectory
            if (isValidDirectory(root)) {
                return operation.execute(root, originalInput);
            }
            return new ArrayList<>();

        } finally {
            // 只有是我们自己打开的 FS 才关闭
            if (shouldClose && fs != null && fs.isOpen()) {
                try {
                    fs.close();
                } catch (IOException e) {
                    log.warn("[ R2MO ] 关闭 JarFileSystem 失败", e);
                }
            }
        }
    }
}