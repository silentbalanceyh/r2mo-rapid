package io.r2mo.spring.doc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    private final SpringDocConfiguration properties;
    private final SwaggerUiConfigProperties swaggerUiProperties;
    private final SpringDocConfigProperties springDocProperties;

    public SwaggerConfiguration(final SpringDocConfiguration properties,
                                final SwaggerUiConfigProperties swaggerUiProperties,
                                final SpringDocConfigProperties springDocProperties) {
        this.properties = properties;
        this.swaggerUiProperties = swaggerUiProperties;
        this.springDocProperties = springDocProperties;
    }

    @PostConstruct
    public void init() {
        final var ui = this.properties.getSwaggerUi();

        // 映射 UI 交互配置
        this.swaggerUiProperties.setTagsSorter(ui.getTagsSorter());
        this.swaggerUiProperties.setOperationsSorter(ui.getOperationsSorter());
        this.swaggerUiProperties.setDocExpansion(ui.getDocExpansion());
        this.swaggerUiProperties.setDisplayRequestDuration(ui.isDisplayRequestDuration());
        this.swaggerUiProperties.setFilter(String.valueOf(ui.isFilter()));
        this.swaggerUiProperties.setDeepLinking(ui.isDeepLinking());
        this.swaggerUiProperties.setTryItOutEnabled(ui.isTryItOutEnabled());
        this.swaggerUiProperties.setValidatorUrl(ui.getValidatorUrl());

        // 映射开启状态
        if (this.springDocProperties.getApiDocs() == null) {
            this.springDocProperties.setApiDocs(new SpringDocConfigProperties.ApiDocs());
        }
        this.springDocProperties.getApiDocs().setEnabled(this.properties.getApiDocs().isEnabled());
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final var openapi = this.properties.getApiDocs();
        return new OpenAPI()
            .info(new Info()
                .title(openapi.getTitle())
                .description(openapi.getDescription())
                .version(openapi.getVersion())
                .contact(new Contact().name(this.properties.getTeam())));
    }
}