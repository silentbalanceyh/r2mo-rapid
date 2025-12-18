package io.r2mo.io.local.operation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 资源 URL 解析工具类
 * 支持从文件系统、多级 ClassPath (优先考虑调用者上下文) 以及 Zip/Jar 包内部提取资源
 *
 * @author lang : 2025-12-16
 */
class HStoreURL {

    /**
     * 简单的资源获取，使用默认的类加载器策略
     */
    static URL toURL(final String resource) {
        return toURL(resource, null);
    }

    /**
     * 指定调用者的资源获取
     *
     * @param resource 资源路径
     * @param caller   调用者类 (用于指定优先使用的 ClassLoader)，可以为 null
     *
     * @return 对应的 URL，未找到返回 null
     */
    static URL toURL(final String resource, final Class<?> caller) {
        if (resource == null || resource.isBlank()) {
            return null;
        }

        URL url;

        // 1. 策略一：尝试作为物理文件系统路径加载
        // ---------------------------------------------------------
        url = getFromFileSystem(resource);
        if (url != null) {
            return url;
        }

        // 2. 策略二：尝试从 ClassPath 加载 (引入 caller 优先级)
        // ---------------------------------------------------------
        url = getFromClassPath(resource, caller);
        if (url != null) {
            return url;
        }

        // 3. 策略三：尝试解析 ZIP/JAR 内部结构
        // ---------------------------------------------------------
        url = getFromZipStructure(resource);

        return url;
    }

    // ========================================================================
    // 私有辅助方法
    // ========================================================================

    private static URL getFromFileSystem(final String resource) {
        try {
            final Path path = Path.of(resource);
            if (Files.exists(path)) {
                return path.toUri().toURL();
            }
        } catch (final InvalidPathException | MalformedURLException | SecurityException ignored) {
            // 忽略异常
        }
        return null;
    }

    /**
     * 核心修改：接收 caller 参数，构建有优先级的 ClassLoader 链条
     */
    private static URL getFromClassPath(final String resource, final Class<?> caller) {
        // ClassLoader.getResource 均不应以 "/" 开头
        final String cleanPath = resource.startsWith("/") ? resource.substring(1) : resource;

        // 使用 LinkedHashSet 严格保证查找顺序
        final Set<ClassLoader> loaders = new LinkedHashSet<>();

        // [优先级 1]：调用者类加载器 (Caller ClassLoader)
        // 这是本次修改的核心：如果用户明确传了类，说明用户希望在这个类的视界内找资源
        if (caller != null) {
            final ClassLoader callerLoader = caller.getClassLoader();
            // 注意：某些核心类(如 String.class) 的 ClassLoader 为 null (Bootstrap)，需跳过
            if (callerLoader != null) {
                loaders.add(callerLoader);
            }
        }

        // [优先级 2]：线程上下文类加载器 (TCCL)
        // 适用于 Web 容器或框架环境
        try {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                loaders.add(contextLoader);
            }
        } catch (final Throwable ignored) {
        }

        // [优先级 3]：当前工具类的加载器 (作为中间件自身的兜底)
        final ClassLoader selfLoader = HStoreURL.class.getClassLoader();
        if (selfLoader != null) {
            loaders.add(selfLoader);
        }

        // [优先级 4]：系统类加载器 (System ClassLoader)
        try {
            loaders.add(ClassLoader.getSystemClassLoader());
        } catch (final Throwable ignored) {
        }

        // 依次遍历查找
        for (final ClassLoader loader : loaders) {
            final URL url = loader.getResource(cleanPath);
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    private static URL getFromZipStructure(final String resource) {
        if (!resource.contains("!/")) {
            return null;
        }
        try {
            final String[] parts = resource.split("!/", 2);
            final File zipFile = new File(parts[0]);
            if (zipFile.exists() && zipFile.isFile()) {
                return new URL("jar:file:" + zipFile.getAbsolutePath() + "!/" + parts[1]);
            }
        } catch (final MalformedURLException | SecurityException ignored) {
        }
        return null;
    }
}