package com.example.crm.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查资源类
 * 提供应用健康状态检查接口
 */
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "健康检查", description = "应用健康状态检查")
public class HealthResource {

    /**
     * 健康检查端点
     */
    @GET
    @Operation(summary = "健康检查", description = "检查应用是否正常运行")
    @APIResponse(responseCode = "200", description = "应用运行正常")
    public Response health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "CRM Service");
        health.put("version", "1.0.0");
        
        return Response.ok(health).build();
    }

    /**
     * 就绪检查端点
     */
    @GET
    @Path("/ready")
    @Operation(summary = "就绪检查", description = "检查应用是否准备就绪")
    @APIResponse(responseCode = "200", description = "应用准备就绪")
    public Response ready() {
        Map<String, Object> ready = new HashMap<>();
        ready.put("status", "READY");
        ready.put("timestamp", LocalDateTime.now());
        
        return Response.ok(ready).build();
    }
}
