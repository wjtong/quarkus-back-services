package com.example.crm.service;

import com.example.crm.dto.QuickOrderRequest;
import com.example.crm.entity.OrderHeader;
import com.example.crm.entity.OrderItem;
import com.example.crm.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 快速订单服务类
 * 提供快速创建单个商品销售订单的业务逻辑
 */
@ApplicationScoped
public class QuickOrderService {

    @Inject
    OrderHeaderService orderHeaderService;

    @Inject
    OrderItemService orderItemService;

    /**
     * 创建快速订单
     * 同时创建OrderHeader和OrderItem
     */
    @Transactional
    public OrderHeader createQuickOrder(@Valid QuickOrderRequest request) {
        // 验证商品是否存在
        validateProduct(request.productId);

        // 生成订单ID
        String orderId = generateOrderId();

        // 创建订单头
        OrderHeader orderHeader = createOrderHeader(orderId, request);

        // 创建订单项
        OrderItem orderItem = createOrderItem(orderId, request);

        // 保存订单头
        orderHeaderService.createOrder(orderHeader);

        // 保存订单项
        orderItemService.createOrderItem(orderItem);

        // 返回完整的订单信息（包含订单项）
        return orderHeaderService.getOrderById(orderId).orElse(orderHeader);
    }

    /**
     * 验证商品是否存在
     */
    private void validateProduct(String productId) {
        Product product = Product.<Product>findByIdOptional(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + productId);
        }
    }

    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "QO-" + timestamp + "-" + uuid;
    }

    /**
     * 创建订单头
     */
    private OrderHeader createOrderHeader(String orderId, QuickOrderRequest request) {
        OrderHeader orderHeader = new OrderHeader();
        
        // 基本信息
        orderHeader.orderId = orderId;
        orderHeader.orderName = request.getDefaultOrderName();
        orderHeader.orderTypeId = "SALES_ORDER";
        orderHeader.statusId = "ORDER_CREATED";
        orderHeader.priority = request.priority != null ? request.priority : "M";
        orderHeader.salesChannelEnumId = request.salesChannelEnumId;
        orderHeader.currencyUom = request.currencyUom != null ? request.currencyUom : "USD";
        orderHeader.createdBy = request.createdBy;

        // 日期信息
        orderHeader.orderDate = request.salesDate;
        orderHeader.entryDate = LocalDateTime.now();

        // 金额信息
        orderHeader.grandTotal = request.calculateTotalPrice();
        orderHeader.remainingSubTotal = request.calculateTotalPrice();

        // 其他默认值
        orderHeader.needsInventoryIssuance = "Y";
        orderHeader.isRushOrder = "N";
        orderHeader.isViewed = "N";

        return orderHeader;
    }

    /**
     * 创建订单项
     */
    private OrderItem createOrderItem(String orderId, QuickOrderRequest request) {
        OrderItem orderItem = new OrderItem();
        
        // 基本信息
        orderItem.orderId = orderId;
        orderItem.orderItemSeqId = "00001"; // 第一个订单项
        orderItem.orderItemTypeId = "PRODUCT_ORDER_ITEM";
        orderItem.productId = request.productId;
        orderItem.statusId = "ITEM_APPROVED";

        // 数量和价格
        orderItem.quantity = request.quantity;
        orderItem.unitPrice = request.unitPrice;
        orderItem.unitListPrice = request.unitPrice;
        orderItem.selectedAmount = request.calculateTotalPrice();

        // 描述信息
        orderItem.itemDescription = request.itemDescription;
        orderItem.comments = request.itemComments;

        // 其他默认值
        orderItem.isModifiedPrice = "N";

        return orderItem;
    }

    /**
     * 批量创建快速订单
     * 为多个商品创建独立的订单
     */
    @Transactional
    public java.util.List<OrderHeader> createBatchQuickOrders(java.util.List<QuickOrderRequest> requests) {
        java.util.List<OrderHeader> createdOrders = new java.util.ArrayList<>();
        
        for (QuickOrderRequest request : requests) {
            try {
                OrderHeader order = createQuickOrder(request);
                createdOrders.add(order);
            } catch (Exception e) {
                // 记录错误但继续处理其他订单
                System.err.println("创建快速订单失败: " + e.getMessage());
                throw new RuntimeException("批量创建订单时发生错误: " + e.getMessage(), e);
            }
        }
        
        return createdOrders;
    }

    /**
     * 创建快速订单（带客户信息）
     */
    @Transactional
    public OrderHeader createQuickOrderWithCustomer(@Valid QuickOrderRequest request, String customerId) {
        // 验证客户是否存在
        validateCustomer(customerId);
        
        // 设置客户ID
        request.customerId = customerId;
        
        // 创建订单
        OrderHeader order = createQuickOrder(request);
        
        // 可以在这里添加客户相关的业务逻辑
        // 比如更新客户统计信息、发送通知等
        
        return order;
    }

    /**
     * 验证客户是否存在
     */
    private void validateCustomer(String customerId) {
        // 这里可以添加客户验证逻辑
        // 暂时简单验证非空
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
    }

    /**
     * 获取快速订单统计信息
     */
    public java.util.Map<String, Object> getQuickOrderStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 统计快速订单数量（以QO-开头的订单）
        long quickOrderCount = OrderHeader.count("orderId LIKE ?1", "QO-%");
        stats.put("totalQuickOrders", quickOrderCount);
        
        // 统计今天的快速订单数量
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plusDays(1);
        long todayQuickOrderCount = OrderHeader.count("orderId LIKE ?1 AND orderDate BETWEEN ?2 AND ?3", 
                "QO-%", today, tomorrow);
        stats.put("todayQuickOrders", todayQuickOrderCount);
        
        // 统计快速订单总金额
        java.util.List<OrderHeader> quickOrders = OrderHeader.find("orderId LIKE ?1", "QO-%").list();
        BigDecimal totalAmount = quickOrders.stream()
                .map(order -> order.grandTotal != null ? order.grandTotal : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalQuickOrderAmount", totalAmount);
        
        return stats;
    }
}
