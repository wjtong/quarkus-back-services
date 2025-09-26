package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderHeader DTO 类
 * 用于订单数据传输
 */
public class OrderHeaderDto {

    @NotBlank(message = "订单 ID 不能为空")
    @Size(max = 60, message = "订单 ID 长度不能超过60个字符")
    public String orderId;

    @Size(max = 60, message = "订单类型 ID 长度不能超过60个字符")
    public String orderTypeId;

    @Size(max = 60, message = "订单名称长度不能超过60个字符")
    public String orderName;

    @Size(max = 60, message = "外部 ID 长度不能超过60个字符")
    public String externalId;

    @Size(max = 60, message = "销售渠道枚举 ID 长度不能超过60个字符")
    public String salesChannelEnumId;

    public LocalDateTime orderDate;

    @Size(max = 1, message = "优先级长度不能超过1个字符")
    public String priority;

    public LocalDateTime entryDate;

    public LocalDateTime pickSheetPrintedDate;

    @Size(max = 60, message = "访问代码长度不能超过60个字符")
    public String visitId;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    public String statusId;

    @Size(max = 320, message = "创建用户长度不能超过320个字符")
    public String createdBy;

    @Size(max = 60, message = "首次尝试订单 ID 长度不能超过60个字符")
    public String firstAttemptOrderId;

    @Size(max = 60, message = "货币单位 ID 长度不能超过60个字符")
    public String currencyUom;

    @Size(max = 60, message = "同步状态 ID 长度不能超过60个字符")
    public String syncStatusId;

    @Size(max = 60, message = "计费账户 ID 长度不能超过60个字符")
    public String billingAccountId;

    @Size(max = 60, message = "原始货币单位 ID 长度不能超过60个字符")
    public String originCurrencyUom;

    @Size(max = 60, message = "原始订单 ID 长度不能超过60个字符")
    public String originOrderId;

    public String needsInventoryIssuance;

    public String isRushOrder;

    @Size(max = 60, message = "内部代码长度不能超过60个字符")
    public String internalCode;

    public BigDecimal remainingSubTotal;

    public BigDecimal grandTotal;

    public String isViewed;

    @Size(max = 60, message = "发票 ID 长度不能超过60个字符")
    public String invoiceId;

    @Size(max = 60, message = "发票类型 ID 长度不能超过60个字符")
    public String invoiceTypeId;

    public LocalDateTime invoiceDate;

    @Size(max = 60, message = "发票消息长度不能超过60个字符")
    public String invoiceMessage;

    @Size(max = 60, message = "发票状态 ID 长度不能超过60个字符")
    public String invoiceStatusId;

    @Size(max = 60, message = "支付方法 ID 长度不能超过60个字符")
    public String paymentMethodId;

    @Size(max = 60, message = "支付方法类型 ID 长度不能超过60个字符")
    public String paymentMethodTypeId;

    @Size(max = 60, message = "支付状态 ID 长度不能超过60个字符")
    public String paymentStatusId;

    public LocalDateTime paymentDate;

    @Size(max = 60, message = "支付参考号长度不能超过60个字符")
    public String paymentRefNum;

    public String paymentGatewayResponse;

    // 关联的订单项列表
    public List<OrderItemDto> orderItems;

    // 统计信息
    public Integer itemCount;
    public BigDecimal totalAmount;

    /**
     * 获取订单显示名称
     */
    public String getDisplayName() {
        return orderName != null ? orderName : orderId;
    }

    /**
     * 检查订单是否为紧急订单
     */
    public boolean isRushOrder() {
        return "Y".equals(isRushOrder);
    }

    /**
     * 检查是否需要库存发放
     */
    public boolean needsInventoryIssuance() {
        return "Y".equals(needsInventoryIssuance);
    }

    /**
     * 检查订单是否已查看
     */
    public boolean isViewed() {
        return "Y".equals(isViewed);
    }
}
