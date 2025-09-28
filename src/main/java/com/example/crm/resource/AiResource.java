package com.example.crm.resource;

import com.example.crm.ai.CrmAiServiceImpl;
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

/**
 * AI Agent REST 资源类
 * 提供 AI 相关的 REST API 端点
 */
@Path("/api/ai")
@Tag(name = "AI Agent", description = "AI 智能助手相关接口")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {
    
    private static final Logger LOG = Logger.getLogger(AiResource.class);
    
    @Inject
    CrmAiServiceImpl aiService;
    
    @POST
    @Path("/chat")
    @Operation(
        summary = "AI 聊天", 
        description = "与 AI 助手进行对话，可以询问 CRM 系统相关问题"
    )
    @APIResponse(responseCode = "200", description = "聊天成功")
    @APIResponse(responseCode = "500", description = "服务器内部错误")
    @RolesAllowed("USER")
    public Response chat(
        @Parameter(description = "用户消息", required = true)
        @QueryParam("message") String message) {
        
        if (message == null || message.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"消息内容不能为空\"}")
                .build();
        }
        
        try {
            LOG.info("收到 AI 聊天请求: " + message);
            String response = aiService.chat(message);
            return Response.ok("{\"response\": \"" + response + "\"}").build();
        } catch (Exception e) {
            LOG.error("AI 聊天服务异常", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"AI 服务暂时不可用\"}")
                .build();
        }
    }
    
    @POST
    @Path("/analyze")
    @Operation(
        summary = "数据分析", 
        description = "使用 AI 分析客户数据，提供业务洞察"
    )
    @APIResponse(responseCode = "200", description = "分析成功")
    @APIResponse(responseCode = "500", description = "服务器内部错误")
    @RolesAllowed("USER")
    public Response analyze(
        @Parameter(description = "分析查询", required = true)
        @QueryParam("query") String query) {
        
        if (query == null || query.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"查询内容不能为空\"}")
                .build();
        }
        
        try {
            LOG.info("收到数据分析请求: " + query);
            String response = aiService.analyzeCustomerData(query);
            return Response.ok("{\"analysis\": \"" + response + "\"}").build();
        } catch (Exception e) {
            LOG.error("AI 数据分析服务异常", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"数据分析失败\"}")
                .build();
        }
    }
    
    @POST
    @Path("/report")
    @Operation(
        summary = "生成报告", 
        description = "使用 AI 生成客户报告和业务建议"
    )
    @APIResponse(responseCode = "200", description = "报告生成成功")
    @APIResponse(responseCode = "500", description = "服务器内部错误")
    @RolesAllowed("USER")
    public Response generateReport(
        @Parameter(description = "报告需求", required = true)
        @QueryParam("requirements") String requirements) {
        
        if (requirements == null || requirements.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"报告需求不能为空\"}")
                .build();
        }
        
        try {
            LOG.info("收到报告生成请求: " + requirements);
            String response = aiService.generateCustomerReport(requirements);
            return Response.ok("{\"report\": \"" + response + "\"}").build();
        } catch (Exception e) {
            LOG.error("AI 报告生成服务异常", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"报告生成失败\"}")
                .build();
        }
    }
    
    @POST
    @Path("/service")
    @Operation(
        summary = "客户服务", 
        description = "AI 客户服务支持，回答客户问题"
    )
    @APIResponse(responseCode = "200", description = "服务响应成功")
    @APIResponse(responseCode = "500", description = "服务器内部错误")
    @RolesAllowed("USER")
    public Response customerService(
        @Parameter(description = "客户问题", required = true)
        @QueryParam("question") String question) {
        
        if (question == null || question.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"问题内容不能为空\"}")
                .build();
        }
        
        try {
            LOG.info("收到客户服务请求: " + question);
            String response = aiService.customerService(question);
            return Response.ok("{\"answer\": \"" + response + "\"}").build();
        } catch (Exception e) {
            LOG.error("AI 客户服务异常", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"客户服务暂时不可用\"}")
                .build();
        }
    }
    
    @GET
    @Path("/status")
    @Operation(
        summary = "AI 服务状态", 
        description = "获取 AI 服务的运行状态"
    )
    @APIResponse(responseCode = "200", description = "状态查询成功")
    @RolesAllowed("USER")
    public Response getStatus() {
        try {
            String status = aiService.getStatus();
            return Response.ok("{\"status\": \"" + status + "\"}").build();
        } catch (Exception e) {
            LOG.error("获取 AI 服务状态异常", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"无法获取服务状态\"}")
                .build();
        }
    }
}
