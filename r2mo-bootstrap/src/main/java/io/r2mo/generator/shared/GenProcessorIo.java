package io.r2mo.generator.shared;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author lang : 2025-09-04
 */
@Slf4j
class GenProcessorIo extends AbstractGenProcessor {
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);

        // 获取源代码路径
        dataModel.put("fieldsRequest", "");
        String javaFile = this.getClassName(entity) + "CommonRequest";
        Path sourcePath = this.getSource(javaFile, "io", config);
        this.sourceGenerate("io-request-swagger.ftl", sourcePath, dataModel);


        dataModel.put("fieldsResponse", "");
        javaFile = this.getClassName(entity) + "CommonResponse";
        sourcePath = this.getSource(javaFile, "io", config);
        this.sourceGenerate("io-response-swagger.ftl", sourcePath, dataModel);
    }

    @Override
    protected Path getPackage(final GenConfig config) {
        return config.outApi();
    }
}
