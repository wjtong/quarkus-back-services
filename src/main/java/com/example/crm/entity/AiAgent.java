package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * AI Agent 实体类
 * 代表每个员工的专属 AI Agent，包含职位、职责、权限等信息
 */
@Entity
@Table(name = "ai_agent")
public class AiAgent extends PanacheEntityBase {

    @Id
    @NotBlank(message = "AI Agent ID 不能为空")
    @Size(max = 60, message = "AI Agent ID 长度不能超过60个字符")
    @Column(name = "agent_id", length = 60, nullable = false)
    public String agentId;

    @NotBlank(message = "Agent 名称不能为空")
    @Size(max = 100, message = "Agent 名称长度不能超过100个字符")
    @Column(name = "agent_name", length = 100, nullable = false)
    public String agentName;

    @Size(max = 255, message = "Agent 描述长度不能超过255个字符")
    @Column(name = "description", length = 255)
    public String description;

    @NotBlank(message = "所属员工 ID 不能为空")
    @Size(max = 60, message = "员工 ID 长度不能超过60个字符")
    @Column(name = "employee_id", length = 60, nullable = false)
    public String employeeId;

    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 100, message = "员工姓名长度不能超过100个字符")
    @Column(name = "employee_name", length = 100, nullable = false)
    public String employeeName;

    @NotBlank(message = "职位不能为空")
    @Size(max = 100, message = "职位长度不能超过100个字符")
    @Column(name = "position", length = 100, nullable = false)
    public String position;

    @NotBlank(message = "部门不能为空")
    @Size(max = 100, message = "部门长度不能超过100个字符")
    @Column(name = "department", length = 100, nullable = false)
    public String department;

    @Column(name = "responsibilities", columnDefinition = "text")
    public String responsibilities;

    @Column(name = "permissions", columnDefinition = "text")
    public String permissions;

    @Column(name = "capabilities", columnDefinition = "text")
    public String capabilities;

    @Column(name = "personality_traits", columnDefinition = "text")
    public String personalityTraits;

    @Column(name = "communication_style", columnDefinition = "text")
    public String communicationStyle;

    @Column(name = "knowledge_base", columnDefinition = "text")
    public String knowledgeBase;

    @Column(name = "work_schedule", columnDefinition = "text")
    public String workSchedule;

    @Column(name = "priority_level")
    public Integer priorityLevel;

    @Column(name = "max_concurrent_tasks")
    public Integer maxConcurrentTasks;

    @Column(name = "response_timeout")
    public Integer responseTimeout;

    @Column(name = "is_active", length = 1)
    public String isActive;

    @Column(name = "is_learning_enabled", length = 1)
    public String isLearningEnabled;

    @Column(name = "learning_rate")
    public Double learningRate;

    @Column(name = "confidence_threshold")
    public Double confidenceThreshold;

    @Column(name = "last_training_date")
    public LocalDateTime lastTrainingDate;

    @Column(name = "total_interactions")
    public Long totalInteractions;

    @Column(name = "success_rate")
    public Double successRate;

    @Column(name = "average_response_time")
    public Double averageResponseTime;

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

    // 关联的员工信息（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "party_id", insertable = false, updatable = false)
    public Party employee;

    // 关联的 AI Agent 任务（一对多关系）
    // 注意：由于循环依赖，这里暂时注释掉，在实际使用时通过查询获取
    // @OneToMany(mappedBy = "aiAgent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // public List<AiAgentTask> tasks;

    // 关联的 AI Agent 对话记录（一对多关系）
    // 注意：由于循环依赖，这里暂时注释掉，在实际使用时通过查询获取
    // @OneToMany(mappedBy = "aiAgent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // public List<AiAgentConversation> conversations;

    /**
     * 获取 Agent 显示名称
     */
    public String getDisplayName() {
        return agentName != null ? agentName : agentId;
    }

    /**
     * 检查 Agent 是否激活
     */
    public boolean isActive() {
        return "Y".equals(isActive);
    }

    /**
     * 检查是否启用学习功能
     */
    public boolean isLearningEnabled() {
        return "Y".equals(isLearningEnabled);
    }

    /**
     * 获取 Agent 完整描述
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("AI Agent: ").append(agentName).append("\n");
        sb.append("员工: ").append(employeeName).append("\n");
        sb.append("职位: ").append(position).append("\n");
        sb.append("部门: ").append(department).append("\n");
        if (description != null) {
            sb.append("描述: ").append(description).append("\n");
        }
        if (responsibilities != null) {
            sb.append("职责: ").append(responsibilities).append("\n");
        }
        return sb.toString();
    }

    /**
     * 检查是否有特定权限
     */
    public boolean hasPermission(String permission) {
        if (permissions == null) return false;
        return permissions.contains(permission);
    }

    /**
     * 检查是否有特定能力
     */
    public boolean hasCapability(String capability) {
        if (capabilities == null) return false;
        return capabilities.contains(capability);
    }

    /**
     * 更新交互统计
     */
    public void updateInteractionStats(boolean success, double responseTime) {
        if (totalInteractions == null) totalInteractions = 0L;
        totalInteractions++;
        
        if (successRate == null) successRate = 0.0;
        if (averageResponseTime == null) averageResponseTime = 0.0;
        
        // 更新成功率
        double totalSuccess = successRate * (totalInteractions - 1);
        if (success) totalSuccess++;
        successRate = totalSuccess / totalInteractions;
        
        // 更新平均响应时间
        double totalTime = averageResponseTime * (totalInteractions - 1);
        totalTime += responseTime;
        averageResponseTime = totalTime / totalInteractions;
        
        lastUpdatedStamp = LocalDateTime.now();
    }

    /**
     * 检查是否可以接受新任务
     * 注意：由于循环依赖，这里暂时返回 true，实际使用时通过服务层查询
     */
    public boolean canAcceptNewTask() {
        if (!isActive()) return false;
        if (maxConcurrentTasks == null) return true;
        
        // 实际使用时应该通过 AiAgentTaskService 查询当前任务数量
        // 这里暂时返回 true，避免循环依赖
        return true;
    }
}
