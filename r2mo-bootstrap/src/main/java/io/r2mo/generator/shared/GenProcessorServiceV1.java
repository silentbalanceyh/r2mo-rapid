package io.r2mo.generator.shared;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenMeta;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author lang : 2025-09-04
 */
@Slf4j
class GenProcessorServiceV1 extends AbstractGenProcessor {
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);


        // 获取源代码路径
        final GenMeta meta = config.getMetadata();

        // 生成 ServiceV?
        String javaFile = getClassName(entity) + "Service" + meta.V();
        Path sourcePath = getSource(javaFile, "business." + meta.v(), config);
        this.sourceGenerate("service-v1-interface.ftl", sourcePath, dataModel);

        // 生成 ServiceV?Impl
        javaFile = getClassName(entity) + "Service" + meta.V() + "Impl";
        sourcePath = getSource(javaFile, "business." + meta.v(), config);
        this.sourceGenerate("service-v1-impl.ftl", sourcePath, dataModel);
    }
}
