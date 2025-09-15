package io.r2mo.io.modeling;

import io.r2mo.typed.domain.extension.AbstractNormObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
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
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransferStatistics extends AbstractNormObject {
    private UUID nodeId;                        // 统计节点
    private Long totalUploads;                  // 总上传次数
    private Long totalDownloads;                // 总下载次数
    private Long todayUploads;                  // 今日上传次数
    private Long todayDownloads;                // 今日下载次数
    private Long weekUploads;                   // 本周上传次数
    private Long weekDownloads;                 // 本周下载次数
    private Long monthUploads;                  // 本月上传次数
    private Long monthDownloads;                // 本月下载次数
    private Map<String, Long> dailyUploads;     // 最近30天每日上传次数
    private Map<String, Long> dailyDownloads;   // 最近30天每日下载次数
}
