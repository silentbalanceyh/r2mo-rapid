package io.r2mo.io.local.operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * 本地文件比较工具类
 * 只开放包域的 isSame 方法供外部调用
 *
 * @author lang : 2025-09-06
 */
@SuppressWarnings("all")
class LocalCompare {

    /**
     * 包域访问权限的比较方法
     * 比较两个路径下的内容是否一致
     *
     * @param path1 第一个路径
     * @param path2 第二个路径
     *
     * @return 如果内容一致返回true，否则返回false
     */
    static boolean isSame(String path1, String path2) {
        try {
            if (path1 == null && path2 == null) {
                return true;
            }
            if (path1 == null || path2 == null) {
                return false;
            }

            Path p1 = Paths.get(path1);
            Path p2 = Paths.get(path2);

            return comparePaths(p1, p2);
        } catch (Exception e) {
            // 不抛出异常，出现异常认为不相同
            return false;
        }
    }

    /**
     * 比较两个路径的核心逻辑
     */
    private static boolean comparePaths(Path path1, Path path2) throws IOException {
        // 检查存在性
        boolean exists1 = Files.exists(path1);
        boolean exists2 = Files.exists(path2);

        if (!exists1 && !exists2) {
            return true; // 两个都不存在，认为相等
        }
        if (!exists1 || !exists2) {
            return false; // 一个存在一个不存在，不相等
        }

        // 检查是否为相同路径
        if (path1.equals(path2) || path1.normalize().equals(path2.normalize())) {
            return true;
        }

        // 检查类型是否相同
        boolean isDir1 = Files.isDirectory(path1);
        boolean isDir2 = Files.isDirectory(path2);

        if (isDir1 != isDir2) {
            return false; // 一个是目录一个是文件，不相等
        }

        if (isDir1) {
            // 都是目录，递归比较内容
            return compareDirectories(path1, path2);
        } else {
            // 都是文件，比较文件名和内容
            return compareFiles(path1, path2);
        }
    }

    /**
     * 比较两个文件（包括文件名和内容）
     */
    private static boolean compareFiles(Path file1, Path file2) throws IOException {
        // 比较文件名
        String fileName1 = file1.getFileName().toString();
        String fileName2 = file2.getFileName().toString();

        if (!fileName1.equals(fileName2)) {
            return false;
        }

        // 比较文件大小
        long size1 = Files.size(file1);
        long size2 = Files.size(file2);

        if (size1 != size2) {
            return false;
        }

        // 比较文件内容
        return Files.mismatch(file1, file2) == -1;
    }

    /**
     * 比较两个目录的内容
     */
    private static boolean compareDirectories(Path dir1, Path dir2) throws IOException {
        // 获取并排序目录内容
        Path[] list1 = Files.list(dir1)
            .sorted(Comparator.comparing(Path::getFileName))
            .toArray(Path[]::new);

        Path[] list2 = Files.list(dir2)
            .sorted(Comparator.comparing(Path::getFileName))
            .toArray(Path[]::new);

        // 比较文件数量
        if (list1.length != list2.length) {
            return false;
        }

        // 逐个比较每个文件/目录
        for (int i = 0; i < list1.length; i++) {
            Path path1 = list1[i];
            Path path2 = list2[i];

            // 比较文件名
            if (!path1.getFileName().toString().equals(path2.getFileName().toString())) {
                return false;
            }

            // 递归比较内容
            if (!comparePaths(path1, path2)) {
                return false;
            }
        }

        return true;
    }
}
