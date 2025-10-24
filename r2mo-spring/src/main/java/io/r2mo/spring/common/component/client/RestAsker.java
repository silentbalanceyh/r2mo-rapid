package io.r2mo.spring.common.component.client;

import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author lang : 2025-09-03
 */
@Slf4j
public class RestAsker {
    private static final Cc<String, RestAsker> CCT_CLIENT = Cc.openThread();
    private final RestClient executor;
    private final RestMeta meta;

    private RestAsker(final RestMeta meta) {
        this.executor = RestClient.create();
        this.meta = meta;
    }

    public static RestAsker of(final RestMeta meta) {
        return CCT_CLIENT.pick(() -> new RestAsker(meta), String.valueOf(meta.hashCode()));
    }

    private Map<String, Object> withSignature(final Map<String, Object> body) {
        final Map<String, Object> payload = new TreeMap<>(body);
        // payload.types(this.meta.appId(), this.meta.appId());
        if (this.meta.signer() != null) {
            payload.put(this.meta.headerSign(), this.meta.signer().sign(payload));
        }
        return payload;
    }

    private RestClient.RequestBodySpec preparePost(final String path, final Map<String, Object> body) {
        return this.executor.post()
            .uri(this.meta.apiUrl(path))
            .contentType(MediaType.APPLICATION_JSON)
            .header(this.meta.headerApp(), this.meta.appId())
            .body(this.withSignature(body));
    }

    private RestClient.RequestBodySpec preparePut(final String path, final Map<String, Object> body) {
        return this.executor.put()
            .uri(this.meta.apiUrl(path))
            .contentType(MediaType.APPLICATION_JSON)
            .header(this.meta.headerApp(), this.meta.appId())
            .body(this.withSignature(body));
    }

    private RestClient.RequestHeadersSpec<?> prepareGet(final String path, final Map<String, Object> params) {
        final String url = this.prepareUrl(path, params);
        return this.executor.get()
            .uri(url)
            .header(this.meta.headerApp(), this.meta.appId());
    }

    private RestClient.RequestHeadersSpec<?> prepareDelete(final String path, final Map<String, Object> params) {
        final String url = this.prepareUrl(path, params);
        return this.executor.delete()
            .uri(url)
            .header(this.meta.headerApp(), this.meta.appId());
    }

    private String prepareUrl(final String path, final Map<String, Object> params) {
        final StringBuilder url = new StringBuilder(this.meta.apiUrl(path));
        if (params != null && !params.isEmpty()) {
            url.append("?");
            params.forEach((k, v) -> url.append(k).append("=").append(v).append("&"));
            url.deleteCharAt(url.length() - 1);
        }
        return url.toString();
    }

    // -------------------- 对外方法 --------------------

    public <T> T post(final String path, final Map<String, Object> body, final Class<T> respType) {
        return this.preparePost(path, body).retrieve().body(respType);
    }

    public <T> T put(final String path, final Map<String, Object> body, final Class<T> respType) {
        return this.preparePut(path, body).retrieve().body(respType);
    }

    public <T> T get(final String path, final Map<String, Object> params, final Class<T> respType) {
        return this.prepareGet(path, params).retrieve().body(respType);
    }

    public <T> T delete(final String path, final Map<String, Object> params, final Class<T> respType) {
        return this.prepareDelete(path, params).retrieve().body(respType);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> post(final String path, final Map<String, Object> body) {
        return this.preparePost(path, body)
            .retrieve()
            .body(Map.class);   // Spring 会把 JSON 解析成 LinkedHashMap
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> put(final String path, final Map<String, Object> body) {
        return this.preparePut(path, body)
            .retrieve()
            .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> get(final String path, final Map<String, Object> params) {
        return this.prepareGet(path, params)
            .retrieve()
            .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> delete(final String path, final Map<String, Object> params) {
        return this.prepareDelete(path, params)
            .retrieve()
            .body(Map.class);
    }
}
