package io.r2mo.jce.component.lic.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.json.jackson.WithoutSecondSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
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
 * LicenseData: 许可证核心信息封装
 * <pre>
 * 设计要点：
 * - 核心标识属性必须唯一，保证许可不可混淆
 * - 结合产品、用户、设备三维度进行约束
 * - 支持多租户与跨应用场景
 * </pre>
 *
 * @author lang
 * @since 2025-09-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LicenseData extends AbstractNormObject implements Serializable {

    /**
     * 许可证ID 🔴
     * - 必须：全局唯一标识，用于追踪和校验许可证
     * - 设计：可使用 UUID 或数据库生成的唯一 LicenseID
     * - 通用性：所有许可系统的核心字段，适用于 SaaS、离线授权、硬件绑定
     */
    private String licenseId;

    /**
     * 许可证名称 🔵
     * - 可选：便于展示和人类可读性
     * - 设计：通常为简短描述，例如 “企业版许可证”
     * - 通用性：适用于后台管理界面、导出报表时展示
     */
    private String name;

    /**
     * 许可证编号 🔵
     * - 可选：业务编号或流水号，方便运维/财务对账
     * - 设计：和 licenseId 区别在于更贴近业务语义，可人工识别
     * - 通用性：常见于 B2B 软件合同与售后支持
     */
    private String code;

    /**
     * 许可证版本 🔵
     * - 可选：控制许可文件结构或兼容性
     * - 设计：当许可格式升级时，客户端可根据版本解析
     * - 通用性：跨版本支持时非常重要，例如 V1 JSON/V2 二进制格式
     */
    private String version;

    /**
     * 产品ID 🔴
     * - 必须：标识该许可对应的产品（或 App）
     * - 设计：可与 appId 对应，用于区分不同产品线
     * - 通用性：适用于多产品企业（如同一客户买了不同模块）
     */
    private UUID issuedApp;

    /**
     * 产品名称 🔵
     * - 可选：用于展示，和 issuedApp 对应的人类可读值
     * - 通用性：报告、界面展示时更直观
     */
    private String issuedName;

    /**
     * 签发时间 🔴
     * - 必须：记录许可生成时间
     * - 设计：用于追踪签发批次，配合到期时间做有效性校验
     * - 通用性：所有许可系统必备字段
     */
    @JsonSerialize(using = WithoutSecondSerializer.class)
    private LocalDateTime issuedAt;

    /**
     * 过期时间 🔴
     * - 必须：控制许可生命周期
     * - 设计：到期后许可失效，强制更新或续期
     * - 通用性：普遍存在于商业软件、SaaS、订阅模式
     */
    @JsonSerialize(using = WithoutSecondSerializer.class)
    private LocalDateTime expireAt;

    /**
     * 最大用户数 🔵
     * - 可选：限制系统能创建或登录的用户数
     * - 通用性：B2B SaaS、企业软件常见，用于分级收费
     */
    private Long maxUsers;

    /**
     * 最大设备数 🔵
     * - 可选：限制并发设备或客户端数
     * - 通用性：常见于 IoT 平台、终端授权软件
     */
    private Long maxDevices;

    /**
     * 版权信息 🔵
     * - 可选：版权声明/厂商信息
     * - 通用性：多见于最终交付文件，法律合规需要
     */
    private String copyRight;

    /**
     * 拥有者ID 🔴
     * - 必须：绑定许可证的实际所有者（企业/用户唯一标识）
     * - 设计：通常关联客户数据库的主键或 UUID
     * - 通用性：SaaS 租户绑定、B2B 企业授权
     */
    private UUID ownerId;

    /**
     * 拥有者名称 🔵
     * - 可选：显示企业名称或个人姓名
     * - 通用性：报告、合同或客户 UI 上展示
     */
    private String ownerName;

    /**
     * 拥有者头衔 🔵
     * - 可选：例如 “IT Manager”、“CEO”
     * - 通用性：在企业级许可中用于合同/签署人信息
     */
    private String ownerTitle;

    /**
     * 设备指纹 🔴
     * - 必须（如果启用绑定机制）：绑定特定硬件/环境，防止复制传播
     * - 设计：可以是 CPU/MAC/Disk 等硬件特征的哈希
     * - 通用性：常见于工业控制软件、离线授权系统
     */
    private String signFinger;

    /**
     * 路径计算依靠此方法
     *
     * @return License LicenseID
     */
    public String licenseId() {
        return this.licenseId;
    }
}
