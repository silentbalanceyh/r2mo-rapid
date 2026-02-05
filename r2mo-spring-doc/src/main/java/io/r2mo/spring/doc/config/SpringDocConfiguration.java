package io.r2mo.spring.doc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "springdoc")
@Data
public class SpringDocConfiguration {
    private final ApiDocs apiDocs = new ApiDocs();
    private final SwaggerUi swaggerUi = new SwaggerUi();
    private final String team = "Lang.Yu @ silentbalanceyh@126.com";

    @Data
    public static class ApiDocs {
        private boolean enabled = true;
        private String path = "openapi.yaml";
        private String version = "1.0.0";
        private String title = "Zero Ecotope API Docs";
        private String description = "";
    }

    @Data
    public static class SwaggerUi {
        private boolean enabled = true;
        private String validatorUrl = "";
        private String tagsSorter = "alpha";
        private String operationsSorter = "method";
        private String docExpansion = "none";
        private boolean displayRequestDuration = true;
        private boolean filter = true;
        private boolean deepLinking = true;
        private boolean tryItOutEnabled = true;
    }
}
