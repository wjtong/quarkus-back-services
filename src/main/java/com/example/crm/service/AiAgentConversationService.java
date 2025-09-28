package com.example.crm.service;

import com.example.crm.entity.AiAgentConversation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI Agent 对话管理服务
 * 负责 AI Agent 与用户的对话记录和管理
 */
@ApplicationScoped
public class AiAgentConversationService {

    private static final Logger LOG = Logger.getLogger(AiAgentConversationService.class);

    /**
     * 开始新对话
     */
    @Transactional
    public AiAgentConversation startConversation(String agentId, String userId, String userName, 
                                               String messageContent, String contextData) {
        try {
            AiAgentConversation conversation = new AiAgentConversation();
            conversation.conversationId = generateConversationId();
            conversation.agentId = agentId;
            conversation.setUserMessage(messageContent, userId, userName);
            conversation.contextData = contextData;
            conversation.language = "zh-CN"; // 默认中文
            conversation.createdStamp = LocalDateTime.now();
            conversation.createdTxStamp = LocalDateTime.now();
            conversation.lastUpdatedStamp = LocalDateTime.now();
            conversation.lastUpdatedTxStamp = LocalDateTime.now();

            conversation.persist();
            LOG.info("开始新对话: " + conversation.conversationId);
            
            return conversation;
        } catch (Exception e) {
            LOG.error("开始对话失败", e);
            throw new RuntimeException("开始对话失败: " + e.getMessage());
        }
    }

    /**
     * 添加 AI 响应
     */
    @Transactional
    public AiAgentConversation addAiResponse(String conversationId, String responseContent, 
                                           Double confidenceScore, String intent, String entities) {
        try {
            AiAgentConversation conversation = AiAgentConversation.findById(conversationId);
            if (conversation == null) {
                throw new IllegalArgumentException("对话不存在: " + conversationId);
            }

            conversation.setAiResponse(responseContent, confidenceScore);
            conversation.intent = intent;
            conversation.entities = entities;
            conversation.lastModifiedDate = LocalDateTime.now();
            conversation.lastUpdatedStamp = LocalDateTime.now();
            conversation.lastUpdatedTxStamp = LocalDateTime.now();

            conversation.persist();
            LOG.info("添加 AI 响应到对话: " + conversationId);
            
            return conversation;
        } catch (Exception e) {
            LOG.error("添加 AI 响应失败", e);
            throw new RuntimeException("添加 AI 响应失败: " + e.getMessage());
        }
    }

    /**
     * 添加对话反馈
     */
    @Transactional
    public AiAgentConversation addFeedback(String conversationId, Integer satisfactionRating, 
                                         String feedback, String sentiment) {
        try {
            AiAgentConversation conversation = AiAgentConversation.findById(conversationId);
            if (conversation == null) {
                throw new IllegalArgumentException("对话不存在: " + conversationId);
            }

            conversation.setFeedback(satisfactionRating, feedback);
            conversation.sentiment = sentiment;
            conversation.lastModifiedDate = LocalDateTime.now();
            conversation.lastUpdatedStamp = LocalDateTime.now();
            conversation.lastUpdatedTxStamp = LocalDateTime.now();

            conversation.persist();
            LOG.info("添加对话反馈: " + conversationId);
            
            return conversation;
        } catch (Exception e) {
            LOG.error("添加对话反馈失败", e);
            throw new RuntimeException("添加对话反馈失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有对话记录
     */
    public List<AiAgentConversation> getAllConversations() {
        return AiAgentConversation.listAll();
    }

    /**
     * 根据 Agent ID 获取对话记录
     */
    public List<AiAgentConversation> getConversationsByAgentId(String agentId) {
        return AiAgentConversation.find("agentId", agentId).list();
    }

    /**
     * 根据用户 ID 获取对话记录
     */
    public List<AiAgentConversation> getConversationsByUserId(String userId) {
        return AiAgentConversation.find("userId", userId).list();
    }

    /**
     * 获取已完成的对话记录
     */
    public List<AiAgentConversation> getCompletedConversations() {
        return AiAgentConversation.find("status", "COMPLETED").list();
    }

    /**
     * 获取进行中的对话记录
     */
    public List<AiAgentConversation> getInProgressConversations() {
        return AiAgentConversation.find("status", "IN_PROGRESS").list();
    }

    /**
     * 获取高满意度对话记录
     */
    public List<AiAgentConversation> getHighSatisfactionConversations(int minRating) {
        return AiAgentConversation.find("satisfactionRating >= ?1", minRating).list();
    }

    /**
     * 获取低满意度对话记录
     */
    public List<AiAgentConversation> getLowSatisfactionConversations(int maxRating) {
        return AiAgentConversation.find("satisfactionRating <= ?1", maxRating).list();
    }

    /**
     * 获取对话统计信息
     */
    public String getConversationStatistics() {
        try {
            List<AiAgentConversation> allConversations = getAllConversations();
            
            long totalConversations = allConversations.size();
            long completedConversations = allConversations.stream()
                .filter(AiAgentConversation::isCompleted).count();
            long inProgressConversations = allConversations.stream()
                .filter(AiAgentConversation::isInProgress).count();

            double avgResponseTime = allConversations.stream()
                .filter(c -> c.responseTime != null)
                .mapToInt(c -> c.responseTime)
                .average()
                .orElse(0.0);

            double avgConfidence = allConversations.stream()
                .filter(c -> c.confidenceScore != null)
                .mapToDouble(c -> c.confidenceScore)
                .average()
                .orElse(0.0);

            double avgSatisfaction = allConversations.stream()
                .filter(c -> c.satisfactionRating != null)
                .mapToInt(c -> c.satisfactionRating)
                .average()
                .orElse(0.0);

            long positiveSentiment = allConversations.stream()
                .filter(c -> "POSITIVE".equals(c.sentiment))
                .count();
            long negativeSentiment = allConversations.stream()
                .filter(c -> "NEGATIVE".equals(c.sentiment))
                .count();
            long neutralSentiment = allConversations.stream()
                .filter(c -> "NEUTRAL".equals(c.sentiment))
                .count();

            StringBuilder stats = new StringBuilder();
            stats.append("AI Agent 对话统计信息:\n");
            stats.append("总对话数: ").append(totalConversations).append("\n");
            stats.append("已完成: ").append(completedConversations).append("\n");
            stats.append("进行中: ").append(inProgressConversations).append("\n");
            stats.append("平均响应时间: ").append(String.format("%.2f秒", avgResponseTime)).append("\n");
            stats.append("平均置信度: ").append(String.format("%.2f", avgConfidence)).append("\n");
            stats.append("平均满意度: ").append(String.format("%.2f", avgSatisfaction)).append("\n");
            stats.append("积极情感: ").append(positiveSentiment).append("\n");
            stats.append("消极情感: ").append(negativeSentiment).append("\n");
            stats.append("中性情感: ").append(neutralSentiment).append("\n");

            return stats.toString();
        } catch (Exception e) {
            LOG.error("获取对话统计信息失败", e);
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 获取 Agent 对话统计信息
     */
    public String getAgentConversationStatistics(String agentId) {
        try {
            List<AiAgentConversation> conversations = getConversationsByAgentId(agentId);
            
            long totalConversations = conversations.size();
            long completedConversations = conversations.stream()
                .filter(AiAgentConversation::isCompleted).count();

            double avgResponseTime = conversations.stream()
                .filter(c -> c.responseTime != null)
                .mapToInt(c -> c.responseTime)
                .average()
                .orElse(0.0);

            double avgConfidence = conversations.stream()
                .filter(c -> c.confidenceScore != null)
                .mapToDouble(c -> c.confidenceScore)
                .average()
                .orElse(0.0);

            double avgSatisfaction = conversations.stream()
                .filter(c -> c.satisfactionRating != null)
                .mapToInt(c -> c.satisfactionRating)
                .average()
                .orElse(0.0);

            StringBuilder stats = new StringBuilder();
            stats.append("Agent ").append(agentId).append(" 对话统计:\n");
            stats.append("总对话数: ").append(totalConversations).append("\n");
            stats.append("已完成: ").append(completedConversations).append("\n");
            stats.append("平均响应时间: ").append(String.format("%.2f秒", avgResponseTime)).append("\n");
            stats.append("平均置信度: ").append(String.format("%.2f", avgConfidence)).append("\n");
            stats.append("平均满意度: ").append(String.format("%.2f", avgSatisfaction)).append("\n");

            return stats.toString();
        } catch (Exception e) {
            LOG.error("获取 Agent 对话统计信息失败", e);
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 清理过期对话记录
     */
    @Transactional
    public int cleanupOldConversations(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            List<AiAgentConversation> oldConversations = AiAgentConversation.find(
                "createdDate < ?1", cutoffDate).list();
            
            int deletedCount = 0;
            for (AiAgentConversation conversation : oldConversations) {
                conversation.delete();
                deletedCount++;
            }
            
            LOG.info("清理了 " + deletedCount + " 条过期对话记录");
            return deletedCount;
        } catch (Exception e) {
            LOG.error("清理过期对话记录失败", e);
            throw new RuntimeException("清理过期对话记录失败: " + e.getMessage());
        }
    }

    /**
     * 生成唯一的对话 ID
     */
    private String generateConversationId() {
        return "CONV_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
