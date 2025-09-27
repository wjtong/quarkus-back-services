package com.example.crm.resource;

import com.example.crm.dto.QuickOrderRequest;
import com.example.crm.entity.OrderHeader;
import com.example.crm.service.QuickOrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

/**
 * 快速订单 REST API 资源类
 * 提供快速创建单个商品销售订单的 RESTful 接口
 */
@Path("/quick-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "快速订单管理", description = "快速创建单个商品销售订单的操作")
public class QuickOrderResource {

    @Inject
    QuickOrderService quickOrderService;

    /**
     * 创建快速订单
     */
    @POST
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "创建快速订单", description = "针对单个商品快速创建销售订单，同时创建订单头和订单项")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "快速订单创建成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或商品不存在")
    })
    public Response createQuickOrder(@Valid QuickOrderRequest request) {
        try {
            OrderHeader createdOrder = quickOrderService.createQuickOrder(request);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"创建快速订单时发生错误: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 创建带客户信息的快速订单
     */
    @POST
    @Path("/with-customer")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "创建带客户信息的快速订单", description = "为指定客户创建快速订单")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "快速订单创建成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或客户/商品不存在")
    })
    public Response createQuickOrderWithCustomer(@Valid QuickOrderRequest request,
                                               @QueryParam("customerId") String customerId) {
        try {
            if (customerId == null || customerId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"客户ID不能为空\"}")
                        .build();
            }
            
            OrderHeader createdOrder = quickOrderService.createQuickOrderWithCustomer(request, customerId);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"创建快速订单时发生错误: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 批量创建快速订单
     */
    @POST
    @Path("/batch")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "批量创建快速订单", description = "批量创建多个快速订单")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "批量快速订单创建成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效")
    })
    public Response createBatchQuickOrders(@Valid List<QuickOrderRequest> requests) {
        try {
            if (requests == null || requests.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"请求列表不能为空\"}")
                        .build();
            }
            
            List<OrderHeader> createdOrders = quickOrderService.createBatchQuickOrders(requests);
            return Response.status(Response.Status.CREATED).entity(createdOrders).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"批量创建快速订单时发生错误: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 获取快速订单统计信息
     */
    @GET
    @Path("/statistics")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取快速订单统计信息", description = "获取快速订单的统计信息")
    @APIResponse(responseCode = "200", description = "成功获取统计信息")
    public Response getQuickOrderStatistics() {
        try {
            Map<String, Object> statistics = quickOrderService.getQuickOrderStatistics();
            return Response.ok(statistics).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"获取统计信息时发生错误: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 创建快速订单（简化版本）
     * 只需要商品ID、数量、单价和销售日期
     */
    @POST
    @Path("/simple")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "创建简化快速订单", description = "使用最简参数创建快速订单")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "快速订单创建成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求参数无效")
    })
    public Response createSimpleQuickOrder(@QueryParam("productId") String productId,
                                         @QueryParam("quantity") String quantity,
                                         @QueryParam("unitPrice") String unitPrice,
                                         @QueryParam("salesDate") String salesDate) {
        try {
            // 验证必需参数
            if (productId == null || productId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"商品ID不能为空\"}")
                        .build();
            }
            if (quantity == null || quantity.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"数量不能为空\"}")
                        .build();
            }
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"单价不能为空\"}")
                        .build();
            }
            if (salesDate == null || salesDate.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"销售日期不能为空\"}")
                        .build();
            }

            // 解析参数
            java.math.BigDecimal quantityDecimal = new java.math.BigDecimal(quantity);
            java.math.BigDecimal unitPriceDecimal = new java.math.BigDecimal(unitPrice);
            java.time.LocalDateTime salesDateTime = java.time.LocalDateTime.parse(salesDate);

            // 创建请求对象
            QuickOrderRequest request = new QuickOrderRequest();
            request.productId = productId;
            request.quantity = quantityDecimal;
            request.unitPrice = unitPriceDecimal;
            request.salesDate = salesDateTime;

            // 创建订单
            OrderHeader createdOrder = quickOrderService.createQuickOrder(request);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"数量或单价格式不正确\"}")
                    .build();
        } catch (java.time.format.DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"销售日期格式不正确，请使用 yyyy-MM-ddTHH:mm:ss 格式\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"创建快速订单时发生错误: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
