package io.r2mo.io.local.service;

import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.service.TransferTokenService;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
@Slf4j
public class LocalTokenService implements TransferTokenService {
    private static final JUtil UT = SPI.V_UTIL;
    private final TransferTokenPool cache;

    public LocalTokenService(final TransferTokenPool cache) {
        if (Objects.isNull(cache)) {
            log.info("[ R2MO ] 使用默认本地令牌池");
            this.cache = new LocalTokenPool();
        } else {
            log.info("[ R2MO ] 使用自定义令牌池: {}", cache.getClass().getName());
            this.cache = cache;
        }
    }

    @Override
    public TransferToken runValidate(final String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("[ R2MO ] 验证令牌失败：令牌为空");
                return null;
            }

            final TransferToken transferToken = this.cache.findBy(token);
            if (transferToken == null) {
                log.warn("[ R2MO ] 验证令牌失败：令牌不存在或已过期: {}", token);
                return null;
            }

            log.debug("[ R2MO ] 令牌验证成功: tokenId={}", token);
            return transferToken;

        } catch (final Exception e) {
            log.error("[ R2MO ] 验证令牌时发生错误: tokenId={}", token, e);
            return null;
        }
    }

    @Override
    public TransferToken getToken(final String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("[ R2MO ] 获取令牌失败：令牌为空");
                return null;
            }

            final TransferToken transferToken = this.cache.findBy(token);
            if (transferToken == null) {
                log.warn("[ R2MO ] 获取令牌失败：令牌不存在: {}", token);
                return null;
            }

            log.debug("[ R2MO ] 获取令牌成功: tokenId={}", token);
            return transferToken;

        } catch (final Exception e) {
            log.error("[ R2MO ] 获取令牌时发生错误: tokenId={}", token, e);
            return null;
        }
    }

    @Override
    public boolean runRevoke(final String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("[ R2MO ] 撤销令牌失败：令牌为空");
                return false;
            }

            final boolean result = this.cache.runDelete(token);
            if (result) {
                log.info("[ R2MO ] 撤销令牌成功: tokenId={}", token);
            } else {
                log.warn("[ R2MO ] 撤销令牌失败：令牌不存在: tokenId={}", token);
            }

            return result;

        } catch (final Exception e) {
            log.error("[ R2MO ] 撤销令牌时发生错误: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public boolean runExtend(final String token, final long expireSeconds) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("[ R2MO ] 延长令牌失败：令牌为空");
                return false;
            }

            final long newExpireTime = System.currentTimeMillis() + expireSeconds * 1000;
            final boolean result = this.cache.runExtend(token, newExpireTime);

            if (result) {
                log.info("[ R2MO ] 延长令牌成功: tokenId={}, newExpireTime={}", token, newExpireTime);
            } else {
                log.warn("[ R2MO ] 延长令牌失败：令牌不存在或操作失败: tokenId={}", token);
            }

            return result;

        } catch (final Exception e) {
            log.error("[ R2MO ] 延长令牌时发生错误: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public TransferToken initialize(final TransferRequest request) {
        try {
            if (request == null) {
                log.error("[ R2MO ] 初始化令牌失败：请求为空");
                return null;
            }

            // 生成唯一的令牌
            final String tokenId = UUID.randomUUID().toString().replace("-", "");

            // 创建 TransferToken 对象
            final TransferToken transferToken = new TransferToken();
            transferToken.setToken(tokenId);
            transferToken.setUserId(request.getUserId());
            transferToken.setType(request.getType());
            transferToken.setClientAgent(request.getClientAgent());
            transferToken.setClientIp(request.getClientIp());
            transferToken.setIsMultipart(request.getIsMultipart());
            transferToken.setIsDirectory(request.getIsDirectory());

            // 特殊对象参数
            final JObject parameters = request.getParameters();
            transferToken.setServiceProvider(parameters.getString(TransferToken.NAME.SERVICE_PROVIDER));
            transferToken.setServiceConsumer(parameters.getString(TransferToken.NAME.SERVICE_CONSUMER));

            // 设置过期时间（默认1小时）
            final long expireTime = System.currentTimeMillis() + 3600000L; // 1小时
            transferToken.setExpiredAt(LocalDateTime.now().plusHours(1));

            // 设置关联对象
            if (request.getNodeId() != null) {
                final String nodeType = parameters.getString(TransferToken.NAME.NODE_TYPE);
                transferToken.setRef(Ref.of(nodeType, request.getNodeId()));
            }

            // 保存到缓存
            final boolean saved = this.cache.runSave(transferToken, expireTime);
            if (saved) {
                log.info("[ R2MO ] 初始化令牌成功: tokenId={}, type={}", tokenId, request.getType());
                return transferToken;
            } else {
                log.error("[ R2MO ] 初始化令牌失败：保存到缓存失败");
                return null;
            }

        } catch (final Exception e) {
            log.error("[ R2MO ] 初始化令牌时发生错误: request={}", request, e);
            return null;
        }
    }

    @Override
    public List<JObject> data(final String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("[ R2MO ] 获取令牌数据失败：令牌为空");
                return List.of();
            }

            final TransferToken transferToken = this.cache.findBy(token);
            if (transferToken == null) {
                log.warn("[ R2MO ] 获取令牌数据失败：令牌不存在: {}", token);
                return List.of();
            }

            // 将 TransferToken 转换为 JObject 列表
            final JObject tokenData = UT.serializeJson(transferToken);
            log.debug("[ R2MO ] 获取令牌数据成功: tokenId={}", token);
            return List.of(tokenData);

        } catch (final Exception e) {
            log.error("[ R2MO ] 获取令牌数据时发生错误: tokenId={}", token, e);
            return List.of();
        }
    }

    @Override
    public List<JObject> data(final UUID id) {
        try {
            if (id == null) {
                log.warn("[ R2MO ] 获取节点数据失败：ID为空");
                return List.of();
            }

            log.debug("[ R2MO ] 获取节点数据: nodeId={}", id);
            // 这里应该从存储服务获取节点相关数据
            // 目前返回空列表，实际实现需要根据业务需求
            return List.of();

        } catch (final Exception e) {
            log.error("[ R2MO ] 获取节点数据时发生错误: nodeId={}", id, e);
            return List.of();
        }
    }
}