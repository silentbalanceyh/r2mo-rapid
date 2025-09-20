package io.r2mo.jce.component.lic;

import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-19
 */
public interface LicenseService {

    boolean generate(String directory);

    LicenseFile build(LicenseData data, PrivateKey privateKey);

    LicenseData extract(LicenseFile file, PublicKey publicKey);
}
