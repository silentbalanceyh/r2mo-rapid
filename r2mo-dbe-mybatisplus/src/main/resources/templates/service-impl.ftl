package ${packageName}.service.gen.${classModule};

import ${entityPackage}.${entityName};
import ${packageName}.mapper.${className}Mapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class I${className}ServiceImpl extends ServiceImpl<${className}Mapper, ${entityName}> implements I${className}Service {

}