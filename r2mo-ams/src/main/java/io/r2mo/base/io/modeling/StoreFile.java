package io.r2mo.base.io.modeling;

import io.r2mo.typed.domain.extension.AbstractStoreObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
 *     - nodeId         （关联节点ID）
 *     - size
 *     - attributes
 *     - storePath
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreFile extends AbstractStoreObject {
    private String fileName;            // 文件名称
    private String fileExtension;       // 文件扩展名
    private String fileType;            // 文件类型
    private String mimeType;            // 文件MIME类型

    private Boolean isMultipart;        // 是否分片文件
    private Integer countChunk;         // 分片数量
    private String checksumMD5;         // MD5校验码
    private String checksumSHA256;      // SHA256校验码
}
