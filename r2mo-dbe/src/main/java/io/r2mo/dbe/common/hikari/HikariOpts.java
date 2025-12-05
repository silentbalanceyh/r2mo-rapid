package io.r2mo.dbe.common.hikari;

/**
 * Hikari 配置项常量（OPT_ 前缀）
 * - 与 Spring application.yml 的 spring.datasource.hikari.* 命名对齐（去掉前缀，只保留键名）
 * - MySQL 驱动 dataSourceProperties.* 用 OPT_DSP_ 前缀
 * - 附带旧版（legacy）键，便于兼容/迁移
 */
final class HikariOpts {
    private HikariOpts() {
    }

    /* ===== Hikari 常用项（spring.datasource.hikari.*）===== */
    static final String OPT_POOL_NAME = "pool-name";
    static final String OPT_MAXIMUM_POOL_SIZE = "maximum-pool-size";
    static final String OPT_MINIMUM_IDLE = "minimum-idle";
    static final String OPT_CONNECTION_TIMEOUT = "connection-timeout";      // ms
    static final String OPT_VALIDATION_TIMEOUT = "validation-timeout";      // ms
    static final String OPT_IDLE_TIMEOUT = "idle-timeout";            // ms
    static final String OPT_MAX_LIFETIME = "max-lifetime";            // ms
    static final String OPT_KEEPALIVE_TIME = "keepalive-time";          // ms
    static final String OPT_LEAK_DETECTION_THRESHOLD = "leak-detection-threshold";// ms
    static final String OPT_INITIALIZATION_FAIL_TM = "initialization-fail-timeout";
    static final String OPT_AUTO_COMMIT = "auto-commit";
    static final String OPT_READ_ONLY = "read-only";
    static final String OPT_TRANSACTION_ISOLATION = "transaction-isolation";
    static final String OPT_SCHEMA = "schema";
    static final String OPT_CATALOG = "catalog";
    static final String OPT_CONNECTION_TEST_QUERY = "connection-test-query";
    static final String OPT_CONNECTION_INIT_SQL = "connection-init-sql";
    static final String OPT_ALLOW_POOL_SUSPENSION = "allow-pool-suspension";
    static final String OPT_REGISTER_MBEANS = "register-mbeans";

    // ===== MySQL Driver dataSourceProperties.*（不带前缀）=====
    static final String DSP_CACHE_PREP_STMTS = "cachePrepStmts";
    static final String DSP_PREP_STMT_CACHE_SIZE = "prepStmtCacheSize";
    static final String DSP_PREP_STMT_CACHE_SQL_LIMIT = "prepStmtCacheSqlLimit";
    static final String DSP_USE_SERVER_PREP_STMTS = "useServerPrepStmts";
    static final String DSP_REWRITE_BATCHED_STMTS = "rewriteBatchedStatements";
    static final String DSP_CACHE_RS_METADATA = "cacheResultSetMetadata";
    static final String DSP_CACHE_SERVER_CONFIG = "cacheServerConfiguration";
    static final String DSP_ELIDE_SET_AUTOCOMMITS = "elideSetAutoCommits";
    static final String DSP_USE_LOCAL_SESSION_STATE = "useLocalSessionState";
    static final String DSP_USE_LOCAL_TX_STATE = "useLocalTransactionState";
    static final String DSP_USE_COMPRESSION = "useCompression";
    static final String DSP_USE_UNICODE = "useUnicode";
    static final String DSP_CHARACTER_ENCODING = "characterEncoding";
    static final String DSP_SERVER_TIMEZONE = "serverTimezone";
}
