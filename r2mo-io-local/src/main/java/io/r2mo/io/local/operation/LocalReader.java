package io.r2mo.io.local.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author lang : 2025-09-02
 */
@Slf4j
@SuppressWarnings("all")
class LocalReader {

    /**
     * 列出指定目录中的所有文件（仅当前目录），文件名中包含指定关键字。
     * 如果 keyword 为 null，则列出所有文件。
     *
     * @param dirPath 目录路径
     * @param keyword 文件名关键字
     *
     * @return 文件列表
     */
    public static List<String> lsFiles(final String dirPath, final String keyword) {
        final Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("[R2MO] 提供的路径不是一个目录：" + dirPath);
        }

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            return StreamSupport.stream(stream.spliterator(), false)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(name -> keyword == null || name.contains(keyword))
                .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("[R2MO] 列出目录文件时发生错误：" + dirPath, e);
            return new ArrayList<>();
        }
    }

    /**
     * 列出指定目录及其子目录中的所有文件，文件名中包含指定关键字。
     * 如果 keyword 为 null，则列出所有文件。
     *
     * @param dirPath 目录路径
     * @param keyword 文件名关键字
     *
     * @return 文件列表
     */
    public static List<String> lsFilesN(final String dirPath, final String keyword) {
        final Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("[R2MO] 提供的路径不是一个目录：" + dirPath);
        }

        final List<String> files = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    final String fileName = file.getFileName().toString();
                    if (keyword == null || fileName.contains(keyword)) {
                        files.add(file.toString()); // 保留完整路径
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException e) {
            log.error("[R2MO] 遍历目录文件时发生错误：" + dirPath, e);
        }
        return files;
    }

    /**
     * 列出指定目录中的所有子目录（仅当前层）。
     *
     * @param dirPath 目录路径
     *
     * @return 子目录列表
     */
    public static List<String> lsDirs(final String dirPath) {
        final Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("[R2MO] 提供的路径不是一个目录：" + dirPath);
        }

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            return StreamSupport.stream(stream.spliterator(), false)
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("[R2MO] 列出子目录时发生错误：" + dirPath, e);
            return new ArrayList<>();
        }
    }

    /**
     * 列出指定目录及其子目录中的所有子目录。
     *
     * @param dirPath 目录路径
     *
     * @return 所有子目录列表（包括嵌套）
     */
    public static List<String> lsDirsN(final String dirPath) {
        final Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("[R2MO] 提供的路径不是一个目录：" + dirPath);
        }

        final List<String> dirs = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path dir, final BasicFileAttributes attrs) {
                    if (!path.equals(dir)) { // 不包含根目录自身
                        dirs.add(dir.toString()); // 返回完整路径
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException e) {
            log.error("[R2MO] 遍历子目录时发生错误：" + dirPath, e);
        }
        return dirs;
    }
}