package io.r2mo.io.common;

import io.r2mo.base.io.HTransfer;
import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.token.TransferTokenPool;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferDirectoryService;
import io.r2mo.io.service.TransferFileService;
import io.r2mo.io.service.TransferLargeService;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 远程文件处理，这种模式主要用于处理大文件上传下载，避免内存溢出
 *
 * @author lang : 2025-09-16
 */
@Slf4j
public class RFS {
    private static final Cc<String, RFS> CCT_RFS = Cc.openThread();
    private final HTransfer transfer;
    private final TransferTokenService token;

    private RFS(final HTransfer transfer, final TransferTokenPool cache) {
        this.transfer = transfer;
        this.token = transfer.serviceToken(cache);
    }

    public static RFS of(final HTransfer transfer, final TransferTokenPool cache) {
        Objects.requireNonNull(transfer);
        final String cacheKey = transfer.hashCode() + "@" + (cache == null ? "default" : String.valueOf(cache.hashCode()));
        return CCT_RFS.pick(() -> new RFS(transfer, cache), cacheKey);
    }

    private TransferFileService serviceFile() {
        return this.transfer.serviceOfFile(this.token);
    }

    private TransferLargeService serviceLarge() {
        return this.transfer.serviceOfLarge(this.token);
    }

    private TransferDirectoryService serviceDirectory() {
        return this.transfer.serviceOfDirectory(this.token);
    }

    // ---------------- 下载
    public Binary ioDownload(final TransferRequest request) {
        final boolean isDirectory = request.getIsDirectory();
        if (isDirectory) {
            final TransferDirectoryService service = this.serviceDirectory();
            final TransferResponse response = service.initialize(request);
            return service.runDownload(response.getToken());
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(request);
            return service.runDownload(response.getToken());
        }
    }

    public Binary ioDownload(final TransferRequest request, final FileRange range) {
        final boolean isMultipart = request.getIsMultipart();
        if (isMultipart) {
            final TransferLargeService service = this.serviceLarge();
            final TransferResponse response = service.initialize(request);
            return service.runDownload(response.getToken(), range);
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(request);
            return service.runDownload(response.getToken(), range);
        }
    }

    public Binary ioDownload(final TransferRequest request, final OutputStream outStream, final StoreChunk chunk) {
        final boolean isMultipart = request.getIsMultipart();
        if (!isMultipart) {
            throw new IllegalArgumentException("[ R2MO ] 非分片传输不可以使用分片下载！");
        }
        final TransferLargeService service = this.serviceLarge();
        final TransferResponse response = service.initialize(request);
        return service.runDownload(response.getToken(), chunk);
    }

    // ---------------- 上传
    public TransferResult ioUpload(final TransferRequest request, final InputStream in) {
        final boolean isDirectory = request.getIsDirectory();
        if (isDirectory) {
            final TransferDirectoryService service = this.serviceDirectory();
            final TransferResponse response = service.initialize(request);
            return service.runUpload(response.getToken(), in);
        } else {
            final TransferFileService service = this.serviceFile();
            final TransferResponse response = service.initialize(request);
            return service.runUpload(response.getToken(), in);
        }
    }

    public TransferResult ioUpload(final TransferRequest request, final InputStream in, final StoreChunk chunk) {
        final boolean isMultipart = request.getIsMultipart();
        if (!isMultipart) {
            throw new IllegalArgumentException("[ R2MO ] 非分片传输不可以使用分片上传！");
        }
        final TransferLargeService service = this.serviceLarge();
        final TransferResponse response = service.initialize(request);
        return service.runUpload(response.getToken(), in, chunk);
    }

    // TODO: 大文件上传的进度提取以及统计信息
}
