package io.r2mo.base.generator.configure;

import java.nio.file.Path;

/**
 * @author lang : 2025-08-29
 */
class GenPathDPA implements GenPath {

    private Path outModule(final String suffix) {
        final Path path = Path.of(System.getProperty("user.dir"));
        final String module = path.toFile().getName();
        final String submodule = module + suffix;
        return path.resolve(submodule);
    }

    @Override
    public Path outDao() {
        return this.outModule("-provider");
    }

    @Override
    public Path outService() {
        return this.outModule("-provider");
    }

    @Override
    public Path outApi() {
        return this.outModule("-api");
    }

    @Override
    public Path outSchema() {
        return this.outModule("-domain");
    }
}
