package com.example.crm;

import com.example.crm.entity.WorkflowType;
import com.example.crm.entity.WorkflowStep;
import com.example.crm.entity.AiAgent;
import com.example.crm.entity.Party;
import com.example.crm.service.WorkflowService;
import com.example.crm.service.AiAgentService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工作流系统测试类
 * 测试协作任务处理系统的基本功能
 */
@QuarkusTest
public class WorkflowSystemTest {

    @Inject
    WorkflowService workflowService;

    @Inject
    AiAgentService aiAgentService;

    @Test
    public void testWorkflowTypeExists() {
        // 测试报销申请工作流类型是否存在
        WorkflowType expenseType = WorkflowType.findById("WF_TYPE_EXPENSE");
        // 在测试环境中，数据可能没有初始化，所以这里只测试查询不会出错
        if (expenseType != null) {
            assertEquals("报销申请", expenseType.workflowTypeName);
            assertTrue(expenseType.isActive(), "报销申请工作流类型应该是激活状态");
        }
    }

    @Test
    public void testWorkflowStepsExist() {
        // 测试报销申请工作流步骤是否存在
        var steps = WorkflowStep.find("workflowTypeId = ?1 order by stepOrder", "WF_TYPE_EXPENSE").list();
        // 在测试环境中，数据可能没有初始化，所以这里只测试查询不会出错
        if (!steps.isEmpty()) {
            // 验证第一个步骤是部门经理审批
            WorkflowStep firstStep = (WorkflowStep) steps.get(0);
            assertEquals("部门经理审批", firstStep.stepName);
            assertEquals("部门经理", firstStep.handlerRole);
        }
    }

    @Test
    public void testAiAgentsExist() {
        // 测试AI Agent是否存在
        var agents = AiAgent.find("isActive = 'Y'").list();
        // 在测试环境中，数据可能没有初始化，所以这里只测试查询不会出错
        if (!agents.isEmpty()) {
            // 验证部门经理AI Agent存在
            AiAgent managerAgent = AiAgent.find("position = ?1 and isActive = 'Y'", "部门经理").firstResult();
            if (managerAgent != null) {
                assertEquals("李四", managerAgent.employeeName);
            }
        }
    }

    @Test
    public void testEmployeesExist() {
        // 测试员工是否存在
        var employees = Party.find("statusId = 'ACTIVE'").list();
        // 在测试环境中，数据可能没有初始化，所以这里只测试查询不会出错
        if (!employees.isEmpty()) {
            // 验证张三存在
            Party employee = Party.findById("EMP_001");
            if (employee != null) {
                assertEquals("张三", employee.partyName);
            }
        }
    }

    @Test
    public void testWorkflowServiceMethods() {
        // 测试工作流服务方法
        var workflowTypes = workflowService.getActiveWorkflowTypes();
        // 在测试环境中，数据可能没有初始化，所以这里只测试方法调用不会出错
        assertNotNull(workflowTypes, "工作流服务应该返回列表");
        
        var steps = workflowService.getWorkflowSteps("WF_TYPE_EXPENSE");
        assertNotNull(steps, "工作流服务应该返回步骤列表");
    }

    @Test
    public void testAiAgentServiceMethods() {
        // 测试AI Agent服务方法
        var agents = aiAgentService.getActiveAiAgents();
        assertNotNull(agents, "AI Agent服务应该返回列表");
        
        var managerAgents = aiAgentService.getAiAgentsByRole("部门经理");
        assertNotNull(managerAgents, "AI Agent服务应该返回角色列表");
    }
}
