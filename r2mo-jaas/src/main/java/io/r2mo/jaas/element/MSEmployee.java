package io.r2mo.jaas.element;

import io.r2mo.jaas.enums.EmployeeStatus;
import io.r2mo.jaas.enums.EmployeeType;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * 员工核心数据信息
 *
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MSEmployee extends AbstractNormObject implements Serializable {

    @Schema(description = "用户ID")
    private UUID userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "员工类型")
    private EmployeeType type = EmployeeType.FULL_TIME;

    @Schema(description = "员工状态")
    private EmployeeStatus status = EmployeeStatus.WORKING;

    // ---------------------- 员工基础数据
    @Schema(description = "工作邮箱")
    private String workEmail;

    @Schema(description = "工作手机")
    private String workMobile;

    @Schema(description = "工作电话")
    private String workPhone;

    @Schema(description = "工作传真")
    private String workFax;

    @Schema(description = "工作号码")
    private String workNumber;

    @Schema(description = "工作职位")
    private String workTitle;

    @Schema(description = "工作分机")
    private String workExtension;

    // ---------------------- 部门基础数据
    @Schema(description = "部门ID")
    private UUID deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "部门经理")
    private String deptManager;

    @Schema(description = "部门邮箱")
    private String deptEmail;

    // ---------------------- 公司基础数据
    @Schema(description = "公司ID")
    private UUID companyId;

    @Schema(description = "公司标题")
    private String companyTitle;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "公司Logo")
    private String companyLogo;

    @Schema(description = "公司邮箱")
    private String companyEmail;

    @Schema(description = "公司电话")
    private String companyPhone;

    @Schema(description = "公司传真")
    private String companyFax;

    @Schema(description = "公司地址")
    private String companyAddress;

    @Schema(description = "公司主页")
    private String companyHome;

    @Schema(description = "公司描述")
    private String companyDescription;
}
