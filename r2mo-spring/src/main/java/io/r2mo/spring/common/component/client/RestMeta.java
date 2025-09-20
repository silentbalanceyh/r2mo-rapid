package io.r2mo.spring.common.component.client;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
public class RestMeta implements Serializable {

    private final String appId;
    private String apiBase;
    private RestSigner signer;

    private String pHeaderApp = "X-App-Id";
    private String pHeaderSign = "sig";

    public RestMeta(final String appId) {
        this.appId = appId;
    }

    public RestMeta headerApp(final String headerApp) {
        this.pHeaderApp = headerApp;
        return this;
    }

    public String headerApp() {
        return this.pHeaderApp;
    }

    public RestMeta headerSign(final String headerSign) {
        this.pHeaderSign = headerSign;
        return this;
    }

    public String headerSign() {
        return this.pHeaderSign;
    }

    public RestMeta sign(final String appKey) {
        this.signer = new RestSigner(appKey);
        return this;
    }

    public RestSigner signer() {
        return this.signer;
    }

    public RestMeta apiBase(final String apiBase) {
        this.apiBase = apiBase;
        return this;
    }

    public String apiBase() {
        return this.apiBase;
    }

    public String appId() {
        return this.appId;
    }

    public String apiUrl(final String path) {
        if (Objects.isNull(this.apiBase)) {
            return path;
        } else {
            return this.apiBase + path;
        }
    }
}
