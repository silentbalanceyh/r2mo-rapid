package io.r2mo.base.web.i18n;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 合并同名 ResourceBundle（properties）资源的 Control。
 * - 支持从多个依赖(JAR/模块)中加载同名 i18n/error*.properties 并合并。
 * - 默认覆盖策略：后处理的资源覆盖先前的键值。
 * - TTL 可配置：开发期可禁用缓存，生产期可永久缓存。
 *
 * @author lang
 * @since 2025-09-29
 */
@Slf4j
public class ForLocaleBundle extends ResourceBundle.Control {

    /** 开发期：不缓存；生产期可改为 TTL_NO_EXPIRATION_CONTROL */
    private final long ttl;

    /** 是否总是触发 reload（配合开发期 TTL_DONT_CACHE 使用） */
    private final boolean alwaysReload;

    /**
     * 开发期构造：不缓存 + 总是 reload（热加载体验更好）
     */
    public ForLocaleBundle() {
        this(ResourceBundle.Control.TTL_DONT_CACHE, true);
    }

    /**
     * 自定义构造
     *
     * @param ttl          缓存时间；使用 {@link #TTL_DONT_CACHE} 或 {@link #TTL_NO_EXPIRATION_CONTROL}
     * @param alwaysReload 是否总是触发 reload（通常与 TTL_DONT_CACHE 搭配）
     */
    public ForLocaleBundle(final long ttl, final boolean alwaysReload) {
        this.ttl = ttl;
        this.alwaysReload = alwaysReload;
    }

    /** 只处理 properties 资源 */
    @Override
    public List<String> getFormats(final String baseName) {
        return Collections.singletonList("java.properties");
    }

    /** 控制缓存 TTL（开发期建议 TTL_DONT_CACHE；生产期建议 TTL_NO_EXPIRATION_CONTROL） */
    @Override
    public long getTimeToLive(final String baseName, final Locale locale) {
        return this.ttl;
    }

    /** 是否需要 reload（开发期返回 true；生产期可返回 false） */
    @Override
    public boolean needsReload(final String baseName, final Locale locale, final String format,
                               final ClassLoader loader, final ResourceBundle bundle, final long loadTime) {
        return this.alwaysReload;
    }

    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale,
                                    final String format, final ClassLoader loader,
                                    final boolean reload) throws IOException {

        if (!"java.properties".equals(format)) {
            return null;
        }

        final String bundleName = this.toBundleName(baseName, locale);
        final String resourceName = this.toResourceName(bundleName, "properties");

        // 收集全部同名资源（来自 A、B…）
        final List<URL> urls = Collections.list(loader.getResources(resourceName));
        if (urls.isEmpty()) {
            return null; // 交给上层回退机制（例如父 Locale）
        }

        // === 如需“固定优先级”，可在此处排序 urls ===
        // 例：让包含 "-a-" 的 jar 最后处理，从而覆盖其它来源：
        // urls.sort(Comparator.comparing(u -> u.toString().contains("-a-") ? 1 : 0));

        final Properties merged = new Properties();

        for (final URL url : urls) {
            try (final InputStream in = open(url, reload);
                 final Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                final Properties p = new Properties();
                p.load(r);

                // 后处理的覆盖先前的
                for (final String k : p.stringPropertyNames()) {
                    merged.put(k, p.getProperty(k));
                }
            }
        }

        if (merged.isEmpty()) {
            return null;
        }

        // 包装成 ResourceBundle（用 StringReader 避免额外文件对象）
        return new PropertyResourceBundle(new StringReader(toPropertiesText(merged)));
    }

    /** reload=true 时禁用 URL 缓存，避免读到旧内容 */
    private static InputStream open(final URL url, final boolean reload) throws IOException {
        if (!reload) {
            return url.openStream();
        }
        final URLConnection conn = url.openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    /** 将合并后的 Properties 写成 .properties 文本，再由 PropertyResourceBundle 读取 */
    private static String toPropertiesText(final Properties props) throws IOException {
        final StringWriter sw = new StringWriter();
        // 第二个参数是注释（会带时间戳），通常传 null，避免影响缓存一致性
        props.store(sw, null);
        return sw.toString();
    }
}
