package io.r2mo.typed.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 可重复读取请求体的 HttpServletRequest 包装器。
 * <p>
 * 与 Spring 的 ContentCachingRequestWrapper 不同，本实现的 getInputStream()
 * 每次调用都返回一个全新的流，确保下游多个组件（Filter、ArgumentResolver、
 * AOP 切面等）都能独立读取请求体而不会出现 "end-of-input" 错误。
 * <p>
 * 使用场景：Filter 链中需要缓存 body 供下游多次读取时，用本类包装原始
 * request 再传入 chain.doFilter()。
 * <p>
 * 注意：构造时一次性读取全部 body 到内存（byte[]）。仅适用于 body 较小的
 * 请求（如登录、表单提交），不适合大文件上传场景。
 *
 * @author lang : 2026-07-27
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    /**
     * @param request 原始请求，其 InputStream 在构造时被全量读取并缓存
     * @throws IOException 读取 InputStream 失败时抛出
     */
    public CachedBodyHttpServletRequest(final HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = request.getInputStream().readAllBytes();
    }

    /**
     * 直接获取缓存的 body 字节数组，供需要 raw bytes 的调用方使用。
     */
    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    /**
     * 每次调用返回一个全新的 ServletInputStream，内部基于缓存的 byte[]。
     * 多次调用互不干扰。
     */
    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    // ---- 内部类 ----

    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        CachedBodyServletInputStream(final byte[] cachedBody) {
            this.buffer = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return this.buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(final ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() {
            return this.buffer.read();
        }

        @Override
        public int read(final byte[] b, final int off, final int len) {
            return this.buffer.read(b, off, len);
        }
    }
}
