package com.example.crm.resource;

import com.example.crm.entity.WorkflowInstance;
import com.example.crm.entity.WorkflowType;
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
 * 工作流 REST API 资源类
 * 提供工作流相关的 RESTful API 端点
 */
@Path("/api/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "工作流管理", description = "工作流相关的API端点")
public class WorkflowResource {

    private static final Logger LOG = Logger.getLogger(WorkflowResource.class);

    @Inject
    WorkflowService workflowService;

    /**
     * 获取所有激活的工作流类型
     */
    @GET
    @Path("/types")
    @Operation(summary = "获取工作流类型列表", description = "获取系统中所有激活的工作流类型")
    @APIResponse(responseCode = "200", description = "成功获取工作流类型列表",
            content = @Content(schema = @Schema(implementation = WorkflowType.class)))
    public Response getWorkflowTypes() {
        try {
            List<WorkflowType> types = workflowService.getActiveWorkflowTypes();
            return Response.ok(types).build();
        } catch (Exception e) {
            LOG.error("获取工作流类型失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取工作流类型失败: " + e.getMessage()).build();
        }
    }

    /**
     * 根据ID获取工作流类型
     */
    @GET
    @Path("/types/{workflowTypeId}")
    @Operation(summary = "获取工作流类型详情", description = "根据ID获取工作流类型的详细信息")
    @APIResponse(responseCode = "200", description = "成功获取工作流类型详情",
            content = @Content(schema = @Schema(implementation = WorkflowType.class)))
    @APIResponse(responseCode = "404", description = "工作流类型不存在")
    public Response getWorkflowType(@PathParam("workflowTypeId") String workflowTypeId) {
        try {
            WorkflowType type = WorkflowType.findById(workflowTypeId);
            if (type == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("工作流类型不存在: " + workflowTypeId).build();
            }
            return Response.ok(type).build();
        } catch (Exception e) {
            LOG.error("获取工作流类型失败: " + workflowTypeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取工作流类型失败: " + e.getMessage()).build();
        }
    }

    /**
     * 获取工作流类型的步骤列表
     */
    @GET
    @Path("/types/{workflowTypeId}/steps")
    @Operation(summary = "获取工作流步骤列表", description = "获取指定工作流类型的所有步骤")
    @APIResponse(responseCode = "200", description = "成功获取工作流步骤列表")
    @APIResponse(responseCode = "404", description = "工作流类型不存在")
    public Response getWorkflowSteps(@PathParam("workflowTypeId") String workflowTypeId) {
        try {
            WorkflowType type = WorkflowType.findById(workflowTypeId);
            if (type == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("工作流类型不存在: " + workflowTypeId).build();
            }

            List<com.example.crm.entity.WorkflowStep> steps = workflowService.getWorkflowSteps(workflowTypeId);
            return Response.ok(steps).build();
        } catch (Exception e) {
            LOG.error("获取工作流步骤失败: " + workflowTypeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取工作流步骤失败: " + e.getMessage()).build();
        }
    }

    /**
     * 创建工作流实例
     */
    @POST
    @Path("/instances")
    @Operation(summary = "创建工作流实例", description = "发起新的工作流申请")
    @APIResponse(responseCode = "201", description = "工作流实例创建成功",
            content = @Content(schema = @Schema(implementation = WorkflowInstance.class)))
    @APIResponse(responseCode = "400", description = "请求参数错误")
    public Response createWorkflowInstance(CreateWorkflowRequest request) {
        try {
            if (request.workflowTypeId == null || request.workflowTypeId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("工作流类型ID不能为空").build();
            }
            if (request.initiatorId == null || request.initiatorId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("发起人ID不能为空").build();
            }
            if (request.title == null || request.title.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("标题不能为空").build();
            }

            WorkflowInstance instance = workflowService.createWorkflowInstance(
                    request.workflowTypeId,
                    request.initiatorId,
                    request.title,
                    request.description,
                    request.requestData
            );

            return Response.status(Response.Status.CREATED).entity(instance).build();
        } catch (Exception e) {
            LOG.error("创建工作流实例失败", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("创建工作流实例失败: " + e.getMessage()).build();
        }
    }

    /**
     * 根据ID获取工作流实例
     */
    @GET
    @Path("/instances/{workflowInstanceId}")
    @Operation(summary = "获取工作流实例详情", description = "根据ID获取工作流实例的详细信息")
    @APIResponse(responseCode = "200", description = "成功获取工作流实例详情",
            content = @Content(schema = @Schema(implementation = WorkflowInstance.class)))
    @APIResponse(responseCode = "404", description = "工作流实例不存在")
    public Response getWorkflowInstance(@PathParam("workflowInstanceId") String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("工作流实例不存在: " + workflowInstanceId).build();
            }
            return Response.ok(instance).build();
        } catch (Exception e) {
            LOG.error("获取工作流实例失败: " + workflowInstanceId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取工作流实例失败: " + e.getMessage()).build();
        }
    }

    /**
     * 获取工作流实例的执行历史
     */
    @GET
    @Path("/instances/{workflowInstanceId}/executions")
    @Operation(summary = "获取工作流执行历史", description = "获取工作流实例的详细执行历史")
    @APIResponse(responseCode = "200", description = "成功获取工作流执行历史")
    @APIResponse(responseCode = "404", description = "工作流实例不存在")
    public Response getWorkflowExecutions(@PathParam("workflowInstanceId") String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("工作流实例不存在: " + workflowInstanceId).build();
            }

            List<com.example.crm.entity.WorkflowExecution> executions = workflowService.getWorkflowExecutions(workflowInstanceId);
            return Response.ok(executions).build();
        } catch (Exception e) {
            LOG.error("获取工作流执行历史失败: " + workflowInstanceId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("获取工作流执行历史失败: " + e.getMessage()).build();
        }
    }

    /**
     * 重新启动工作流实例
     */
    @POST
    @Path("/instances/{workflowInstanceId}/restart")
    @Operation(summary = "重新启动工作流", description = "重新启动指定的工作流实例")
    @APIResponse(responseCode = "200", description = "工作流重新启动成功")
    @APIResponse(responseCode = "404", description = "工作流实例不存在")
    public Response restartWorkflowInstance(@PathParam("workflowInstanceId") String workflowInstanceId) {
        try {
            WorkflowInstance instance = workflowService.getWorkflowInstance(workflowInstanceId);
            if (instance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("工作流实例不存在: " + workflowInstanceId).build();
            }

            workflowService.startWorkflowExecution(workflowInstanceId);
            return Response.ok("工作流重新启动成功").build();
        } catch (Exception e) {
            LOG.error("重新启动工作流失败: " + workflowInstanceId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("重新启动工作流失败: " + e.getMessage()).build();
        }
    }

    /**
     * 创建工作流请求DTO
     */
    public static class CreateWorkflowRequest {
        public String workflowTypeId;
        public String initiatorId;
        public String title;
        public String description;
        public String requestData;
    }
}
