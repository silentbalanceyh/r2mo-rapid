package ${packageName}.controller.gen.${classModule};

import ${entityPackage}.${entityName};
import ${sourcePackage}.typed.common.Pagination;
import ${sourcePackage}.typed.webflow.R;
import ${sourcePackage}.typed.json.JObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * ${className} Controller接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Tag(name = "${entityDisplay}", description = "${entityDisplay}相关API接口")
public interface ${className}CrudController {

    @PostMapping("")
    @Operation(summary = "创建${entityDisplay}", description = "管理端/创建${entityDisplay}")
    R<${className}CommonResponse> createSingle(@Valid @RequestBody ${className}CommonRequest request);


    @PutMapping("/{id}")
    @Operation(summary = "更新${entityDisplay}", description = "管理端/更新${entityDisplay}")
    R<${className}CommonResponse> updateSingle(@PathVariable String id,
        @Valid @RequestBody ${className}CommonRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "获取${entityDisplay}详情", description = "管理端/获取${entityDisplay}详情")
    R<${entityName}> findSingle(@PathVariable String id);

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${entityDisplay}", description = "管理端/删除${entityDisplay}")
    R<Boolean> removeSingle(@PathVariable String id);

    @PostMapping("/search")
    @Operation(summary = "分页查询${entityDisplay}", description = "管理端/分页查询${entityDisplay}")
    R<Pagination<${entityName}>> findPage(@RequestBody JObject query);

    @GetMapping("/all")
    @Operation(summary = "查询所有${entityDisplay}", description = "管理端/查询所有${entityDisplay}")
    R<List<${entityName}>> findAll();

    @PostMapping("/import")
    @Operation(summary = "导入${entityDisplay}", description = "管理端/导入${entityDisplay}")
    R<Boolean> uploadData(@RequestPart("file") MultipartFile file,
                          @RequestPart("config") JObject config);

    @PostMapping("/export")
    @Operation(summary = "导出${entityDisplay}", description = "管理端/导出${entityDisplay}")
    void downloadBy(@RequestBody JObject query);
}
