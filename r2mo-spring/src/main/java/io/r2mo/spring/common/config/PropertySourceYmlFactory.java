package io.r2mo.spring.common.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.Objects;

/**
 * yml 配置源工厂
 */
public class PropertySourceYmlFactory extends DefaultPropertySourceFactory {


    @Override
    @SuppressWarnings("all")
    public PropertySource<?> createPropertySource(final String name, final EncodedResource resource) throws IOException {
        final String sourceName = resource.getResource().getFilename();
        if (StringUtils.isNotBlank(sourceName) && Strings.CS.endsWithAny(sourceName, ".yml", ".yaml")) {
            final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return new PropertiesPropertySource(sourceName, Objects.requireNonNull(factory.getObject()));
        }
        return super.createPropertySource(name, resource);
    }
}
