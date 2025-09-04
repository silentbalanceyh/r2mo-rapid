package io.r2mo.dbe.mybatisplus.generator;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author lang : 2025-07-28
 */
@Slf4j
class GenProcessorService extends AbstractGenProcessor {

    @SuppressWarnings("all")
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);


        // 获取源代码路径
        final String javaFile = getClassName(entity) + "Service";
        final Path sourcePath = getSource(javaFile, "service", config);


        // 生成
        this.sourceGenerate("service-interface.ftl", sourcePath, dataModel);
    }
}
