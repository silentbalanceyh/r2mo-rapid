package io.r2mo.dbe.mybatisplus.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
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
        try {
            final Configuration cfg = this.getConfigurationFromDependency();
            // 数据模型
            final Map<String, Object> dataModel = this.getDataParameters(entity);
            // Mapper 接口生成
            final Template mapperTemplate = cfg.getTemplate("mapper-xml.ftl");
            final String mapperXml = getClassName(entity) + "Mapper";
            // 确保目录存在
            final Path packagePath = config.outProviderXml();
            if (!Files.exists(packagePath)) {
                Files.createDirectories(packagePath);
                log.info("[ GEN ] Directory created: " + packagePath);
            }
            final Path mapperInterfacePath = packagePath.resolve(mapperXml + ".xml");
            try (final var writer = Files.newBufferedWriter(mapperInterfacePath)) {
                mapperTemplate.process(dataModel, writer);
                log.info("[ GEN ] Mapper: " + mapperInterfacePath + " generated successfully.");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
