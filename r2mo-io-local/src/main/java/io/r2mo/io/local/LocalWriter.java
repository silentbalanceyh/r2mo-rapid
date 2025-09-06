package io.r2mo.io.local;

import io.r2mo.function.Fn;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author lang : 2025-09-02
 */
@Slf4j
class LocalWriter {

    static boolean rm(final String filename) {
        final Path path = Paths.get(filename);
        // 被删除的路径必须存在
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("[ R2MO ] 删除的文件/目录不存在：" + filename);
        }
        final boolean isDirectory = Files.isDirectory(path);
        if (isDirectory) {
            directoryDelete(path);
        } else {
            Fn.jvmAt(() -> Files.delete(path));
        }
        return true;
    }

    static boolean write(final String filename, final String content, final boolean append) {
        final Path path = Paths.get(filename);
        pathParent(path);
        try (final BufferedWriter writer = Files.newBufferedWriter(
            path,
            StandardCharsets.UTF_8,
            append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE
        )) {
            writer.write(content);
        } catch (final IOException e) {
            log.error("[ R2MO ] 写入文件时发生错误：" + filename, e);
            return false;
        }
        return true;
    }

    static boolean mkdir(final String dir) {
        if (null == dir || dir.isBlank()) {
            return false;
        }
        final Path path = Paths.get(dir);
        if (Files.exists(path)) {
            log.warn("[ R2MO ] 目录已存在，无需创建：{}", dir);
            return true;
        }
        Fn.jvmAt(() -> Files.createDirectories(path));
        return true;
    }

    static boolean move(final String from, final String to) {
        final Path fromPath = pathSource(from);
        final Path toPath = Paths.get(to);
        final boolean isDirectoryF = Files.isDirectory(fromPath);
        final boolean isDirectoryT = Files.isDirectory(toPath);

        if (isDirectoryF) {
            // 源是目录
            if (Files.exists(toPath)) {
                if (!isDirectoryT) {
                    // 目标是文件，直接抛异常
                    throw new IllegalStateException("[ R2MO ] 不能移动到文件：" + to);
                }
                // 目标是目录，存在，移动目录到目标目录之下
                final Path targetPath = toPath.resolve(fromPath.getFileName());
                directoryMove(fromPath, targetPath);
            } else {
                // 目标不存在，拷贝目录到目标路径，此处没有递归，且 toPath 就是目录
                Fn.jvmAt(() -> Files.createDirectories(toPath));
                directoryMove(fromPath, toPath);
            }
        } else {
            // 源是文件
            if (Files.exists(toPath)) {
                if (isDirectoryT) {
                    // 目标是目录，将文件移动到该目录中
                    final Path targetFile = toPath.resolve(fromPath.getFileName());
                    fileMove(fromPath, targetFile);
                } else {
                    // 目标是文件，覆盖现有文件
                    fileMove(fromPath, toPath);
                }
            } else {
                // 目标不存在，确保目标路径的父目录存在，然后移动文件
                pathParent(toPath);
                fileMove(fromPath, toPath);
            }
        }

        return true;
    }

    static boolean copy(final String from, final String to) {
        final Path fromPath = pathSource(from);
        final Path toPath = Paths.get(to);
        final boolean isDirectoryF = Files.isDirectory(fromPath);
        final boolean isDirectoryT = Files.isDirectory(toPath);

        // 源是一定存在的
        if (isDirectoryF) {
            // 源 = 目录
            if (Files.exists(toPath)) {
                if (!isDirectoryT) {
                    // 目标是文件，直接抛异常
                    throw new IllegalStateException("[ R2MO ] 不能复制到文件：" + to);
                }
                // 目标是目录，存在，拷贝目录到目录之下
                directoryCopy(fromPath, toPath.resolve(fromPath.getFileName()));
            } else {
                directoryCopy(fromPath, toPath);
            }
        } else {
            // 源 = 文件
            if (Files.exists(toPath)) {
                if (isDirectoryT) {
                    // 目标存在，目录
                    final Path targetFile = toPath.resolve(fromPath.getFileName());
                    fileCopy(fromPath, targetFile);
                } else {
                    // 目标存在，文件 -> 文件（覆盖）
                    fileCopy(fromPath, toPath);
                }
            } else {
                // 目标不存在，不可能是文件到文件
                mkdir(to);
                // 只可能是目录
                final Path targetFile = toPath.resolve(fromPath.getFileName());
                fileCopy(fromPath, targetFile);
            }
        }
        return true;
    }

    static void fileCopy(final Path source, final Path target) {
        Fn.jvmAt(() -> Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING));
    }

    static void fileMove(final Path source, final Path target) {
        Fn.jvmAt(() -> Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE));
    }

    @SuppressWarnings("all")
    static void directoryMove(final Path source, final Path target) {
        Fn.jvmAt(() -> Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.move(file, targetFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 删除空的源目录
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        }));
    }

    @SuppressWarnings("all")
    static void directoryDelete(final Path directory) {
        Fn.jvmAt(() -> Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // 目录遍历失败
                    throw exc;
                }
            }
        }));
    }

    @SuppressWarnings("all")
    static void directoryCopy(final Path source, final Path target) {
        Fn.jvmAt(() -> Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                final Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                final Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        }));
    }

    private static void pathParent(final Path path) {
        final Path parent = path.getParent();
        if (parent != null && Files.notExists(parent)) {
            Fn.jvmAt(() -> Files.createDirectories(parent));
        }
    }

    private static Path pathSource(final String from) {
        final Path fromPath = Paths.get(from);
        if (Files.notExists(fromPath)) {
            throw new IllegalArgumentException("[ R2MO ] 源文件/目录不存在：" + from);
        }
        return fromPath;
    }
}
