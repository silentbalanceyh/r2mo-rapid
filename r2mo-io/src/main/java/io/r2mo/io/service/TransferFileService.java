package io.r2mo.io.service;

import io.r2mo.base.io.HTransferService;
import io.r2mo.base.io.enums.TransferResult;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreRange;
import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.io.modeling.TransferResponse;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件的上传下载，已继承方法
 * <pre>
 *     - RESP initialize(REQ request);
 *     - List<DATA> data(String token);
 *     - List<DATA> data(UUID id);
 *     等待重写
 *     - default TransferResult cancel(final String token) {...}
 *     - default TransferResult complete(final String token) {...}
 * </pre>
 *
 * @author lang : 2025-09-16
 */
public interface TransferFileService extends HTransferService<TransferRequest, TransferResponse, StoreChunk> {

    /**
     * 运行文件上传
     *
     * @param token    令牌
     * @param fileData 文件数据流
     *
     * @return 上传结果
     */
    TransferResult runUpload(String token, InputStream fileData);

    /**
     * 运行文件下载
     *
     * @param token    令牌
     * @param fileData 文件数据流
     *
     * @return 下载结果
     */
    TransferResult runDownload(String token, OutputStream fileData);

    /**
     * 运行文件下载，带范围
     *
     * @param token    令牌
     * @param fileData 文件数据流
     * @param range    范围
     *
     * @return 下载结果
     */
    TransferResult runDownload(String token, OutputStream fileData, StoreRange range);
}
