package io.r2mo.dbcp.hikari;

import com.zaxxer.hikari.HikariConfig;
import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;

/**
 * MySQL 专用的 dataSourceProperties 构建器
 * - dsp（data-source-properties）保证非空：仅当某个键不存在时使用默认值
 * - 默认值与旧版保持一致；elideSetAutoCommits 仅在显式配置时下发
 * 字符集/时区可选（通常放到 JDBC URL）
 */
public class HikariBuilderMySQL implements HikariBuilder {

    @Override
    public void initialize(final HikariConfig config, final Database database) {
        // Hikari 扩展根：spring.datasource.hikari.*
        final JObject ext = database.getExtension(HikariName.HIKARI_SPID);
        // data-source-properties 子节点；此处约定永不为 null
        final JObject dsp = SPI.V_UTIL.valueJObject(ext, HikariName.HIKARI_EXTENSION);

        // ===== Default attributes（旧版默认）=====
        // 语句缓存
        this.add(config, dsp, HikariOpts.DSP_CACHE_PREP_STMTS, "true");
        this.add(config, dsp, HikariOpts.DSP_PREP_STMT_CACHE_SIZE, "2048");
        this.add(config, dsp, HikariOpts.DSP_PREP_STMT_CACHE_SQL_LIMIT, "4096");

        // Use Related
        this.add(config, dsp, HikariOpts.DSP_USE_SERVER_PREP_STMTS, "true");
        this.add(config, dsp, HikariOpts.DSP_USE_LOCAL_SESSION_STATE, "true");
        this.add(config, dsp, HikariOpts.DSP_USE_LOCAL_TX_STATE, "true");
        this.add(config, dsp, HikariOpts.DSP_USE_COMPRESSION, "true");

        // Advanced Configuration
        this.add(config, dsp, HikariOpts.DSP_REWRITE_BATCHED_STMTS, "true");
        this.add(config, dsp, HikariOpts.DSP_CACHE_RS_METADATA, "true");
        this.add(config, dsp, HikariOpts.DSP_CACHE_SERVER_CONFIG, "true");

        // 仅在显式配置时下发（旧版注释态）
        if (dsp.containsKey(HikariOpts.DSP_ELIDE_SET_AUTOCOMMITS)) {
            config.addDataSourceProperty(HikariOpts.DSP_ELIDE_SET_AUTOCOMMITS,
                dsp.getString(HikariOpts.DSP_ELIDE_SET_AUTOCOMMITS));
        }

        // maintainTimeStats（旧版默认 false）
        this.add(config, dsp, "maintainTimeStats", "false");

        // 可选：字符集/时区（通常放到 JDBC URL）
        // add(config, dsp, HikariOpts.DSP_USE_UNICODE,            "true");
        // add(config, dsp, HikariOpts.DSP_CHARACTER_ENCODING,     "utf8");
        // add(config, dsp, HikariOpts.DSP_SERVER_TIMEZONE,        "UTC");
    }

    /** 如果 dsp 中存在 key 用其值，否则用默认值 def 下发到 dataSourceProperties */
    private void add(final HikariConfig cfg, final JObject dsp, final String key, final String def) {
        final String val = dsp.containsKey(key) ? dsp.getString(key, def) : def;
        cfg.addDataSourceProperty(key, val);
    }
}
