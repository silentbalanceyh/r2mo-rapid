package io.r2mo.generator.normalize;

import com.baomidou.mybatisplus.annotation.TableName;
import io.r2mo.base.generator.AbstractGenConfig;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;

/**
 * @author lang : 2025-09-04
 */
@Slf4j
public abstract class GenConfigMybatisPlus extends AbstractGenConfig {

    @Override
    public List<Class<?>> getEntities() {
        final String entityPackageName = this.getBasePackage().getName() + ".domain";
        log.info("[ GEN ] Entity package name: {}", entityPackageName);
        final Reflections reflections = new Reflections(entityPackageName);
        return reflections.getTypesAnnotatedWith(TableName.class)
            .stream()
            .toList();
    }
}
