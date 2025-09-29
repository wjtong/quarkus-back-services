package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 工作流实例实体类
 * 代表具体的工作流执行实例
 */
@Entity
@Table(name = "workflow_instance")
public class WorkflowInstance extends PanacheEntityBase {

    @Id
    @NotBlank(message = "工作流实例ID不能为空")
    @Size(max = 60, message = "工作流实例ID长度不能超过60个字符")
    @Column(name = "workflow_instance_id", length = 60, nullable = false)
    public String workflowInstanceId;

    @NotBlank(message = "工作流类型ID不能为空")
    @Size(max = 60, message = "工作流类型ID长度不能超过60个字符")
    @Column(name = "workflow_type_id", length = 60, nullable = false)
    public String workflowTypeId;

    @NotBlank(message = "发起人ID不能为空")
    @Size(max = 60, message = "发起人ID长度不能超过60个字符")
    @Column(name = "initiator_id", length = 60, nullable = false)
    public String initiatorId;

    @NotBlank(message = "发起人姓名不能为空")
    @Size(max = 100, message = "发起人姓名长度不能超过100个字符")
    @Column(name = "initiator_name", length = 100, nullable = false)
    public String initiatorName;

    @Size(max = 200, message = "工作流标题长度不能超过200个字符")
    @Column(name = "title", length = 200)
    public String title;

    @Column(name = "description", columnDefinition = "text")
    public String description;

    @Column(name = "request_data", columnDefinition = "text")
    public String requestData;

    @Size(max = 50, message = "状态长度不能超过50个字符")
    @Column(name = "status", length = 50)
    public String status;

    @Column(name = "priority")
    public Integer priority;

    @Column(name = "current_step")
    public Integer currentStep;

    @Column(name = "total_steps")
    public Integer totalSteps;

    @Column(name = "started_date")
    public LocalDateTime startedDate;

    @Column(name = "completed_date")
    public LocalDateTime completedDate;

    @Column(name = "due_date")
    public LocalDateTime dueDate;

    @Column(name = "timeout_date")
    public LocalDateTime timeoutDate;

    @Column(name = "estimated_duration")
    public Integer estimatedDuration;

    @Column(name = "actual_duration")
    public Integer actualDuration;

    @Column(name = "result_data", columnDefinition = "text")
    public String resultData;

    @Column(name = "error_message", columnDefinition = "text")
    public String errorMessage;

    @Column(name = "retry_count")
    public Integer retryCount;

    @Column(name = "max_retry_attempts")
    public Integer maxRetryAttempts;

    @Column(name = "approval_required", length = 1)
    public String approvalRequired;

    @Column(name = "auto_approved", length = 1)
    public String autoApproved;

    @Column(name = "notification_sent", length = 1)
    public String notificationSent;

    @Column(name = "audit_logged", length = 1)
    public String auditLogged;

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

    // 关联的发起人（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "party_id", insertable = false, updatable = false)
    public Party initiator;

    /**
     * 获取工作流实例显示名称
     */
    public String getDisplayName() {
        return title != null ? title : workflowInstanceId;
    }

    /**
     * 检查工作流是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * 检查工作流是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    /**
     * 检查工作流是否进行中
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    /**
     * 检查工作流是否待处理
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * 检查工作流是否已取消
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * 检查工作流是否已过期
     */
    public boolean isOverdue() {
        if (dueDate == null || isCompleted() || isCancelled()) return false;
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * 检查是否需要审批
     */
    public boolean requiresApproval() {
        return "Y".equals(approvalRequired);
    }

    /**
     * 检查是否已自动审批
     */
    public boolean isAutoApproved() {
        return "Y".equals(autoApproved);
    }

    /**
     * 检查是否已发送通知
     */
    public boolean isNotificationSent() {
        return "Y".equals(notificationSent);
    }

    /**
     * 检查是否已记录审计日志
     */
    public boolean isAuditLogged() {
        return "Y".equals(auditLogged);
    }

    /**
     * 计算工作流执行时间
     */
    public Integer getExecutionTime() {
        if (startedDate == null) return null;
        LocalDateTime endTime = completedDate != null ? completedDate : LocalDateTime.now();
        return (int) java.time.Duration.between(startedDate, endTime).toMinutes();
    }

    /**
     * 更新工作流状态
     */
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.lastModifiedDate = LocalDateTime.now();
        
        if ("IN_PROGRESS".equals(newStatus) && startedDate == null) {
            this.startedDate = LocalDateTime.now();
        } else if ("COMPLETED".equals(newStatus) && completedDate == null) {
            this.completedDate = LocalDateTime.now();
            if (startedDate != null) {
                this.actualDuration = getExecutionTime();
            }
        }
    }

    /**
     * 设置工作流结果
     */
    public void setResult(String resultData) {
        this.resultData = resultData;
        this.status = "COMPLETED";
        this.completedDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        if (startedDate != null) {
            this.actualDuration = getExecutionTime();
        }
    }

    /**
     * 设置工作流错误
     */
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "FAILED";
        this.completedDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        if (retryCount == null) retryCount = 0;
        retryCount++;
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        if (maxRetryAttempts == null) return true;
        if (retryCount == null) return true;
        return retryCount < maxRetryAttempts;
    }

    /**
     * 获取进度百分比
     */
    public double getProgressPercentage() {
        if (totalSteps == null || totalSteps == 0) return 0.0;
        if (currentStep == null) return 0.0;
        return Math.min(100.0, (currentStep.doubleValue() / totalSteps.doubleValue()) * 100.0);
    }
}
