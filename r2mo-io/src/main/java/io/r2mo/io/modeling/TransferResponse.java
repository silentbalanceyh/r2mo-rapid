package io.r2mo.io.modeling;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreDirectory;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.base.io.modeling.StoreNode;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.exception.AbstractException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransferResponse extends AbstractNormObject {

    private String token;                           // 传输令牌
    private String url;                             // 传输URL

    private StoreNode storeNode;                    // 关联存储节点
    private StoreFile storeFile;                    // 关联存储文件
    private StoreDirectory storeDirectory;          // 关联存储目录
    private List<StoreChunk> storeChunks;           // 关联存储分片列表

    private Boolean isMultipart;                    // 是否分片文件
    private Long size;                              // 总大小
    private Long countFile;                         // 文件数量

    private AbstractException error;
}
