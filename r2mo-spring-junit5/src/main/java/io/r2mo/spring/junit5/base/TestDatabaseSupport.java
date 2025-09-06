package io.r2mo.spring.junit5.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.SourceReflect;
import io.r2mo.dbe.mybatisplus.DBE;
import io.r2mo.io.common.HFS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.transaction.annotation.Transactional;

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
public abstract class TestDatabaseSupport<T> {

    private final Class<T> entityCls;

    @Autowired
    protected TestSQLExecutor executor;

    @Autowired
    private BaseMapper<T> mapper;

    public TestDatabaseSupport() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    protected DBE<T> db() {
        return DBE.of(this.entityCls, this.mapper);
    }

    protected void executeSQL(final String... files) {
        Arrays.stream(files).forEach(this.executor::execute);
    }

    protected HFS fs() {
        return HFS.of();
    }

    @BeforeTestMethod
    protected void outputEnvironment() {
        final DBE<T> db = this.db();
        log.info("[ R2MO ] 数据库引擎：{}", db);
        final HFS fs = this.fs();
        log.info("[ R2MO ] 文件系统：{}", fs);
        log.info("[ R2MOMO ] 数据库Mapper: {}", this.mapper);
        log.info("[ R2MOMO ] 实体类Entity: {}", this.entityCls);
    }
}
