package io.r2mo.jce.component.lic.owner;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-21
 */
public interface PreActiveService {

    Cc<String, PreActiveService> CC_PRE_ACTIVE = Cc.openThread();

    static PreActiveService of(final HStore store) {
        return CC_PRE_ACTIVE.pick(() -> new PreActiveServiceCommon(store), String.valueOf(store.hashCode()));
    }

    Activation generate(LicenseData licenseData, LicenseConfiguration configuration);

    boolean verify(Activation code, LicenseConfiguration configuration);
}
