package io.r2mo.base.generator;

import io.r2mo.base.generator.configure.GenPath;
import io.r2mo.typed.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * @author lang : 2025-07-28
 */
@Slf4j
public abstract class AbstractGenConfig implements GenConfig {
    private transient final Package basePackage;

    protected AbstractGenConfig() {
        this.basePackage = this.getClass().getPackage();
    }

    @Override
    public Package getBasePackage() {
        return this.basePackage;
    }

    @Override
    public Path outProvider() {
        final Path path = this.gen().outDao();
        return path.resolve("src/main/java");
    }

    @Override
    public Path outProviderXml() {
        final Path path = this.gen().outDao();
        final String module = Path.of(System.getProperty("user.dir")).toFile().getName();
        return path.resolve("src/main/resources/" + module + "/mapper");
    }

    @Override
    public Path outSql() {
        final Path path = this.gen().outSchema();
        final String module = Path.of(System.getProperty("user.dir")).toFile().getName();
        return path.resolve("src/main/resources/" + module + "/database");
    }

    @Override
    public SourceStructure metaStructure() {
        return SourceStructure.DPA;
    }

    @Override
    public DatabaseType metaDatabaseType() {
        return DatabaseType.MYSQL_8;
    }

    private GenPath gen() {
        final GenMeta meta = this.getMetadata();
        return GenPath.of(meta.getStructure());
    }

    @Override
    public Path outApi() {
        final Path path = this.gen().outApi();
        return path.resolve("src/main/java");
    }
}
