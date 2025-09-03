package io.r2mo.spring.common.client;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.GdbSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
public class RestClient {
    private static final Cc<String, RestClient> CCT_CLIENT = Cc.openThread();
    private final RestTemplate executor;
    private final RestMeta meta;

    private RestClient(final RestTemplate executor, final RestMeta meta) {
        this.executor = executor;
        this.meta = meta;
    }

    public static RestClient of(final RestTemplate executor, final RestMeta meta) {
        final String key = executor.hashCode() + "@" + meta.hashCode();
        return CCT_CLIENT.pick(() -> new RestClient(executor, meta), key);
    }

    public <T> T get(final String url, final Class<T> respType) {
        final ResponseEntity<T> response = this.executor.getForEntity(url, respType);
        log.info("[ R2MO ] GET 请求 URL：{}", url);
        return this.postResponse(response);
    }

    public <T> T post(final String path, final Map<String, Object> body, final Class<T> respType) {
        // 1. 预处理 body
        final HttpEntity<Map<String, Object>> request = this.preRequest(body);
        // 2. 执行请求
        final String url = this.meta.apiUrl(path);
        final ResponseEntity<T> response = this.executor.postForEntity(url, request, respType);
        log.info("[ R2MO ] POST 请求 URL：{}", url);
        return this.postResponse(response);
    }

    private <T> T postResponse(final ResponseEntity<T> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("[ R2MO ] 请求失败，状态码：" + response.getStatusCode());
        }
        return response.getBody();
    }

    private HttpEntity<Map<String, Object>> preRequest(final Map<String, Object> body) {
        // 1. 预处理 body
        final Map<String, Object> request = this.preBody(body);
        // 2. 预处理 headers
        final HttpHeaders headers = this.preHeaders();
        return new HttpEntity<>(request, headers);
    }

    private Map<String, Object> preBody(final Map<String, Object> body) {
        if (Objects.isNull(body) || body.isEmpty()) {
            return new TreeMap<>();
        }
        final Map<String, Object> request = new TreeMap<>(body);
        final GdbSigner signer = this.meta.signer();
        if (Objects.nonNull(signer)) {
            final String sig = signer.sign(request);
            request.put(this.meta.headerSign(), sig);
        }
        return request;
    }

    private HttpHeaders preHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String appId = this.meta.appId();
        if (Objects.nonNull(appId)) {
            headers.add(this.meta.headerApp(), appId);
        }
        return headers;
    }
}
