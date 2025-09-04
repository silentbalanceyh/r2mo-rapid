package io.r2mo.generator.normalize;

import com.baomidou.mybatisplus.annotation.TableName;
import io.r2mo.SourcePackage;
import io.r2mo.base.generator.AbstractGenConfig;
import io.r2mo.base.generator.GenMeta;
import io.r2mo.base.generator.SourceStructure;
import io.r2mo.typed.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;

/**
 * @author lang : 2025-09-04
 */
@Slf4j
public class GenConfigMybatisPlus extends AbstractGenConfig {

    @Override
    public List<Class<?>> getEntities() {
        final String entityPackageName = this.getBasePackage().getName() + ".domain";
        log.info("[ GEN ] Entity package name: {}", entityPackageName);
        final Reflections reflections = new Reflections(entityPackageName);
        return reflections.getTypesAnnotatedWith(TableName.class)
            .stream()
            .toList();
    }

    @Override
    public GenMeta getMetadata() {
        final Package sourcePackage = SourcePackage.class.getPackage();
        return GenMeta.builder()
            .schema("V1__init_schema.sql")
            .spi("GenMybatisPlus")
            .database(DatabaseType.MYSQL_8)
            .structure(SourceStructure.DPA)
            .sourcePackage(sourcePackage.getName())
            .version("v1")
            .build();
    }
}
