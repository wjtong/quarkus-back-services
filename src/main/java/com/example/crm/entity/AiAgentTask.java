package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * AI Agent 任务实体类
 * 记录 AI Agent 执行的任务信息
 */
@Entity
@Table(name = "ai_agent_task")
public class AiAgentTask extends PanacheEntityBase {

    @Id
    @NotBlank(message = "任务 ID 不能为空")
    @Size(max = 60, message = "任务 ID 长度不能超过60个字符")
    @Column(name = "task_id", length = 60, nullable = false)
    public String taskId;

    @NotBlank(message = "AI Agent ID 不能为空")
    @Size(max = 60, message = "AI Agent ID 长度不能超过60个字符")
    @Column(name = "agent_id", length = 60, nullable = false)
    public String agentId;

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过200个字符")
    @Column(name = "task_title", length = 200, nullable = false)
    public String taskTitle;

    @Column(name = "task_description", columnDefinition = "text")
    public String taskDescription;

    @NotBlank(message = "任务类型不能为空")
    @Size(max = 50, message = "任务类型长度不能超过50个字符")
    @Column(name = "task_type", length = 50, nullable = false)
    public String taskType;

    @Size(max = 50, message = "任务状态长度不能超过50个字符")
    @Column(name = "status", length = 50)
    public String status;

    @Column(name = "priority")
    public Integer priority;

    @Column(name = "assigned_date")
    public LocalDateTime assignedDate;

    @Column(name = "started_date")
    public LocalDateTime startedDate;

    @Column(name = "completed_date")
    public LocalDateTime completedDate;

    @Column(name = "due_date")
    public LocalDateTime dueDate;

    @Column(name = "estimated_duration")
    public Integer estimatedDuration;

    @Column(name = "actual_duration")
    public Integer actualDuration;

    @Column(name = "input_data", columnDefinition = "text")
    public String inputData;

    @Column(name = "output_data", columnDefinition = "text")
    public String outputData;

    @Column(name = "result_summary", columnDefinition = "text")
    public String resultSummary;

    @Column(name = "error_message", columnDefinition = "text")
    public String errorMessage;

    @Column(name = "confidence_score")
    public Double confidenceScore;

    @Column(name = "quality_rating")
    public Double qualityRating;

    @Column(name = "feedback", columnDefinition = "text")
    public String feedback;

    @Size(max = 60, message = "创建者 ID 长度不能超过60个字符")
    @Column(name = "created_by", length = 60)
    public String createdBy;

    @Size(max = 60, message = "分配者 ID 长度不能超过60个字符")
    @Column(name = "assigned_by", length = 60)
    public String assignedBy;

    @Column(name = "created_date")
    public LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    public LocalDateTime lastModifiedDate;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    // 关联的 AI Agent（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", referencedColumnName = "agent_id", insertable = false, updatable = false)
    public AiAgent aiAgent;

    /**
     * 获取任务显示名称
     */
    public String getDisplayName() {
        return taskTitle != null ? taskTitle : taskId;
    }

    /**
     * 检查任务是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * 检查任务是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    /**
     * 检查任务是否进行中
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    /**
     * 检查任务是否待处理
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * 检查任务是否已过期
     */
    public boolean isOverdue() {
        if (dueDate == null || isCompleted()) return false;
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * 计算任务执行时间
     */
    public Integer getExecutionTime() {
        if (startedDate == null) return null;
        LocalDateTime endTime = completedDate != null ? completedDate : LocalDateTime.now();
        return (int) java.time.Duration.between(startedDate, endTime).toMinutes();
    }

    /**
     * 更新任务状态
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
     * 设置任务结果
     */
    public void setResult(String outputData, String resultSummary, Double confidenceScore) {
        this.outputData = outputData;
        this.resultSummary = resultSummary;
        this.confidenceScore = confidenceScore;
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * 设置任务错误
     */
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "FAILED";
        this.completedDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }
}
