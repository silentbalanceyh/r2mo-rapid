package io.r2mo.io.component.node;

import io.r2mo.base.io.enums.NodeType;
import io.r2mo.base.io.transfer.HTransferParam;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-17
 */
class StoreInitToken implements StoreInit<TransferToken> {
    private static final JUtil UT = SPI.V_UTIL;

    @Override
    public TransferToken input(final TransferRequest request) {
        Objects.requireNonNull(request, "[ R2MO ] TransferRequest 不能为空");
        Objects.requireNonNull(request.getNodeId(), "[ R2MO ] Token 初始化失败：资源对象 ID 不能为空");
        // 从请求中提取 TransferToken 的信息
        final TransferToken token = new TransferToken();
        token.inFrom(request);
        // 唯一令牌的生成
        final String tokenId = UUID.randomUUID().toString().replace("-", "");
        token.setToken(tokenId);

        // ===== 基础属性
        token.setUserId(request.getUserId());
        token.setClientIp(request.getClientIp());
        token.setClientAgent(request.getClientAgent());

        // ===== 传输属性
        token.setType(request.getType());
        token.setIsDirectory(request.getIsDirectory());
        token.setIsMultipart(request.getIsMultipart());

        // ===== 特殊对象参数
        final JObject parameters = request.getParameters();
        if (UT.isNotEmpty(parameters)) {
            token.setServiceProvider(parameters.getString(HTransferParam.TOKEN.SERVICE_PROVIDER));
            token.setServiceConsumer(parameters.getString(HTransferParam.TOKEN.SERVICE_CONSUMER));
        }

        // ===== 设置关联对象
        final NodeType nodeType = request.getIsDirectory() ? NodeType.DIRECTORY : NodeType.FILE;
        token.setRef(Ref.of(nodeType.name(), request.getNodeId()));


        // ===== 设置过期时间（默认1小时）
        token.setExpiredAt(LocalDateTime.now().plusHours(1));
        return token;
    }

    @Override
    public TransferResponse output(final TransferToken node) {
        throw new _501NotSupportException("[ R2MO ] Token 输出不适用于此接口，无法通过 Token 直接转 Response");
    }
}
