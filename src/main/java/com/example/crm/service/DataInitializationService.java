package com.example.crm.service;

import com.example.crm.entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

/**
 * 数据初始化服务类
 * 负责初始化系统的基础数据，包括工作流类型、步骤、AI Agent等
 */
@ApplicationScoped
public class DataInitializationService {

    private static final Logger LOG = Logger.getLogger(DataInitializationService.class);

    @ConfigProperty(name = "data.initialization.enabled", defaultValue = "true")
    Boolean initializationEnabled;

    @PostConstruct
    @Transactional
    public void initializeData() {
        if (!initializationEnabled) {
            LOG.info("数据初始化已禁用");
            return;
        }

        try {
            LOG.info("开始初始化系统数据...");

            // 初始化员工数据
            initializeEmployees();

            // 初始化AI Agent数据
            initializeAiAgents();

            // 初始化工作流类型数据
            initializeWorkflowTypes();

            // 初始化工作流步骤数据
            initializeWorkflowSteps();

            LOG.info("系统数据初始化完成");
        } catch (Exception e) {
            LOG.error("数据初始化失败", e);
        }
    }

    /**
     * 初始化员工数据
     */
    @Transactional
    public void initializeEmployees() {
        LOG.info("初始化员工数据...");

        // 创建示例员工
        createEmployeeIfNotExists("EMP_001", "张三", "普通员工", "技术部");
        createEmployeeIfNotExists("MGR_001", "李四", "部门经理", "技术部");
        createEmployeeIfNotExists("DIR_001", "王五", "总监", "技术部");
        createEmployeeIfNotExists("FIN_001", "赵六", "财务", "财务部");
        createEmployeeIfNotExists("CEO_001", "孙七", "总经理", "总经理办公室");
    }

    /**
     * 初始化AI Agent数据
     */
    @Transactional
    public void initializeAiAgents() {
        LOG.info("初始化AI Agent数据...");

        // 普通员工AI Agent
        createAiAgentIfNotExists("AGENT_EMP_001", "张三的AI助手", "张三", "EMP_001", "普通员工", "技术部",
                "负责处理日常工作任务，包括报销申请、请假申请等",
                "报销申请处理,请假申请处理,日常事务处理",
                "专业,高效,友好",
                "简洁明了,及时响应",
                "公司规章制度,业务流程,部门信息");

        // 部门经理AI Agent
        createAiAgentIfNotExists("AGENT_MGR_001", "李四的AI助手", "李四", "MGR_001", "部门经理", "技术部",
                "负责部门管理，审批下属员工的申请，处理部门事务",
                "申请审批,部门管理,团队协调",
                "严谨,公正,负责",
                "专业权威,决策明确",
                "公司管理制度,审批流程,团队信息");

        // 总监AI Agent
        createAiAgentIfNotExists("AGENT_DIR_001", "王五的AI助手", "王五", "DIR_001", "总监", "技术部",
                "负责部门战略规划，重要决策审批，跨部门协调",
                "战略规划,重要决策,跨部门协调",
                "战略思维,决策果断,全局观念",
                "高层视角,战略导向",
                "公司战略,高层决策,跨部门信息");

        // 财务AI Agent
        createAiAgentIfNotExists("AGENT_FIN_001", "赵六的AI助手", "赵六", "FIN_001", "财务", "财务部",
                "负责财务审批，预算管理，费用审核",
                "财务审批,预算管理,费用审核",
                "严谨,细致,合规",
                "专业准确,合规导向",
                "财务制度,预算管理,费用标准");

        // 总经理AI Agent
        createAiAgentIfNotExists("AGENT_CEO_001", "孙七的AI助手", "孙七", "CEO_001", "总经理", "总经理办公室",
                "负责公司重大决策，战略规划，重要事项审批",
                "重大决策,战略规划,重要审批",
                "战略高度,决策权威,全局视野",
                "高层决策,战略导向",
                "公司战略,重大决策,全局信息");
    }

    /**
     * 初始化工作流类型数据
     */
    @Transactional
    public void initializeWorkflowTypes() {
        LOG.info("初始化工作流类型数据...");

        // 报销申请工作流
        createWorkflowTypeIfNotExists("WF_TYPE_EXPENSE", "报销申请", "员工报销申请流程",
                "{\"description\":\"员工报销申请工作流\",\"category\":\"expense\",\"priority\":\"normal\"}",
                "{\"amount_thresholds\":[{\"min\":0,\"max\":500,\"approvers\":[\"部门经理\",\"总监\"]},{\"min\":500,\"max\":1000,\"approvers\":[\"部门经理\",\"总监\",\"财务\"]},{\"min\":1000,\"max\":999999,\"approvers\":[\"部门经理\",\"总监\",\"财务\",\"总经理\"]}]}",
                "{\"普通员工\":[\"部门经理\",\"总监\"],\"部门经理\":[\"总监\"],\"总监\":[\"财务\"],\"财务\":[\"总经理\"]}",
                1, "Y", 5, 1440, 3, "Y", "N", "Y", "Y");

        // 请假申请工作流
        createWorkflowTypeIfNotExists("WF_TYPE_LEAVE", "请假申请", "员工请假申请流程",
                "{\"description\":\"员工请假申请工作流\",\"category\":\"leave\",\"priority\":\"normal\"}",
                "{\"leave_types\":[\"年假\",\"病假\",\"事假\",\"调休\"],\"approval_matrix\":{\"年假\":[\"部门经理\"],\"病假\":[\"部门经理\"],\"事假\":[\"部门经理\",\"总监\"],\"调休\":[\"部门经理\"]}}",
                "{\"普通员工\":[\"部门经理\"],\"部门经理\":[\"总监\"]}",
                1, "Y", 5, 720, 3, "Y", "N", "Y", "Y");

        // 采购申请工作流
        createWorkflowTypeIfNotExists("WF_TYPE_PURCHASE", "采购申请", "采购申请流程",
                "{\"description\":\"采购申请工作流\",\"category\":\"purchase\",\"priority\":\"normal\"}",
                "{\"amount_thresholds\":[{\"min\":0,\"max\":1000,\"approvers\":[\"部门经理\"]},{\"min\":1000,\"max\":5000,\"approvers\":[\"部门经理\",\"总监\"]},{\"min\":5000,\"max\":999999,\"approvers\":[\"部门经理\",\"总监\",\"财务\",\"总经理\"]}]}",
                "{\"普通员工\":[\"部门经理\"],\"部门经理\":[\"总监\"],\"总监\":[\"财务\"],\"财务\":[\"总经理\"]}",
                1, "Y", 5, 2880, 3, "Y", "N", "Y", "Y");
    }

    /**
     * 初始化工作流步骤数据
     */
    @Transactional
    public void initializeWorkflowSteps() {
        LOG.info("初始化工作流步骤数据...");

        // 报销申请工作流步骤
        initializeExpenseWorkflowSteps();

        // 请假申请工作流步骤
        initializeLeaveWorkflowSteps();

        // 采购申请工作流步骤
        initializePurchaseWorkflowSteps();
    }

    /**
     * 初始化报销申请工作流步骤
     */
    private void initializeExpenseWorkflowSteps() {
        // 步骤1：部门经理审批
        createWorkflowStepIfNotExists("WF_STEP_EXPENSE_MGR", "WF_TYPE_EXPENSE", "部门经理审批", "部门经理审批报销申请", 1,
                "APPROVAL", "部门经理", null, null,
                "{\"conditions\":[\"amount >= 0\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 500\",\"next_step\":\"WF_STEP_EXPENSE_DIR\"},{\"condition\":\"amount >= 500\",\"next_step\":\"WF_STEP_EXPENSE_DIR\"}]}",
                "WF_STEP_EXPENSE_DIR", null, "Y");

        // 步骤2：总监审批
        createWorkflowStepIfNotExists("WF_STEP_EXPENSE_DIR", "WF_TYPE_EXPENSE", "总监审批", "总监审批报销申请", 2,
                "APPROVAL", "总监", null, null,
                "{\"conditions\":[\"amount >= 0\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 500\",\"next_step\":\"WF_STEP_EXPENSE_COMPLETE\"},{\"condition\":\"amount >= 500\",\"next_step\":\"WF_STEP_EXPENSE_FIN\"}]}",
                "WF_STEP_EXPENSE_FIN", "WF_STEP_EXPENSE_COMPLETE", "Y");

        // 步骤3：财务审批（金额>=500时）
        createWorkflowStepIfNotExists("WF_STEP_EXPENSE_FIN", "WF_TYPE_EXPENSE", "财务审批", "财务审批报销申请", 3,
                "APPROVAL", "财务", null, null,
                "{\"conditions\":[\"amount >= 500\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 1000\",\"next_step\":\"WF_STEP_EXPENSE_COMPLETE\"},{\"condition\":\"amount >= 1000\",\"next_step\":\"WF_STEP_EXPENSE_CEO\"}]}",
                "WF_STEP_EXPENSE_CEO", "WF_STEP_EXPENSE_COMPLETE", "Y");

        // 步骤4：总经理审批（金额>=1000时）
        createWorkflowStepIfNotExists("WF_STEP_EXPENSE_CEO", "WF_TYPE_EXPENSE", "总经理审批", "总经理审批报销申请", 4,
                "APPROVAL", "总经理", null, null,
                "{\"conditions\":[\"amount >= 1000\"]}", "Y", "N", 120, 3, "N", "Y",
                null, "WF_STEP_EXPENSE_COMPLETE", null, "Y");

        // 步骤5：完成
        createWorkflowStepIfNotExists("WF_STEP_EXPENSE_COMPLETE", "WF_TYPE_EXPENSE", "报销完成", "报销申请处理完成", 5,
                "COMPLETE", null, null, null,
                null, "N", "Y", 0, 0, "N", "N",
                null, null, null, "Y");
    }

    /**
     * 初始化请假申请工作流步骤
     */
    private void initializeLeaveWorkflowSteps() {
        // 步骤1：部门经理审批
        createWorkflowStepIfNotExists("WF_STEP_LEAVE_MGR", "WF_TYPE_LEAVE", "部门经理审批", "部门经理审批请假申请", 1,
                "APPROVAL", "部门经理", null, null,
                "{\"conditions\":[\"leave_type in [\\\"年假\\\",\\\"病假\\\",\\\"事假\\\",\\\"调休\\\"]\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"leave_type in [\\\"年假\\\",\\\"病假\\\",\\\"调休\\\"]\",\"next_step\":\"WF_STEP_LEAVE_COMPLETE\"},{\"condition\":\"leave_type == \\\"事假\\\"\",\"next_step\":\"WF_STEP_LEAVE_DIR\"}]}",
                "WF_STEP_LEAVE_DIR", "WF_STEP_LEAVE_COMPLETE", "Y");

        // 步骤2：总监审批（事假时）
        createWorkflowStepIfNotExists("WF_STEP_LEAVE_DIR", "WF_TYPE_LEAVE", "总监审批", "总监审批事假申请", 2,
                "APPROVAL", "总监", null, null,
                "{\"conditions\":[\"leave_type == \\\"事假\\\"\"]}", "Y", "N", 60, 3, "N", "Y",
                null, "WF_STEP_LEAVE_COMPLETE", null, "Y");

        // 步骤3：完成
        createWorkflowStepIfNotExists("WF_STEP_LEAVE_COMPLETE", "WF_TYPE_LEAVE", "请假完成", "请假申请处理完成", 3,
                "COMPLETE", null, null, null,
                null, "N", "Y", 0, 0, "N", "N",
                null, null, null, "Y");
    }

    /**
     * 初始化采购申请工作流步骤
     */
    private void initializePurchaseWorkflowSteps() {
        // 步骤1：部门经理审批
        createWorkflowStepIfNotExists("WF_STEP_PURCHASE_MGR", "WF_TYPE_PURCHASE", "部门经理审批", "部门经理审批采购申请", 1,
                "APPROVAL", "部门经理", null, null,
                "{\"conditions\":[\"amount >= 0\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 1000\",\"next_step\":\"WF_STEP_PURCHASE_COMPLETE\"},{\"condition\":\"amount >= 1000\",\"next_step\":\"WF_STEP_PURCHASE_DIR\"}]}",
                "WF_STEP_PURCHASE_DIR", "WF_STEP_PURCHASE_COMPLETE", "Y");

        // 步骤2：总监审批（金额>=1000时）
        createWorkflowStepIfNotExists("WF_STEP_PURCHASE_DIR", "WF_TYPE_PURCHASE", "总监审批", "总监审批采购申请", 2,
                "APPROVAL", "总监", null, null,
                "{\"conditions\":[\"amount >= 1000\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 5000\",\"next_step\":\"WF_STEP_PURCHASE_COMPLETE\"},{\"condition\":\"amount >= 5000\",\"next_step\":\"WF_STEP_PURCHASE_FIN\"}]}",
                "WF_STEP_PURCHASE_FIN", "WF_STEP_PURCHASE_COMPLETE", "Y");

        // 步骤3：财务审批（金额>=5000时）
        createWorkflowStepIfNotExists("WF_STEP_PURCHASE_FIN", "WF_TYPE_PURCHASE", "财务审批", "财务审批采购申请", 3,
                "APPROVAL", "财务", null, null,
                "{\"conditions\":[\"amount >= 5000\"]}", "Y", "N", 60, 3, "N", "Y",
                "{\"next_conditions\":[{\"condition\":\"amount < 10000\",\"next_step\":\"WF_STEP_PURCHASE_COMPLETE\"},{\"condition\":\"amount >= 10000\",\"next_step\":\"WF_STEP_PURCHASE_CEO\"}]}",
                "WF_STEP_PURCHASE_CEO", "WF_STEP_PURCHASE_COMPLETE", "Y");

        // 步骤4：总经理审批（金额>=10000时）
        createWorkflowStepIfNotExists("WF_STEP_PURCHASE_CEO", "WF_TYPE_PURCHASE", "总经理审批", "总经理审批采购申请", 4,
                "APPROVAL", "总经理", null, null,
                "{\"conditions\":[\"amount >= 10000\"]}", "Y", "N", 120, 3, "N", "Y",
                null, "WF_STEP_PURCHASE_COMPLETE", null, "Y");

        // 步骤5：完成
        createWorkflowStepIfNotExists("WF_STEP_PURCHASE_COMPLETE", "WF_TYPE_PURCHASE", "采购完成", "采购申请处理完成", 5,
                "COMPLETE", null, null, null,
                null, "N", "Y", 0, 0, "N", "N",
                null, null, null, "Y");
    }

    /**
     * 创建AI Agent（如果不存在）
     */
    private void createAiAgentIfNotExists(String agentId, String agentName, String employeeName,
                                        String employeeId, String position, String department,
                                        String responsibilities, String capabilities,
                                        String personalityTraits, String communicationStyle,
                                        String knowledgeBase) {
        if (AiAgent.findById(agentId) == null) {
            AiAgent agent = new AiAgent();
            agent.agentId = agentId;
            agent.agentName = agentName;
            agent.employeeId = employeeId;
            agent.employeeName = employeeName;
            agent.position = position;
            agent.department = department;
            agent.responsibilities = responsibilities;
            agent.capabilities = capabilities;
            agent.personalityTraits = personalityTraits;
            agent.communicationStyle = communicationStyle;
            agent.knowledgeBase = knowledgeBase;
            agent.priorityLevel = 5;
            agent.maxConcurrentTasks = 10;
            agent.responseTimeout = 30;
            agent.isActive = "Y";
            agent.isLearningEnabled = "Y";
            agent.learningRate = 0.1;
            agent.confidenceThreshold = 0.8;
            agent.totalInteractions = 0L;
            agent.successRate = 0.0;
            agent.averageResponseTime = 0.0;
            agent.createdDate = LocalDateTime.now();
            agent.lastModifiedDate = LocalDateTime.now();
            agent.lastUpdatedStamp = LocalDateTime.now();
            agent.createdStamp = LocalDateTime.now();

            agent.persist();
            LOG.info("创建AI Agent: " + agentId);
        }
    }

    /**
     * 创建工作流类型（如果不存在）
     */
    private void createWorkflowTypeIfNotExists(String workflowTypeId, String workflowTypeName, String description,
                                             String workflowDefinition, String businessRules, String approvalMatrix,
                                             Integer version, String isActive, Integer priorityLevel,
                                             Integer defaultTimeout, Integer maxRetryAttempts,
                                             String requiresApproval, String autoApprove,
                                             String notificationEnabled, String auditRequired) {
        if (WorkflowType.findById(workflowTypeId) == null) {
            WorkflowType type = new WorkflowType();
            type.workflowTypeId = workflowTypeId;
            type.workflowTypeName = workflowTypeName;
            type.description = description;
            type.workflowDefinition = workflowDefinition;
            type.businessRules = businessRules;
            type.approvalMatrix = approvalMatrix;
            type.version = version;
            type.isActive = isActive;
            type.priorityLevel = priorityLevel;
            type.defaultTimeout = defaultTimeout;
            type.maxRetryAttempts = maxRetryAttempts;
            type.requiresApproval = requiresApproval;
            type.autoApprove = autoApprove;
            type.notificationEnabled = notificationEnabled;
            type.auditRequired = auditRequired;
            type.createdDate = LocalDateTime.now();
            type.lastModifiedDate = LocalDateTime.now();
            type.lastUpdatedStamp = LocalDateTime.now();
            type.createdStamp = LocalDateTime.now();

            type.persist();
            LOG.info("创建工作流类型: " + workflowTypeId);
        }
    }

    /**
     * 创建工作流步骤（如果不存在）
     */
    private void createWorkflowStepIfNotExists(String workflowStepId, String workflowTypeId, String stepName,
                                             String stepDescription, Integer stepOrder, String stepType,
                                             String handlerRole, String handlerId, String handlerName,
                                             String stepConditions, String approvalRequired, String autoApprove,
                                             Integer timeoutMinutes, Integer retryAttempts, String parallelExecution,
                                             String notificationEnabled, String nextStepConditions,
                                             String nextStepId, String alternativeStepId, String isActive) {
        if (WorkflowStep.findById(workflowStepId) == null) {
            WorkflowStep step = new WorkflowStep();
            step.workflowStepId = workflowStepId;
            step.workflowTypeId = workflowTypeId;
            step.stepName = stepName;
            step.stepDescription = stepDescription;
            step.stepOrder = stepOrder;
            step.stepType = stepType;
            step.handlerRole = handlerRole;
            step.handlerId = handlerId;
            step.handlerName = handlerName;
            step.stepConditions = stepConditions;
            step.approvalRequired = approvalRequired;
            step.autoApprove = autoApprove;
            step.timeoutMinutes = timeoutMinutes;
            step.retryAttempts = retryAttempts;
            step.parallelExecution = parallelExecution;
            step.notificationEnabled = notificationEnabled;
            step.nextStepConditions = nextStepConditions;
            step.nextStepId = nextStepId;
            step.alternativeStepId = alternativeStepId;
            step.isActive = isActive;
            step.createdDate = LocalDateTime.now();
            step.lastModifiedDate = LocalDateTime.now();
            step.lastUpdatedStamp = LocalDateTime.now();
            step.createdStamp = LocalDateTime.now();

            step.persist();
            LOG.info("创建工作流步骤: " + workflowStepId);
        }
    }

    /**
     * 创建员工（如果不存在）
     */
    private void createEmployeeIfNotExists(String partyId, String partyName, String position, String department) {
        if (Party.findById(partyId) == null) {
            Party employee = new Party();
            employee.partyId = partyId;
            employee.partyName = partyName;
            employee.statusId = "ACTIVE";
            employee.createdDate = LocalDateTime.now();
            employee.lastModifiedDate = LocalDateTime.now();
            employee.lastUpdatedStamp = LocalDateTime.now();
            employee.createdStamp = LocalDateTime.now();

            employee.persist();
            LOG.info("创建员工: " + partyId + " - " + partyName);
        }
    }
}
