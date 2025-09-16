package io.r2mo.base.io.transfer;

/**
 * @author lang : 2025-09-15
 */
public enum TransferResult {
    SUCCESS,            // 操作成功
    SUCCESS_COMPLETED,  // 操作结束
    SUCCESS_CONTINUE,   // 继续操作
    FAILURE,            // 操作失败
    FAILURE_CANCEL,     // 操作取消
    FAILURE_ACTION,     // 操作异常，对应一些类似 Pause, Resume, Update 等操作
}
