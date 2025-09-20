package io.r2mo.io.service;

import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.transfer.HTransferService;
import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.base.io.transfer.TransferResult;
import io.r2mo.io.modeling.TransferResponse;
import io.r2mo.typed.common.Binary;

import java.io.InputStream;
import java.util.List;

/**
 * 大文件的上传下载，已继承方法
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
public interface TransferLargeService extends HTransferService<TransferRequest, TransferResponse, StoreChunk> {
    /**
     * 上传文件分片
     *
     * @param token    令牌
     * @param fileData 文件数据流
     * @param chunk    分片信息
     *
     * @return 上传结果
     */
    TransferResult runUpload(String token, InputStream fileData, StoreChunk chunk);

    /**
     * 运行文件下载
     *
     * @param token 令牌
     * @param chunk 分片信息
     *
     * @return 下载结果
     */
    Binary runDownload(String token, StoreChunk chunk);

    /**
     * 运行文件下载，带范围
     *
     * @param token 令牌
     * @param range 范围
     *
     * @return 下载结果
     */
    Binary runDownload(String token, FileRange range);

    /**
     * 已上传的分片列表
     *
     * @return 分片列表
     */
    List<StoreChunk> dataUploaded();

    /**
     * 等待上传的分片列表
     *
     * @return 分片列表
     */
    List<StoreChunk> dataWaiting();
}
