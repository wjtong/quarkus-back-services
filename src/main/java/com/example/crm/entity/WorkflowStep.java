package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 工作流步骤实体类
 * 定义工作流中的具体步骤和流转规则
 */
@Entity
@Table(name = "workflow_step")
public class WorkflowStep extends PanacheEntityBase {

    @Id
    @NotBlank(message = "工作流步骤ID不能为空")
    @Size(max = 60, message = "工作流步骤ID长度不能超过60个字符")
    @Column(name = "workflow_step_id", length = 60, nullable = false)
    public String workflowStepId;

    @NotBlank(message = "工作流类型ID不能为空")
    @Size(max = 60, message = "工作流类型ID长度不能超过60个字符")
    @Column(name = "workflow_type_id", length = 60, nullable = false)
    public String workflowTypeId;

    @NotBlank(message = "步骤名称不能为空")
    @Size(max = 100, message = "步骤名称长度不能超过100个字符")
    @Column(name = "step_name", length = 100, nullable = false)
    public String stepName;

    @Column(name = "step_description", columnDefinition = "text")
    public String stepDescription;

    @Column(name = "step_order")
    public Integer stepOrder;

    @Size(max = 50, message = "步骤类型长度不能超过50个字符")
    @Column(name = "step_type", length = 50)
    public String stepType;

    @Size(max = 50, message = "处理角色长度不能超过50个字符")
    @Column(name = "handler_role", length = 50)
    public String handlerRole;

    @Size(max = 60, message = "处理人ID长度不能超过60个字符")
    @Column(name = "handler_id", length = 60)
    public String handlerId;

    @Size(max = 100, message = "处理人姓名长度不能超过100个字符")
    @Column(name = "handler_name", length = 100)
    public String handlerName;

    @Column(name = "step_conditions", columnDefinition = "text")
    public String stepConditions;

    @Column(name = "approval_required", length = 1)
    public String approvalRequired;

    @Column(name = "auto_approve", length = 1)
    public String autoApprove;

    @Column(name = "timeout_minutes")
    public Integer timeoutMinutes;

    @Column(name = "retry_attempts")
    public Integer retryAttempts;

    @Column(name = "parallel_execution", length = 1)
    public String parallelExecution;

    @Column(name = "notification_enabled", length = 1)
    public String notificationEnabled;

    @Column(name = "next_step_conditions", columnDefinition = "text")
    public String nextStepConditions;

    @Column(name = "next_step_id", length = 60)
    public String nextStepId;

    @Column(name = "alternative_step_id", length = 60)
    public String alternativeStepId;

    @Column(name = "is_active", length = 1)
    public String isActive;

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

    // 关联的工作流类型（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_type_id", referencedColumnName = "workflow_type_id", insertable = false, updatable = false)
    public WorkflowType workflowType;

    // 关联的处理人（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handler_id", referencedColumnName = "party_id", insertable = false, updatable = false)
    public Party handler;

    /**
     * 获取步骤显示名称
     */
    public String getDisplayName() {
        return stepName != null ? stepName : workflowStepId;
    }

    /**
     * 检查步骤是否激活
     */
    public boolean isActive() {
        return "Y".equals(isActive);
    }

    /**
     * 检查是否需要审批
     */
    public boolean requiresApproval() {
        return "Y".equals(approvalRequired);
    }

    /**
     * 检查是否自动审批
     */
    public boolean isAutoApprove() {
        return "Y".equals(autoApprove);
    }

    /**
     * 检查是否支持并行执行
     */
    public boolean isParallelExecution() {
        return "Y".equals(parallelExecution);
    }

    /**
     * 检查是否启用通知
     */
    public boolean isNotificationEnabled() {
        return "Y".equals(notificationEnabled);
    }

    /**
     * 获取步骤完整描述
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("步骤: ").append(stepName).append("\n");
        sb.append("顺序: ").append(stepOrder != null ? stepOrder : 0).append("\n");
        sb.append("类型: ").append(stepType != null ? stepType : "未设置").append("\n");
        if (handlerRole != null) {
            sb.append("处理角色: ").append(handlerRole).append("\n");
        }
        if (handlerName != null) {
            sb.append("处理人: ").append(handlerName).append("\n");
        }
        sb.append("需要审批: ").append(requiresApproval() ? "是" : "否").append("\n");
        sb.append("自动审批: ").append(isAutoApprove() ? "是" : "否").append("\n");
        if (timeoutMinutes != null) {
            sb.append("超时时间: ").append(timeoutMinutes).append("分钟\n");
        }
        return sb.toString();
    }

    /**
     * 检查步骤条件是否满足
     */
    public boolean checkConditions(String requestData) {
        if (stepConditions == null || stepConditions.trim().isEmpty()) {
            return true;
        }
        
        // 这里应该实现具体的条件检查逻辑
        // 例如：检查金额、角色、部门等条件
        // 暂时返回true，实际实现时需要根据业务规则进行判断
        return true;
    }

    /**
     * 获取下一步骤ID
     */
    public String getNextStepId(String requestData) {
        if (nextStepConditions != null && !nextStepConditions.trim().isEmpty()) {
            // 根据条件判断下一步骤
            // 这里应该实现具体的条件判断逻辑
            // 暂时返回默认的下一步骤
        }
        return nextStepId;
    }

    /**
     * 获取替代步骤ID
     */
    public String getAlternativeStepId(String requestData) {
        if (nextStepConditions != null && !nextStepConditions.trim().isEmpty()) {
            // 根据条件判断替代步骤
            // 这里应该实现具体的条件判断逻辑
        }
        return alternativeStepId;
    }
}
