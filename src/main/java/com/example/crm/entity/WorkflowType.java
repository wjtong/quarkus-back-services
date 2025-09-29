package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 工作流类型实体类
 * 定义不同类型的工作流（如报销申请、请假申请等）
 */
@Entity
@Table(name = "workflow_type")
public class WorkflowType extends PanacheEntityBase {

    @Id
    @NotBlank(message = "工作流类型ID不能为空")
    @Size(max = 60, message = "工作流类型ID长度不能超过60个字符")
    @Column(name = "workflow_type_id", length = 60, nullable = false)
    public String workflowTypeId;

    @NotBlank(message = "工作流类型名称不能为空")
    @Size(max = 100, message = "工作流类型名称长度不能超过100个字符")
    @Column(name = "workflow_type_name", length = 100, nullable = false)
    public String workflowTypeName;

    @Size(max = 255, message = "工作流类型描述长度不能超过255个字符")
    @Column(name = "description", length = 255)
    public String description;

    @Column(name = "workflow_definition", columnDefinition = "text")
    public String workflowDefinition;

    @Column(name = "business_rules", columnDefinition = "text")
    public String businessRules;

    @Column(name = "approval_matrix", columnDefinition = "text")
    public String approvalMatrix;

    @Column(name = "version")
    public Integer version;

    @Column(name = "is_active", length = 1)
    public String isActive;

    @Column(name = "priority_level")
    public Integer priorityLevel;

    @Column(name = "default_timeout")
    public Integer defaultTimeout;

    @Column(name = "max_retry_attempts")
    public Integer maxRetryAttempts;

    @Column(name = "requires_approval", length = 1)
    public String requiresApproval;

    @Column(name = "auto_approve", length = 1)
    public String autoApprove;

    @Column(name = "notification_enabled", length = 1)
    public String notificationEnabled;

    @Column(name = "audit_required", length = 1)
    public String auditRequired;

    @Column(name = "created_date")
    public LocalDateTime createdDate;

    @Size(max = 320, message = "创建用户登录名长度不能超过320个字符")
    @Column(name = "created_by_user_login", length = 320)
    public String createdByUserLogin;

    @Column(name = "last_modified_date")
    public LocalDateTime lastModifiedDate;

    @Size(max = 320, message = "最后修改用户登录名长度不能超过320个字符")
    @Column(name = "last_modified_by_user_login", length = 320)
    public String lastModifiedByUserLogin;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    /**
     * 获取工作流类型显示名称
     */
    public String getDisplayName() {
        return workflowTypeName != null ? workflowTypeName : workflowTypeId;
    }

    /**
     * 检查工作流类型是否激活
     */
    public boolean isActive() {
        return "Y".equals(isActive);
    }

    /**
     * 检查是否需要审批
     */
    public boolean requiresApproval() {
        return "Y".equals(requiresApproval);
    }

    /**
     * 检查是否自动审批
     */
    public boolean isAutoApprove() {
        return "Y".equals(autoApprove);
    }

    /**
     * 检查是否启用通知
     */
    public boolean isNotificationEnabled() {
        return "Y".equals(notificationEnabled);
    }

    /**
     * 检查是否需要审计
     */
    public boolean isAuditRequired() {
        return "Y".equals(auditRequired);
    }

    /**
     * 获取工作流完整描述
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("工作流类型: ").append(workflowTypeName).append("\n");
        sb.append("版本: ").append(version != null ? version : 1).append("\n");
        if (description != null) {
            sb.append("描述: ").append(description).append("\n");
        }
        sb.append("状态: ").append(isActive() ? "激活" : "未激活").append("\n");
        sb.append("需要审批: ").append(requiresApproval() ? "是" : "否").append("\n");
        return sb.toString();
    }
}
