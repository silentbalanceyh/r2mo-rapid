package io.r2mo.io.common;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreRange;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.TransferToken;
import io.r2mo.base.io.transfer.TransferTokenPool;
import io.r2mo.io.enums.TransferOf;
import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferDirectoryService;
import io.r2mo.io.service.TransferFileService;
import io.r2mo.io.service.TransferLargeService;
import io.r2mo.io.service.TransferTokenService;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.domain.builder.BuilderOf;

import java.io.InputStream;
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
    private final TransferTokenPool cache;

    private RFS(final HTransfer transfer, final TransferTokenPool cache) {
        this.transfer = transfer;
        this.cache = cache;
    }

    public static BuilderOf<TransferRequest> ofBuilder() {
        return BuilderOfTransfer.of();
    }

    public static RFS of(final HTransfer transfer, final TransferTokenPool cache) {
        Objects.requireNonNull(transfer);
        final String cacheKey = transfer.hashCode() + "@" + (cache == null ? "default" : String.valueOf(cache.hashCode()));
        return CCT_RFS.pick(() -> new RFS(transfer, cache), cacheKey);
    }

    private TransferTokenService serviceToken() {
        return this.transfer.serviceToken(this.cache);
    }

    private TransferFileService serviceFile() {
        return this.transfer.serviceOfFile(this.cache);
    }

    private TransferLargeService serviceLarge() {
        return this.transfer.serviceOfLarge(this.cache);
    }

    private TransferDirectoryService serviceDirectory() {
        return this.transfer.serviceOfDirectory(this.cache);
    }

    // ---------------- Token 令牌申请服务 -----------------
    public TransferToken token(final TransferRequest request) {
        // 不传参数使用智能模式
        return this.token(request, true);
    }

    public TransferToken token(final TransferRequest requestToken, final boolean isAuto) {
        if (isAuto) {
            // 先检查是否有旧令牌
            final TransferToken oldToken = this.serviceToken().getToken(requestToken.getToken());
            if (Objects.nonNull(oldToken)) {
                return oldToken;
            }
            // 创建新令牌，取消智能模式
            return this.token(requestToken, false);
        } else {
            requestToken.setToken(null);
            return this.serviceToken().initialize(requestToken);
        }
    }

    public TransferToken token(final TransferParameter parameter) {
        parameter.transferOf(TransferOf.SERVICE_TOKEN);
        // 此处创建的新请求本来令牌就是空的
        final TransferRequest requestToken = this.tokenBuilder().waitFor(parameter);
        return this.token(requestToken, false);
    }

    private BuilderPre<TransferRequest> tokenBuilder() {
        return BuilderOfTransfer.CCT_PRE.pick(BuilderPreToken::new, TransferOf.SERVICE_TOKEN.name());
    }

    private TransferRequest tokenExchange(final TransferRequest request) {
        final TransferToken token = this.token(request, true);
        request.setToken(token.getToken());
        return request;
    }

    private TransferRequest tokenExchange(final TransferParameter parameter) {
        final TransferOf transferOf = parameter.transferOf();
        if (TransferOf.SERVICE_TOKEN == transferOf) {
            throw new IllegalArgumentException("[ R2MO ] 令牌请求不可以执行交换！");
        }
        final TransferRequest request = ofBuilder().create(parameter);
        // 直接交换
        return this.tokenExchange(request);
    }

    // ---------------- 下载
    public TransferResult ioDownload(final TransferRequest request, final OutputStream outStream) {
        final TransferRequest ioRequest = this.tokenExchange(request);
        final boolean isDirectory = request.getIsDirectory();
        if (isDirectory) {
            final TransferDirectoryService service = this.serviceDirectory();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runDownload(response.getToken(), outStream);
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runDownload(response.getToken(), outStream);
        }
    }

    public TransferResult ioDownload(final TransferParameter parameter, final OutputStream outStream) {
        final TransferRequest request = this.tokenExchange(parameter);
        return this.ioDownload(request, outStream);
    }

    public TransferResult ioDownload(final TransferRequest request, final OutputStream outStream, final StoreRange range) {
        final TransferRequest ioRequest = this.tokenExchange(request);
        final boolean isMultipart = request.getIsMultipart();
        if (isMultipart) {
            final TransferLargeService service = this.serviceLarge();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runDownload(response.getToken(), outStream, range);
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runDownload(response.getToken(), outStream, range);
        }
    }

    public TransferResult ioDownload(final TransferParameter parameter, final OutputStream outStream, final StoreRange range) {
        final TransferRequest request = this.tokenExchange(parameter);
        return this.ioDownload(request, outStream, range);
    }

    public TransferResult ioDownload(final TransferRequest request, final OutputStream outStream, final StoreChunk chunk) {
        final boolean isMultipart = request.getIsMultipart();
        if (!isMultipart) {
            throw new IllegalArgumentException("[ R2MO ] 非分片传输不可以使用分片下载！");
        }
        final TransferRequest ioRequest = this.tokenExchange(request);
        final TransferLargeService service = this.serviceLarge();
        final TransferResponse response = service.initialize(ioRequest);
        return service.runDownload(response.getToken(), outStream, chunk);
    }

    public TransferResult ioDownload(final TransferParameter parameter, final OutputStream outStream, final StoreChunk chunk) {
        final TransferRequest request = this.tokenExchange(parameter);
        return this.ioDownload(request, outStream, chunk);
    }

    // ---------------- 上传
    public TransferResult ioUpload(final TransferRequest request, final InputStream in) {
        final TransferRequest ioRequest = this.tokenExchange(request);
        final boolean isDirectory = request.getIsDirectory();
        if (isDirectory) {
            final TransferDirectoryService service = this.serviceDirectory();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runUpload(response.getToken(), in);
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(ioRequest);
            return service.runUpload(response.getToken(), in);
        }
    }

    public TransferResult ioUpload(final TransferParameter parameter, final InputStream in) {
        final TransferRequest request = this.tokenExchange(parameter);
        return this.ioUpload(request, in);
    }

    public TransferResult ioUpload(final TransferRequest request, final InputStream in, final StoreChunk chunk) {
        final boolean isMultipart = request.getIsMultipart();
        if (!isMultipart) {
            throw new IllegalArgumentException("[ R2MO ] 非分片传输不可以使用分片上传！");
        }
        final TransferRequest ioRequest = this.tokenExchange(request);
        final TransferLargeService service = this.serviceLarge();
        final TransferResponse response = service.initialize(ioRequest);
        return service.runUpload(response.getToken(), in, chunk);
    }

    public TransferResult ioUpload(final TransferParameter parameter, final InputStream in, final StoreChunk chunk) {
        final TransferRequest request = this.tokenExchange(parameter);
        return this.ioUpload(request, in, chunk);
    }

    // TODO: 大文件上传的进度提取以及统计信息
}
