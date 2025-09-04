package io.r2mo.base.generator;

import java.nio.file.Path;
import java.util.List;

/**
 * 代码生成基本定义（代码生成的入口）
 *
 * @author lang : 2025-07-28
 */
public interface GenConfig {

    // 包名
    Package getBasePackage();

    // 实体信息
    List<Class<?>> getEntities();

    // --------------- 输出路径
    Path outProvider();

    Path outProviderXml();

    Path outSql();

    Path outApi();

    // --------------- 元信息
    GenMeta getMetadata();
}
