package com.example.crm.service;

import com.example.crm.entity.AiAgent;
import com.example.crm.entity.AiAgentTask;
import com.example.crm.ai.CrmAiService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI Agent 服务类
 * 处理 AI Agent 的任务分配、执行和状态管理
 */
@ApplicationScoped
public class AiAgentService {

    private static final Logger LOG = Logger.getLogger(AiAgentService.class);

    @Inject
    CrmAiService crmAiService;

    /**
     * 处理工作流任务
     */
    @Transactional
    public String processWorkflowTask(String agentId, String taskDescription, 
                                    String inputData, String conditions) {
        try {
            // 获取AI Agent信息
            AiAgent aiAgent = AiAgent.findById(agentId);
            if (aiAgent == null) {
                throw new RuntimeException("AI Agent不存在: " + agentId);
            }

            if (!aiAgent.isActive()) {
                throw new RuntimeException("AI Agent未激活: " + agentId);
            }

            // 创建AI Agent任务记录
            AiAgentTask task = createAiAgentTask(agentId, taskDescription, inputData);

            // 构建AI处理提示
            String aiPrompt = buildWorkflowTaskPrompt(aiAgent, taskDescription, inputData, conditions);

            // 调用AI服务处理任务
            String result = crmAiService.chat(aiPrompt);

            // 更新任务结果
            task.setResult(result, "工作流任务处理完成", 0.9);
            task.persist();

            // 更新AI Agent统计信息
            aiAgent.updateInteractionStats(true, 1.0);
            aiAgent.persist();

            LOG.info("AI Agent处理工作流任务成功: " + agentId + ", 任务: " + task.taskId);
            return result;

        } catch (Exception e) {
            LOG.error("AI Agent处理工作流任务失败: " + agentId, e);
            throw new RuntimeException("AI Agent处理工作流任务失败: " + e.getMessage());
        }
    }

    /**
     * 创建AI Agent任务
     */
    @Transactional
    public AiAgentTask createAiAgentTask(String agentId, String taskDescription, String inputData) {
        try {
            AiAgentTask task = new AiAgentTask();
            task.taskId = generateTaskId();
            task.agentId = agentId;
            task.taskTitle = "工作流任务处理";
            task.taskDescription = taskDescription;
            task.taskType = "WORKFLOW_TASK";
            task.status = "IN_PROGRESS";
            task.priority = 5;
            task.assignedDate = LocalDateTime.now();
            task.startedDate = LocalDateTime.now();
            task.inputData = inputData;
            task.createdBy = agentId;
            task.createdDate = LocalDateTime.now();
            task.lastModifiedDate = LocalDateTime.now();
            task.lastUpdatedStamp = LocalDateTime.now();
            task.createdStamp = LocalDateTime.now();

            task.persist();
            return task;
        } catch (Exception e) {
            LOG.error("创建AI Agent任务失败", e);
            throw new RuntimeException("创建AI Agent任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI Agent信息
     */
    public AiAgent getAiAgent(String agentId) {
        return AiAgent.findById(agentId);
    }

    /**
     * 获取AI Agent任务列表
     */
    public List<AiAgentTask> getAiAgentTasks(String agentId, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return AiAgentTask.find("agentId = ?1 and status = ?2 order by assignedDate desc", agentId, status).list();
        } else {
            return AiAgentTask.find("agentId = ?1 order by assignedDate desc", agentId).list();
        }
    }

    /**
     * 获取所有激活的AI Agent
     */
    public List<AiAgent> getActiveAiAgents() {
        return AiAgent.find("isActive = 'Y' order by agentName").list();
    }

    /**
     * 根据角色获取AI Agent
     */
    public List<AiAgent> getAiAgentsByRole(String role) {
        return AiAgent.find("position = ?1 and isActive = 'Y'", role).list();
    }

    /**
     * 根据部门获取AI Agent
     */
    public List<AiAgent> getAiAgentsByDepartment(String department) {
        return AiAgent.find("department = ?1 and isActive = 'Y'", department).list();
    }

    /**
     * 检查AI Agent是否可以接受新任务
     */
    public boolean canAcceptNewTask(String agentId) {
        try {
            AiAgent aiAgent = AiAgent.findById(agentId);
            if (aiAgent == null || !aiAgent.isActive()) {
                return false;
            }

            // 检查当前任务数量
            Long currentTaskCount = AiAgentTask.count("agentId = ?1 and status in ('PENDING', 'IN_PROGRESS')", agentId);
            
            if (aiAgent.maxConcurrentTasks != null && currentTaskCount >= aiAgent.maxConcurrentTasks) {
                return false;
            }

            return true;
        } catch (Exception e) {
            LOG.error("检查AI Agent任务接受能力失败: " + agentId, e);
            return false;
        }
    }

    /**
     * 构建工作流任务AI提示
     */
    private String buildWorkflowTaskPrompt(AiAgent aiAgent, String taskDescription, 
                                         String inputData, String conditions) {
        StringBuilder sb = new StringBuilder();
        
        // AI Agent身份信息
        sb.append("你是").append(aiAgent.agentName).append("，");
        sb.append("代表").append(aiAgent.employeeName).append("（").append(aiAgent.position).append("）").append("。\n\n");
        
        // 任务描述
        sb.append("任务描述：\n").append(taskDescription).append("\n\n");
        
        // 输入数据
        sb.append("输入数据：\n").append(inputData).append("\n\n");
        
        // 处理条件
        if (conditions != null && !conditions.trim().isEmpty()) {
            sb.append("处理条件：\n").append(conditions).append("\n\n");
        }
        
        // AI Agent职责和能力
        if (aiAgent.responsibilities != null) {
            sb.append("你的职责：\n").append(aiAgent.responsibilities).append("\n\n");
        }
        
        if (aiAgent.capabilities != null) {
            sb.append("你的能力：\n").append(aiAgent.capabilities).append("\n\n");
        }
        
        // 处理指令
        sb.append("请根据以上信息，以").append(aiAgent.employeeName).append("的身份处理这个任务。");
        sb.append("请提供清晰的处理结果和建议，包括是否批准、需要什么条件、下一步应该怎么做等。");
        sb.append("请用专业、友好的语气回复。");
        
        return sb.toString();
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "AAT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * 更新AI Agent状态
     */
    @Transactional
    public void updateAiAgentStatus(String agentId, String status) {
        try {
            AiAgent aiAgent = AiAgent.findById(agentId);
            if (aiAgent != null) {
                aiAgent.isActive = "Y".equals(status) ? "Y" : "N";
                aiAgent.lastModifiedDate = LocalDateTime.now();
                aiAgent.persist();
                LOG.info("更新AI Agent状态成功: " + agentId + " -> " + status);
            }
        } catch (Exception e) {
            LOG.error("更新AI Agent状态失败: " + agentId, e);
            throw new RuntimeException("更新AI Agent状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI Agent统计信息
     */
    public String getAiAgentStatistics(String agentId) {
        try {
            AiAgent aiAgent = AiAgent.findById(agentId);
            if (aiAgent == null) {
                return "AI Agent不存在: " + agentId;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("AI Agent统计信息:\n");
            sb.append("- Agent名称: ").append(aiAgent.agentName).append("\n");
            sb.append("- 员工: ").append(aiAgent.employeeName).append("\n");
            sb.append("- 职位: ").append(aiAgent.position).append("\n");
            sb.append("- 部门: ").append(aiAgent.department).append("\n");
            sb.append("- 状态: ").append(aiAgent.isActive() ? "激活" : "未激活").append("\n");
            sb.append("- 总交互次数: ").append(aiAgent.totalInteractions != null ? aiAgent.totalInteractions : 0).append("\n");
            sb.append("- 成功率: ").append(aiAgent.successRate != null ? String.format("%.2f%%", aiAgent.successRate * 100) : "0%").append("\n");
            sb.append("- 平均响应时间: ").append(aiAgent.averageResponseTime != null ? String.format("%.2f秒", aiAgent.averageResponseTime) : "0秒").append("\n");

            return sb.toString();
        } catch (Exception e) {
            LOG.error("获取AI Agent统计信息失败: " + agentId, e);
            return "获取统计信息失败: " + e.getMessage();
        }
    }
}