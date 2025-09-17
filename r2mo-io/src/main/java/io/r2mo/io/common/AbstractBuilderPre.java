package io.r2mo.io.common;

import io.r2mo.base.io.enums.NodeType;
import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.io.enums.TransferOf;
import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.constant.DefaultConstantValue;
import io.r2mo.typed.constant.DefaultField;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-16
 */
@Slf4j
abstract class AbstractBuilderPre implements BuilderOf<TransferRequest> {

    @Override
    public void updateRef(final TransferRequest target, final Ref ref) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateRef ");
    }

    @Override
    public TransferRequest create() {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ create ");
    }

    @Override
    public <R> void updateConditional(final TransferRequest target, final R source) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateConditional ");
    }

    @Override
    public void updateOverwrite(final TransferRequest target, final Object source) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateOverwrite ");
    }

    /**
     * 设置字段
     * <pre>
     *     id               - （令牌）存储标识
     *     appId            - （令牌）应用标识
     *     tenantId         - （令牌）租户标识
     *     userId           - （令牌）用户标识
     * </pre>
     *
     * @param request   传输请求
     * @param parameter 参数对象
     */
    protected void waitForOwner(final TransferRequest request, final JObject parameter) {
        request.setId(this.safeUuid(parameter, DefaultField.ID, UUID::randomUUID));

        request.setAppId(this.safeUuid(parameter, DefaultField.APP_ID));
        request.setTenantId(this.safeUuid(parameter, DefaultField.TENANT_ID));

        request.setUserId(this.safeUuid(parameter, DefaultField.USER_ID));
    }

    private UUID safeUuid(final JObject parameter, final String field) {
        return this.safeUuid(parameter, field, null);
    }

    private UUID safeUuid(final JObject parameter, final String field, final Supplier<UUID> supplier) {
        final Object value = parameter.get(field);
        if (value == null) {
            return Objects.isNull(supplier) ? null : supplier.get();
        }
        if (value instanceof String) {
            return UUID.fromString((String) value);
        } else if (value instanceof UUID) {
            return (UUID) value;
        } else {
            // 尝试转换为字符串再解析
            return UUID.fromString(value.toString());
        }
    }

    /**
     * 基础信息（可共享部分的提取）
     * <pre>
     *     createdAt       - 创建时间
     *     createdBy       - 创建用户
     *     updatedAt       - 更新时间
     *     updatedBy       - 更新用户
     * </pre>
     *
     * @param request   传输请求
     * @param parameter 参数对象
     */
    protected void waitForCommon(final TransferRequest request, final JObject parameter) {
        final UUID userId = this.safeUuid(parameter, DefaultField.USER_ID, () -> DefaultConstantValue.BY_SYSTEM);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        if (Objects.nonNull(userId)) {
            request.setCreatedBy(userId);
            request.setUpdatedBy(userId);
        }
    }

    /**
     * 扩展参数
     *
     * @param request   传输请求
     * @param parameter 参数对象
     */
    protected void waitForParameters(final TransferRequest request, final JObject parameter) {
        final JObject paramCopy = parameter.copy();
        Set.of(
            DefaultField.ID,
            DefaultField.APP_ID,
            DefaultField.TENANT_ID,
            DefaultField.USER_ID,
            DefaultField.CREATED_AT,
            DefaultField.CREATED_BY,
            DefaultField.UPDATED_AT,
            DefaultField.UPDATED_BY
        ).forEach(paramCopy::remove);
        request.setParameters(paramCopy);
    }

    /**
     * 节点类型
     * <pre>
     *    isDirectory     - （令牌）是否目录
     *    nodeId          - （令牌）节点标识
     * </pre>
     *
     * @param request   传输请求
     * @param parameter 参数对象
     */
    protected void waitForPredicate(final TransferRequest request, final JObject parameter) {
        // nodeType，必须属性
        NodeType nodeType = null;
        final Object nodeTypeObj = parameter.get(TransferToken.NAME.NODE_TYPE);
        if (Objects.nonNull(nodeTypeObj)) {
            nodeType = NodeType.valueOf(nodeTypeObj.toString());
        }
        request.setIsDirectory(NodeType.DIRECTORY == nodeType);
        request.setNodeId(this.safeUuid(parameter, TransferToken.NAME.NODE_ID));
    }

    protected void waitForClient(final TransferRequest request, final JObject parameter) {
        if (parameter.containsKey(TransferToken.NAME.CLIENT_IP)) {
            request.setClientIp((String) parameter.get(TransferToken.NAME.CLIENT_IP));
        }
        if (parameter.containsKey(TransferToken.NAME.CLIENT_AGENT)) {
            request.setClientAgent((String) parameter.get(TransferToken.NAME.CLIENT_AGENT));
        }
    }

    protected TransferRequest waitForOwner(final TransferParameter parameter) {
        final TransferRequest transferRequest = new TransferRequest();


        /*
         * 填充字段：
         * - id, appId, tenantId, userId
         */
        final JObject params = parameter.data();
        this.waitForOwner(transferRequest, params);


        /*
         * 填充字段：
         * - type
         * - isMultipart
         */
        transferRequest.setType(parameter.transferType());
        final TransferOf transferOf = parameter.transferOf();
        transferRequest.setIsMultipart(TransferOf.SERVICE_FILE != transferOf);


        /*
         * 填充字段：
         * - nodeId
         * - isDirectory
         * - clientId
         * - clientAgent
         */
        this.waitForPredicate(transferRequest, params);
        this.waitForClient(transferRequest, params);

        this.waitForCommon(transferRequest, params);
        this.waitForParameters(transferRequest, params);
        return transferRequest;
    }
}
