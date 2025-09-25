package io.r2mo.boot.spring.generator.shared;

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

        // 生成 IServiceV?
        final String className = dataModel.get("className").toString();
        final String classModule = dataModel.get("classModule").toString();
        String javaFile = "I" + className + "Service" + meta.V();
        Path sourcePath = this.getSource(javaFile,
            "service.gen." + classModule, config);
        this.sourceGenerate("service-v1-interface.ftl", sourcePath, dataModel);

        // 生成 IServiceV?Impl
        javaFile = "I" + className + "Service" + meta.V() + "Impl";
        sourcePath = this.getSource(javaFile,
            "service.gen." + classModule, config);
        this.sourceGenerate("service-v1-impl.ftl", sourcePath, dataModel);
    }
}
