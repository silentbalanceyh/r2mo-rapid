package io.r2mo.io.local.service;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.modeling.StoreNode;

/**
 * 实际访问文件的存储服务，此处会有 {@link HStore} 的本地 SPI 获取，执行时已经是 -store 的服务在访问此处，不仅如此，它还需要动态维护一个
 * nodeId -> {@link StoreNode} 的映射关系，而此处的 token 一定是执行过程中的基础 token 信息
 *
 * @author lang : 2025-09-18
 */
interface StoreService {

}
