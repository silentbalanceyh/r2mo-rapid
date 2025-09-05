package ${packageName}.business.${v};

import ${baseAct};
import ${entityPackage}.${entityName};
import ${packageName}.mapper.${className}Mapper;
import ${packageName}.service.${className}Service;
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
public class ${className}Service${V}Impl extends ${baseActName}<${entityName}> implements ${className}Service${V} {

@Autowired
private ${className}Service service;


@Override
@SuppressWarnings("all")
protected ${className}Mapper executor() {
return (${className}Mapper) this.service.getBaseMapper();
}
}