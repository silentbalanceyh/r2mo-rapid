package io.r2mo.dbe.mybatisplus.generator;

import freemarker.template.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lang : 2025-07-28
 */
public abstract class AbstractGenProcessor implements GenProcessor {

    private final static Map<String, Map<String, Object>> DEFAULT_MODEL = new HashMap<>();

    protected Map<String, Object> getDataParameters(final Class<?> entity) {
        if (DEFAULT_MODEL.containsKey(entity.getName())) {
            return DEFAULT_MODEL.get(entity.getName());
        }

        final String entityPackageName = entity.getPackage().getName();
        final String parentPackageName = entityPackageName.contains(".domain") ?
            entityPackageName.substring(0, entityPackageName.lastIndexOf(".domain")) :
            entityPackageName;
        final Map<String, Object> model = new HashMap<>();
        model.put("className", this.getClassName(entity));
        model.put("entityName", entity.getSimpleName());
        model.put("packageName", parentPackageName);
        model.put("entityPackage", entity.getPackage().getName());
        model.put("author", System.getProperty("user.name"));
        model.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        DEFAULT_MODEL.put(entity.getName(), model);
        return model;
    }

    protected String getClassName(final Class<?> entity) {
        return entity.getSimpleName().replace("Entity", "");
    }

    protected Configuration getConfigurationFromDependency() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_34);
        configuration.setClassLoaderForTemplateLoading(
            Thread.currentThread().getContextClassLoader(),
            "templates"
        );
        configuration.setDefaultEncoding("UTF-8");
        return configuration;
    }
}
