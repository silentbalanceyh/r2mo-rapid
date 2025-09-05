package io.r2mo.spring.junit5;

import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

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
@Import(TestDatabase.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TestDatabaseSupport {

    @Autowired
    protected TestDatabase testDatabase;

    protected void executeSQL(final String... files) {
        this.testDatabase.executeSQL(files);
    }
}
