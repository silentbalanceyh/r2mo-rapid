package io.r2mo.dbe.mybatisplus.generator;

import io.r2mo.base.generator.AbstractGenProcessor;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenField;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.dbe.mybatisplus.generator.field.GenFieldMybatisPlus;
import io.r2mo.typed.annotation.SPID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-09-04
 */
@Slf4j
@SPID("GenMybatisPlus")
public class GenProcessorMybatisPlus extends AbstractGenProcessor {
    private final GenProcessor processorSql = new GenProcessorSql();
    private final GenProcessor processorMapper = new GenProcessorMapper();
    private final GenProcessor processorXml = new GenProcessorMapperXml();
    private final GenProcessor processorService = new GenProcessorService();
    private final GenProcessor processorServiceImpl = new GenProcessorServiceImpl(); // 这里可以替换为具体的实现类

    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        // SQL
        this.processorSql.generate(entity, config);
        // Mapper Interface
        this.processorMapper.generate(entity, config);
        // Mapper XML
        this.processorXml.generate(entity, config);
        // Service Interface
        this.processorService.generate(entity, config);
        // Service Impl
        this.processorServiceImpl.generate(entity, config);
    }

    @Override
    public GenField getFieldProcessor() {
        return new GenFieldMybatisPlus();
    }
}
