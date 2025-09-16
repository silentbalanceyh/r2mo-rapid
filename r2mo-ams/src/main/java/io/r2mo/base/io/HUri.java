package io.r2mo.base.io;

import io.r2mo.base.io.enums.UriScheme;

import java.io.Serializable;

/**
 * @author lang : 2025-09-15
 */
public interface HUri extends Serializable {

    /* 协议，默认是本地文件协议 */
    default UriScheme scheme() {
        return UriScheme.FILE;
    }

    /* 主机，默认空 */
    default String host() {
        return null;
    }

    /* 端口 */
    default int port() {
        return -1;
    }

    /* 账号信息 */
    default String account() {
        return null;
    }

    /* 上下文（通常是根目录）*/
    String context();

    interface T {
        String SEPARATOR = "/";

        /**
         * 解析并连接目录路径和文件路径
         * 自动处理路径分隔符，无论目录是否以 / 结尾
         *
         * @param directory 目录路径
         * @param path      文件路径或相对路径
         *
         * @return 连接后的完整路径
         */
        static String resolve(final String directory, final String path) {
            if (directory == null || directory.isEmpty()) {
                return normalize(path);
            }

            if (path == null || path.isEmpty()) {
                return normalize(directory);
            }

            // 标准化路径
            final String normalizedDir = normalize(directory);
            final String normalizedPath = normalize(path);

            // 处理连接逻辑
            if (normalizedDir.endsWith(SEPARATOR)) {
                if (normalizedPath.startsWith(SEPARATOR)) {
                    // 目录以 / 结尾，路径以 / 开头，去除路径开头的 /
                    return normalizedDir + normalizedPath.substring(1);
                } else {
                    // 目录以 / 结尾，路径不以 / 开头，直接连接
                    return normalizedDir + normalizedPath;
                }
            } else {
                if (normalizedPath.startsWith(SEPARATOR)) {
                    // 目录不以 / 结尾，路径以 / 开头，直接连接
                    return normalizedDir + normalizedPath;
                } else {
                    // 目录不以 / 结尾，路径不以 / 开头，中间添加 /
                    return normalizedDir + SEPARATOR + normalizedPath;
                }
            }
        }

        /**
         * 标准化路径格式
         */
        private static String normalize(final String path) {
            if (path == null || path.isEmpty()) {
                return path;
            }

            // 统一使用正斜杠
            return path.replace('\\', '/');
        }
    }
}
