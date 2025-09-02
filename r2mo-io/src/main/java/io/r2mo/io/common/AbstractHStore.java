package io.r2mo.io.common;

import io.r2mo.base.io.HStore;
import io.r2mo.function.Fn;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author lang : 2025-09-02
 */
public abstract class AbstractHStore implements HStore {
    @Override
    public URL toURL(final File file) {
        if (null == file) {
            return null;
        }
        final Path path = file.toPath();
        return this.toURL(path);
    }

    @Override
    public URL toURL(final Path path) {
        if (null == path) {
            return null;
        }
        return Fn.jvmOr(path.toUri()::toURL, null);
    }
}
