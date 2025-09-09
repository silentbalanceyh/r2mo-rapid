package io.r2mo.generator.shared;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenField;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.spi.SPI;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

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
        final GenField fieldGen = this.getFieldGenerator(config);
        if(Objects.isNull(fieldGen)){
            dataModel.put("fieldsRequest", "");
            dataModel.put("fieldsResponse", "");
            dataModel.put("enumsImport", "");
        }else {
            final String req = fieldGen.generateReq(entity, config);
            dataModel.put("fieldsRequest", req);
            final String resp = fieldGen.generateResp(entity, config);
            dataModel.put("fieldsResponse", resp);
            final String enums = fieldGen.generateEnum(entity);
            dataModel.put("enumsImport", enums);
        }
        final String className = dataModel.get("className").toString();
        final String classModule = dataModel.get("classModule").toString();
        String javaFile = className + "CommonRequest";
        Path sourcePath = this.getSource(javaFile,
            "controller.gen." + classModule, config);
        this.sourceGenerate("controller-io-request.ftl", sourcePath, dataModel);

        javaFile = className + "CommonResponse";
        sourcePath = this.getSource(javaFile,
            "controller.gen." + classModule, config);
        this.sourceGenerate("controller-io-response.ftl", sourcePath, dataModel);
    }

    private GenField getFieldGenerator(final GenConfig config) {
        final String name = config.getMetadata().getSpi();
        final GenProcessor processor = SPI.findOne(GenProcessor.class, name);
        if(Objects.isNull(processor)){
            return null;
        }
        return processor.getFieldProcessor();
    }

    @Override
    protected Path getPackage(final GenConfig config) {
        return config.outApi();
    }
}
