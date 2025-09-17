package io.r2mo.base.io.common;

import java.util.*;

/**
 * @author lang : 2025-09-17
 */
public class FileHelper {

    // 常见的复合扩展名映射
    private static final Map<String, String> COMPOUND_EXTENSIONS = new LinkedHashMap<>();

    // 常见的压缩和归档格式
    private static final Set<String> ARCHIVE_EXTENSIONS = new HashSet<>(Arrays.asList(
        "tar", "gz", "bz2", "xz", "zip", "rar", "7z", "tgz", "tbz2", "txz"
    ));

    // 常见的文档格式
    private static final Set<String> DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp"
    ));

    // 常见的媒体格式
    private static final Set<String> MEDIA_EXTENSIONS = new HashSet<>(Arrays.asList(
        "mp3", "mp4", "avi", "mkv", "mov", "wmv", "flv", "wav", "flac", "aac"
    ));

    static {
        // 初始化复合扩展名映射（按优先级排序）
        COMPOUND_EXTENSIONS.put("tar.gz", "tar.gz");
        COMPOUND_EXTENSIONS.put("tar.bz2", "tar.bz2");
        COMPOUND_EXTENSIONS.put("tar.xz", "tar.xz");
        COMPOUND_EXTENSIONS.put("tar.zst", "tar.zst");

        // 其他常见复合扩展名
        COMPOUND_EXTENSIONS.put("gz", "gz");
        COMPOUND_EXTENSIONS.put("bz2", "bz2");
        COMPOUND_EXTENSIONS.put("xz", "xz");
        COMPOUND_EXTENSIONS.put("zst", "zst");
    }

    /**
     * 从文件名提取扩展名
     * 支持复合扩展名（如 .tar.gz）
     *
     * @param fileName 文件名
     *
     * @return 扩展名，不包含点号，如 "tar.gz", "jpg", "txt"
     */
    public static String fileExtension(final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // 移除路径部分，只保留文件名
        final String name = getFileName(fileName);
        if (name.isEmpty()) {
            return "";
        }

        // 特殊处理：没有扩展名的文件
        if (!name.contains(".")) {
            return "";
        }

        // 尝试匹配复合扩展名
        final String compoundExtension = matchCompoundExtension(name);
        if (compoundExtension != null && !compoundExtension.isEmpty()) {
            return compoundExtension;
        }

        // 提取最后一个扩展名
        return extractLastExtension(name);
    }

    /**
     * 从文件名提取基本名称（不含扩展名）
     *
     * @param fileName 文件名
     *
     * @return 基本名称
     */
    public static String fileName(final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        final String name = getFileName(fileName);
        if (!name.contains(".")) {
            return name;
        }

        // 尝试移除复合扩展名
        final String compoundExtension = matchCompoundExtension(name);
        if (compoundExtension != null && !compoundExtension.isEmpty()) {
            return name.substring(0, name.length() - compoundExtension.length() - 1); // -1 for the dot
        }

        // 移除最后一个扩展名
        final int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex > 0 ? name.substring(0, lastDotIndex) : name;
    }

    /**
     * 判断是否为复合扩展名
     *
     * @param extension 扩展名
     *
     * @return 是否为复合扩展名
     */
    public static boolean isCompound(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return extension.contains(".") && extension.contains("tar");
    }

    /**
     * 判断是否为压缩文件扩展名
     *
     * @param extension 扩展名
     *
     * @return 是否为压缩文件
     */
    public static boolean isArchive(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        final String lowerExt = extension.toLowerCase();

        // 直接匹配
        if (ARCHIVE_EXTENSIONS.contains(lowerExt)) {
            return true;
        }

        // 复合扩展名匹配
        for (final String archiveExt : ARCHIVE_EXTENSIONS) {
            if (lowerExt.endsWith("." + archiveExt) || lowerExt.startsWith(archiveExt + ".")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为文档扩展名
     *
     * @param extension 扩展名
     *
     * @return 是否为文档文件
     */
    public static boolean isDocument(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        final String lowerExt = extension.toLowerCase();
        return DOCUMENT_EXTENSIONS.contains(lowerExt);
    }

    /**
     * 判断是否为媒体扩展名
     *
     * @param extension 扩展名
     *
     * @return 是否为媒体文件
     */
    public static boolean isMedia(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        final String lowerExt = extension.toLowerCase();
        return MEDIA_EXTENSIONS.contains(lowerExt);
    }

    /**
     * 获取文件类型分类
     *
     * @param extension 扩展名
     *
     * @return 文件类型
     */
    public static FileType fileType(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return FileType.UNKNOWN;
        }

        final String lowerExt = extension.toLowerCase();

        if (isArchive(lowerExt)) {
            return FileType.ARCHIVE;
        } else if (isDocument(lowerExt)) {
            return FileType.DOCUMENT;
        } else if (isMedia(lowerExt)) {
            return FileType.MEDIA;
        } else if (lowerExt.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|svg).*")) {
            return FileType.IMAGE;
        } else if (lowerExt.matches(".*\\.(exe|dll|so|dylib).*")) {
            return FileType.EXECUTABLE;
        } else {
            return FileType.UNKNOWN;
        }
    }

    /**
     * 从完整路径中提取文件名
     */
    private static String getFileName(final String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "";
        }

        // 处理不同操作系统的路径分隔符
        final String normalizedPath = fullPath.replace('\\', '/');
        final int lastSlashIndex = normalizedPath.lastIndexOf('/');

        if (lastSlashIndex >= 0 && lastSlashIndex < normalizedPath.length() - 1) {
            return normalizedPath.substring(lastSlashIndex + 1);
        }

        return normalizedPath;
    }

    /**
     * 匹配复合扩展名
     */
    private static String matchCompoundExtension(final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        final String lowerFileName = fileName.toLowerCase();

        // 按照定义的复合扩展名进行匹配
        for (final String compoundExt : COMPOUND_EXTENSIONS.keySet()) {
            if (lowerFileName.endsWith("." + compoundExt)) {
                return compoundExt;
            }
        }

        // 动态匹配 .tar.xxx 格式
        if (lowerFileName.matches(".*\\.tar\\.[a-zA-Z0-9]+$")) {
            final int tarIndex = lowerFileName.lastIndexOf(".tar.");
            if (tarIndex > 0) {
                final String suffix = fileName.substring(tarIndex + 1); // 包含 .tar.xxx
                // 验证后面的扩展名是否合理
                final String[] parts = suffix.split("\\.");
                if (parts.length == 3 && "tar".equals(parts[1])) {
                    return suffix.substring(1); // 移除开头的点
                }
            }
        }

        return null;
    }

    /**
     * 提取最后一个扩展名
     */
    private static String extractLastExtension(final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        final int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        ARCHIVE,    // 压缩归档文件
        DOCUMENT,   // 文档文件
        IMAGE,      // 图像文件
        MEDIA,      // 媒体文件
        EXECUTABLE, // 可执行文件
        UNKNOWN     // 未知类型
    }
}