package io.r2mo.base.io;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 实现 HPath 接口的抽象类，主要是提供前两种方法的核心功能
 * <pre>
 *     1. JAVA_HOME
 *     2. 用户根目录（按不同操作系统有所区别）
 * </pre>
 *
 * @author lang : 2025-09-06
 */
@Slf4j
public abstract class AbstractHPath implements HPath {
    @Override
    public Path pathUser(final String path) {
        // 获取用户目录，考虑不同操作系统
        final String userHome = System.getProperty("user.home");
        if (userHome == null) {
            return null;
        }

        // 根据操作系统调整路径
        final String osName = System.getProperty("os.name").toLowerCase();
        final Path basePath;
        if (osName.contains("windows")) {
            // Windows 系统
            final String userProfile = System.getenv("USERPROFILE");
            basePath = Paths.get(Objects.requireNonNullElse(userProfile, userHome));
        } else {
            // macOS 系统 / Linux 系统
            basePath = Paths.get(userHome);
        }
        return basePath.resolve(path);
    }

    @Override
    public Path pathJava(final String path) {
        // 检查 JAVA_HOME 环境变量
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null || javaHome.isEmpty()) {
            // 如果 JAVA_HOME 不存在，尝试使用 java.home 系统属性
            javaHome = System.getProperty("java.home");
            if (javaHome == null || javaHome.isEmpty()) {
                log.warn("[ R2MO ] JAVA_HOME 环境变量未设置，且无法通过系统属性获取 Java 安装路径。");
                return null;
            }
        }
        return Paths.get(javaHome, path);
    }
}
