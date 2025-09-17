package io.r2mo.io.service;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.spi.FactoryIo;
import io.r2mo.typed.json.JObject;

/**
 * Dubbo 专用远程 Service，可对接底层的 {@link FactoryIo} 去得到实现，主要用于获取两个核心组件
 * <pre>
 *     1. {@link HStore} 负责底层IO操作的服务
 *        - 查找到对应组件之后就可直接执行 io 级别的操作，此时不调用 HFS 来实现完整的IO操作，即使开启了 SPI，此 SPI 也会是
 *        - 用户开发的 Dubbo -> SPI（Store服务依赖）-> 实现目标执行
 *        - 所以当前接口查找到的组件的操作机器在目标机上（非本机）
 *     2. {@link HTransfer} 负责传输行为如上传、下载、大文件的服务
 *        - 查找对应组件之后可直接执行传输操作（上传、下载等），HFS 中会包含基本类型负责构造请求数据，
 *          所有传输动作如：
 *        - 用户开发的 Dubbo -> SPI (Transfer服务依赖）->
 *                         -> 调用 HFS.ofBuilder() 工具函数获取参数构造器，构造参数内容同底层实现对齐
 *                            如本地 SPI 提供了本地参数构造，那么直接根据 JObject 数据部分构造 {@link TransferRequest}
 *                         -> 从 Transfer 中提取对应的服务 Service 来执行传输行为
 *     3. 如果在非分布式环境，直接调用 SPI 部分即可
 *        - 分布式架构
 *          消费 / service-xxx ( Dubbo Reference 调用 )
 *          提供 / service-store ( 依赖中选择实现 )
 *                  -> SPI -> 执行底层双组件 {@link HStore} 和 {@link HTransfer} 的操作
 *        - 非分布式架构
 *          直接依赖中选择实现
 *                  -> SPI -> 执行底层双组件 {@link HStore} 和 {@link HTransfer} 的操作
 * </pre>
 * 远程模式没有默认场景，必须通过名称获取
 * <pre>
 *     1. Store 的名称：
 *        - spi.io.store.{0}    默认：spi.io.store.DEFAULT
 *     2. Transfer 的名称：
 *        - spi.io.transfer.{0} 默认：spi.io.transfer.DEFAULT
 * </pre>
 *
 * @author lang : 2025-09-16
 */
public interface RemoteIoService {
    HStore findStore(JObject storageJ);

    HStore findStore(String name);

    HTransfer findTransfer(JObject storageJ);

    HTransfer findTransfer(String name);
}
