package io.r2mo.base.io;

/**
 * 存储操作专用接口，操作主要分两部分
 * <pre>
 *     1. 目录操作
 *     2. 文件操作
 * </pre>
 * 根据不同实现会分为本地模式、网络模式，其中网络模式会包含 FTP、SFTP、HDFS 等等，可实现分布式存储管理
 *
 * @author lang : 2025-08-28
 */
public interface HStorage {

    HDirectory ofDirectory();

    HFile ofFile();
}
