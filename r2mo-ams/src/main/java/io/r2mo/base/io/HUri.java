package io.r2mo.base.io;

import io.r2mo.base.io.enums.UriScheme;

/**
 * @author lang : 2025-09-15
 */
public interface HUri {

    /* 协议，默认是本地文件协议 */
    default UriScheme scheme() {
        return UriScheme.FILE;
    }

    /* 主机，默认空 */
    default String host() {
        return null;
    }

    /* 端口 */
    default int port() {
        return -1;
    }

    /* 账号信息 */
    default String account() {
        return null;
    }

    /* 上下文（通常是根目录）*/
    String context();
}
