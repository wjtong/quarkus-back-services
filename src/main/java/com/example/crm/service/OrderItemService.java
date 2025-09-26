package com.example.crm.service;

import com.example.crm.entity.OrderItem;
import com.example.crm.entity.OrderHeader;
import com.example.crm.util.RsqlUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * OrderItem 服务类
 * 提供订单项相关的业务逻辑
 */
@ApplicationScoped
public class OrderItemService {

    @Inject
    QueryService queryService;

    /**
     * 获取所有订单项
     */
    public List<OrderItem> getAllOrderItems() {
        return OrderItem.listAll();
    }

    /**
     * 根据订单ID和订单项序号获取订单项
     */
    public Optional<OrderItem> getOrderItemById(String orderId, String orderItemSeqId) {
        return OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId));
    }

    /**
     * 根据订单ID获取所有订单项
     */
    public List<OrderItem> getOrderItemsByOrderId(String orderId) {
        return OrderItem.find("orderId", orderId).list();
    }

    /**
     * 根据产品ID获取订单项列表
     */
    public List<OrderItem> getOrderItemsByProductId(String productId) {
        return OrderItem.find("productId", productId).list();
    }

    /**
     * 根据状态获取订单项列表
     */
    public List<OrderItem> getOrderItemsByStatus(String statusId) {
        return OrderItem.find("statusId", statusId).list();
    }

    /**
     * 根据订单类型获取订单项列表
     */
    public List<OrderItem> getOrderItemsByType(String orderItemTypeId) {
        return OrderItem.find("orderItemTypeId", orderItemTypeId).list();
    }

    /**
     * 创建新订单项
     */
    @Transactional
    public OrderItem createOrderItem(@Valid OrderItem orderItem) {
        if (orderItem.orderId == null || orderItem.orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        if (orderItem.orderItemSeqId == null || orderItem.orderItemSeqId.trim().isEmpty()) {
            throw new IllegalArgumentException("订单项序号不能为空");
        }

        // 检查订单项是否已存在
        if (OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderItem.orderId, orderItem.orderItemSeqId)).isPresent()) {
            throw new IllegalArgumentException("订单项已存在: " + orderItem.orderId + "-" + orderItem.orderItemSeqId);
        }

        // 检查订单是否存在
        if (!OrderHeader.<OrderHeader>findByIdOptional(orderItem.orderId).isPresent()) {
            throw new IllegalArgumentException("订单不存在: " + orderItem.orderId);
        }

        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        orderItem.createdStamp = now;
        orderItem.lastUpdatedStamp = now;
        orderItem.createdTxStamp = now;
        orderItem.lastUpdatedTxStamp = now;

        orderItem.persist();
        return orderItem;
    }

    /**
     * 更新订单项信息
     */
    @Transactional
    public OrderItem updateOrderItem(String orderId, String orderItemSeqId, @Valid OrderItem orderItem) {
        OrderItem existingItem = OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId))
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderId + "-" + orderItemSeqId));

        // 更新字段
        existingItem.externalId = orderItem.externalId;
        existingItem.orderItemTypeId = orderItem.orderItemTypeId;
        existingItem.orderItemGroupSeqId = orderItem.orderItemGroupSeqId;
        existingItem.productId = orderItem.productId;
        existingItem.productFeatureId = orderItem.productFeatureId;
        existingItem.prodCatalogId = orderItem.prodCatalogId;
        existingItem.supplierProductId = orderItem.supplierProductId;
        existingItem.supplierId = orderItem.supplierId;
        existingItem.supplierAgreementId = orderItem.supplierAgreementId;
        existingItem.agreementItemSeqId = orderItem.agreementItemSeqId;
        existingItem.agreementId = orderItem.agreementId;
        existingItem.agreementSeqId = orderItem.agreementSeqId;
        existingItem.shoppingListId = orderItem.shoppingListId;
        existingItem.shoppingListItemSeqId = orderItem.shoppingListItemSeqId;
        existingItem.quoteId = orderItem.quoteId;
        existingItem.quoteItemSeqId = orderItem.quoteItemSeqId;
        existingItem.configId = orderItem.configId;
        existingItem.autoName = orderItem.autoName;
        existingItem.requirementId = orderItem.requirementId;
        existingItem.requirementSeqId = orderItem.requirementSeqId;
        existingItem.requirementCommitmentId = orderItem.requirementCommitmentId;
        existingItem.requirementCommitmentSeqId = orderItem.requirementCommitmentSeqId;
        existingItem.correspondingPoId = orderItem.correspondingPoId;
        existingItem.statusId = orderItem.statusId;
        existingItem.syncStatusId = orderItem.syncStatusId;
        existingItem.estimatedShipDate = orderItem.estimatedShipDate;
        existingItem.estimatedDeliveryDate = orderItem.estimatedDeliveryDate;
        existingItem.autoCancelDate = orderItem.autoCancelDate;
        existingItem.dontCancelSetDate = orderItem.dontCancelSetDate;
        existingItem.dontCancelSetUserLogin = orderItem.dontCancelSetUserLogin;
        existingItem.shipBeforeDate = orderItem.shipBeforeDate;
        existingItem.shipAfterDate = orderItem.shipAfterDate;
        existingItem.cancelBackOrderDate = orderItem.cancelBackOrderDate;
        existingItem.overrideGlAccountId = orderItem.overrideGlAccountId;
        existingItem.salesOpportunityId = orderItem.salesOpportunityId;
        existingItem.changeByUserLoginId = orderItem.changeByUserLoginId;
        existingItem.quantity = orderItem.quantity;
        existingItem.cancelQuantity = orderItem.cancelQuantity;
        existingItem.selectedAmount = orderItem.selectedAmount;
        existingItem.unitPrice = orderItem.unitPrice;
        existingItem.unitListPrice = orderItem.unitListPrice;
        existingItem.unitAverageCost = orderItem.unitAverageCost;
        existingItem.unitRecurringPrice = orderItem.unitRecurringPrice;
        existingItem.isModifiedPrice = orderItem.isModifiedPrice;
        existingItem.recurringFreqUomId = orderItem.recurringFreqUomId;
        existingItem.itemDescription = orderItem.itemDescription;
        existingItem.comments = orderItem.comments;

        // 更新修改时间
        existingItem.lastUpdatedStamp = LocalDateTime.now();
        existingItem.lastUpdatedTxStamp = LocalDateTime.now();

        existingItem.persist();
        return existingItem;
    }

    /**
     * 删除订单项
     */
    @Transactional
    public boolean deleteOrderItem(String orderId, String orderItemSeqId) {
        OrderItem orderItem = OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId)).orElse(null);
        if (orderItem != null) {
            orderItem.delete();
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
     * 使用 RSQL 查询订单项
     */
    public List<OrderItem> queryOrderItems(String rsqlQuery) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderItem.listAll();
        }
        return OrderItem.find(queryResult.getQuery(), queryResult.getParamsArray()).list();
    }

    /**
     * 使用 RSQL 查询订单项（带分页）
     */
    public List<OrderItem> queryOrderItemsWithPagination(String rsqlQuery, int page, int size) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderItem.findAll().page(page, size).list();
        }
        return OrderItem.find(queryResult.getQuery(), queryResult.getParamsArray()).page(page, size).list();
    }

    /**
     * 使用 RSQL 查询订单项数量
     */
    public long queryOrderItemsCount(String rsqlQuery) {
        RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
        if (queryResult.getQuery().isEmpty()) {
            return OrderItem.count();
        }
        return OrderItem.find(queryResult.getQuery(), queryResult.getParamsArray()).count();
    }

    /**
     * 更新订单项状态
     */
    @Transactional
    public OrderItem updateOrderItemStatus(String orderId, String orderItemSeqId, String statusId) {
        OrderItem orderItem = OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId))
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderId + "-" + orderItemSeqId));
        
        orderItem.statusId = statusId;
        orderItem.lastUpdatedStamp = LocalDateTime.now();
        orderItem.lastUpdatedTxStamp = LocalDateTime.now();
        
        orderItem.persist();
        return orderItem;
    }

    /**
     * 取消订单项
     */
    @Transactional
    public OrderItem cancelOrderItem(String orderId, String orderItemSeqId, java.math.BigDecimal cancelQuantity) {
        OrderItem orderItem = OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId))
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderId + "-" + orderItemSeqId));
        
        if (cancelQuantity == null || cancelQuantity.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("取消数量必须大于0");
        }
        
        java.math.BigDecimal remainingQuantity = orderItem.getRemainingQuantity();
        if (cancelQuantity.compareTo(remainingQuantity) > 0) {
            throw new IllegalArgumentException("取消数量不能超过剩余数量");
        }
        
        orderItem.cancelQuantity = orderItem.cancelQuantity != null ? 
            orderItem.cancelQuantity.add(cancelQuantity) : cancelQuantity;
        orderItem.lastUpdatedStamp = LocalDateTime.now();
        orderItem.lastUpdatedTxStamp = LocalDateTime.now();
        
        orderItem.persist();
        return orderItem;
    }

    /**
     * 修改订单项价格
     */
    @Transactional
    public OrderItem modifyOrderItemPrice(String orderId, String orderItemSeqId, java.math.BigDecimal newPrice) {
        OrderItem orderItem = OrderItem.<OrderItem>findByIdOptional(new OrderItemId(orderId, orderItemSeqId))
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderId + "-" + orderItemSeqId));
        
        if (newPrice == null || newPrice.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        
        orderItem.unitPrice = newPrice;
        orderItem.isModifiedPrice = "Y";
        orderItem.lastUpdatedStamp = LocalDateTime.now();
        orderItem.lastUpdatedTxStamp = LocalDateTime.now();
        
        orderItem.persist();
        return orderItem;
    }

    /**
     * 获取订单项统计信息
     */
    public java.util.Map<String, Object> getOrderItemStatistics(String orderId) {
        List<OrderItem> items = getOrderItemsByOrderId(orderId);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalItems", items.size());
        stats.put("totalQuantity", items.stream()
                .map(item -> item.quantity != null ? item.quantity : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        stats.put("totalAmount", items.stream()
                .map(item -> item.calculateTotalPrice())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        stats.put("cancelledItems", items.stream()
                .mapToInt(item -> item.isFullyCancelled() ? 1 : 0)
                .sum());
        
        return stats;
    }

    /**
     * 订单项ID类（用于复合主键）
     */
    public static class OrderItemId {
        public String orderId;
        public String orderItemSeqId;
        
        public OrderItemId() {}
        
        public OrderItemId(String orderId, String orderItemSeqId) {
            this.orderId = orderId;
            this.orderItemSeqId = orderItemSeqId;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            OrderItemId that = (OrderItemId) obj;
            return java.util.Objects.equals(orderId, that.orderId) &&
                   java.util.Objects.equals(orderItemSeqId, that.orderItemSeqId);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(orderId, orderItemSeqId);
        }
    }
}
