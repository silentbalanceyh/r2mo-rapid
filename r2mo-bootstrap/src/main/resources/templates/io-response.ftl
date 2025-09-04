package ${packageName}.io;

import ${entityPackage}.${entityName};
import ${sourcePackage}.spring.common.webflow.PostResponse;
import ${sourcePackage}.typed.webflow.WebResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
