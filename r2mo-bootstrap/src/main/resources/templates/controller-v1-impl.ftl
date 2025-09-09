package ${packageName}.controller.gen.${classModule};

import ${packageName}.service.gen.${classModule}.I${className}Service${V};
import ${entityPackage}.${entityName};
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
public class ${className}CrudController${V} extends BaseController<${entityName}, ${className}CommonRequest, ${className}CommonResponse> implements ${className}CrudController {

    @Autowired
    private I${className}Service${V} service;

    @Override
    protected I${className}Service${V} service() {
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