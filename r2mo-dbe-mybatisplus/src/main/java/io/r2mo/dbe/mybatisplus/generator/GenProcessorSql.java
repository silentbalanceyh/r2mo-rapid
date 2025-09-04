package io.r2mo.dbe.mybatisplus.generator;

import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenMeta;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.typed.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-08-29
 */
@Slf4j
class GenProcessorSql implements GenProcessor {

    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        final GenMeta meta = config.getMetadata();
        final DatabaseType databaseType = meta.getDatabase();
        if (DatabaseType.isMySQL(databaseType)) {
            final GenProcessor processor = new GenProcessorSqlMySQL();
            processor.generate(entity, config);
        }
    }
}
