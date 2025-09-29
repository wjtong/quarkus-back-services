package com.example.crm.service;

import com.example.crm.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 工作流服务类
 * 处理工作流的创建、执行、流转等核心业务逻辑
 */
@ApplicationScoped
public class WorkflowService {

    private static final Logger LOG = Logger.getLogger(WorkflowService.class);

    @Inject
    AiAgentService aiAgentService;

    /**
     * 创建工作流实例
     */
    @Transactional
    public WorkflowInstance createWorkflowInstance(String workflowTypeId, String initiatorId, 
                                                  String title, String description, String requestData) {
        try {
            // 验证工作流类型是否存在且激活
            WorkflowType workflowType = WorkflowType.findById(workflowTypeId);
            if (workflowType == null) {
                throw new RuntimeException("工作流类型不存在: " + workflowTypeId);
            }
            if (!workflowType.isActive()) {
                throw new RuntimeException("工作流类型未激活: " + workflowTypeId);
            }

            // 获取发起人信息
            Party initiator = Party.findById(initiatorId);
            if (initiator == null) {
                throw new RuntimeException("发起人不存在: " + initiatorId);
            }

            // 创建工作流实例
            WorkflowInstance instance = new WorkflowInstance();
            instance.workflowInstanceId = generateWorkflowInstanceId();
            instance.workflowTypeId = workflowTypeId;
            instance.initiatorId = initiatorId;
            instance.initiatorName = initiator.partyName != null ? initiator.partyName : "未知";
            instance.title = title;
            instance.description = description;
            instance.requestData = requestData;
            instance.status = "PENDING";
            instance.priority = workflowType.priorityLevel != null ? workflowType.priorityLevel : 5;
            instance.currentStep = 0;
            instance.totalSteps = getTotalSteps(workflowTypeId);
            instance.approvalRequired = workflowType.requiresApproval() ? "Y" : "N";
            instance.autoApproved = workflowType.isAutoApprove() ? "Y" : "N";
            instance.notificationSent = "N";
            instance.auditLogged = "N";
            instance.retryCount = 0;
            instance.maxRetryAttempts = workflowType.maxRetryAttempts != null ? workflowType.maxRetryAttempts : 3;
            instance.createdDate = LocalDateTime.now();
            instance.lastModifiedDate = LocalDateTime.now();
            instance.lastUpdatedStamp = LocalDateTime.now();
            instance.createdStamp = LocalDateTime.now();

            // 设置超时时间
            if (workflowType.defaultTimeout != null) {
                instance.timeoutDate = LocalDateTime.now().plusMinutes(workflowType.defaultTimeout);
            }

            instance.persist();
            LOG.info("创建工作流实例成功: " + instance.workflowInstanceId);

            // 启动工作流执行
            startWorkflowExecution(instance.workflowInstanceId);

            return instance;
        } catch (Exception e) {
            LOG.error("创建工作流实例失败", e);
            throw new RuntimeException("创建工作流实例失败: " + e.getMessage());
        }
    }

    /**
     * 启动工作流执行
     */
    @Transactional
    public void startWorkflowExecution(String workflowInstanceId) {
        try {
            WorkflowInstance instance = WorkflowInstance.findById(workflowInstanceId);
            if (instance == null) {
                throw new RuntimeException("工作流实例不存在: " + workflowInstanceId);
            }

            // 更新实例状态
            instance.updateStatus("IN_PROGRESS");
            instance.persist();

            // 获取第一个步骤
            WorkflowStep firstStep = getFirstStep(instance.workflowTypeId);
            if (firstStep == null) {
                throw new RuntimeException("工作流类型没有定义步骤: " + instance.workflowTypeId);
            }

            // 执行第一个步骤
            executeWorkflowStep(instance, firstStep);

            LOG.info("启动工作流执行成功: " + workflowInstanceId);
        } catch (Exception e) {
            LOG.error("启动工作流执行失败: " + workflowInstanceId, e);
            throw new RuntimeException("启动工作流执行失败: " + e.getMessage());
        }
    }

    /**
     * 执行工作流步骤
     */
    @Transactional
    public void executeWorkflowStep(WorkflowInstance instance, WorkflowStep step) {
        try {
            // 检查步骤条件
            if (!step.checkConditions(instance.requestData)) {
                LOG.info("步骤条件不满足，跳过执行: " + step.workflowStepId);
                return;
            }

            // 创建工作流执行记录
            WorkflowExecution execution = new WorkflowExecution();
            execution.workflowExecutionId = generateWorkflowExecutionId();
            execution.workflowInstanceId = instance.workflowInstanceId;
            execution.workflowStepId = step.workflowStepId;
            execution.executionOrder = step.stepOrder;
            execution.executionStatus = "PENDING";
            execution.inputData = instance.requestData;
            execution.retryCount = 0;
            execution.notificationSent = "N";
            execution.auditLogged = "N";
            execution.createdDate = LocalDateTime.now();
            execution.lastModifiedDate = LocalDateTime.now();
            execution.lastUpdatedStamp = LocalDateTime.now();
            execution.createdStamp = LocalDateTime.now();

            // 设置超时时间
            if (step.timeoutMinutes != null) {
                execution.timeoutDate = LocalDateTime.now().plusMinutes(step.timeoutMinutes);
            }

            execution.persist();

            // 查找合适的AI Agent执行任务
            AiAgent aiAgent = findSuitableAiAgent(step);
            if (aiAgent != null) {
                execution.setAiAgentInfo(aiAgent.agentId);
                execution.executorId = aiAgent.employeeId;
                execution.executorName = aiAgent.employeeName;
                execution.persist();

                // 使用AI Agent执行任务
                executeWithAiAgent(execution, aiAgent, step, instance);
            } else {
                // 没有找到合适的AI Agent，标记为待处理
                execution.updateStatus("PENDING");
                execution.persist();
                LOG.warn("没有找到合适的AI Agent执行步骤: " + step.workflowStepId);
            }

        } catch (Exception e) {
            LOG.error("执行工作流步骤失败: " + step.workflowStepId, e);
            throw new RuntimeException("执行工作流步骤失败: " + e.getMessage());
        }
    }

    /**
     * 使用AI Agent执行任务
     */
    @Transactional
    public void executeWithAiAgent(WorkflowExecution execution, AiAgent aiAgent, 
                                  WorkflowStep step, WorkflowInstance instance) {
        try {
            execution.updateStatus("IN_PROGRESS");
            execution.persist();

            // 构建任务描述
            String taskDescription = buildTaskDescription(step, instance);
            
            // 使用AI Agent处理任务
            String result = aiAgentService.processWorkflowTask(aiAgent.agentId, taskDescription, 
                                                              execution.inputData, step.stepConditions);

            // 设置执行结果
            execution.setResult(execution.inputData, result, 0.9); // 默认置信度0.9
            execution.persist();

            // 检查是否需要流转到下一步
            if (step.nextStepId != null) {
                WorkflowStep nextStep = WorkflowStep.findById(step.nextStepId);
                if (nextStep != null) {
                    // 更新实例当前步骤
                    instance.currentStep = step.stepOrder;
                    instance.persist();
                    
                    // 执行下一步
                    executeWorkflowStep(instance, nextStep);
                } else {
                    // 没有下一步，完成工作流
                    completeWorkflowInstance(instance.workflowInstanceId, result);
                }
            } else {
                // 没有下一步，完成工作流
                completeWorkflowInstance(instance.workflowInstanceId, result);
            }

            LOG.info("AI Agent执行任务成功: " + execution.workflowExecutionId);
        } catch (Exception e) {
            LOG.error("AI Agent执行任务失败: " + execution.workflowExecutionId, e);
            execution.setError("AI Agent执行失败: " + e.getMessage());
            execution.persist();
        }
    }

    /**
     * 完成工作流实例
     */
    @Transactional
    public void completeWorkflowInstance(String workflowInstanceId, String result) {
        try {
            WorkflowInstance instance = WorkflowInstance.findById(workflowInstanceId);
            if (instance == null) {
                throw new RuntimeException("工作流实例不存在: " + workflowInstanceId);
            }

            instance.setResult(result);
            instance.persist();

            LOG.info("工作流实例完成: " + workflowInstanceId);
        } catch (Exception e) {
            LOG.error("完成工作流实例失败: " + workflowInstanceId, e);
            throw new RuntimeException("完成工作流实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取工作流实例状态
     */
    public WorkflowInstance getWorkflowInstance(String workflowInstanceId) {
        return WorkflowInstance.findById(workflowInstanceId);
    }

    /**
     * 获取工作流执行历史
     */
    public List<WorkflowExecution> getWorkflowExecutions(String workflowInstanceId) {
        return WorkflowExecution.find("workflowInstanceId = ?1 order by executionOrder", workflowInstanceId).list();
    }

    /**
     * 获取工作流类型列表
     */
    public List<WorkflowType> getActiveWorkflowTypes() {
        return WorkflowType.find("isActive = 'Y' order by workflowTypeName").list();
    }

    /**
     * 获取工作流步骤列表
     */
    public List<WorkflowStep> getWorkflowSteps(String workflowTypeId) {
        return WorkflowStep.find("workflowTypeId = ?1 and isActive = 'Y' order by stepOrder", workflowTypeId).list();
    }

    /**
     * 查找合适的AI Agent
     */
    private AiAgent findSuitableAiAgent(WorkflowStep step) {
        // 如果步骤指定了具体的处理人
        if (step.handlerId != null) {
            return AiAgent.find("employeeId = ?1 and isActive = 'Y'", step.handlerId).firstResult();
        }

        // 如果步骤指定了处理角色
        if (step.handlerRole != null) {
            return AiAgent.find("position = ?1 and isActive = 'Y'", step.handlerRole).firstResult();
        }

        // 默认返回第一个激活的AI Agent
        return AiAgent.find("isActive = 'Y'").firstResult();
    }

    /**
     * 构建任务描述
     */
    private String buildTaskDescription(WorkflowStep step, WorkflowInstance instance) {
        StringBuilder sb = new StringBuilder();
        sb.append("工作流任务: ").append(step.stepName).append("\n");
        sb.append("工作流类型: ").append(instance.workflowTypeId).append("\n");
        sb.append("任务描述: ").append(step.stepDescription != null ? step.stepDescription : "无").append("\n");
        sb.append("请求数据: ").append(instance.requestData).append("\n");
        if (step.stepConditions != null) {
            sb.append("处理条件: ").append(step.stepConditions).append("\n");
        }
        return sb.toString();
    }

    /**
     * 获取工作流总步骤数
     */
    private Integer getTotalSteps(String workflowTypeId) {
        Long count = WorkflowStep.count("workflowTypeId = ?1 and isActive = 'Y'", workflowTypeId);
        return count.intValue();
    }

    /**
     * 获取第一个步骤
     */
    private WorkflowStep getFirstStep(String workflowTypeId) {
        return WorkflowStep.find("workflowTypeId = ?1 and isActive = 'Y' order by stepOrder", workflowTypeId).firstResult();
    }

    /**
     * 生成工作流实例ID
     */
    private String generateWorkflowInstanceId() {
        return "WFI_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * 生成工作流执行ID
     */
    private String generateWorkflowExecutionId() {
        return "WFE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
