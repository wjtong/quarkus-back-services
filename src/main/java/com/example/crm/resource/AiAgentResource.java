package com.example.crm.resource;

import com.example.crm.entity.AiAgent;
import com.example.crm.entity.AiAgentTask;
import com.example.crm.entity.AiAgentConversation;
import com.example.crm.service.AiAgentService;
import com.example.crm.service.AiAgentTaskService;
import com.example.crm.service.AiAgentConversationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * AI Agent REST 资源类
 * 提供 AI Agent 管理的 REST API 端点
 */
@Path("/api/ai-agents")
@Tag(name = "AI Agent Management", description = "AI Agent 管理相关接口")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiAgentResource {

    private static final Logger LOG = Logger.getLogger(AiAgentResource.class);

    @Inject
    AiAgentService aiAgentService;

    @Inject
    AiAgentTaskService taskService;

    @Inject
    AiAgentConversationService conversationService;

    /**
     * 为员工创建 AI Agent
     */
    @POST
    @Path("/create")
    @Operation(
        summary = "创建 AI Agent", 
        description = "为指定员工创建专属的 AI Agent"
    )
    @APIResponse(responseCode = "200", description = "创建成功")
    @APIResponse(responseCode = "400", description = "请求参数错误")
    @APIResponse(responseCode = "500", description = "服务器内部错误")
    @RolesAllowed("ADMIN")
    public Response createAgent(
        @Parameter(description = "员工 ID", required = true) @QueryParam("employeeId") String employeeId,
        @Parameter(description = "Agent 名称", required = true) @QueryParam("agentName") String agentName,
        @Parameter(description = "职位", required = true) @QueryParam("position") String position,
        @Parameter(description = "部门", required = true) @QueryParam("department") String department,
        @Parameter(description = "职责描述") @QueryParam("responsibilities") String responsibilities,
        @Parameter(description = "权限列表") @QueryParam("permissions") String permissions) {
        
        try {
            if (employeeId == null || agentName == null || position == null || department == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "缺少必需参数"))
                    .build();
            }

            AiAgent agent = aiAgentService.createAgentForEmployee(
                employeeId, agentName, position, department, responsibilities, permissions);
            
            return Response.ok(Map.of(
                "success", true,
                "message", "AI Agent 创建成功",
                "agent", agent
            )).build();
        } catch (Exception e) {
            LOG.error("创建 AI Agent 失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "创建 AI Agent 失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取所有 AI Agent
     */
    @GET
    @Operation(
        summary = "获取所有 AI Agent", 
        description = "获取系统中所有的 AI Agent 列表"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getAllAgents() {
        try {
            List<AiAgent> agents = aiAgentService.getAllAgents();
            return Response.ok(Map.of(
                "success", true,
                "agents", agents,
                "count", agents.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取 AI Agent 列表失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 AI Agent 列表失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 根据员工 ID 获取 AI Agent
     */
    @GET
    @Path("/employee/{employeeId}")
    @Operation(
        summary = "根据员工 ID 获取 AI Agent", 
        description = "获取指定员工的专属 AI Agent"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("USER")
    public Response getAgentByEmployeeId(
        @Parameter(description = "员工 ID", required = true) @PathParam("employeeId") String employeeId) {
        
        try {
            return aiAgentService.getAgentByEmployeeId(employeeId)
                .map(agent -> Response.ok(Map.of(
                    "success", true,
                    "agent", agent
                )).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "未找到该员工的 AI Agent"))
                    .build());
        } catch (Exception e) {
            LOG.error("获取 AI Agent 失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 AI Agent 失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 根据部门获取 AI Agent 列表
     */
    @GET
    @Path("/department/{department}")
    @Operation(
        summary = "根据部门获取 AI Agent", 
        description = "获取指定部门的所有 AI Agent"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getAgentsByDepartment(
        @Parameter(description = "部门名称", required = true) @PathParam("department") String department) {
        
        try {
            List<AiAgent> agents = aiAgentService.getAgentsByDepartment(department);
            return Response.ok(Map.of(
                "success", true,
                "agents", agents,
                "count", agents.size(),
                "department", department
            )).build();
        } catch (Exception e) {
            LOG.error("获取部门 AI Agent 失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取部门 AI Agent 失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 更新 AI Agent 信息
     */
    @PUT
    @Path("/{agentId}")
    @Operation(
        summary = "更新 AI Agent", 
        description = "更新指定 AI Agent 的信息"
    )
    @APIResponse(responseCode = "200", description = "更新成功")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("ADMIN")
    public Response updateAgent(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId,
        @Parameter(description = "Agent 名称") @QueryParam("agentName") String agentName,
        @Parameter(description = "职位") @QueryParam("position") String position,
        @Parameter(description = "部门") @QueryParam("department") String department,
        @Parameter(description = "职责描述") @QueryParam("responsibilities") String responsibilities,
        @Parameter(description = "权限列表") @QueryParam("permissions") String permissions,
        @Parameter(description = "能力列表") @QueryParam("capabilities") String capabilities,
        @Parameter(description = "性格特征") @QueryParam("personalityTraits") String personalityTraits,
        @Parameter(description = "沟通风格") @QueryParam("communicationStyle") String communicationStyle) {
        
        try {
            AiAgent agent = aiAgentService.updateAgent(
                agentId, agentName, position, department, responsibilities, 
                permissions, capabilities, personalityTraits, communicationStyle);
            
            return Response.ok(Map.of(
                "success", true,
                "message", "AI Agent 更新成功",
                "agent", agent
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("更新 AI Agent 失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "更新 AI Agent 失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 激活/停用 AI Agent
     */
    @PUT
    @Path("/{agentId}/status")
    @Operation(
        summary = "切换 AI Agent 状态", 
        description = "激活或停用指定的 AI Agent"
    )
    @APIResponse(responseCode = "200", description = "状态更新成功")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("ADMIN")
    public Response toggleAgentStatus(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId,
        @Parameter(description = "是否激活", required = true) @QueryParam("active") boolean active) {
        
        try {
            AiAgent agent = aiAgentService.toggleAgentStatus(agentId, active);
            return Response.ok(Map.of(
                "success", true,
                "message", "AI Agent 状态更新成功",
                "agent", agent,
                "status", active ? "激活" : "停用"
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("更新 AI Agent 状态失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "更新 AI Agent 状态失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 为 AI Agent 分配任务
     */
    @POST
    @Path("/{agentId}/tasks")
    @Operation(
        summary = "分配任务给 AI Agent", 
        description = "为指定的 AI Agent 分配新任务"
    )
    @APIResponse(responseCode = "200", description = "任务分配成功")
    @APIResponse(responseCode = "400", description = "请求参数错误")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("USER")
    public Response assignTask(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId,
        @Parameter(description = "任务标题", required = true) @QueryParam("taskTitle") String taskTitle,
        @Parameter(description = "任务描述") @QueryParam("taskDescription") String taskDescription,
        @Parameter(description = "任务类型", required = true) @QueryParam("taskType") String taskType,
        @Parameter(description = "输入数据") @QueryParam("inputData") String inputData,
        @Parameter(description = "优先级") @QueryParam("priority") Integer priority) {
        
        try {
            if (taskTitle == null || taskType == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "缺少必需参数"))
                    .build();
            }

            AiAgentTask task = aiAgentService.assignTaskToAgent(
                agentId, taskTitle, taskDescription, taskType, inputData, priority);
            
            return Response.ok(Map.of(
                "success", true,
                "message", "任务分配成功",
                "task", task
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("分配任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "分配任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取 AI Agent 的任务列表
     */
    @GET
    @Path("/{agentId}/tasks")
    @Operation(
        summary = "获取 AI Agent 任务列表", 
        description = "获取指定 AI Agent 的所有任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getAgentTasks(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId) {
        
        try {
            List<AiAgentTask> tasks = aiAgentService.getAgentTasks(agentId);
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size(),
                "agentId", agentId
            )).build();
        } catch (Exception e) {
            LOG.error("获取 AI Agent 任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 AI Agent 任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取 AI Agent 的对话历史
     */
    @GET
    @Path("/{agentId}/conversations")
    @Operation(
        summary = "获取 AI Agent 对话历史", 
        description = "获取指定 AI Agent 的对话记录"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getAgentConversations(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId) {
        
        try {
            List<AiAgentConversation> conversations = aiAgentService.getAgentConversations(agentId);
            return Response.ok(Map.of(
                "success", true,
                "conversations", conversations,
                "count", conversations.size(),
                "agentId", agentId
            )).build();
        } catch (Exception e) {
            LOG.error("获取 AI Agent 对话历史失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 AI Agent 对话历史失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取 AI Agent 统计信息
     */
    @GET
    @Path("/{agentId}/statistics")
    @Operation(
        summary = "获取 AI Agent 统计信息", 
        description = "获取指定 AI Agent 的详细统计信息"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("USER")
    public Response getAgentStatistics(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId) {
        
        try {
            String statistics = aiAgentService.getAgentStatistics(agentId);
            return Response.ok(Map.of(
                "success", true,
                "statistics", statistics,
                "agentId", agentId
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("获取 AI Agent 统计信息失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 AI Agent 统计信息失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 训练 AI Agent
     */
    @POST
    @Path("/{agentId}/train")
    @Operation(
        summary = "训练 AI Agent", 
        description = "使用指定数据训练 AI Agent"
    )
    @APIResponse(responseCode = "200", description = "训练成功")
    @APIResponse(responseCode = "404", description = "未找到 AI Agent")
    @RolesAllowed("ADMIN")
    public Response trainAgent(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId,
        @Parameter(description = "训练数据", required = true) @QueryParam("trainingData") String trainingData) {
        
        try {
            AiAgent agent = aiAgentService.trainAgent(agentId, trainingData);
            return Response.ok(Map.of(
                "success", true,
                "message", "AI Agent 训练成功",
                "agent", agent
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("训练 AI Agent 失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "训练 AI Agent 失败: " + e.getMessage()))
                .build();
        }
    }
}
