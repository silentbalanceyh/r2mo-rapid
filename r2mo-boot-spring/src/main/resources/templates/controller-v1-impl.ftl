package ${packageName}.controller.gen.${classModule};

import ${packageName}.service.gen.${classModule}.I${className}Service${V};
import ${entityPackage}.${entityName};
import ${sourcePackage}.spring.common.webflow.BaseController;
import ${sourcePackage}.typed.common.Pagination;
import ${sourcePackage}.typed.json.JObject;
import ${sourcePackage}.typed.webflow.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ${className} Controller接口
 *
 * @author ${author}
 * @since ${date}
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/${v}/${actor}")
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

    // POST /${v}/${actor}
    @Override
    public R<${className}CommonResponse> createSingle(final ${className}CommonRequest request) {
        return super.createSingle(request);
    }

    // PUT /${v}/${actor}/{id}
    @Override
    public R
    <${className}CommonResponse> updateSingle(final String id, final ${className}CommonRequest request) {
        return super.updateSingle(id, request);
    }

    // GET /${v}/${actor}/{id}
    @Override
    public R<${entityName}> findSingle(final String id) {
        return super.findSingle(id);
    }

    // DELETE /${v}/${actor}/{id}
    @Override
    public R<Boolean> removeSingle(final String id) {
        return super.removeSingle(id);
    }

    // POST /${v}/${actor}/search
    @Override
    public R<Pagination<${entityName}>> findPage(final JObject query) {
        return super.findPage(query);
    }

    // GET /${v}/${actor}/all
    @Override
    public R<List<${entityName}>> findAll() {
        return super.findAll();
    }

    // POST /${v}/${actor}/import
    @Override
    public R<Boolean> uploadData(final MultipartFile file, final JObject config) {
        return super.uploadData(file, config);
    }

    // POST /${v}/${actor}/export
    @Override
    public void downloadBy(final JObject query) {
        super.downloadBy(query);
    }
}