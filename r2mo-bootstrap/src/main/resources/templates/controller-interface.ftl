package ${packageName}.controller;

import ${packageName}.io.${className}CommonRequest;
import ${packageName}.io.${className}CommonResponse;
import ${sourcePackage}.typed.webflow.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * ${className} Controller接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Tag(name = "${entityDisplay}", description = "${entityDisplay}相关API接口")
public interface ${className}Controller {

}
