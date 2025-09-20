package io.r2mo.jce.component.lic.io;

import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.constant.LicFormat;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-20
 */
public interface LicenseFormat {

    Cc<String, LicenseFormat> CCT_FORMAT = Cc.openThread();

    static LicenseFormat of(final LicFormat format) {
        if (LicFormat.TEXT == format) {
            return CCT_FORMAT.pick(LicenseFormatLic::new, LicenseFormatLic.class.getName());
        }
        return null;
    }

    String format(LicenseFile file);

    LicenseFile parse(String data);
}
