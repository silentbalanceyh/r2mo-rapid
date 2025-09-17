package io.r2mo.io.common;

import io.r2mo.base.io.HPath;
import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 默认令牌构造器，主要针对文件的上传下载处理，此处的 {@link TransferParameter} 的基础结构
 * <pre>
 *     {@link HPath} 中存储的属性：
 *     - id             / 存储标识
 *     - context        / 存储上下文
 *     下边属性为 UriApp 的扩展：
 *     - scheme         / 协议
 *     - version        / 版本
 *     - os             / 操作系统
 *     - cpu            / 处理器
 *
 *     核心结构属性
 *     - transferType   / 传输类型（上传/下载）
 * </pre>
 *
 * @author lang : 2025-09-16
 */
@Slf4j
class BuilderPreToken extends AbstractBuilderPre implements BuilderPre<TransferRequest> {

    @Override
    public TransferRequest waitFor(final TransferParameter parameter) {
        if (Objects.isNull(parameter)) {
            return null;
        }

        final TransferType transferType = parameter.transferType();
        if (Objects.isNull(transferType)) {
            return null;
        }

        log.info("[ R2MO ] 创建令牌传输请求，类型: {}", transferType.name());
        return switch (transferType) {
            case UPLOAD -> this.waitForUpload(parameter);
            case DOWNLOAD -> this.waitForDownload(parameter);
        };
    }

    private TransferRequest waitForDownload(final TransferParameter parameter) {
        final TransferRequest requestDownload = this.waitForOwner(parameter);

        final JObject params = parameter.data();
        final String pathSource = params.getString(TransferToken.NAME.PATH_SOURCE);
        requestDownload.setPathSource(pathSource);
        return requestDownload;
    }

    private TransferRequest waitForUpload(final TransferParameter parameter) {
        final TransferRequest requestUpload = this.waitForOwner(parameter);

        final JObject params = parameter.data();
        final String pathSource = params.getString(TransferToken.NAME.PATH_TARGET);
        requestUpload.setPathTarget(pathSource);
        return requestUpload;
    }
}
