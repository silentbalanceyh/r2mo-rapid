package io.r2mo.io.component.node;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.common.FileHelper;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.base.io.transfer.HTransferParam;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.Objects;

/**
 * @author lang : 2025-09-17
 */
class StoreInitFile implements StoreInit<StoreFile> {
    private static final JUtil UT = SPI.V_UTIL;

    @Override
    public StoreFile input(final TransferRequest request) {
        if (request.getIsDirectory()) {
            throw new _501NotSupportException("[ R2MO ] 此接口不支持目录操作！");
        }
        final StoreFile storeFile = new StoreFile();
        storeFile.inFrom(request);


        storeFile.setIsMultipart(request.getIsMultipart());
        if (request.getIsMultipart()) {
            // 分片数量统计
            storeFile.setCountChunk(request.getChunkCount());
        }
        storeFile.setOwnerId(request.getUserId());


        /*
         * 省略属性
         * - parentId
         *   只有在目录上传或下载执行多个节点时才会使用，所以此处不用它
         */
        final TransferType transferType = request.getType();
        if (TransferType.DOWNLOAD == transferType) {
            // 下载流程
            this.inputDownload(request, storeFile);
        } else {
            // 上传流程
            this.inputUpload(request, storeFile);
        }

        return storeFile;
    }

    private void inputUpload(final TransferRequest request, final StoreFile storeFile) {
        Objects.requireNonNull(request.getPathTarget(), "[ R2MO ] 上传请求中必须指定目标路径！");
        /*
         * 上传请求中包含了文件名、文件尺寸等相关信息
         */
        final String filename = FileHelper.fileName(request.getFileName());
        storeFile.setFileName(filename);
        storeFile.setSize(request.getFileSize());

        final String extension = FileHelper.fileExtension(filename);
        storeFile.setFileExtension(extension);
        storeFile.setFileType(FileHelper.fileType(extension).name());

        final JObject parameters = request.getParameters();
        if (UT.isNotEmpty(parameters)) {
            final String mimeType = parameters.getString(HTransferParam.REQUEST.MIME);
            storeFile.setMimeType(mimeType);
            final String ext = parameters.getString(HTransferParam.REQUEST.EXTENSION);
            // 重写扩展名和文件类型
            if (StrUtil.isNotEmpty(ext)) {
                storeFile.setFileExtension(ext);
                storeFile.setFileType(FileHelper.fileType(ext).name());
            }
        }
        /*
         * 关键属性，上传过程中必须指定的属性，此值为相对路径，如果是分布式系统则表示分布式系统的相对路径，它的核心配置会位于
         * 请求的扩展参数 / parameters 中
         * - meta_home
         * - meta_schema
         * - meta_host
         * - meta_port
         * - meta_context
         * - meta_account
         */
        storeFile.setStorePath(request.getPathTarget());
    }

    private void inputDownload(final TransferRequest request, final StoreFile storeFile) {
        Objects.requireNonNull(request.getPathSource(), "[ R2MO ] 下载请求中必须指定源路径！");
        /*
         * nodeId 重写
         * nodeId 在请求时必须携带，它表示请求之前对应的 node 信息，此信息回在处理流程中被存储起来，主要用于后续的更新操作，当 token
         * 被反序列化查询提取到 nodeId 时，要使用此 nodeId 定位到对应的 node 信息，如此才可以让 Response 中携带正确的信息，在
         * Service 执行过程中 StoreFile 不仅仅会绑定到 Response 上，还会在缓存中存储
         *
         * token = StoreFile 的相关信息
         *           -> nodeId -> 可以直接定位服务中实际的文件或目录信息
         *
         * 此处 nodeId 要和请求中的 nodeId 保持一致
         */
        storeFile.setId(request.getNodeId());


        /*
         * 下载过程中下边属性忽略
         * - fileName
         * - fileExtension
         * - fileType
         * - mimeType
         */
        storeFile.setStorePath(request.getPathSource());
    }

    @Override
    public TransferResponse output(final StoreFile node) {
        final TransferResponse response = new TransferResponse();
        node.outTo(response);

        response.setSize(node.getSize());
        response.setIsMultipart(node.getIsMultipart());
        response.setFile(node);
        if (node.getIsMultipart()) {
            response.setCountChunk(node.getCountChunk());
        }
        return response;
    }
}
