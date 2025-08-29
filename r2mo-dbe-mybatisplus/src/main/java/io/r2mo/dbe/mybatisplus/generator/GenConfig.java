package io.r2mo.dbe.mybatisplus.generator;

import java.nio.file.Path;
import java.util.List;

/**
 * 代码生成基本定义（代码生成的入口）
 *
 * @author lang : 2025-07-28
 */
public interface GenConfig {

    Package getBasePackage();

    List<Class<?>> getEntities();

    Path outProvider();

    Path outProviderXml();

    Path outSql();

    Path outApi();
}
