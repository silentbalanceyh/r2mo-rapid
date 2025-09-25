package io.r2mo.boot.spring.generator.shared;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenMeta;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author lang : 2025-09-04
 */
class GenProcessorController extends AbstractGenProcessor {
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);


        // 获取源代码路径
        final GenMeta meta = config.getMetadata();

        final String className = dataModel.get("className").toString();
        final String classModule = dataModel.get("classModule").toString();

        String javaFile = className + "CrudController";
        Path sourcePath = this.getSource(javaFile,
            "controller.gen." + classModule, config);
        this.sourceGenerate("controller-v1-interface.ftl", sourcePath, dataModel);

        javaFile = className + "CrudController" + meta.V();
        sourcePath = this.getSource(javaFile,
            "controller.gen." + classModule, config);
        this.sourceGenerate("controller-v1-impl.ftl", sourcePath, dataModel);
    }

    @Override
    protected Path getPackage(final GenConfig config) {
        return config.outApi();
    }
}
