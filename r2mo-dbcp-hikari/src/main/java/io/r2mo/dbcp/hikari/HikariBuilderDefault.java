package io.r2mo.dbcp.hikari;

import com.zaxxer.hikari.HikariConfig;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.enums.DatabaseType;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.util.Objects;

/**
 * 默认 Hikari 配置构建器（去掉静态初始化/静态导入）
 * - 默认值保持与旧版一致
 * - ext 为空直接返回，不额外覆盖 Hikari 默认
 */
class HikariBuilderDefault implements HikariBuilder {

    @Override
    public void initialize(final HikariConfig config, final Database database) {
        Objects.requireNonNull(config, "[R2MO] HikariConfig 不能为空");
        Objects.requireNonNull(database, "[R2MO] Database 不能为空");

        /* ===== JDBC 基础配置 ===== */
        config.setJdbcUrl(database.getUrl());
        config.setUsername(database.getUsername());
        config.setPassword(database.getPasswordDecrypted());
        final String driverCls = database.getDriverClassName();
        if (driverCls != null && !driverCls.isBlank()) {
            config.setDriverClassName(driverCls); // 避免 "no suitable driver"
        }

        /* ===== Hikari 扩展，不存在就直接返回 ===== */
        final JObject ext = database.getExtension(HikariName.HIKARI_SPID);
        if (ext == null) {
            return;
        }

        // —— 行为与规模（旧版默认值保持不变）——
        config.setAutoCommit(ext.getBool(HikariOpts.OPT_AUTO_COMMIT, true));
        config.setConnectionTimeout(ext.getLong(HikariOpts.OPT_CONNECTION_TIMEOUT, 300_000L));
        // idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.
        // config.setIdleTimeout(ext.getLong(HikariOpts.OPT_IDLE_TIMEOUT, 60_000L));
        config.setMaxLifetime(ext.getLong(HikariOpts.OPT_MAX_LIFETIME, 120_000L));
        config.setKeepaliveTime(ext.getLong(HikariOpts.OPT_KEEPALIVE_TIME, 90_000L));

        if (ext.containsKey(HikariOpts.OPT_MINIMUM_IDLE)) {
            config.setMinimumIdle(ext.getInt(HikariOpts.OPT_MINIMUM_IDLE));
        }
        config.setMaximumPoolSize(ext.containsKey(HikariOpts.OPT_MAXIMUM_POOL_SIZE)
            ? ext.getInt(HikariOpts.OPT_MAXIMUM_POOL_SIZE) : 2048);

        config.setPoolName(ext.containsKey(HikariOpts.OPT_POOL_NAME)
            ? ext.getString(HikariOpts.OPT_POOL_NAME) : HikariName.HIKARI_POOL_NAME);

        // —— 只读与隔离级别（可选）——
        if (ext.containsKey(HikariOpts.OPT_READ_ONLY)) {
            config.setReadOnly(ext.getBool(HikariOpts.OPT_READ_ONLY, false));
        }
        if (ext.containsKey(HikariOpts.OPT_TRANSACTION_ISOLATION)) {
            config.setTransactionIsolation(ext.getString(HikariOpts.OPT_TRANSACTION_ISOLATION));
        }

        // —— 校验与初始化（可选）——
        if (ext.containsKey(HikariOpts.OPT_VALIDATION_TIMEOUT)) {
            config.setValidationTimeout(ext.getLong(HikariOpts.OPT_VALIDATION_TIMEOUT));
        }
        if (ext.containsKey(HikariOpts.OPT_CONNECTION_TEST_QUERY)) {
            config.setConnectionTestQuery(ext.getString(HikariOpts.OPT_CONNECTION_TEST_QUERY));
        }
        if (ext.containsKey(HikariOpts.OPT_CONNECTION_INIT_SQL)) {
            config.setConnectionInitSql(ext.getString(HikariOpts.OPT_CONNECTION_INIT_SQL));
        }

        // —— 启动/泄露检测（可选）——
        if (ext.containsKey(HikariOpts.OPT_INITIALIZATION_FAIL_TM)) {
            config.setInitializationFailTimeout(ext.getLong(HikariOpts.OPT_INITIALIZATION_FAIL_TM));
        }
        if (ext.containsKey(HikariOpts.OPT_LEAK_DETECTION_THRESHOLD)) {
            config.setLeakDetectionThreshold(ext.getLong(HikariOpts.OPT_LEAK_DETECTION_THRESHOLD));
        }

        // —— 架构、目录（可选）——
        if (ext.containsKey(HikariOpts.OPT_SCHEMA)) {
            config.setSchema(ext.getString(HikariOpts.OPT_SCHEMA));
        }
        if (ext.containsKey(HikariOpts.OPT_CATALOG)) {
            config.setCatalog(ext.getString(HikariOpts.OPT_CATALOG));
        }

        // —— 其他（可选）——
        if (ext.containsKey(HikariOpts.OPT_ALLOW_POOL_SUSPENSION)) {
            config.setAllowPoolSuspension(ext.getBool(HikariOpts.OPT_ALLOW_POOL_SUSPENSION, false));
        }
        if (ext.containsKey(HikariOpts.OPT_REGISTER_MBEANS)) {
            config.setRegisterMbeans(ext.getBool(HikariOpts.OPT_REGISTER_MBEANS, false));
        }

        // 扩展，直接从 data-source-properties 中提取信息
        if (ext.containsKey(HikariName.HIKARI_EXTENSION)) {
            // 分数据库提取
            this.ofBuilder(database.getType()).initialize(config, database);
        }
    }

    private HikariBuilder ofBuilder(final DatabaseType databaseType) {
        if (DatabaseType.isMySQL(databaseType)) {
            return HikariBuilder.CC_BUILDER.pick(HikariBuilderMySQL::new, HikariBuilderMySQL.class.getName());
        }
        throw new _501NotSupportException("[ R2MO ] 等待未来支持：" + databaseType);
    }
}
