package com.example.crm.resource;

import com.example.crm.entity.AiAgent;
import com.example.crm.entity.AiAgentTask;
import com.example.crm.service.AiAgentService;
import com.example.crm.service.WorkflowService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * AI Agent 工作流 REST API 资源类
 * 提供 AI Agent 工作流相关的 RESTful API 端点
 */
@Path("/api/ai-agent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "AI Agent 工作流管理", description = "AI Agent 工作流相关的API端点")
public class AiAgentWorkflowResource {

    private static final Logger LOG = Logger.getLogger(AiAgentWorkflowResource.class);

    @Inject
    AiAgentService aiAgentService;

    @Inject
    WorkflowService workflowService;

    /**
     * 获取所有激活的AI Agent
     */
    @GET
    @Path("/agents")
    @Operation(summary = "获取AI Agent列表", description = "获取系统中所有激活的AI Agent")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent列表",
            content = @Content(schema = @Schema(implementation = AiAgent.class)))
    public Response getAiAgents() {
        try {
            List<AiAgent> agents = aiAgentService.getActiveAiAgents();
            return Response.ok(agents).build();
        } catch (Exception e) {
            LOG.error("获取AI Agent列表失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取AI Agent列表失败: " + e.getMessage()).build();
        }
    }

    /**
     * 根据ID获取AI Agent
     */
    @GET
    @Path("/agents/{agentId}")
    @Operation(summary = "获取AI Agent详情", description = "根据ID获取AI Agent的详细信息")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent详情",
            content = @Content(schema = @Schema(implementation = AiAgent.class)))
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response getAiAgent(@PathParam("agentId") String agentId) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }
            return Response.ok(agent).build();
        } catch (Exception e) {
            LOG.error("获取AI Agent失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取AI Agent失败: " + e.getMessage()).build();
        }
    }

    /**
     * 获取AI Agent统计信息
     */
    @GET
    @Path("/agents/{agentId}/statistics")
    @Operation(summary = "获取AI Agent统计信息", description = "获取AI Agent的详细统计信息")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent统计信息")
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response getAiAgentStatistics(@PathParam("agentId") String agentId) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }

            String statistics = aiAgentService.getAiAgentStatistics(agentId);
            return Response.ok(statistics).build();
        } catch (Exception e) {
            LOG.error("获取AI Agent统计信息失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取AI Agent统计信息失败: " + e.getMessage()).build();
        }
    }

    /**
     * 根据角色获取AI Agent
     */
    @GET
    @Path("/agents/by-role/{role}")
    @Operation(summary = "根据角色获取AI Agent", description = "根据角色获取AI Agent列表")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent列表",
            content = @Content(schema = @Schema(implementation = AiAgent.class)))
    public Response getAiAgentsByRole(@PathParam("role") String role) {
        try {
            List<AiAgent> agents = aiAgentService.getAiAgentsByRole(role);
            return Response.ok(agents).build();
        } catch (Exception e) {
            LOG.error("根据角色获取AI Agent失败: " + role, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("根据角色获取AI Agent失败: " + e.getMessage()).build();
        }
    }

    /**
     * 根据部门获取AI Agent
     */
    @GET
    @Path("/agents/by-department/{department}")
    @Operation(summary = "根据部门获取AI Agent", description = "根据部门获取AI Agent列表")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent列表",
            content = @Content(schema = @Schema(implementation = AiAgent.class)))
    public Response getAiAgentsByDepartment(@PathParam("department") String department) {
        try {
            List<AiAgent> agents = aiAgentService.getAiAgentsByDepartment(department);
            return Response.ok(agents).build();
        } catch (Exception e) {
            LOG.error("根据部门获取AI Agent失败: " + department, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("根据部门获取AI Agent失败: " + e.getMessage()).build();
        }
    }

    /**
     * 获取AI Agent任务列表
     */
    @GET
    @Path("/agents/{agentId}/tasks")
    @Operation(summary = "获取AI Agent任务列表", description = "获取指定AI Agent的任务列表")
    @APIResponse(responseCode = "200", description = "成功获取AI Agent任务列表",
            content = @Content(schema = @Schema(implementation = AiAgentTask.class)))
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response getAiAgentTasks(@PathParam("agentId") String agentId,
                                   @QueryParam("status") String status) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }

            List<AiAgentTask> tasks = aiAgentService.getAiAgentTasks(agentId, status);
            return Response.ok(tasks).build();
        } catch (Exception e) {
            LOG.error("获取AI Agent任务列表失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取AI Agent任务列表失败: " + e.getMessage()).build();
        }
    }

    /**
     * 检查AI Agent是否可以接受新任务
     */
    @GET
    @Path("/agents/{agentId}/can-accept-task")
    @Operation(summary = "检查AI Agent任务接受能力", description = "检查AI Agent是否可以接受新任务")
    @APIResponse(responseCode = "200", description = "成功检查AI Agent任务接受能力")
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response canAcceptNewTask(@PathParam("agentId") String agentId) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }

            boolean canAccept = aiAgentService.canAcceptNewTask(agentId);
            return Response.ok(canAccept).build();
        } catch (Exception e) {
            LOG.error("检查AI Agent任务接受能力失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("检查AI Agent任务接受能力失败: " + e.getMessage()).build();
        }
    }

    /**
     * 更新AI Agent状态
     */
    @PUT
    @Path("/agents/{agentId}/status")
    @Operation(summary = "更新AI Agent状态", description = "更新AI Agent的激活状态")
    @APIResponse(responseCode = "200", description = "AI Agent状态更新成功")
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response updateAiAgentStatus(@PathParam("agentId") String agentId,
                                       UpdateStatusRequest request) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }

            aiAgentService.updateAiAgentStatus(agentId, request.status);
            return Response.ok("AI Agent状态更新成功").build();
        } catch (Exception e) {
            LOG.error("更新AI Agent状态失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("更新AI Agent状态失败: " + e.getMessage()).build();
        }
    }

    /**
     * 手动触发工作流任务处理
     */
    @POST
    @Path("/agents/{agentId}/process-task")
    @Operation(summary = "手动处理工作流任务", description = "手动触发AI Agent处理工作流任务")
    @APIResponse(responseCode = "200", description = "工作流任务处理成功")
    @APIResponse(responseCode = "404", description = "AI Agent不存在")
    public Response processWorkflowTask(@PathParam("agentId") String agentId,
                                       ProcessTaskRequest request) {
        try {
            AiAgent agent = aiAgentService.getAiAgent(agentId);
            if (agent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("AI Agent不存在: " + agentId).build();
            }

            if (!aiAgentService.canAcceptNewTask(agentId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("AI Agent当前无法接受新任务").build();
            }

            String result = aiAgentService.processWorkflowTask(
                    agentId, request.taskDescription, request.inputData, request.conditions);

            return Response.ok(result).build();
        } catch (Exception e) {
            LOG.error("处理工作流任务失败: " + agentId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("处理工作流任务失败: " + e.getMessage()).build();
        }
    }

    /**
     * 更新状态请求DTO
     */
    public static class UpdateStatusRequest {
        public String status;
    }

    /**
     * 处理任务请求DTO
     */
    public static class ProcessTaskRequest {
        public String taskDescription;
        public String inputData;
        public String conditions;
    }
}
