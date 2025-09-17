package io.r2mo.io.local.service;

import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-09-17
 */
@Slf4j
public abstract class AbstractTransferService {
    protected static final JUtil UT = SPI.V_UTIL;
    static final NodeManager nm = NodeManager.of();
    protected final TransferTokenService token;

    protected AbstractTransferService(final TransferTokenService token) {
        Objects.requireNonNull(token);
        this.token = token;
    }

    protected void verifyRequest(final TransferRequest request) {
        // 1. 基础请求校验
        if (Objects.isNull(request) || Objects.isNull(request.getNodeId())) {
            throw new _400BadRequestException("[ R2MO ] TransferRequest 不能为空，且必须指定 NodeId");
        }
        // 2. 从存储系统获取文件内容
        /*
         * 此处比较特殊是因为 nodeId = StoreNode 的节点数据是在第一次转换之后得到的，毕竟这是一个库，并不是完整的存储和文件系统，所以
         * findFileBy(nodeId) 这种方式在这里是行不通的，不应该在初始化信息中因为无法找到文件而报错
         */
    }
}
