package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 快速订单请求 DTO
 * 用于快速创建单个商品的销售订单
 */
public class QuickOrderRequest {

    @NotBlank(message = "商品 ID 不能为空")
    @Size(max = 60, message = "商品 ID 长度不能超过60个字符")
    public String productId;

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    public BigDecimal quantity;

    @NotNull(message = "单价不能为空")
    @Positive(message = "单价必须大于0")
    public BigDecimal unitPrice;

    @NotNull(message = "销售日期不能为空")
    public LocalDateTime salesDate;

    @Size(max = 60, message = "订单名称长度不能超过60个字符")
    public String orderName;

    @Size(max = 60, message = "客户 ID 长度不能超过60个字符")
    public String customerId;

    @Size(max = 60, message = "销售渠道枚举 ID 长度不能超过60个字符")
    public String salesChannelEnumId;

    @Size(max = 1, message = "优先级长度不能超过1个字符")
    public String priority;

    @Size(max = 60, message = "货币单位 ID 长度不能超过60个字符")
    public String currencyUom;

    @Size(max = 320, message = "创建用户长度不能超过320个字符")
    public String createdBy;

    @Size(max = 255, message = "订单项描述长度不能超过255个字符")
    public String itemDescription;

    @Size(max = 255, message = "订单项备注长度不能超过255个字符")
    public String itemComments;

    /**
     * 计算订单项总价
     */
    public BigDecimal calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(quantity);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取默认订单名称
     */
    public String getDefaultOrderName() {
        if (orderName != null && !orderName.trim().isEmpty()) {
            return orderName;
        }
        return "快速订单-" + productId + "-" + salesDate.toLocalDate();
    }
}
