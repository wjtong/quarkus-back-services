package com.example.crm.ai;

import com.example.crm.entity.WorkflowInstance;
import com.example.crm.entity.WorkflowType;
import com.example.crm.entity.AiAgent;
import com.example.crm.service.WorkflowService;
import com.example.crm.service.AiAgentService;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * 工作流AI服务类
 * 为AI Agent提供工作流处理能力
 */
@ApplicationScoped
public class WorkflowAiService {

    private static final Logger LOG = Logger.getLogger(WorkflowAiService.class);

    @Inject
    WorkflowService workflowService;

    @Inject
    AiAgentService aiAgentService;

    /**
     * 发起工作流申请
     */
    @Tool("发起工作流申请，包括报销申请、请假申请等")
    public String initiateWorkflow(String workflowTypeId, String initiatorId, 
                                  String title, String description, String requestData) {
        try {
            LOG.info("AI Agent发起工作流申请: " + workflowTypeId + " by " + initiatorId);
            
            // 创建工作流实例
            WorkflowInstance instance = workflowService.createWorkflowInstance(
                workflowTypeId, initiatorId, title, description, requestData);
            
            return String.format("工作流申请已成功发起！\n" +
                "申请ID: %s\n" +
                "工作流类型: %s\n" +
                "标题: %s\n" +
                "状态: %s\n" +
                "当前步骤: %d/%d\n" +
                "创建时间: %s\n\n" +
                "系统将自动处理您的申请，请耐心等待处理结果。",
                instance.workflowInstanceId,
                instance.workflowTypeId,
                instance.title,
                instance.status,
                instance.currentStep,
                instance.totalSteps,
                instance.createdDate);
                
        } catch (Exception e) {
            LOG.error("发起工作流申请失败", e);
            return "发起工作流申请失败: " + e.getMessage();
        }
    }

    /**
     * 查询工作流状态
     */
    @Tool("查询工作流申请的处理状态和进度")
    public String queryWorkflowStatus(String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return "未找到工作流实例: " + workflowInstanceId;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("工作流状态查询结果:\n");
            sb.append("申请ID: ").append(instance.workflowInstanceId).append("\n");
            sb.append("工作流类型: ").append(instance.workflowTypeId).append("\n");
            sb.append("标题: ").append(instance.title).append("\n");
            sb.append("发起人: ").append(instance.initiatorName).append("\n");
            sb.append("状态: ").append(instance.status).append("\n");
            sb.append("进度: ").append(instance.currentStep).append("/").append(instance.totalSteps);
            sb.append(" (").append(String.format("%.1f", instance.getProgressPercentage())).append("%)\n");
            sb.append("创建时间: ").append(instance.createdDate).append("\n");
            
            if (instance.startedDate != null) {
                sb.append("开始时间: ").append(instance.startedDate).append("\n");
            }
            
            if (instance.completedDate != null) {
                sb.append("完成时间: ").append(instance.completedDate).append("\n");
            }
            
            if (instance.dueDate != null) {
                sb.append("截止时间: ").append(instance.dueDate).append("\n");
            }
            
            if (instance.isOverdue()) {
                sb.append("⚠️ 注意：此申请已超时！\n");
            }
            
            if (instance.resultData != null) {
                sb.append("处理结果: ").append(instance.resultData).append("\n");
            }
            
            if (instance.errorMessage != null) {
                sb.append("错误信息: ").append(instance.errorMessage).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("查询工作流状态失败", e);
            return "查询工作流状态失败: " + e.getMessage();
        }
    }

    /**
     * 获取可用的工作流类型
     */
    @Tool("获取系统中可用的工作流类型列表")
    public String getAvailableWorkflowTypes() {
        try {
            List<WorkflowType> workflowTypes = workflowService.getActiveWorkflowTypes();
            if (workflowTypes.isEmpty()) {
                return "当前没有可用的工作流类型";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("可用的工作流类型:\n");
            for (WorkflowType type : workflowTypes) {
                sb.append("- ").append(type.workflowTypeId).append(": ").append(type.workflowTypeName);
                if (type.description != null) {
                    sb.append(" (").append(type.description).append(")");
                }
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("获取工作流类型失败", e);
            return "获取工作流类型失败: " + e.getMessage();
        }
    }

    /**
     * 获取工作流执行历史
     */
    @Tool("获取工作流申请的详细执行历史")
    public String getWorkflowExecutionHistory(String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return "未找到工作流实例: " + workflowInstanceId;
            }

            var executions = workflowService.getWorkflowExecutions(workflowInstanceId);
            if (executions.isEmpty()) {
                return "工作流实例 " + workflowInstanceId + " 暂无执行记录";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("工作流执行历史:\n");
            sb.append("申请ID: ").append(workflowInstanceId).append("\n");
            sb.append("标题: ").append(instance.title).append("\n\n");

            for (var execution : executions) {
                sb.append("步骤 ").append(execution.executionOrder).append(":\n");
                sb.append("  状态: ").append(execution.executionStatus).append("\n");
                sb.append("  执行人: ").append(execution.executorName != null ? execution.executorName : "系统").append("\n");
                sb.append("  开始时间: ").append(execution.startedDate != null ? execution.startedDate : "未开始").append("\n");
                sb.append("  完成时间: ").append(execution.completedDate != null ? execution.completedDate : "未完成").append("\n");
                
                if (execution.isAiAgentUsed()) {
                    sb.append("  AI Agent: ").append(execution.aiAgentId).append("\n");
                }
                
                if (execution.executionResult != null) {
                    sb.append("  执行结果: ").append(execution.executionResult).append("\n");
                }
                
                if (execution.errorMessage != null) {
                    sb.append("  错误信息: ").append(execution.errorMessage).append("\n");
                }
                
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("获取工作流执行历史失败", e);
            return "获取工作流执行历史失败: " + e.getMessage();
        }
    }

    /**
     * 获取AI Agent信息
     */
    @Tool("获取AI Agent的详细信息")
    public String getAiAgentInfo(String agentId) {
        try {
            return aiAgentService.getAiAgentStatistics(agentId);
        } catch (Exception e) {
            LOG.error("获取AI Agent信息失败", e);
            return "获取AI Agent信息失败: " + e.getMessage();
        }
    }

    /**
     * 获取AI Agent任务列表
     */
    @Tool("获取AI Agent的任务列表")
    public String getAiAgentTasks(String agentId, String status) {
        try {
            var tasks = aiAgentService.getAiAgentTasks(agentId, status);
            if (tasks.isEmpty()) {
                return "AI Agent " + agentId + " 暂无任务";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("AI Agent任务列表:\n");
            for (var task : tasks) {
                sb.append("- 任务ID: ").append(task.taskId).append("\n");
                sb.append("  标题: ").append(task.taskTitle).append("\n");
                sb.append("  类型: ").append(task.taskType).append("\n");
                sb.append("  状态: ").append(task.status).append("\n");
                sb.append("  优先级: ").append(task.priority).append("\n");
                sb.append("  分配时间: ").append(task.assignedDate).append("\n");
                if (task.completedDate != null) {
                    sb.append("  完成时间: ").append(task.completedDate).append("\n");
                }
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("获取AI Agent任务列表失败", e);
            return "获取AI Agent任务列表失败: " + e.getMessage();
        }
    }

    /**
     * 获取可用的AI Agent列表
     */
    @Tool("获取系统中可用的AI Agent列表")
    public String getAvailableAiAgents() {
        try {
            List<AiAgent> agents = aiAgentService.getActiveAiAgents();
            if (agents.isEmpty()) {
                return "当前没有可用的AI Agent";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("可用的AI Agent:\n");
            for (AiAgent agent : agents) {
                sb.append("- ").append(agent.agentId).append(": ").append(agent.agentName);
                sb.append(" (").append(agent.employeeName).append(" - ").append(agent.position).append(")");
                sb.append(" [").append(agent.department).append("]\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("获取AI Agent列表失败", e);
            return "获取AI Agent列表失败: " + e.getMessage();
        }
    }

    /**
     * 根据角色获取AI Agent
     */
    @Tool("根据角色获取AI Agent列表")
    public String getAiAgentsByRole(String role) {
        try {
            List<AiAgent> agents = aiAgentService.getAiAgentsByRole(role);
            if (agents.isEmpty()) {
                return "没有找到角色为 " + role + " 的AI Agent";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("角色为 ").append(role).append(" 的AI Agent:\n");
            for (AiAgent agent : agents) {
                sb.append("- ").append(agent.agentId).append(": ").append(agent.agentName);
                sb.append(" (").append(agent.employeeName).append(")");
                sb.append(" [").append(agent.department).append("]\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOG.error("根据角色获取AI Agent失败", e);
            return "根据角色获取AI Agent失败: " + e.getMessage();
        }
    }
}
