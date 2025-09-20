package io.r2mo.jce.component.lic.domain;

import io.r2mo.typed.domain.extension.AbstractNormObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
 * @author lang : 2025-09-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LicenseData extends AbstractNormObject implements Serializable {
    private String licenseId;       // 许可证ID
    private String name;            // 许可证名称
    private String code;            // 许可证编号
    private String version;         // 许可证版本

    private String issuedApp;       // 产品ID
    private String issuedName;      // 产品名称 -> App 的名称
    private String issuedAt;        // 签发时间
    private String expireAt;        // 过期时间

    private Long maxUsers;          // 最大用户数
    private Long maxDevices;        // 最大设备数
    private String copyRight;       // 版权信息

    private UUID ownerId;           // 拥有者ID
    private String ownerName;       // 拥有者名称（企业名、姓名）
    private String ownerTitle;      // 拥有者头衔（职位）

    /**
     * 路径计算依靠此方法
     *
     * @return License ID
     */
    public String licenseId() {
        return this.licenseId;
    }
}
