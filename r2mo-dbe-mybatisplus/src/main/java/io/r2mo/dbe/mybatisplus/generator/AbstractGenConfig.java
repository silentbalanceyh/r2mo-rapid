package io.r2mo.dbe.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.TableName;
import io.r2mo.dbe.common.enums.DatabaseType;
import io.r2mo.dbe.mybatisplus.generator.configure.GenPath;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.nio.file.Path;
import java.util.List;

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
    public List<Class<?>> getEntities() {
        final String entityPackageName = this.basePackage.getName() + ".domain";
        log.info("[ GEN ] Entity package name: {}", entityPackageName);
        final Reflections reflections = new Reflections(entityPackageName);
        return reflections.getTypesAnnotatedWith(TableName.class)
            .stream()
            .toList();
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
        return GenPath.of(this.metaStructure());
    }

    @Override
    public Path outApi() {
        final Path path = this.gen().outApi();
        return path.resolve("src/main/java");
    }
}
