package io.r2mo.jce.component.lic;

/**
 * @author lang : 2025-09-20
 */
public interface AlgLicenseSpec {
    String alg();

    int length();

    String algCipher();

    default String algSign() {
        return null;
    }
}
