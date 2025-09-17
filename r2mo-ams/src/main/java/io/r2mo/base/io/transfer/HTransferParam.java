package io.r2mo.base.io.transfer;

import io.r2mo.base.io.HPath;

/**
 * @author lang : 2025-09-17
 */
public interface HTransferParam {
    /**
     * 是否忽略PWD，如果忽略PWD则表示 pathSource / pathTarget 中传入的是绝对路径，那么就不会调用 {@link HPath#ioPwd()} 进行计算，
     * 否则在计算路径时一定会使用 PWD 进行追加计算，主要目的是保证路径中存储的是相对路径而不是绝对路径
     */
    String $_IGNORE_PWD = "$_IGNORE_PWD";
    // ------------ 下边的内容全部存储于 metadata 中
    String META_HOME = "meta_home";          // 根目录
    String META_SCHEME = "meta_scheme";      // 协议
    String META_HOST = "meta_host";          // 主机
    String META_PORT = "meta_port";          // 端口
    String META_CONTEXT = "meta_context";    // 上下文
    String META_ACCOUNT = "meta_account";    // 账号

    interface TOKEN {
        String __ = "token";
        String SERVICE_PROVIDER = "serviceProvider";
        String SERVICE_CONSUMER = "serviceConsumer";
        String PATH_TARGET = "pathTarget";
        String PATH_SOURCE = "pathSource";
        String NODE_TYPE = "nodeType";
        String NODE_ID = "nodeId";
        String CLIENT_IP = "clientIp";
        String CLIENT_AGENT = "clientAgent";
    }

    interface REQUEST {
        String __ = "request";
        String MIME = "mime";
        String EXTENSION = "extension";
    }
}
