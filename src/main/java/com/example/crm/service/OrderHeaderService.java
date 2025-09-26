package com.example.crm.service;

import com.example.crm.entity.OrderHeader;
import com.example.crm.entity.OrderItem;
import com.example.crm.util.RsqlUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * OrderHeader 服务类
 * 提供订单头相关的业务逻辑
 */
@ApplicationScoped
public class OrderHeaderService {

    @Inject
    QueryService queryService;

    /**
     * 获取所有订单
     */
    public List<OrderHeader> getAllOrders() {
        return OrderHeader.find("SELECT DISTINCT oh FROM OrderHeader oh LEFT JOIN FETCH oh.orderItems").list();
    }

    /**
     * 根据ID获取订单
     */
    public Optional<OrderHeader> getOrderById(String orderId) {
        return OrderHeader.find("SELECT DISTINCT oh FROM OrderHeader oh LEFT JOIN FETCH oh.orderItems WHERE oh.orderId = ?1", orderId).firstResultOptional();
    }

    /**
     * 根据订单名称搜索订单
     */
    public List<OrderHeader> searchOrdersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllOrders();
        }
        
        String searchPattern = "%" + name.toLowerCase() + "%";
        return OrderHeader.find("LOWER(orderName) LIKE ?1", searchPattern).list();
    }

    /**
     * 根据订单类型获取订单列表
     */
    public List<OrderHeader> getOrdersByType(String orderTypeId) {
        return OrderHeader.find("orderTypeId", orderTypeId).list();
    }

    /**
     * 根据状态获取订单列表
     */
    public List<OrderHeader> getOrdersByStatus(String statusId) {
        return OrderHeader.find("statusId", statusId).list();
    }

    /**
     * 根据客户ID获取订单列表
     */
    public List<OrderHeader> getOrdersByCustomer(String customerId) {
        return OrderHeader.find("SELECT oh FROM OrderHeader oh JOIN oh.orderItems oi WHERE oi.productId = ?1", customerId).list();
    }

    /**
     * 根据日期范围获取订单列表
     */
    public List<OrderHeader> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return OrderHeader.find("orderDate BETWEEN ?1 AND ?2", startDate, endDate).list();
    }

    /**
     * 创建新订单
     */
    @Transactional
    public OrderHeader createOrder(@Valid OrderHeader order) {
        if (order.orderId == null || order.orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        // 检查订单ID是否已存在
        if (OrderHeader.findByIdOptional(order.orderId).isPresent()) {
            throw new IllegalArgumentException("订单ID已存在: " + order.orderId);
        }

        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        order.createdStamp = now;
        order.lastUpdatedStamp = now;
        order.createdTxStamp = now;
        order.lastUpdatedTxStamp = now;

        // 如果没有设置订单日期，使用当前时间
        if (order.orderDate == null) {
            order.orderDate = now;
        }

        // 如果没有设置进入日期，使用当前时间
        if (order.entryDate == null) {
            order.entryDate = now;
        }

        order.persist();
        return order;
    }

    /**
     * 更新订单信息
     */
    @Transactional
    public OrderHeader updateOrder(String orderId, @Valid OrderHeader order) {
        OrderHeader existingOrder = OrderHeader.<OrderHeader>findByIdOptional(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

        // 更新字段
        existingOrder.orderTypeId = order.orderTypeId;
        existingOrder.orderName = order.orderName;
        existingOrder.externalId = order.externalId;
        existingOrder.salesChannelEnumId = order.salesChannelEnumId;
        existingOrder.orderDate = order.orderDate;
        existingOrder.priority = order.priority;
        existingOrder.entryDate = order.entryDate;
        existingOrder.pickSheetPrintedDate = order.pickSheetPrintedDate;
        existingOrder.visitId = order.visitId;
        existingOrder.statusId = order.statusId;
        existingOrder.createdBy = order.createdBy;
        existingOrder.firstAttemptOrderId = order.firstAttemptOrderId;
        existingOrder.currencyUom = order.currencyUom;
        existingOrder.syncStatusId = order.syncStatusId;
        existingOrder.billingAccountId = order.billingAccountId;
        existingOrder.originCurrencyUom = order.originCurrencyUom;
        existingOrder.originOrderId = order.originOrderId;
        existingOrder.needsInventoryIssuance = order.needsInventoryIssuance;
        existingOrder.isRushOrder = order.isRushOrder;
        existingOrder.internalCode = order.internalCode;
        existingOrder.remainingSubTotal = order.remainingSubTotal;
        existingOrder.grandTotal = order.grandTotal;
        existingOrder.isViewed = order.isViewed;
        existingOrder.invoiceId = order.invoiceId;
        existingOrder.invoiceTypeId = order.invoiceTypeId;
        existingOrder.invoiceDate = order.invoiceDate;
        existingOrder.invoiceMessage = order.invoiceMessage;
        existingOrder.invoiceStatusId = order.invoiceStatusId;
        existingOrder.paymentMethodId = order.paymentMethodId;
        existingOrder.paymentMethodTypeId = order.paymentMethodTypeId;
        existingOrder.paymentStatusId = order.paymentStatusId;
        existingOrder.paymentDate = order.paymentDate;
        existingOrder.paymentRefNum = order.paymentRefNum;
        existingOrder.paymentGatewayResponse = order.paymentGatewayResponse;

        // 更新修改时间
        existingOrder.lastUpdatedStamp = LocalDateTime.now();
        existingOrder.lastUpdatedTxStamp = LocalDateTime.now();

        existingOrder.persist();
        return existingOrder;
    }

    /**
     * 删除订单
     */
    @Transactional
    public boolean deleteOrder(String orderId) {
        OrderHeader order = OrderHeader.<OrderHeader>findByIdOptional(orderId).orElse(null);
        if (order != null) {
            order.delete();
            return true;
        }
        return false;
    }

    /**
     * 验证 RSQL 查询语法
     */
    public boolean isValidRsqlQuery(String rsqlQuery) {
        return RsqlUtil.isValidRsqlQuery(rsqlQuery);
    }

    /**
     * 获取 RSQL 解析错误信息
     */
    public String getRsqlParseError(String rsqlQuery) {
        return RsqlUtil.getRsqlParseError(rsqlQuery);
    }

    /**
     * 使用 RSQL 查询订单
     */
    public List<OrderHeader> queryOrders(String rsqlQuery) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderHeader.listAll();
        }
        return OrderHeader.find(queryResult.getQuery(), queryResult.getParamsArray()).list();
    }

    /**
     * 使用 RSQL 查询订单（带分页）
     */
    public List<OrderHeader> queryOrdersWithPagination(String rsqlQuery, int page, int size) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderHeader.findAll().page(page, size).list();
        }
        return OrderHeader.find(queryResult.getQuery(), queryResult.getParamsArray()).page(page, size).list();
    }

    /**
     * 使用 RSQL 查询订单数量
     */
    public long queryOrdersCount(String rsqlQuery) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderHeader.count();
        }
        return OrderHeader.find(queryResult.getQuery(), queryResult.getParamsArray()).count();
    }

    /**
     * 获取订单的订单项列表
     */
    public List<OrderItem> getOrderItems(String orderId) {
        return OrderItem.find("orderId", orderId).list();
    }

    /**
     * 计算订单总金额
     */
    public java.math.BigDecimal calculateOrderTotal(String orderId) {
        List<OrderItem> items = getOrderItems(orderId);
        return items.stream()
                .map(item -> item.unitPrice != null && item.quantity != null ? 
                    item.unitPrice.multiply(item.quantity) : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public OrderHeader updateOrderStatus(String orderId, String statusId) {
        OrderHeader order = OrderHeader.<OrderHeader>findByIdOptional(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        
        order.statusId = statusId;
        order.lastUpdatedStamp = LocalDateTime.now();
        order.lastUpdatedTxStamp = LocalDateTime.now();
        
        order.persist();
        return order;
    }

    /**
     * 标记订单为已查看
     */
    @Transactional
    public OrderHeader markOrderAsViewed(String orderId) {
        OrderHeader order = OrderHeader.<OrderHeader>findByIdOptional(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));
        
        order.isViewed = "Y";
        order.lastUpdatedStamp = LocalDateTime.now();
        order.lastUpdatedTxStamp = LocalDateTime.now();
        
        order.persist();
        return order;
    }

    /**
     * 获取待处理订单列表
     */
    public List<OrderHeader> getPendingOrders() {
        return OrderHeader.find("statusId IN ?1", List.of("ORDER_CREATED", "ORDER_APPROVED", "ORDER_PROCESSING")).list();
    }

    /**
     * 获取已完成订单列表
     */
    public List<OrderHeader> getCompletedOrders() {
        return OrderHeader.find("statusId IN ?1", List.of("ORDER_COMPLETED", "ORDER_SHIPPED", "ORDER_DELIVERED")).list();
    }

    /**
     * 获取已取消订单列表
     */
    public List<OrderHeader> getCancelledOrders() {
        return OrderHeader.find("statusId IN ?1", List.of("ORDER_CANCELLED", "ORDER_REJECTED")).list();
    }
}
