package io.r2mo.io.common;

import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 默认文件传输请求构造器
 *
 * @author lang : 2025-09-16
 */
@Slf4j
class BuilderPreFile extends AbstractBuilderPre implements BuilderPre<TransferRequest> {

    @Override
    public TransferRequest waitFor(final TransferParameter parameter) {
        if (Objects.isNull(parameter)) {
            return null;
        }

        final TransferType transferType = parameter.transferType();
        if (Objects.isNull(transferType)) {
            return null;
        }

        log.info("[ R2MO ] 创建文件传输请求，类型: {}", transferType.name());
        return switch (transferType) {
            case UPLOAD -> this.waitForUpload(parameter);
            case DOWNLOAD -> this.waitForDownload(parameter);
        };
    }

    private TransferRequest waitForUpload(final TransferParameter parameter) {
        final TransferRequest requestUpload = this.waitForCore(parameter);

        final JObject params = parameter.data();
        final String pathSource = params.getString(TransferToken.NAME.PATH_TARGET);
        requestUpload.setPathTarget(pathSource);
        return requestUpload;
    }

    private TransferRequest waitForDownload(final TransferParameter parameter) {
        final TransferRequest requestDownload = this.waitForCore(parameter);
        final JObject params = parameter.data();
        final String pathSource = params.getString(TransferToken.NAME.PATH_SOURCE);
        requestDownload.setPathSource(pathSource);
        return requestDownload;
    }
}
