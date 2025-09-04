package io.r2mo.generator.shared;

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

        // 生成 ServiceV?
        String javaFile = this.getClassName(entity) + "Controller";
        Path sourcePath = this.getSource(javaFile, "controller", config);
        this.sourceGenerate("controller-interface.ftl", sourcePath, dataModel);

        // 生成 ServiceV?Impl
        javaFile = this.getClassName(entity) + "Controller" + meta.V();
        sourcePath = this.getSource(javaFile, "controller." + meta.v(), config);
        this.sourceGenerate("controller-v1-impl.ftl", sourcePath, dataModel);
    }

    @Override
    protected Path getPackage(final GenConfig config) {
        return config.outApi();
    }
}
