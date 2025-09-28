package com.example.crm.service;

import com.example.crm.entity.AiAgent;
import com.example.crm.entity.AiAgentTask;
import com.example.crm.entity.AiAgentConversation;
import com.example.crm.entity.Party;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AI Agent 管理服务
 * 负责 AI Agent 的创建、管理和任务分配
 */
@ApplicationScoped
public class AiAgentService {

    private static final Logger LOG = Logger.getLogger(AiAgentService.class);

    // 注意：由于循环依赖，这里暂时注释掉，在实际使用时通过直接调用服务
    // @Inject
    // AiAgentTaskService taskService;

    // @Inject
    // AiAgentConversationService conversationService;

    /**
     * 为员工创建专属 AI Agent
     */
    @Transactional
    public AiAgent createAgentForEmployee(String employeeId, String agentName, String position, 
                                        String department, String responsibilities, String permissions) {
        try {
            // 检查员工是否存在
            Party employee = Party.findById(employeeId);
            if (employee == null) {
                throw new IllegalArgumentException("员工不存在: " + employeeId);
            }

            // 检查是否已存在该员工的 AI Agent
            AiAgent existingAgent = AiAgent.find("employeeId", employeeId).firstResult();
            if (existingAgent != null) {
                throw new IllegalArgumentException("该员工已存在 AI Agent: " + existingAgent.agentId);
            }

            // 创建新的 AI Agent
            AiAgent agent = new AiAgent();
            agent.agentId = generateAgentId();
            agent.agentName = agentName;
            agent.employeeId = employeeId;
            agent.employeeName = employee.getDisplayName();
            agent.position = position;
            agent.department = department;
            agent.responsibilities = responsibilities;
            agent.permissions = permissions;
            
            // 设置默认值
            agent.isActive = "Y";
            agent.isLearningEnabled = "Y";
            agent.priorityLevel = 5;
            agent.maxConcurrentTasks = 3;
            agent.responseTimeout = 30;
            agent.learningRate = 0.1;
            agent.confidenceThreshold = 0.7;
            agent.totalInteractions = 0L;
            agent.successRate = 0.0;
            agent.averageResponseTime = 0.0;
            
            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            agent.createdDate = now;
            agent.createdStamp = now;
            agent.createdTxStamp = now;
            agent.lastUpdatedStamp = now;
            agent.lastUpdatedTxStamp = now;

            agent.persist();
            LOG.info("为员工 " + employeeId + " 创建 AI Agent: " + agent.agentId);
            
            return agent;
        } catch (Exception e) {
            LOG.error("创建 AI Agent 失败", e);
            throw new RuntimeException("创建 AI Agent 失败: " + e.getMessage());
        }
    }

    /**
     * 更新 AI Agent 信息
     */
    @Transactional
    public AiAgent updateAgent(String agentId, String agentName, String position, 
                             String department, String responsibilities, String permissions,
                             String capabilities, String personalityTraits, String communicationStyle) {
        try {
            AiAgent agent = AiAgent.findById(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("AI Agent 不存在: " + agentId);
            }

            // 更新基本信息
            if (agentName != null) agent.agentName = agentName;
            if (position != null) agent.position = position;
            if (department != null) agent.department = department;
            if (responsibilities != null) agent.responsibilities = responsibilities;
            if (permissions != null) agent.permissions = permissions;
            if (capabilities != null) agent.capabilities = capabilities;
            if (personalityTraits != null) agent.personalityTraits = personalityTraits;
            if (communicationStyle != null) agent.communicationStyle = communicationStyle;

            // 更新时间戳
            LocalDateTime now = LocalDateTime.now();
            agent.lastModifiedDate = now;
            agent.lastUpdatedStamp = now;
            agent.lastUpdatedTxStamp = now;

            agent.persist();
            LOG.info("更新 AI Agent: " + agentId);
            
            return agent;
        } catch (Exception e) {
            LOG.error("更新 AI Agent 失败", e);
            throw new RuntimeException("更新 AI Agent 失败: " + e.getMessage());
        }
    }

    /**
     * 激活/停用 AI Agent
     */
    @Transactional
    public AiAgent toggleAgentStatus(String agentId, boolean active) {
        try {
            AiAgent agent = AiAgent.findById(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("AI Agent 不存在: " + agentId);
            }

            agent.isActive = active ? "Y" : "N";
            agent.lastModifiedDate = LocalDateTime.now();
            agent.lastUpdatedStamp = LocalDateTime.now();
            agent.lastUpdatedTxStamp = LocalDateTime.now();

            agent.persist();
            LOG.info("AI Agent " + agentId + " 状态变更为: " + (active ? "激活" : "停用"));
            
            return agent;
        } catch (Exception e) {
            LOG.error("更新 AI Agent 状态失败", e);
            throw new RuntimeException("更新 AI Agent 状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有 AI Agent
     */
    public List<AiAgent> getAllAgents() {
        return AiAgent.listAll();
    }

    /**
     * 根据员工 ID 获取 AI Agent
     */
    public Optional<AiAgent> getAgentByEmployeeId(String employeeId) {
        return Optional.ofNullable(AiAgent.find("employeeId", employeeId).firstResult());
    }

    /**
     * 根据部门获取 AI Agent 列表
     */
    public List<AiAgent> getAgentsByDepartment(String department) {
        return AiAgent.find("department", department).list();
    }

    /**
     * 获取活跃的 AI Agent 列表
     */
    public List<AiAgent> getActiveAgents() {
        return AiAgent.find("isActive", "Y").list();
    }

    /**
     * 为 AI Agent 分配任务
     * 注意：这里暂时返回 null，实际使用时应该调用 AiAgentTaskService
     */
    @Transactional
    public AiAgentTask assignTaskToAgent(String agentId, String taskTitle, String taskDescription, 
                                       String taskType, String inputData, Integer priority) {
        try {
            AiAgent agent = AiAgent.findById(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("AI Agent 不存在: " + agentId);
            }

            if (!agent.canAcceptNewTask()) {
                throw new IllegalStateException("AI Agent " + agentId + " 当前无法接受新任务");
            }

            // 实际使用时应该调用 taskService.createTask()
            // 这里暂时返回 null，避免循环依赖
            LOG.info("为 AI Agent " + agentId + " 分配任务: " + taskTitle);
            return null;
        } catch (Exception e) {
            LOG.error("为 AI Agent 分配任务失败", e);
            throw new RuntimeException("分配任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取 AI Agent 的任务列表
     */
    public List<AiAgentTask> getAgentTasks(String agentId) {
        return AiAgentTask.find("agentId", agentId).list();
    }

    /**
     * 获取 AI Agent 的对话历史
     */
    public List<AiAgentConversation> getAgentConversations(String agentId) {
        return AiAgentConversation.find("agentId", agentId).list();
    }

    /**
     * 训练 AI Agent
     */
    @Transactional
    public AiAgent trainAgent(String agentId, String trainingData) {
        try {
            AiAgent agent = AiAgent.findById(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("AI Agent 不存在: " + agentId);
            }

            // 更新训练信息
            agent.lastTrainingDate = LocalDateTime.now();
            agent.knowledgeBase = trainingData;
            agent.lastModifiedDate = LocalDateTime.now();
            agent.lastUpdatedStamp = LocalDateTime.now();
            agent.lastUpdatedTxStamp = LocalDateTime.now();

            agent.persist();
            LOG.info("AI Agent " + agentId + " 训练完成");
            
            return agent;
        } catch (Exception e) {
            LOG.error("训练 AI Agent 失败", e);
            throw new RuntimeException("训练 AI Agent 失败: " + e.getMessage());
        }
    }

    /**
     * 获取 AI Agent 统计信息
     */
    public String getAgentStatistics(String agentId) {
        try {
            AiAgent agent = AiAgent.findById(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("AI Agent 不存在: " + agentId);
            }

            List<AiAgentTask> tasks = getAgentTasks(agentId);
            List<AiAgentConversation> conversations = getAgentConversations(agentId);

            long completedTasks = tasks.stream().filter(AiAgentTask::isCompleted).count();
            long failedTasks = tasks.stream().filter(AiAgentTask::isFailed).count();
            long pendingTasks = tasks.stream().filter(AiAgentTask::isPending).count();

            double avgSatisfaction = conversations.stream()
                .filter(c -> c.satisfactionRating != null)
                .mapToInt(c -> c.satisfactionRating)
                .average()
                .orElse(0.0);

            StringBuilder stats = new StringBuilder();
            stats.append("AI Agent 统计信息:\n");
            stats.append("Agent ID: ").append(agent.agentId).append("\n");
            stats.append("员工: ").append(agent.employeeName).append("\n");
            stats.append("职位: ").append(agent.position).append("\n");
            stats.append("部门: ").append(agent.department).append("\n");
            stats.append("状态: ").append(agent.isActive() ? "激活" : "停用").append("\n");
            stats.append("总交互次数: ").append(agent.totalInteractions).append("\n");
            stats.append("成功率: ").append(String.format("%.2f%%", agent.successRate * 100)).append("\n");
            stats.append("平均响应时间: ").append(String.format("%.2f秒", agent.averageResponseTime)).append("\n");
            stats.append("完成任务: ").append(completedTasks).append("\n");
            stats.append("失败任务: ").append(failedTasks).append("\n");
            stats.append("待处理任务: ").append(pendingTasks).append("\n");
            stats.append("平均满意度: ").append(String.format("%.2f", avgSatisfaction)).append("\n");

            return stats.toString();
        } catch (Exception e) {
            LOG.error("获取 AI Agent 统计信息失败", e);
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 生成唯一的 Agent ID
     */
    private String generateAgentId() {
        return "AGENT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
