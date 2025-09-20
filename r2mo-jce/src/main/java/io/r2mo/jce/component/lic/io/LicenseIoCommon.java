package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.jce.constant.LicFormat;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-09-20
 */
class LicenseIoCommon extends AbstractLicenseIo implements LicenseIo {
    private static final Cc<String, LicenseIo> CCT_IO = Cc.openThread();
    private static final ConcurrentMap<LicFormat, Function<HStore, LicenseIo>> MAP_IO =
        new ConcurrentHashMap<>() {
            {
                this.put(LicFormat.TEXT, LicenseIoLic::new);
            }
        };

    LicenseIoCommon(final HStore store) {
        super(store);
    }

    @Override
    public Binary writeTo(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        this.ensureConfiguration(configuration);
        this.ensureFile(licenseFile);

        return this.executor(licenseFile.format()).writeTo(licenseFile, configuration);
    }

    private void ensureConfiguration(final LicenseConfiguration configuration) {
        if (Objects.isNull(configuration)) {
            throw new IllegalArgumentException("[ R2MO ] LicenseConfiguration 不能为空！");
        }
        if (configuration.isOk()) {
            throw new IllegalArgumentException("[ R2MO ] LicenseConfiguration 参数不完整，无法执行操作！");
        }
    }

    private void ensureFile(final LicenseFile licenseFile) {
        if (Objects.isNull(licenseFile)) {
            throw new IllegalArgumentException("[ R2MO ] LicenseFile 不能为空！");
        }
        if (licenseFile.isOk()) {
            throw new IllegalArgumentException("[ R2MO ] LicenseFile 参数不完整，无法执行操作！");
        }
    }

    private void ensurePath(final LicensePath path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("[ R2MO ] LicensePath 不能为空！");
        }
        if (path.isOk()) {
            throw new IllegalArgumentException("[ R2MO ] LicensePath 参数不完整，无法执行操作！");
        }
    }

    private LicenseIo executor(final LicFormat format) {
        final Function<HStore, LicenseIo> constructor = MAP_IO.get(format);
        if (Objects.isNull(constructor)) {
            throw new IllegalArgumentException("[ R2MO ] 当前不支持的 License 格式：" + format);
        }
        return CCT_IO.pick(() -> constructor.apply(this.store), format.name());
    }

    @Override
    public LicenseFile readIn(final LicensePath path, final LicenseConfiguration configuration) {
        this.ensureConfiguration(configuration);
        this.ensurePath(path);

        return this.executor(path.format()).readIn(path, configuration);
    }

    @Override
    public LicenseData verify(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        this.ensureConfiguration(configuration);
        this.ensureFile(licenseFile);

        return this.executor(licenseFile.format()).verify(licenseFile, configuration);
    }
}
