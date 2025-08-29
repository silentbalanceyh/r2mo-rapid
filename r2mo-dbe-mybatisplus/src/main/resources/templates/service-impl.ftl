package ${packageName}.service.impl;

import ${entityPackage}.${entityName};
import ${packageName}.mapper.${className}Mapper;
import ${packageName}.service.${className}Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ${className}ServiceImpl extends ServiceImpl<${className}Mapper, ${entityName}> implements ${className}Service {

}