package com.example.crm.resource;

import com.example.crm.entity.OrderItem;
import com.example.crm.service.OrderItemService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OrderItem REST API 资源类
 * 提供订单项管理的 RESTful 接口
 */
@Path("/order-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "订单项管理", description = "订单项信息的增删改查操作")
public class OrderItemResource {

    @Inject
    OrderItemService orderItemService;

    /**
     * 获取所有订单项
     */
    @GET
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取所有订单项", description = "返回系统中所有订单项的信息，支持 RSQL 查询")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getAllOrderItems(@QueryParam("q") String rsqlQuery,
                                    @QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<OrderItem> orderItems;
            if (rsqlQuery != null && !rsqlQuery.trim().isEmpty()) {
                // 验证 RSQL 查询
                if (!orderItemService.isValidRsqlQuery(rsqlQuery)) {
                    String error = orderItemService.getRsqlParseError(rsqlQuery);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                            .build();
                }
                
                if (page >= 0 && size > 0) {
                    orderItems = orderItemService.queryOrderItemsWithPagination(rsqlQuery, page, size);
                } else {
                    orderItems = orderItemService.queryOrderItems(rsqlQuery);
                }
            } else {
                if (page >= 0 && size > 0) {
                    orderItems = orderItemService.getAllOrderItems().stream()
                            .skip(page * size)
                            .limit(size)
                            .collect(java.util.stream.Collectors.toList());
                } else {
                    orderItems = orderItemService.getAllOrderItems();
                }
            }
            return Response.ok(orderItems).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 根据订单ID和订单项序号获取订单项
     */
    @GET
    @Path("/{orderId}/{orderItemSeqId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据ID获取订单项", description = "根据订单 ID 和订单项序号获取特定订单项的详细信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "成功获取订单项信息",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response getOrderItemById(@PathParam("orderId") String orderId, 
                                   @PathParam("orderItemSeqId") String orderItemSeqId) {
        Optional<OrderItem> orderItem = orderItemService.getOrderItemById(orderId, orderItemSeqId);
        if (orderItem.isPresent()) {
            return Response.ok(orderItem.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"订单项不存在，ID: " + orderId + "-" + orderItemSeqId + "\"}")
                    .build();
        }
    }

    /**
     * 根据订单ID获取所有订单项
     */
    @GET
    @Path("/order/{orderId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据订单ID获取订单项", description = "获取指定订单的所有订单项")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getOrderItemsByOrderId(@PathParam("orderId") String orderId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return Response.ok(orderItems).build();
    }

    /**
     * 根据产品ID获取订单项列表
     */
    @GET
    @Path("/product/{productId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据产品ID获取订单项", description = "获取包含指定产品的所有订单项")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getOrderItemsByProductId(@PathParam("productId") String productId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByProductId(productId);
        return Response.ok(orderItems).build();
    }

    /**
     * 创建新订单项
     */
    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "创建新订单项", description = "创建新的订单项记录")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "订单项创建成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或订单项已存在")
    })
    public Response createOrderItem(@Valid OrderItem orderItem) {
        try {
            OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
            return Response.status(Response.Status.CREATED).entity(createdOrderItem).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 更新订单项信息
     */
    @PUT
    @Path("/{orderId}/{orderItemSeqId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "更新订单项信息", description = "更新指定订单项的信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单项信息更新成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response updateOrderItem(@PathParam("orderId") String orderId, 
                                  @PathParam("orderItemSeqId") String orderItemSeqId, 
                                  @Valid OrderItem orderItem) {
        try {
            OrderItem updatedOrderItem = orderItemService.updateOrderItem(orderId, orderItemSeqId, orderItem);
            return Response.ok(updatedOrderItem).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 删除订单项
     */
    @DELETE
    @Path("/{orderId}/{orderItemSeqId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "删除订单项", description = "删除指定的订单项记录")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "订单项删除成功"),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response deleteOrderItem(@PathParam("orderId") String orderId, 
                                  @PathParam("orderItemSeqId") String orderItemSeqId) {
        boolean deleted = orderItemService.deleteOrderItem(orderId, orderItemSeqId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"订单项不存在，ID: " + orderId + "-" + orderItemSeqId + "\"}")
                    .build();
        }
    }

    /**
     * 根据状态获取订单项列表
     */
    @GET
    @Path("/status/{statusId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据状态获取订单项", description = "根据订单项状态获取订单项列表")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getOrderItemsByStatus(@PathParam("statusId") String statusId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByStatus(statusId);
        return Response.ok(orderItems).build();
    }

    /**
     * 根据类型获取订单项列表
     */
    @GET
    @Path("/type/{orderItemTypeId}")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "根据类型获取订单项", description = "根据订单项类型获取订单项列表")
    @APIResponse(responseCode = "200", description = "成功获取订单项列表",
            content = @Content(schema = @Schema(implementation = OrderItem.class)))
    public Response getOrderItemsByType(@PathParam("orderItemTypeId") String orderItemTypeId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByType(orderItemTypeId);
        return Response.ok(orderItems).build();
    }

    /**
     * 更新订单项状态
     */
    @PUT
    @Path("/{orderId}/{orderItemSeqId}/status")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "更新订单项状态", description = "更新指定订单项的状态")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单项状态更新成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response updateOrderItemStatus(@PathParam("orderId") String orderId, 
                                        @PathParam("orderItemSeqId") String orderItemSeqId,
                                        @QueryParam("statusId") String statusId) {
        try {
            OrderItem updatedOrderItem = orderItemService.updateOrderItemStatus(orderId, orderItemSeqId, statusId);
            return Response.ok(updatedOrderItem).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 取消订单项
     */
    @PUT
    @Path("/{orderId}/{orderItemSeqId}/cancel")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "取消订单项", description = "取消指定订单项的部分或全部数量")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单项取消成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "400", description = "取消数量无效"),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response cancelOrderItem(@PathParam("orderId") String orderId, 
                                  @PathParam("orderItemSeqId") String orderItemSeqId,
                                  @QueryParam("quantity") BigDecimal cancelQuantity) {
        try {
            OrderItem updatedOrderItem = orderItemService.cancelOrderItem(orderId, orderItemSeqId, cancelQuantity);
            return Response.ok(updatedOrderItem).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 修改订单项价格
     */
    @PUT
    @Path("/{orderId}/{orderItemSeqId}/price")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "修改订单项价格", description = "修改指定订单项的价格")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "订单项价格修改成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "400", description = "价格无效"),
            @APIResponse(responseCode = "404", description = "订单项不存在")
    })
    public Response modifyOrderItemPrice(@PathParam("orderId") String orderId, 
                                       @PathParam("orderItemSeqId") String orderItemSeqId,
                                       @QueryParam("price") BigDecimal newPrice) {
        try {
            OrderItem updatedOrderItem = orderItemService.modifyOrderItemPrice(orderId, orderItemSeqId, newPrice);
            return Response.ok(updatedOrderItem).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 获取订单项统计信息
     */
    @GET
    @Path("/order/{orderId}/statistics")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "获取订单项统计信息", description = "获取指定订单的订单项统计信息")
    @APIResponse(responseCode = "200", description = "成功获取统计信息")
    public Response getOrderItemStatistics(@PathParam("orderId") String orderId) {
        try {
            Map<String, Object> statistics = orderItemService.getOrderItemStatistics(orderId);
            return Response.ok(statistics).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * RSQL 查询订单项
     */
    @GET
    @Path("/query")
    @RolesAllowed({"USER", "ADMIN", "MANAGER"})
    @Operation(summary = "RSQL 查询订单项", description = "使用 RSQL 语法进行灵活查询")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = OrderItem.class))),
            @APIResponse(responseCode = "400", description = "RSQL 查询语法错误")
    })
    public Response queryOrderItems(@QueryParam("q") String rsqlQuery,
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
            if (!orderItemService.isValidRsqlQuery(rsqlQuery)) {
                String error = orderItemService.getRsqlParseError(rsqlQuery);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                        .build();
            }

            List<OrderItem> orderItems;
            if (page >= 0 && size > 0) {
                orderItems = orderItemService.queryOrderItemsWithPagination(rsqlQuery, page, size);
            } else {
                orderItems = orderItemService.queryOrderItems(rsqlQuery);
            }

            if (returnCount) {
                long totalCount = orderItemService.queryOrderItemsCount(rsqlQuery);
                Map<String, Object> result = Map.of(
                    "data", orderItems,
                    "total", totalCount,
                    "page", page,
                    "size", size
                );
                return Response.ok(result).build();
            } else {
                return Response.ok(orderItems).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
