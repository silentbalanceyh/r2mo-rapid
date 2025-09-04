package ${packageName}.io;

import ${entityPackage}.${entityName};
import ${sourcePackage}.spring.common.webflow.PreRequest;
import ${sourcePackage}.typed.webflow.WebRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * ${className} 请求
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ${className}CommonRequest extends PreRequest implements WebRequest<${entityName}> {
    ${fieldsRequest}
}
