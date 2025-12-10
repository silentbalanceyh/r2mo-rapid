package ${packageName}.io;

import ${entityPackage}.${entityName};
import ${sourcePackage}.spring.common.webflow.PostResponse;
import ${sourcePackage}.typed.webflow.WebResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

${enumsImport}

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
/**
* <p>
    * ${className} 响应
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class ${className}CommonResponse extends PostResponse implements WebResponse<${entityName}> {
${fieldsResponse}

@Override
@SuppressWarnings("unchecked")
public ${className}CommonResponse data(${entityName} data) {
final ${className}CommonResponse response = new ${className}CommonResponse();
response.readFrom(data);
return response;
}
}
