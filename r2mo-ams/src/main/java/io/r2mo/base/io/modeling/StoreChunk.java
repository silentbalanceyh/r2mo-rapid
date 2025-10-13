package io.r2mo.base.io.modeling;

import io.r2mo.base.io.transfer.TransferRequest;
import io.r2mo.typed.domain.extension.AbstractStoreObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * <pre>
 *     继承属性
 *     - id
 *     - appId
 *     - tenantId
 *     - createdAt
 *     - createdBy
 *     - updatedAt
 *     - updatedBy
 *     存储单元
 *     - size
 *     - attributes
 *     - storePath
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreChunk extends AbstractStoreObject {
    private UUID fileId;           // 关联文件ID
    private Integer index;         // 分片索引
    private Long byteFrom;         // 起始字节
    private Long byteTo;           // 结束字节
    private String checksum;       // 校验码
    private Boolean done;          // 是否完成

    private String fullFileName;   // 文件名称
    private Long totalSize;       // 文件总大小，初始化时传入，计算总大小使用


    public StoreChunk(final int index, final long totalSize, final TransferRequest request) {
        this(index, request);

        this.setStorePath(request.getPathTarget());// 下载时重置
        this.setTotalSize(totalSize);
        final long curSize = Math.min(request.getChunkSize(), totalSize - this.byteFrom);
        this.setSize(curSize);
        this.setByteTo(this.byteFrom + curSize);
    }

    public StoreChunk(final int index, final TransferRequest request) {
        final UUID id = UUID.randomUUID();
        this.setId(id);
        this.setAppId(request.getAppId());
        this.setTenantId(request.getTenantId());
        this.setIndex(index);
        this.setFullFileName(request.getFileName());
        this.setByteFrom((long) index * request.getChunkSize());
        this.setStorePath(request.getPathTarget() + "\\" + index + "-" + id + ".tmp");


        if (request.getTotalSize() != null) {
            this.setTotalSize(request.getTotalSize());
            final long curSize = Math.min(request.getChunkSize(), request.getTotalSize() - this.byteFrom);
            this.setSize(curSize);
            this.setByteTo(this.byteFrom + curSize);
        }
    }


}
