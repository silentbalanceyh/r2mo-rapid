package io.r2mo.io.enums;

/**
 * @author lang : 2025-09-15
 */
public enum TransferStatus {
    INIT,           // 初始化
    TRANSFERRING,   // 传输中
    PAUSED,         // 暂停
    COMPLETED,      // 完成
    FAILED,         // 失败
    CANCELED        // 取消
}
