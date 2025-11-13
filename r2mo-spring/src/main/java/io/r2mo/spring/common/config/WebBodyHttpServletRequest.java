package io.r2mo.spring.common.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 可重复读取 Body 的 HttpServletRequest 包装类。
 * 适用于需要在 Filter、Aspect 等组件中提前读取 JSON Body，
 * 同时又不影响后续 Spring MVC 参数绑定的场景。
 *
 * @author lang : 2025-11-13
 */
public class WebBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public WebBodyHttpServletRequest(final HttpServletRequest request) throws IOException {
        super(request);
        // 读取原始输入流并缓存
        try (final InputStream inputStream = request.getInputStream()) {
            this.cachedBody = inputStream.readAllBytes();
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 内部 ServletInputStream 实现，基于缓存字节数组
     */
    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CachedServletInputStream(final byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return this.buffer.read();
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
        public void setReadListener(final ReadListener readListener) {
            throw new UnsupportedOperationException("不支持异步读取");
        }
    }
}