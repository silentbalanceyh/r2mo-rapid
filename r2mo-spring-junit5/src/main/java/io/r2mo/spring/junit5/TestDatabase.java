package io.r2mo.spring.junit5;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * 数据库初始化
 * <pre>
 *     1. 加载 SQL 脚本初始化
 * </pre>
 *
 * @author lang : 2025-09-06
 */
@Component
class TestDatabase {

    private final SqlSessionFactory sqlSessionFactory;

    TestDatabase(final SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @SuppressWarnings("all")
    void executeSQL(final String... files) {
        if (files == null || files.length == 0) {
            return;
        }

        try (final SqlSession session = this.sqlSessionFactory.openSession()) {
            final ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setAutoCommit(true);
            runner.setStopOnError(true);
            runner.setSendFullScript(false);
            runner.setEscapeProcessing(false);
            // 关闭默认日志输出（避免 UT 控制台刷屏）
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);

            for (final String file : files) {
                final ClassPathResource resource = new ClassPathResource(file);
                try (final Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    runner.runScript(reader);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
