package io.r2mo.spring.junit5.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.SourceReflect;
import io.r2mo.dbe.mybatisplus.DBE;
import io.r2mo.io.common.HFS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * 主要和文件系统进行交互，作为JUnit5单元测试的基础类
 * <pre>
 *     1. 功能1：可直接加载 SQL 文件初始化数据库进行 DBE 测试
 *     2. 功能2：可直接加载 JSON 文件进行序列化反序列化测试
 *     3. 功能3：可直接访问文件系统进行 HFS 测试
 *     4. 功能4：可实现 Spring 级别的容器测试
 * </pre>
 *
 * @author lang : 2025-09-06
 */
@SpringBootTest
@Transactional
@Component
@Slf4j
public abstract class AppBaseTestSupport<T> {

    private final Class<T> entityCls;

    @Autowired
    private BaseMapper<T> mapper;

    @Autowired
    private ResourceLoader loader;

    @Autowired
    private DataSource dataSource;

    public AppBaseTestSupport() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    public void execute(final String filePath) {
        try {
            final Resource resource = this.loader.getResource(filePath);
            final String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            try (final Connection conn = this.dataSource.getConnection();
                 final Statement stmt = conn.createStatement()) {

                // 按分号分割多个SQL语句
                final String[] sqlStatements = sql.split(";");
                for (final String statement : sqlStatements) {
                    final String trimmedStatement = statement.trim();
                    if (!trimmedStatement.isEmpty()) {
                        stmt.execute(trimmedStatement);
                    }
                }
            }
        } catch (final IOException | SQLException e) {
            throw new RuntimeException("[ R2MO Test ] 执行SQL文件失败: " + filePath, e);
        }
    }

    protected DBE<T> db() {
        return DBE.of(this.entityCls, this.mapper);
    }

    protected void executeSQL(final String... files) {
        Arrays.stream(files).forEach(this::execute);
    }

    protected HFS fs() {
        return HFS.of();
    }

    protected Path root() {
        return Paths.get("");
    }
}
