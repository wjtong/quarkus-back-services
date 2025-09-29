package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 工作流执行记录实体类
 * 记录工作流实例中每个步骤的执行情况
 */
@Entity
@Table(name = "workflow_execution")
public class WorkflowExecution extends PanacheEntityBase {

    @Id
    @NotBlank(message = "工作流执行ID不能为空")
    @Size(max = 60, message = "工作流执行ID长度不能超过60个字符")
    @Column(name = "workflow_execution_id", length = 60, nullable = false)
    public String workflowExecutionId;

    @NotBlank(message = "工作流实例ID不能为空")
    @Size(max = 60, message = "工作流实例ID长度不能超过60个字符")
    @Column(name = "workflow_instance_id", length = 60, nullable = false)
    public String workflowInstanceId;

    @NotBlank(message = "工作流步骤ID不能为空")
    @Size(max = 60, message = "工作流步骤ID长度不能超过60个字符")
    @Column(name = "workflow_step_id", length = 60, nullable = false)
    public String workflowStepId;

    @Size(max = 60, message = "执行人ID长度不能超过60个字符")
    @Column(name = "executor_id", length = 60)
    public String executorId;

    @Size(max = 100, message = "执行人姓名长度不能超过100个字符")
    @Column(name = "executor_name", length = 100)
    public String executorName;

    @Size(max = 50, message = "执行状态长度不能超过50个字符")
    @Column(name = "execution_status", length = 50)
    public String executionStatus;

    @Column(name = "execution_order")
    public Integer executionOrder;

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

    @Column(name = "input_data", columnDefinition = "text")
    public String inputData;

    @Column(name = "output_data", columnDefinition = "text")
    public String outputData;

    @Column(name = "execution_result", columnDefinition = "text")
    public String executionResult;

    @Column(name = "error_message", columnDefinition = "text")
    public String errorMessage;

    @Column(name = "retry_count")
    public Integer retryCount;

    @Column(name = "confidence_score")
    public Double confidenceScore;

    @Column(name = "quality_rating")
    public Double qualityRating;

    @Column(name = "feedback", columnDefinition = "text")
    public String feedback;

    @Column(name = "ai_agent_used", length = 1)
    public String aiAgentUsed;

    @Size(max = 60, message = "AI Agent ID长度不能超过60个字符")
    @Column(name = "ai_agent_id", length = 60)
    public String aiAgentId;

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

    // 关联的工作流实例（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", referencedColumnName = "workflow_instance_id", insertable = false, updatable = false)
    public WorkflowInstance workflowInstance;

    // 关联的工作流步骤（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_step_id", referencedColumnName = "workflow_step_id", insertable = false, updatable = false)
    public WorkflowStep workflowStep;

    // 关联的执行人（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", referencedColumnName = "party_id", insertable = false, updatable = false)
    public Party executor;

    // 关联的AI Agent（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_agent_id", referencedColumnName = "agent_id", insertable = false, updatable = false)
    public AiAgent aiAgent;

    /**
     * 获取执行记录显示名称
     */
    public String getDisplayName() {
        return workflowExecutionId;
    }

    /**
     * 检查执行是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(executionStatus);
    }

    /**
     * 检查执行是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(executionStatus);
    }

    /**
     * 检查执行是否进行中
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(executionStatus);
    }

    /**
     * 检查执行是否待处理
     */
    public boolean isPending() {
        return "PENDING".equals(executionStatus);
    }

    /**
     * 检查执行是否已取消
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(executionStatus);
    }

    /**
     * 检查执行是否已过期
     */
    public boolean isOverdue() {
        if (dueDate == null || isCompleted() || isCancelled()) return false;
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * 检查是否使用了AI Agent
     */
    public boolean isAiAgentUsed() {
        return "Y".equals(aiAgentUsed);
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
     * 计算执行时间
     */
    public Integer getExecutionTime() {
        if (startedDate == null) return null;
        LocalDateTime endTime = completedDate != null ? completedDate : LocalDateTime.now();
        return (int) java.time.Duration.between(startedDate, endTime).toMinutes();
    }

    /**
     * 更新执行状态
     */
    public void updateStatus(String newStatus) {
        this.executionStatus = newStatus;
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
     * 设置执行结果
     */
    public void setResult(String outputData, String executionResult, Double confidenceScore) {
        this.outputData = outputData;
        this.executionResult = executionResult;
        this.confidenceScore = confidenceScore;
        this.executionStatus = "COMPLETED";
        this.completedDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        if (startedDate != null) {
            this.actualDuration = getExecutionTime();
        }
    }

    /**
     * 设置执行错误
     */
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.executionStatus = "FAILED";
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
     * 设置AI Agent信息
     */
    public void setAiAgentInfo(String aiAgentId) {
        this.aiAgentId = aiAgentId;
        this.aiAgentUsed = "Y";
        this.lastModifiedDate = LocalDateTime.now();
    }
}
