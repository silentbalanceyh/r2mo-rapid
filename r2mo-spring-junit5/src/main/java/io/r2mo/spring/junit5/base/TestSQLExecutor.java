package io.r2mo.spring.junit5.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * @author lang : 2025-09-06
 */
@Service
public class TestSQLExecutor {

    @Autowired
    private ResourceLoader loader;

    @Autowired
    private DataSource dataSource;

    void execute(final String filePath) {
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

    public List<String> sqlStatements(final String filePath) {
        try {
            final Resource resource = this.loader.getResource(filePath);
            final String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return Arrays.asList(content.split(";"));
        } catch (final IOException e) {
            throw new RuntimeException("[ R2MO Test ] 读取SQL文件失败: " + filePath, e);
        }
    }
}
