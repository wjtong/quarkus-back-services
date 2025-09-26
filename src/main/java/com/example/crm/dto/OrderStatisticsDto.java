package com.example.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单统计信息 DTO
 * 用于返回订单相关的统计信息
 */
public class OrderStatisticsDto {

    // 基本统计
    public Long totalOrders;
    public Long pendingOrders;
    public Long completedOrders;
    public Long cancelledOrders;

    // 金额统计
    public BigDecimal totalOrderValue;
    public BigDecimal averageOrderValue;
    public BigDecimal maxOrderValue;
    public BigDecimal minOrderValue;

    // 时间统计
    public LocalDateTime earliestOrderDate;
    public LocalDateTime latestOrderDate;

    // 按状态分组统计
    public Map<String, Long> ordersByStatus;

    // 按类型分组统计
    public Map<String, Long> ordersByType;

    // 按日期分组统计（最近30天）
    public Map<String, Long> ordersByDate;

    // 订单项统计
    public Long totalOrderItems;
    public Long cancelledOrderItems;
    public BigDecimal totalItemValue;
    public BigDecimal averageItemValue;

    // 产品统计
    public Long uniqueProducts;
    public Map<String, Long> topProducts;

    // 客户统计
    public Long uniqueCustomers;
    public Map<String, Long> topCustomers;

    /**
     * 计算完成率
     */
    public BigDecimal getCompletionRate() {
        if (totalOrders == null || totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completedOrders != null ? completedOrders : 0)
                .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算取消率
     */
    public BigDecimal getCancellationRate() {
        if (totalOrders == null || totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(cancelledOrders != null ? cancelledOrders : 0)
                .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算平均订单项数量
     */
    public BigDecimal getAverageItemsPerOrder() {
        if (totalOrders == null || totalOrders == 0 || totalOrderItems == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalOrderItems)
                .divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
    }
}
