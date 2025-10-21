package io.r2mo.base.dbe;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.secure.EDCrypto;
import io.r2mo.spi.SPI;
import io.r2mo.typed.enums.DatabaseType;
import io.r2mo.typed.json.JElement;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.r2mo.typed.json.jackson.JObjectDeserializer;
import io.r2mo.typed.json.jackson.JObjectSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.r2mo.base.dbe.DBS.CPFactory.NAMES;

/**
 * @author lang : 2025-10-18
 */
@Data
@Slf4j
public class Database implements Serializable, JElement {
    public static final String CATEGORY = "category";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    public static final String INSTANCE = "instance";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DRIVER_CLASS_NAME = "driver-class-name";
    public static final String OPTIONS = "options";

    private static final JUtil UT = SPI.V_UTIL;
    // -------------- 实例选项 -----------------
    private String hostname;
    private String instance;
    private Integer port = 3306;
    private DatabaseType type = DatabaseType.MYSQL_8;
    private String url;
    private String username;
    private String password;
    @JsonProperty(DRIVER_CLASS_NAME)
    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    @JsonIgnore
    private EDCrypto crypto;

    @JsonIgnore
    @Accessors(fluent = true, chain = true)
    private String name;


    @JsonSerialize(using = JObjectSerializer.class)
    @JsonDeserialize(using = JObjectDeserializer.class)
    private JObject options = SPI.J();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> extension = new HashMap<>();

    @JsonAnySetter            // 反序列化时：任何未匹配到属性的键都会进这里
    public void putExtension(final String key, final Object value) {
        if (value instanceof final JObject valueJ) {
            this.extension.put(key, valueJ);
        } else {
            this.extension.put(key, SPI.J(value));
        }
    }

    @JsonAnyGetter            // 序列化时：把 otherProps 的键值“摊平”到顶层 JSON
    public Map<String, Object> getExtension() {
        return this.extension;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(final String extensionKey) {
        return (T) this.extension.get(extensionKey);
    }

    @Override
    public JObject toJObject() {
        return UT.serializeJson(this);
    }

    @Override
    @SuppressWarnings("all")
    public Database fromJObject(final JObject json) {
        if (UT.isEmpty(json)) {
            log.warn("[ R2MO ] 传入 null, 无法加载配置！");
            return this;
        }
        this.hostname = json.getString(HOSTNAME);
        this.instance = json.getString(INSTANCE);
        this.port = json.getInt(PORT);
        this.url = json.getString(URL);
        this.username = json.getString(USERNAME);
        this.password = json.getString(PASSWORD);
        this.driverClassName = json.getString(DRIVER_CLASS_NAME);

        final String category = json.getString(CATEGORY);
        this.type = StrUtil.isEmpty(category) ? DatabaseType.MYSQL_8 : DatabaseType.valueOf(category);
        final JObject options = UT.valueJObject(json, OPTIONS);
        if (UT.isNotEmpty(options)) {
            this.options = options;
            log.info("[ R2MO ] 数据库配置项：{}", options.encode());
        }
        return this;
    }

    // -------------- 除开 Get / Set 的特殊方法 -----------------

    public String getPasswordDecrypted() {
        return EDCrypto.decryptPassword(this.password);
    }

    public <T> T getOption(final String optionKey) {
        return this.getOption(optionKey, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOption(final String optionKey, final T defaultValue) {
        final Object value = this.options.get(optionKey);
        return Objects.isNull(value) ? defaultValue : (T) value;
    }

    public long getLong(final String optionKey, final Long defaultValue) {
        final long value = this.options.getLong(optionKey);
        return -1 == value ? defaultValue : value;
    }

    public long getLong(final String optionKey) {
        return this.getLong(optionKey, -1L);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final Database database = (Database) object;
        return Objects.equals(this.url, database.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.url);
    }

    // -------------- 静态检查方法 -----------------

    /**
     * 目前版本所有连接池只允许 One Of 选择一个，若有多个可以考虑 user-cp 的方式实现多个连接池的功能，比如筛选和聚合
     * <pre>
     *     1. user-cp -> 自定义连接池 -> 动态选择
     *     2. hikari  -> HikariCP 连接池
     *     3. tomcat  -> Tomcat JDBC 连接池
     *     4. dbcp2   -> Apache DBCP2 连接池
     * </pre>
     *
     * @return 连接池名称
     */
    public String findNameOfDBCP() {
        // 根据扩展看是否启用了连接池
        return this.extension.keySet().stream()
            .filter(k -> k != null && NAMES.contains(k.toLowerCase(java.util.Locale.ROOT)))
            .reduce((a, b) -> {
                throw new IllegalStateException("[R2MO] 检测到多个连接池配置: " + a + ", " + b);
            })
            .orElse(null);
    }

    public static Database createDatabase(final JObject databaseJ) {
        if (UT.isEmpty(databaseJ)) {
            return null;
        }
        final Database database = new Database();
        database.fromJObject(databaseJ);
        return database;
    }

    /**
     * 数据库连接测试
     */
    public static boolean isConnected(final Database database) {
        try {
            DriverManager.getConnection(
                database.getUrl(),
                database.getUsername(),
                database.getPasswordDecrypted()
            );
            return true;
        } catch (final SQLException ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean isConnected() {
        return isConnected(this);
    }
}
