package io.r2mo.io.common;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.service.TransferTokenService;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.domain.builder.BuilderOf;

import java.io.OutputStream;
import java.util.Objects;

/**
 * 远程文件处理，这种模式主要用于处理大文件上传下载，避免内存溢出
 *
 * @author lang : 2025-09-16
 */
public class RFS {
    private static final Cc<String, RFS> CCT_RFS = Cc.openThread();
    private final HTransfer transfer;
    private final TransferTokenService tokenService;

    private RFS(final HTransfer transfer, final TransferTokenPool cache) {
        this.transfer = transfer;
        this.tokenService = transfer.serviceToken(cache);
    }

    public static BuilderOf<TransferRequest> ofBuilder() {
        return BuilderOfTransfer.of();
    }

    public static RFS of(final HTransfer transfer, final TransferTokenPool cache) {
        Objects.requireNonNull(transfer);
        final String cacheKey = transfer.hashCode() + "@" + (cache == null ? "default" : String.valueOf(cache.hashCode()));
        return CCT_RFS.pick(() -> new RFS(transfer, cache), cacheKey);
    }

    /**
     * 令牌申请专用
     *
     * @param requestToken 令牌申请
     *
     * @return 返回传输令牌
     */
    public TransferToken token(final TransferRequest requestToken) {
        return this.tokenService.initialize(requestToken);
    }

    /**
     * 下载请求 / 上传参数不同可重载
     *
     * @param request   传输请求（包含令牌信息）
     * @param outStream 输出流
     */
    public void ioDownload(final TransferRequest request, final OutputStream outStream) {

    }
}
