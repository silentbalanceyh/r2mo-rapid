package io.r2mo.dbe.mybatisplus.generator;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * SQL 脚本生成器，根据 Entity 定义生成脚本，主要脚本包含几个方面
 * <pre>
 *     1. SQL 数据库基础脚本
 *     2. Mapper 中的 interface 和 xml 文件
 * </pre>
 *
 * @author lang : 2025-07-25
 */
@Slf4j
public class SourceGenerator {

    private final GenProcessor processorSql = new GenProcessorSql();
    private final GenProcessor processorMapper = new GenProcessorMapper();
    private final GenProcessor processorXml = new GenProcessorMapperXml();
    private final GenProcessor processorService = new GenProcessorService();
    private final GenProcessor processorServiceImpl = new GenProcessorServiceImpl(); // 这里可以替换为具体的实现类
    private GenConfig genConfig;

    public SourceGenerator(final Class<? extends GenConfig> clazz) {
        try {
            this.genConfig = clazz.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            log.error(e.getMessage());
            this.genConfig = null;
        }
    }

    public void generate() {
        final List<Class<?>> entities = this.genConfig.getEntities();
        // Mapper 生成
        for (final Class<?> entity : entities) {
            // SQL
            this.processorSql.generate(entity, this.genConfig);
            // Mapper Interface
            this.processorMapper.generate(entity, this.genConfig);
            // Mapper XML
            this.processorXml.generate(entity, this.genConfig);
            // Service Interface
            this.processorService.generate(entity, this.genConfig);
            // Service Impl
            this.processorServiceImpl.generate(entity, this.genConfig);
        }
    }
}
