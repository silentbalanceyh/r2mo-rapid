package io.r2mo.spring.common.client;

import io.r2mo.typed.common.GdbSigner;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-09-03
 */
public class RestMeta implements Serializable {

    private final String appId;
    private String apiBase;
    private GdbSigner signer;

    private String pHeaderApp = "X-App-Id";
    private String pHeaderSign = "sig";

    public RestMeta(final String appId) {
        this.appId = appId;
    }

    public void headerApp(final String pNameAppHeader) {
        this.pHeaderApp = pNameAppHeader;
    }

    public String headerApp() {
        return this.pHeaderApp;
    }

    public void headerSign(final String pNameSign) {
        this.pHeaderSign = pNameSign;
    }

    public String headerSign() {
        return this.pHeaderSign;
    }

    public RestMeta sign(final String appKey) {
        this.signer = new GdbSigner(appKey);
        return this;
    }

    public GdbSigner signer() {
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
