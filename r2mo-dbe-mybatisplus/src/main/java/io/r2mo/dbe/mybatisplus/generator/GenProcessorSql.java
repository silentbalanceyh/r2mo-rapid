package io.r2mo.dbe.mybatisplus.generator;

import io.r2mo.dbe.common.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-08-29
 */
@Slf4j
class GenProcessorSql implements GenProcessor {

    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        final DatabaseType databaseType = config.metaDatabaseType();
        if (DatabaseType.isMySQL(databaseType)) {
            final GenProcessor processor = new GenProcessorSqlMySQL();
            processor.generate(entity, config);
        }
    }
}
