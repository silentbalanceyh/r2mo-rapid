package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;

/**
 * 提供 License 对应的路径，根据路径处理后续相关事宜，最终调用流程
 * <pre>
 *     1. 直接让实现层提供 {@link LicenseMgr} 元数据相关接口
 *     2. 此对象可以直接返回 {@link HStore}，上层可让它绑定来完成实现流程
 * </pre>
 *
 * @author lang : 2025-09-20
 */
public interface LicenseMgr {
    /**
     * 存储组件，此处组件引用有两种
     * <pre>
     *     1. 本地模式下直接返回 SPI 中的本地组件
     *     2. 远程模式下会调用 {@see RemoteIoService} 中的方法来提取 {@link HStore}
     *        [云端模式/微服务模式]
     * </pre>
     *
     * @return 返回存储组件
     */
    HStore refStore();
}
