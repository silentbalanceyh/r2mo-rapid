package io.r2mo.io.local.service;

import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.token.TransferTokenService;
import io.r2mo.io.component.node.StoreInit;
import io.r2mo.io.local.transfer.TransDownload;
import io.r2mo.io.local.transfer.TransUpload;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.io.service.TransferFileService;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._404NotFoundException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-16
 */
class LocalFileService extends AbstractTransferService implements TransferFileService {
    private final StoreInit<StoreFile> initializer;

    LocalFileService(final TransferTokenService token) {
        super(token);
        this.initializer = StoreInit.ofFile();
    }


    @Override
    public TransferResponse initialize(final TransferRequest request) {
        this.verifyRequest(request);
        // 1. 先创建传输令牌
        final TransferToken token = this.token.initialize(request);
        if (Objects.isNull(token)) {
            throw new _400BadRequestException("[ R2MO ] 令牌创建失败，请检查请求参数等相关数据！");
        }


        // 2. 将请求数据转换为 StoreFile 数据
        final StoreFile file = this.initializer.input(request);


        // 3. 访问 NodeManager 将 nodeId = StoreFile 的数据存储起来
        nm.put(request.getNodeId(), file);


        // 4. 使用 StoreInit 将 StoreFile 转换为 TransferResponse
        return this.initializer.output(file);
    }


    @Override
    public TransferResult runUpload(final String token, final InputStream fileData) {
        final StoreFile found = this.findStoreFile(token);


        // 执行文件上传
        final boolean upload = TransUpload.of().write(found, fileData);
        return upload ? TransferResult.SUCCESS : TransferResult.FAILURE;
    }


    @Override
    public TransferResult runDownload(final String token, final OutputStream fileData) {
        final StoreFile found = this.findStoreFile(token);


        // 执行文件下载
        final boolean download = TransDownload.of().read(found, fileData);
        return download ? TransferResult.SUCCESS : TransferResult.FAILURE;
    }


    @Override
    public TransferResult runDownload(final String token, final OutputStream fileData, final FileRange range) {
        return null;
    }

    private StoreFile findStoreFile(final String token) {
        // 1. 先验证令牌
        final TransferToken tokenVerified = this.token.runValidate(token);
        if (Objects.isNull(tokenVerified)) {
            throw new _404NotFoundException("[ R2MO ] 令牌无效或已过期: " + token);
        }


        // 2. 从令牌中获取 nodeId
        final Ref ref = tokenVerified.getRef();
        if (Objects.isNull(ref) || Objects.isNull(ref.refId())) {
            throw new _404NotFoundException("[ R2MO ] 令牌关联的资源对象无效: " + token);
        }


        // 3. 根据 nodeId 获取真实文件内容
        final StoreFile found = nm.find(ref.refId());
        if (Objects.isNull(found)) {
            throw new _404NotFoundException("[ R2MO ] 资源对象不存在: " + ref.refId());
        }
        return found;
    }

    @Override
    public List<StoreChunk> data(final String token) {
        return List.of();
    }

    @Override
    public List<StoreChunk> data(final UUID id) {
        return List.of();
    }

    @Override
    public TransferResult cancel(final String token) {
        return null;
    }

    @Override
    public TransferResult complete(final String token) {
        return null;
    }
}
