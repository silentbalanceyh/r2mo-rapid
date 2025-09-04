package ${packageName}.controller.${v};

import ${packageName}.business.${v}.${className}Service${V};
import ${packageName}.controller.${className}Controller;
import ${entityPackage}.${entityName};
import ${packageName}.io.${className}CommonRequest;
import ${packageName}.io.${className}CommonResponse;
import ${sourcePackage}.spring.common.webflow.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ${className} Controller接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/${v}")
public class ${className}Controller${V} extends BaseController<${entityName}, ${className}CommonRequest, ${className}CommonResponse> implements ${className}Controller {

    @Autowired
    private ${className}Service${V} service;

    @Override
    protected ${className}Service${V} service() {
        return this.service;
    }

    @Override
    protected ${className}CommonResponse createResponse() {
        return new ${className}CommonResponse();
    }

    @Override
    protected ${className}CommonRequest createRequest() {
        return new ${className}CommonRequest();
    }
}