package com.example.crm.resource;

import com.example.crm.entity.OrderHeader;
import com.example.crm.entity.OrderItem;
import com.example.crm.service.OrderHeaderService;
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
import java.util.Optional;

/**
 * OrderHeader REST API 资源类
 * 提供订单管理的 RESTful 接口
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "订单管理", description = "订单信息的增删改查操作")
public class OrderHeaderResource {

    @Inject
    OrderHeaderService orderHeaderService;

    /**
     * 获取所有订单
     */
    @GET
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取所有订单", description = "返回系统中所有订单的信息，支持 RSQL 查询")
    @APIResponse(responseCode = "200", description = "成功获取订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getAllOrders(@QueryParam("q") String rsqlQuery,
                                @QueryParam("page") @DefaultValue("0") int page,
                                @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<OrderHeader> orders;
            if (rsqlQuery != null && !rsqlQuery.trim().isEmpty()) {
                // 验证 RSQL 查询
                if (!orderHeaderService.isValidRsqlQuery(rsqlQuery)) {
                    String error = orderHeaderService.getRsqlParseError(rsqlQuery);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                            .build();
                }
                
                if (page >= 0 && size > 0) {
                    orders = orderHeaderService.queryOrdersWithPagination(rsqlQuery, page, size);
                } else {
                    orders = orderHeaderService.queryOrders(rsqlQuery);
                }
            } else {
                if (page >= 0 && size > 0) {
                    orders = orderHeaderService.getAllOrders().stream()
                            .skip(page * size)
                            .limit(size)
                            .collect(java.util.stream.Collectors.toList());
                } else {
                    orders = orderHeaderService.getAllOrders();
                }
            }
            return Response.ok(orders).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 根据ID获取订单
     */
    @GET
    @Path("/{orderId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据ID获取订单", description = "根据订单 ID 获取特定订单的详细信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "成功获取订单信息",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "404", description = "订单不存在")
    })
    public Response getOrderById(@PathParam("orderId") String orderId) {
        Optional<OrderHeader> order = orderHeaderService.getOrderById(orderId);
        if (order.isPresent()) {
            return Response.ok(order.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"订单不存在，ID: " + orderId + "\"}")
                    .build();
        }
    }

    /**
     * 创建新订单
     */
    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "创建新订单", description = "创建新的订单记录")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "订单创建成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或订单 ID 已存在")
    })
    public Response createOrder(@Valid OrderHeader order) {
        try {
            OrderHeader createdOrder = orderHeaderService.createOrder(order);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 更新订单信息
     */
    @PUT
    @Path("/{orderId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "更新订单信息", description = "更新指定订单的信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单信息更新成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "订单不存在")
    })
    public Response updateOrder(@PathParam("orderId") String orderId, @Valid OrderHeader order) {
        try {
            OrderHeader updatedOrder = orderHeaderService.updateOrder(orderId, order);
            return Response.ok(updatedOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 删除订单
     */
    @DELETE
    @Path("/{orderId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "删除订单", description = "删除指定的订单记录")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "订单删除成功"),
            @APIResponse(responseCode = "404", description = "订单不存在")
    })
    public Response deleteOrder(@PathParam("orderId") String orderId) {
        boolean deleted = orderHeaderService.deleteOrder(orderId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"订单不存在，ID: " + orderId + "\"}")
                    .build();
        }
    }

    /**
     * 搜索订单
     */
    @GET
    @Path("/search")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "搜索订单", description = "根据名称搜索订单")
    @APIResponse(responseCode = "200", description = "搜索成功",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response searchOrders(@QueryParam("name") String name) {
        List<OrderHeader> orders = orderHeaderService.searchOrdersByName(name);
        return Response.ok(orders).build();
    }

    /**
     * 根据类型获取订单列表
     */
    @GET
    @Path("/type/{orderTypeId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据类型获取订单", description = "根据订单类型获取订单列表")
    @APIResponse(responseCode = "200", description = "成功获取订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getOrdersByType(@PathParam("orderTypeId") String orderTypeId) {
        List<OrderHeader> orders = orderHeaderService.getOrdersByType(orderTypeId);
        return Response.ok(orders).build();
    }

    /**
     * 根据状态获取订单列表
     */
    @GET
    @Path("/status/{statusId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据状态获取订单", description = "根据订单状态获取订单列表")
    @APIResponse(responseCode = "200", description = "成功获取订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getOrdersByStatus(@PathParam("statusId") String statusId) {
        List<OrderHeader> orders = orderHeaderService.getOrdersByStatus(statusId);
        return Response.ok(orders).build();
    }

    /**
     * 获取待处理订单列表
     */
    @GET
    @Path("/pending")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取待处理订单", description = "获取所有待处理的订单")
    @APIResponse(responseCode = "200", description = "成功获取待处理订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getPendingOrders() {
        List<OrderHeader> orders = orderHeaderService.getPendingOrders();
        return Response.ok(orders).build();
    }

    /**
     * 获取已完成订单列表
     */
    @GET
    @Path("/completed")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取已完成订单", description = "获取所有已完成的订单")
    @APIResponse(responseCode = "200", description = "成功获取已完成订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getCompletedOrders() {
        List<OrderHeader> orders = orderHeaderService.getCompletedOrders();
        return Response.ok(orders).build();
    }

    /**
     * 获取已取消订单列表
     */
    @GET
    @Path("/cancelled")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取已取消订单", description = "获取所有已取消的订单")
    @APIResponse(responseCode = "200", description = "成功获取已取消订单列表",
            content = @Content(schema = @Schema(implementation = OrderHeader.class)))
    public Response getCancelledOrders() {
        List<OrderHeader> orders = orderHeaderService.getCancelledOrders();
        return Response.ok(orders).build();
    }

    /**
     * 更新订单状态
     */
    @PUT
    @Path("/{orderId}/status")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "更新订单状态", description = "更新指定订单的状态")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单状态更新成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "404", description = "订单不存在")
    })
    public Response updateOrderStatus(@PathParam("orderId") String orderId, 
                                     @QueryParam("statusId") String statusId) {
        try {
            OrderHeader updatedOrder = orderHeaderService.updateOrderStatus(orderId, statusId);
            return Response.ok(updatedOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 标记订单为已查看
     */
    @PUT
    @Path("/{orderId}/viewed")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "标记订单为已查看", description = "标记指定订单为已查看")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单标记成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "404", description = "订单不存在")
    })
    public Response markOrderAsViewed(@PathParam("orderId") String orderId) {
        try {
            OrderHeader updatedOrder = orderHeaderService.markOrderAsViewed(orderId);
            return Response.ok(updatedOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 获取订单的订单项列表
     */
    @GET
    @Path("/{orderId}/items")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取订单项列表", description = "获取指定订单的所有订单项")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getOrderItems(@PathParam("orderId") String orderId) {
        List<OrderItem> items = orderHeaderService.getOrderItems(orderId);
        return Response.ok(items).build();
    }

    /**
     * 计算订单总金额
     */
    @GET
    @Path("/{orderId}/total")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "计算订单总金额", description = "计算指定订单的总金额")
    @APIResponse(responseCode = "200", description = "成功计算订单总金额")
    public Response calculateOrderTotal(@PathParam("orderId") String orderId) {
        try {
            java.math.BigDecimal total = orderHeaderService.calculateOrderTotal(orderId);
            Map<String, Object> result = Map.of("orderId", orderId, "total", total);
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * RSQL 查询订单
     */
    @GET
    @Path("/query")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "RSQL 查询订单", description = "使用 RSQL 语法进行灵活查询")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = OrderHeader.class))),
            @APIResponse(responseCode = "400", description = "RSQL 查询语法错误")
    })
    public Response queryOrders(@QueryParam("q") String rsqlQuery,
                               @QueryParam("page") @DefaultValue("0") int page,
                               @QueryParam("size") @DefaultValue("20") int size,
                               @QueryParam("count") @DefaultValue("false") boolean returnCount) {
        try {
            if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"RSQL 查询参数 q 不能为空\"}")
                        .build();
            }

            // 验证 RSQL 查询
            if (!orderHeaderService.isValidRsqlQuery(rsqlQuery)) {
                String error = orderHeaderService.getRsqlParseError(rsqlQuery);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                        .build();
            }

            List<OrderHeader> orders;
            if (page >= 0 && size > 0) {
                orders = orderHeaderService.queryOrdersWithPagination(rsqlQuery, page, size);
            } else {
                orders = orderHeaderService.queryOrders(rsqlQuery);
            }

            if (returnCount) {
                long totalCount = orderHeaderService.queryOrdersCount(rsqlQuery);
                Map<String, Object> result = Map.of(
                    "data", orders,
                    "total", totalCount,
                    "page", page,
                    "size", size
                );
                return Response.ok(result).build();
            } else {
                return Response.ok(orders).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
