package io.r2mo.base.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.r2mo.SourceReflect;
import io.r2mo.function.Fn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-07-28
 */
@Slf4j
public abstract class AbstractGenProcessor implements GenProcessor {

    private final static Map<String, Map<String, Object>> DEFAULT_MODEL = new HashMap<>();

    // 参数提取
    protected Map<String, Object> getDataParameters(final Class<?> entity, final GenConfig config) {
        if (DEFAULT_MODEL.containsKey(entity.getName())) {
            return DEFAULT_MODEL.get(entity.getName());
        }

        final String entityPackageName = entity.getPackage().getName();
        final String parentPackageName = entityPackageName.contains(".domain") ?
            entityPackageName.substring(0, entityPackageName.lastIndexOf(".domain")) :
            entityPackageName;
        final Map<String, Object> model = new HashMap<>();
        final String className = this.getClassName(entity);
        model.put("actor", className.toLowerCase(Locale.ROOT));
        model.put("className", className);
        model.put("classModule", className.toLowerCase(Locale.ROOT));
        model.put("entityName", entity.getSimpleName());
        model.put("packageName", parentPackageName);
        model.put("entityPackage", entity.getPackage().getName());


        model.put("author", System.getProperty("user.name"));
        model.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        final Schema schema = entity.getDeclaredAnnotation(Schema.class);
        model.put("entityDisplay", Objects.isNull(schema) ? "" : schema.name());

        final GenMeta meta = config.getMetadata();
        final Package sourcePackage = SourceReflect.class.getPackage();
        model.put("sourcePackage", sourcePackage.getName());
        model.put("v", meta.getVersion());
        model.put("V", meta.getVersion().toUpperCase(Locale.getDefault()));


        final String baseAct = meta.baseActName(true);
        final String baseActSimple = meta.baseActName(false);
        model.put("baseAct", baseAct);
        model.put("baseActName", baseActSimple);

        DEFAULT_MODEL.put(entity.getName(), model);
        return model;
    }

    protected String getClassName(final Class<?> entity) {
        return entity.getSimpleName().replace("Entity", "");
    }

    private Configuration getConfigurationFromDependency() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_34);
        configuration.setClassLoaderForTemplateLoading(
            Thread.currentThread().getContextClassLoader(),
            "templates"
        );
        configuration.setDefaultEncoding("UTF-8");
        return configuration;
    }

    @SuppressWarnings("all")
    protected void sourceGenerate(final String templateName, final Path sourcePath, final Map<String, Object> dataModel) {
        final Configuration cfg = this.getConfigurationFromDependency();

        final Template template = Fn.jvmOr(() -> cfg.getTemplate(templateName));
        try (final var writer = Files.newBufferedWriter(sourcePath)) {
            template.process(dataModel, writer);
            log.info("[ GEN ] 文件生成：{}", sourcePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 构造文件全路径
     * 1. 基础包结构（统一）
     * 2. Provider 提供，PDA 和 ONE 不同代码结构此处不一样
     * 3. 文件名
     */
    protected Path getSource(final String javaFile, final String subpackage, final GenConfig config) {
        final String basePackage = config.getBasePackage().getName();
        final Path pathPackage = this.getPackage(config);
        final String subPackage = basePackage + "." + subpackage;
        final Path pathModule = pathPackage.resolve(subPackage.replace(".", "/"));
        // 确保目录存在
        if (!Files.exists(pathModule)) {
            Fn.jvmAt(() -> Files.createDirectories(pathModule));
            log.info("[ GEN ] 目录创建：{}", pathModule);
        }
        // 针对目录进行进一步构造
        return pathModule.resolve(javaFile + ".java");
    }

    protected Path getPackage(final GenConfig config) {
        return config.outProvider();
    }
}
