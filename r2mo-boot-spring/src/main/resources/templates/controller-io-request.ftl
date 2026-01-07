package ${packageName}.controller.gen.${classModule};

import ${entityPackage}.${entityName};
import ${sourcePackage}.spring.common.webflow.PreRequest;
import ${sourcePackage}.typed.webflow.WebRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

${enumsImport}

import ${sourcePackage}.typed.json.JObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
/**
 * ${className} 请求
 *
 * @author ${author}
 * @since ${date}
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "${entityDisplay}请求")
public class ${className}CommonRequest extends PreRequest implements WebRequest<${entityName}> {
    ${fieldsRequest}

    @Override
    public ${entityName} data() {
        final ${entityName} entity = new ${entityName}();

        this.writeScope(entity);

        this.writeTo(entity);

        return entity;
    }
}
