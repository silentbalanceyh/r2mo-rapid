package io.r2mo.base.io.locator;

import io.r2mo.base.io.HPath;

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
public class StorePath implements HPath {
    private final String path;
    private String home;

    public StorePath(final String path) {
        this.path = path;
    }

    @Override
    public String context() {
        return this.path;
    }

    public StorePath home(final String home) {
        this.home = home;
        return this;
    }

    @Override
    public String ioHome() {
        return this.home;
    }

    @Override
    public String ioPwd() {
        return T.resolve(this.home, this.path);
    }
}
