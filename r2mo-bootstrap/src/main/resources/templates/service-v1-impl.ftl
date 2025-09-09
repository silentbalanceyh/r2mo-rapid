package ${packageName}.service.gen.${classModule};

import ${baseAct};
import ${entityPackage}.${entityName};
import ${packageName}.mapper.${className}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${className} 服务实现
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class I${className}Service${V}Impl extends ${baseActName}<${entityName}> implements I${className}Service${V} {

    @Autowired
    private I${className}Service service;


    @Override
    @SuppressWarnings("all")
    protected ${className}Mapper executor() {
        return (${className}Mapper) this.service.getBaseMapper();
    }
}