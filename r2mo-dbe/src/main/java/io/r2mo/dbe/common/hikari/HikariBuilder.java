package io.r2mo.dbe.common.hikari;

import com.zaxxer.hikari.HikariConfig;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-18
 */
public interface HikariBuilder {

    Cc<String, HikariBuilder> CC_BUILDER = Cc.openThread();

    static HikariBuilder of() {
        return CC_BUILDER.pick(HikariBuilderDefault::new, HikariBuilderDefault.class.getName());
    }

    void initialize(HikariConfig config, Database database);
}
