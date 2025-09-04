package io.r2mo.dbe.mybatisplus.generator;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.function.Fn;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author lang : 2025-07-28
 */
@Slf4j
class GenProcessorMapperXml extends AbstractGenProcessor {

    @SuppressWarnings("all")
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // 获取参数
        final Map<String, Object> dataModel = this.getDataParameters(entity, config);


        // 获取源代码
        final Path sourcePath = getSource(entity, config);


        // 生成
        this.sourceGenerate("mapper-xml.ftl", sourcePath, dataModel);
    }

    private Path getSource(Class<?> entity, GenConfig config) {
        final String mapperXml = getClassName(entity) + "Mapper";
        // 确保目录存在
        final Path packagePath = config.outProviderXml();
        if (!Files.exists(packagePath)) {
            Fn.jvmAt(() -> Files.createDirectories(packagePath));
            log.info("[ GEN ] 目录创建：{}", packagePath);
        }
        return packagePath.resolve(mapperXml + ".xml");
    }
}
