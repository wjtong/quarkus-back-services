package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * AI Agent 对话记录实体类
 * 记录 AI Agent 与用户的对话历史
 */
@Entity
@Table(name = "ai_agent_conversation")
public class AiAgentConversation extends PanacheEntityBase {

    @Id
    @NotBlank(message = "对话 ID 不能为空")
    @Size(max = 60, message = "对话 ID 长度不能超过60个字符")
    @Column(name = "conversation_id", length = 60, nullable = false)
    public String conversationId;

    @NotBlank(message = "AI Agent ID 不能为空")
    @Size(max = 60, message = "AI Agent ID 长度不能超过60个字符")
    @Column(name = "agent_id", length = 60, nullable = false)
    public String agentId;

    @NotBlank(message = "用户 ID 不能为空")
    @Size(max = 60, message = "用户 ID 长度不能超过60个字符")
    @Column(name = "user_id", length = 60, nullable = false)
    public String userId;

    @Size(max = 100, message = "用户姓名长度不能超过100个字符")
    @Column(name = "user_name", length = 100)
    public String userName;

    @NotBlank(message = "消息类型不能为空")
    @Size(max = 20, message = "消息类型长度不能超过20个字符")
    @Column(name = "message_type", length = 20, nullable = false)
    public String messageType;

    @Column(name = "message_content", columnDefinition = "text", nullable = false)
    public String messageContent;

    @Column(name = "message_timestamp")
    public LocalDateTime messageTimestamp;

    @Column(name = "response_content", columnDefinition = "text")
    public String responseContent;

    @Column(name = "response_timestamp")
    public LocalDateTime responseTimestamp;

    @Column(name = "response_time")
    public Integer responseTime;

    @Column(name = "confidence_score")
    public Double confidenceScore;

    @Column(name = "satisfaction_rating")
    public Integer satisfactionRating;

    @Column(name = "feedback", columnDefinition = "text")
    public String feedback;

    @Size(max = 50, message = "对话状态长度不能超过50个字符")
    @Column(name = "status", length = 50)
    public String status;

    @Column(name = "context_data", columnDefinition = "text")
    public String contextData;

    @Column(name = "intent", length = 100)
    public String intent;

    @Column(name = "entities", columnDefinition = "text")
    public String entities;

    @Column(name = "sentiment", length = 20)
    public String sentiment;

    @Column(name = "language", length = 10)
    public String language;

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
     * 获取对话显示名称
     */
    public String getDisplayName() {
        return conversationId;
    }

    /**
     * 检查是否为用户消息
     */
    public boolean isUserMessage() {
        return "USER".equals(messageType);
    }

    /**
     * 检查是否为 AI 响应
     */
    public boolean isAiResponse() {
        return "AI".equals(messageType);
    }

    /**
     * 检查对话是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * 检查对话是否进行中
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    /**
     * 计算响应时间
     */
    public Integer calculateResponseTime() {
        if (messageTimestamp == null || responseTimestamp == null) return null;
        return (int) java.time.Duration.between(messageTimestamp, responseTimestamp).toSeconds();
    }

    /**
     * 设置用户消息
     */
    public void setUserMessage(String content, String userId, String userName) {
        this.messageType = "USER";
        this.messageContent = content;
        this.userId = userId;
        this.userName = userName;
        this.messageTimestamp = LocalDateTime.now();
        this.status = "IN_PROGRESS";
        this.createdDate = LocalDateTime.now();
    }

    /**
     * 设置 AI 响应
     */
    public void setAiResponse(String content, Double confidenceScore) {
        this.responseContent = content;
        this.responseTimestamp = LocalDateTime.now();
        this.confidenceScore = confidenceScore;
        this.responseTime = calculateResponseTime();
        this.status = "COMPLETED";
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * 设置对话反馈
     */
    public void setFeedback(Integer satisfactionRating, String feedback) {
        this.satisfactionRating = satisfactionRating;
        this.feedback = feedback;
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * 获取对话摘要
     */
    public String getConversationSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("用户: ").append(userName != null ? userName : userId).append("\n");
        sb.append("消息: ").append(messageContent).append("\n");
        if (responseContent != null) {
            sb.append("AI响应: ").append(responseContent).append("\n");
        }
        if (confidenceScore != null) {
            sb.append("置信度: ").append(String.format("%.2f", confidenceScore)).append("\n");
        }
        if (responseTime != null) {
            sb.append("响应时间: ").append(responseTime).append("秒\n");
        }
        return sb.toString();
    }
}
