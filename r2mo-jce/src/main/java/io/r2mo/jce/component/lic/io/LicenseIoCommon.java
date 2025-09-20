package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicenseLocation;
import io.r2mo.jce.constant.LicFormat;
import io.r2mo.typed.cc.Cc;

import java.io.InputStream;
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
    public InputStream writeTo(final LicenseLocation location, final LicenseFile fileData) {
        this.ensureLocation(location);
        this.ensureFileData(fileData);

        return this.executor(fileData.format()).writeTo(location, fileData);
    }

    private void ensureLocation(final LicenseLocation location) {
        if (Objects.isNull(location)) {
            throw new IllegalArgumentException("[ R2MO ] LicenseLocation 不能为空！");
        }
        if (!location.isOk()) {
            throw new IllegalArgumentException("[ R2MO ] LicenseLocation 参数不完整，无法执行操作！");
        }
    }

    private void ensureFileData(final LicenseFile fileData) {
        if (Objects.isNull(fileData)) {
            throw new IllegalArgumentException("[ R2MO ] LicenseFile 不能为空！");
        }
        if (!fileData.isOk()) {
            throw new IllegalArgumentException("[ R2MO ] LicenseFile 参数不完整，无法执行操作！");
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
    public LicenseFile readIn(final LicenseLocation location, final LicFormat format) {
        this.ensureLocation(location);

        return this.executor(format).readIn(location, format);
    }

    @Override
    public LicenseData verify(final LicenseLocation location, final LicenseFile fileData) {
        this.ensureLocation(location);
        this.ensureFileData(fileData);

        return this.executor(fileData.format()).verify(location, fileData);
    }
}
