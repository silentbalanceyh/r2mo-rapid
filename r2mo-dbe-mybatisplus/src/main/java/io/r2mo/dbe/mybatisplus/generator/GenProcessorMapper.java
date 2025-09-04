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
class GenProcessorMapper extends AbstractGenProcessor {

    @SuppressWarnings("all")
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);


        // 获取源代码路径
        final String javaFile = getClassName(entity) + "Mapper";
        final Path sourcePath = getSource(javaFile, "mapper", config);


        // 生成
        this.sourceGenerate("mapper-interface.ftl", sourcePath, dataModel);
    }

    @Override
    protected Path getPackage(final GenConfig config) {
        return config.outProvider();
    }
}
