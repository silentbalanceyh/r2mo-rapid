package io.r2mo.typed.constant;

import java.util.UUID;

/**
 * @author lang : 2025-09-03
 */
public interface DefaultConstantValue {

    String DEFAULT_META_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    String DEFAULT_EXP_UUID = "0e80b484-b62d-40b1-9ba5-5bb21d1f5034";

    String DEFAULT_EXP_TIME = "2025-10-01T12:00:00";

    String DEFAULT_EXP_BOOL = "true";

    String DEFAULT_EXP_JSON = "{\"key\":\"value\"}";

    String DEFAULT_EXP_CODE = "EGH46SYCGRUU934Y";

    String DEFAULT_EXP_JAVA = "com.xxxx.example.XxxxComponentClass";

    String DEFAULT_LANGUAGE = "zh-CN";

    String DEFAULT_VERSION = "1.0.0";

    // --------------- UUID 账号 ---------------

    // 默认系统账号
    UUID BY_SYSTEM = UUID.fromString("00000000-0000-0000-0000-000000000001");

    // 默认测试账号
    UUID BY_TESTER = UUID.fromString("00000000-0000-0000-0000-000000000002");

    // 默认管理账号
    UUID BY_ADMIN = UUID.fromString("00000000-0000-0000-0000-000000000003");

    // 默认用户账号
    UUID BY_USER = UUID.fromString("00000000-0000-0000-0000-000000000004");

    // 默认匿名账号
    UUID BY_ANONYMOUS = UUID.fromString("00000000-0000-0000-0000-000000000005");
}
