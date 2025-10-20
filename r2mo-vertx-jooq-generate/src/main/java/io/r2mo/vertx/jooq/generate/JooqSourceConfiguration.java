package io.r2mo.vertx.jooq.generate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.base.dbe.Database;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jooq.codegen.GenerationTool;

/**
 * 配置对接，根据自身配置来处理，连接到 {@link GenerationTool} 实现代码生成的核心逻辑
 * 1. 数据库的访问不采用静态配置，直接采用动态方式，保证生成代码不仅仅访问到原生的 DataBase
 * 2. 生成代码时的调用包名要根据当前环境做调整
 * 3. 生成代码时的类名根据配置类所在的包名做调整
 *
 * @author lang : 2025-10-20
 */
@Data
@Accessors(fluent = true, chain = true)
public class JooqSourceConfiguration {

    // 数据库连接基本配置
    /*
     * 对应 Jooq 配置
        <jdbc>
            <driver>com.mysql.cj.jdbc.Driver</driver>
            <url>
                <![CDATA[ jdbc:mysql://ox.engine.cn:3306/ZDB?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true ]]>
            </url>
            <username>${jooq.codegen.jdbc.username}</username>
            <password>${jooq.codegen.jdbc.password}</password>
        </jdbc>
       不仅仅如此，有了此处的数据库连接配置之后
        <database>
            <name>org.jooq.meta.mysql.MySQLDatabase</name>
            <inputSchema>ZDB</inputSchema>
        </database>
       这个动作也限定了
     */
    @JsonIgnore
    private Database database = new Database();
    /*
     * 对应 Jooq 配置
        <strategy>
            <name>io.r2mo.vertx.jooq.generate.VertxGeneratorStrategy
            </name>
        </strategy>
     */
    private Class<?> classStrategy;

    /*
     * 对应 Jooq 配置
        <generator>
            <name>io.r2mo.vertx.jooq.generate.classic.ClassicJDBCVertxGenerator</name>
        </generator>
     */
    private Class<?> classGenerator;


    /*
     * 对应 Jooq 配置
     * <includes>(^(E_|R_|T_OA_|T_VENDOR_).*)</includes>
     */
    private String databaseIncludes;


    /*
     * 对应 Jooq 配置
     * <packageName>io.zerows.extension.commerce.erp.domain</packageName>
     */
    private Package classPackage;
    // 对应到 source 的配置项
    private String directory;
}
