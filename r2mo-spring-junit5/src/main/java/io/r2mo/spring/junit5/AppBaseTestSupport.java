package io.r2mo.spring.junit5;

import com.github.yulichang.base.MPJBaseMapper;
import io.r2mo.SourceReflect;
import io.r2mo.dbe.mybatisplus.DBE;
import io.r2mo.dbe.mybatisplus.DBJ;
import io.r2mo.dbe.mybatisplus.Join;
import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
 */
@SpringBootTest
@Transactional
@Component
@Slf4j
public abstract class AppBaseTestSupport<T> extends AppIoTestSupport {

    private static boolean RUN_ONCE = true;
    private final Class<T> entityCls;
    @Autowired
    private MPJBaseMapper<T> mapper;
    @Autowired
    private ResourceLoader loader;
    @Autowired
    private DataSource dataSource;

    public AppBaseTestSupport() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    @SuppressWarnings("all")
    public void executeString(final String sql) {
        try {
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
        } catch (final SQLException e) {
            throw new RuntimeException("[ R2MO Test ] 执行SQL文件失败: " + sql, e);
        }
    }

    protected DBE<T> db() {
        return DBE.of(this.entityCls, this.mapper);
    }

    protected DBJ<T> db(final Join meta) {
        return DBJ.of(meta, this.mapper);
    }

    protected void executeFile(final String file) {
        try {
            // 1. 尝试从当前运行路径加载文件
            final Path path = Paths.get(file);
            final File currentPath = path.toFile();
            if (Files.exists(path)) {
                log.info("[ R2MOMO ] 正在从当前路径加载文件: {}", file);
                final String sql = StreamUtils.copyToString(new FileInputStream(currentPath), StandardCharsets.UTF_8);
                this.executeString(sql);
                return;
            }
            // 2. 如果当前路径找不到文件，则尝试从 ResourceLoader 加载
            log.info("[ R2MOMO ] 当前路径未找到文件, 尝试从资源路径加载: {}", file);
            final Resource resource = this.loader.getResource(file);
            if (resource.exists() && resource.isReadable()) {
                final String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                this.executeString(sql);
            } else {
                throw new IOException("[ R2MO ] 文件在当前路径和资源路径中均未找到: " + file);
            }
        } catch (final IOException e) {
            throw new RuntimeException("[ R2MO ] 执行SQL文件失败: " + file, e);
        }
    }

    protected void executeFiles(final String... files) {
        Arrays.stream(files).forEach(this::executeFile);
    }

    /**
     * 实例模式下的一次性运行行为
     *
     * @param actuator 执行函数
     */
    protected void runOnce(final Actuator actuator) {
        if (RUN_ONCE) {
            Fn.jvmAt(actuator);
            RUN_ONCE = false;
        }
    }
}
