package io.r2mo.spi.common;

import io.r2mo.base.io.AbstractHPath;
import io.r2mo.base.io.HPath;
import io.r2mo.typed.annotation.SPID;

import java.nio.file.Path;
import java.nio.file.Paths;

@SPID(HPath.PATH_DEFAULT_NAME)
public class PathOfHome extends AbstractHPath {

    @Override
    public Path pathHome(final String path, final String envName) {
        if (envName == null || envName.isEmpty()) {
            // 如果 envName 没有传入或为空，从当前路径解析
            if (path == null || path.isEmpty()) {
                // path 也为空，返回当前工作目录
                return Paths.get("");
            } else {
                // 从当前路径解析 path
                return Paths.get(path);
            }
        } else {
            // 如果 envName 传入了，从环境变量路径解析
            final String envPath = System.getenv(envName);
            if (envPath == null || envPath.isEmpty()) {
                // 环境变量不存在，返回 null 或根据需要可以返回当前路径
                return null;
            }

            if (path == null || path.isEmpty()) {
                // path 为空，直接返回环境变量指定的路径
                return Paths.get(envPath);
            } else {
                // 从环境变量路径解析 path
                return Paths.get(envPath, path);
            }
        }
    }
}
