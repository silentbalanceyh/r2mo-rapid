package io.r2mo.jce.component.lic.domain;

import io.r2mo.jce.constant.LicFormat;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-09-21
 */
public interface LicenseID {
    // ------- Io 组件专用
    LicFormat format();

    String licenseId();

    UUID id();

    /**
     * @author lang : 2025-09-20
     */
    interface Valid extends Serializable {

        boolean isOk();

    }
}
