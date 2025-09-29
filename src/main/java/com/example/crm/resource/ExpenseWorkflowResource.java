package com.example.crm.resource;

import com.example.crm.dto.ExpenseRequestDto;
import com.example.crm.dto.WorkflowRequestDto;
import com.example.crm.entity.WorkflowInstance;
import com.example.crm.service.WorkflowService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

/**
 * 报销工作流 REST API 资源类
 * 提供报销申请相关的 RESTful API 端点
 */
@Path("/api/workflow/expense")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "报销工作流管理", description = "报销申请工作流相关的API端点")
public class ExpenseWorkflowResource {

    private static final Logger LOG = Logger.getLogger(ExpenseWorkflowResource.class);

    @Inject
    WorkflowService workflowService;

    /**
     * 发起报销申请
     */
    @POST
    @Path("/apply")
    @Operation(summary = "发起报销申请", description = "员工发起报销申请，系统自动根据金额和规则进行审批流程")
    @APIResponse(responseCode = "201", description = "报销申请创建成功",
            content = @Content(schema = @Schema(implementation = WorkflowInstance.class)))
    @APIResponse(responseCode = "400", description = "请求参数错误")
    public Response applyExpense(ExpenseApplicationRequest request) {
        try {
            // 验证请求参数
            if (request.employeeId == null || request.employeeId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("员工ID不能为空").build();
            }
            if (request.expenseType == null || request.expenseType.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("报销类型不能为空").build();
            }
            if (request.amount == null || request.amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("报销金额必须大于0").build();
            }
            if (request.reason == null || request.reason.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("报销原因不能为空").build();
            }

            // 构建报销申请数据
            ExpenseRequestDto expenseData = new ExpenseRequestDto();
            expenseData.expenseType = request.expenseType;
            expenseData.amount = request.amount;
            expenseData.reason = request.reason;
            expenseData.description = request.description;
            expenseData.attachments = request.attachments;
            expenseData.department = request.department;
            expenseData.project = request.project;

            // 构建工作流标题和描述
            String title = String.format("报销申请 - %s - ¥%s", request.expenseType, request.amount);
            String description = String.format("员工 %s 申请报销，类型：%s，金额：¥%s，原因：%s",
                    request.employeeId, request.expenseType, request.amount, request.reason);

            // 创建工作流实例
            WorkflowInstance instance = workflowService.createWorkflowInstance(
                    "WF_TYPE_EXPENSE",  // 报销申请工作流类型
                    request.employeeId,
                    title,
                    description,
                    expenseData.toJsonString()
            );

            LOG.info("报销申请创建成功: " + instance.workflowInstanceId + " by " + request.employeeId);

            return Response.status(Response.Status.CREATED).entity(instance).build();
        } catch (Exception e) {
            LOG.error("创建报销申请失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("创建报销申请失败: " + e.getMessage()).build();
        }
    }

    /**
     * 查询报销申请状态
     */
    @GET
    @Path("/status/{workflowInstanceId}")
    @Operation(summary = "查询报销申请状态", description = "根据工作流实例ID查询报销申请的处理状态")
    @APIResponse(responseCode = "200", description = "成功获取报销申请状态",
            content = @Content(schema = @Schema(implementation = WorkflowInstance.class)))
    @APIResponse(responseCode = "404", description = "报销申请不存在")
    public Response getExpenseStatus(@PathParam("workflowInstanceId") String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("报销申请不存在: " + workflowInstanceId).build();
            }

            // 验证是否为报销申请
            if (!"WF_TYPE_EXPENSE".equals(instance.workflowTypeId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("不是报销申请工作流: " + workflowInstanceId).build();
            }

            return Response.ok(instance).build();
        } catch (Exception e) {
            LOG.error("查询报销申请状态失败: " + workflowInstanceId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("查询报销申请状态失败: " + e.getMessage()).build();
        }
    }

    /**
     * 获取报销申请执行历史
     */
    @GET
    @Path("/history/{workflowInstanceId}")
    @Operation(summary = "获取报销申请执行历史", description = "获取报销申请的详细执行历史")
    @APIResponse(responseCode = "200", description = "成功获取报销申请执行历史")
    @APIResponse(responseCode = "404", description = "报销申请不存在")
    public Response getExpenseHistory(@PathParam("workflowInstanceId") String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("报销申请不存在: " + workflowInstanceId).build();
            }

            // 验证是否为报销申请
            if (!"WF_TYPE_EXPENSE".equals(instance.workflowTypeId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("不是报销申请工作流: " + workflowInstanceId).build();
            }

            var executions = workflowService.getWorkflowExecutions(workflowInstanceId);
            return Response.ok(executions).build();
        } catch (Exception e) {
            LOG.error("获取报销申请执行历史失败: " + workflowInstanceId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取报销申请执行历史失败: " + e.getMessage()).build();
        }
    }

    /**
     * 报销申请请求DTO
     */
    public static class ExpenseApplicationRequest {
        @NotBlank(message = "员工ID不能为空")
        public String employeeId;

        @NotBlank(message = "报销类型不能为空")
        public String expenseType;

        @NotNull(message = "报销金额不能为空")
        public java.math.BigDecimal amount;

        @NotBlank(message = "报销原因不能为空")
        public String reason;

        public String description;
        public String attachments;
        public String department;
        public String project;

        @Override
        public String toString() {
            return "ExpenseApplicationRequest{" +
                    "employeeId='" + employeeId + '\'' +
                    ", expenseType='" + expenseType + '\'' +
                    ", amount=" + amount +
                    ", reason='" + reason + '\'' +
                    ", description='" + description + '\'' +
                    ", attachments='" + attachments + '\'' +
                    ", department='" + department + '\'' +
                    ", project='" + project + '\'' +
                    '}';
        }
    }
}
