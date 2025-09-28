package com.example.crm.resource;

import com.example.crm.entity.AiAgentTask;
import com.example.crm.service.AiAgentTaskService;
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
 * AI Agent 任务管理 REST 资源类
 * 提供 AI Agent 任务管理的 REST API 端点
 */
@Path("/api/ai-agent-tasks")
@Tag(name = "AI Agent Task Management", description = "AI Agent 任务管理相关接口")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiAgentTaskResource {

    private static final Logger LOG = Logger.getLogger(AiAgentTaskResource.class);

    @Inject
    AiAgentTaskService taskService;

    /**
     * 获取所有任务
     */
    @GET
    @Operation(
        summary = "获取所有任务", 
        description = "获取系统中所有的 AI Agent 任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getAllTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getAllTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取任务列表失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取任务列表失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 根据 Agent ID 获取任务列表
     */
    @GET
    @Path("/agent/{agentId}")
    @Operation(
        summary = "根据 Agent ID 获取任务", 
        description = "获取指定 AI Agent 的所有任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getTasksByAgentId(
        @Parameter(description = "AI Agent ID", required = true) @PathParam("agentId") String agentId) {
        
        try {
            List<AiAgentTask> tasks = taskService.getTasksByAgentId(agentId);
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size(),
                "agentId", agentId
            )).build();
        } catch (Exception e) {
            LOG.error("获取 Agent 任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取 Agent 任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 根据状态获取任务列表
     */
    @GET
    @Path("/status/{status}")
    @Operation(
        summary = "根据状态获取任务", 
        description = "获取指定状态的所有任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getTasksByStatus(
        @Parameter(description = "任务状态", required = true) @PathParam("status") String status) {
        
        try {
            List<AiAgentTask> tasks = taskService.getTasksByStatus(status);
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size(),
                "status", status
            )).build();
        } catch (Exception e) {
            LOG.error("根据状态获取任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "根据状态获取任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取待处理任务
     */
    @GET
    @Path("/pending")
    @Operation(
        summary = "获取待处理任务", 
        description = "获取所有待处理的任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getPendingTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getPendingTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取待处理任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取待处理任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取进行中任务
     */
    @GET
    @Path("/in-progress")
    @Operation(
        summary = "获取进行中任务", 
        description = "获取所有进行中的任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getInProgressTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getInProgressTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取进行中任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取进行中任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取已完成任务
     */
    @GET
    @Path("/completed")
    @Operation(
        summary = "获取已完成任务", 
        description = "获取所有已完成的任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getCompletedTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getCompletedTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取已完成任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取已完成任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取失败任务
     */
    @GET
    @Path("/failed")
    @Operation(
        summary = "获取失败任务", 
        description = "获取所有失败的任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getFailedTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getFailedTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取失败任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取失败任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取过期任务
     */
    @GET
    @Path("/overdue")
    @Operation(
        summary = "获取过期任务", 
        description = "获取所有过期的任务"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getOverdueTasks() {
        try {
            List<AiAgentTask> tasks = taskService.getOverdueTasks();
            return Response.ok(Map.of(
                "success", true,
                "tasks", tasks,
                "count", tasks.size()
            )).build();
        } catch (Exception e) {
            LOG.error("获取过期任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取过期任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 开始执行任务
     */
    @PUT
    @Path("/{taskId}/start")
    @Operation(
        summary = "开始执行任务", 
        description = "开始执行指定的任务"
    )
    @APIResponse(responseCode = "200", description = "开始执行成功")
    @APIResponse(responseCode = "400", description = "任务状态不允许开始")
    @APIResponse(responseCode = "404", description = "未找到任务")
    @RolesAllowed("USER")
    public Response startTask(
        @Parameter(description = "任务 ID", required = true) @PathParam("taskId") String taskId) {
        
        try {
            AiAgentTask task = taskService.startTask(taskId);
            return Response.ok(Map.of(
                "success", true,
                "message", "任务开始执行",
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
            LOG.error("开始执行任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "开始执行任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 完成任务
     */
    @PUT
    @Path("/{taskId}/complete")
    @Operation(
        summary = "完成任务", 
        description = "标记指定任务为完成状态"
    )
    @APIResponse(responseCode = "200", description = "任务完成成功")
    @APIResponse(responseCode = "400", description = "任务状态不允许完成")
    @APIResponse(responseCode = "404", description = "未找到任务")
    @RolesAllowed("USER")
    public Response completeTask(
        @Parameter(description = "任务 ID", required = true) @PathParam("taskId") String taskId,
        @Parameter(description = "输出数据") @QueryParam("outputData") String outputData,
        @Parameter(description = "结果摘要") @QueryParam("resultSummary") String resultSummary,
        @Parameter(description = "置信度分数") @QueryParam("confidenceScore") Double confidenceScore,
        @Parameter(description = "质量评分") @QueryParam("qualityRating") Double qualityRating) {
        
        try {
            AiAgentTask task = taskService.completeTask(taskId, outputData, resultSummary, confidenceScore, qualityRating);
            return Response.ok(Map.of(
                "success", true,
                "message", "任务完成",
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
            LOG.error("完成任务失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "完成任务失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 任务失败
     */
    @PUT
    @Path("/{taskId}/fail")
    @Operation(
        summary = "标记任务失败", 
        description = "标记指定任务为失败状态"
    )
    @APIResponse(responseCode = "200", description = "任务失败标记成功")
    @APIResponse(responseCode = "404", description = "未找到任务")
    @RolesAllowed("USER")
    public Response failTask(
        @Parameter(description = "任务 ID", required = true) @PathParam("taskId") String taskId,
        @Parameter(description = "错误消息", required = true) @QueryParam("errorMessage") String errorMessage) {
        
        try {
            AiAgentTask task = taskService.failTask(taskId, errorMessage);
            return Response.ok(Map.of(
                "success", true,
                "message", "任务标记为失败",
                "task", task
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("标记任务失败失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "标记任务失败失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 更新任务反馈
     */
    @PUT
    @Path("/{taskId}/feedback")
    @Operation(
        summary = "更新任务反馈", 
        description = "为指定任务添加反馈信息"
    )
    @APIResponse(responseCode = "200", description = "反馈更新成功")
    @APIResponse(responseCode = "404", description = "未找到任务")
    @RolesAllowed("USER")
    public Response updateTaskFeedback(
        @Parameter(description = "任务 ID", required = true) @PathParam("taskId") String taskId,
        @Parameter(description = "反馈内容") @QueryParam("feedback") String feedback,
        @Parameter(description = "质量评分") @QueryParam("qualityRating") Double qualityRating) {
        
        try {
            AiAgentTask task = taskService.updateTaskFeedback(taskId, feedback, qualityRating);
            return Response.ok(Map.of(
                "success", true,
                "message", "任务反馈更新成功",
                "task", task
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("更新任务反馈失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "更新任务反馈失败: " + e.getMessage()))
                .build();
        }
    }

    /**
     * 获取任务统计信息
     */
    @GET
    @Path("/statistics")
    @Operation(
        summary = "获取任务统计信息", 
        description = "获取系统中所有任务的统计信息"
    )
    @APIResponse(responseCode = "200", description = "获取成功")
    @RolesAllowed("USER")
    public Response getTaskStatistics() {
        try {
            String statistics = taskService.getTaskStatistics();
            return Response.ok(Map.of(
                "success", true,
                "statistics", statistics
            )).build();
        } catch (Exception e) {
            LOG.error("获取任务统计信息失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "获取任务统计信息失败: " + e.getMessage()))
                .build();
        }
    }
}
