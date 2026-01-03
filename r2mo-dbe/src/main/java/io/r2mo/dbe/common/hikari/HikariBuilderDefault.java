package io.r2mo.dbe.common.hikari;

import com.zaxxer.hikari.HikariConfig;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.enums.DatabaseType;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.util.Objects;

/**
 * 默认 Hikari 配置构建器
 * 修复：移除硬编码的 2048 连接数，改为根据 CPU 核心数动态计算
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
            config.setDriverClassName(driverCls);
        }

        /* ===== Hikari 扩展，不存在就直接返回 ===== */
        final JObject ext = database.getExtension(HikariName.HIKARI_SPID);
        if (ext == null) {
            // 即便没有扩展配置，也建议设置一个合理的默认 PoolSize，防止 Hikari 使用其内部默认值（通常是 10）
            // 但为了安全起见，这里也可以应用动态计算逻辑
            config.setMaximumPoolSize(this.calculateReasonablePoolSize());
            return;
        }

        // —— 行为与规模 ——
        config.setAutoCommit(ext.getBool(HikariOpts.OPT_AUTO_COMMIT, true));
        config.setConnectionTimeout(ext.getLong(HikariOpts.OPT_CONNECTION_TIMEOUT, 300_000L));
        config.setMaxLifetime(ext.getLong(HikariOpts.OPT_MAX_LIFETIME, 120_000L));
        config.setKeepaliveTime(ext.getLong(HikariOpts.OPT_KEEPALIVE_TIME, 90_000L));

        // [关键修改]：动态计算默认最大连接数
        // 如果配置中有值则用配置的，没有则使用 (核心数 * 2 + 1)
        final int defaultPoolSize = this.calculateReasonablePoolSize();

        config.setMaximumPoolSize(ext.containsKey(HikariOpts.OPT_MAXIMUM_POOL_SIZE)
            ? ext.getInt(HikariOpts.OPT_MAXIMUM_POOL_SIZE) : defaultPoolSize);

        // 关于 minimumIdle：
        // 如果未配置，Hikari 默认将其设为与 maximumPoolSize 相等（即固定大小连接池，这是推荐的高性能模式）。
        // 现在 defaultPoolSize 只有几十个，固定大小完全没问题，不会撑爆数据库。
        if (ext.containsKey(HikariOpts.OPT_MINIMUM_IDLE)) {
            config.setMinimumIdle(ext.getInt(HikariOpts.OPT_MINIMUM_IDLE));
        }

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
            this.ofBuilder(database.getType()).initialize(config, database);
        }
    }

    private HikariBuilder ofBuilder(final DatabaseType databaseType) {
        if (DatabaseType.isMySQL(databaseType)) {
            return HikariBuilder.CC_BUILDER.pick(HikariBuilderMySQL::new, HikariBuilderMySQL.class.getName());
        }
        throw new _501NotSupportException("[ R2MO ] 等待未来支持：" + databaseType);
    }

    /**
     * 根据 CPU 核心数计算合理的连接池大小
     * 公式：Core * 2 + 1
     * 例如：4核机器 -> 9 个连接；8核机器 -> 17 个连接
     */
    private int calculateReasonablePoolSize() {
        final int cores = Runtime.getRuntime().availableProcessors();
        final int calculated = cores * 2 + 1;
        // 兜底策略：防止单核机器计算出太小的值，或者防止某些巨型服务器计算出过大的值
        // 这里的 32 只是一个保守的建议值，通常单实例很少需要超过 50 个连接
        return Math.min(calculated, 32);
    }
}