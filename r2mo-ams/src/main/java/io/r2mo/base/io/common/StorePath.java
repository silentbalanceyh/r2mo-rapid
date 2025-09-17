package io.r2mo.base.io.common;

import io.r2mo.base.io.HPath;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存储路径相关信息，如果包含了 home，即包含了当前目录相关信息，针对当前目录信息进行绝对路径计算
 * <pre>
 *     - context(): 返回当前路径
 *       - / 开始：自动绝对路径
 *       - 非 / 开始：相对路径
 *     - ioHome(): 包含了运行的 HOME 主目录
 *     - ioPwd(): 返回当前目录的绝对路径
 * </pre>
 *
 * @author lang : 2025-09-16
 */
@Data
@Accessors(fluent = true)
public class StorePath implements HPath {
    private String context;
    private String home;

    @Override
    public String ioHome() {
        return this.home;
    }

    @Override
    public String ioPwd() {
        return UT.resolve(this.home, this.context);
    }
}
