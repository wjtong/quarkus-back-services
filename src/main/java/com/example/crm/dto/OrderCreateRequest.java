package com.example.crm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单创建请求 DTO
 * 用于创建订单的请求数据
 */
public class OrderCreateRequest {

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

    @Size(max = 60, message = "访问代码长度不能超过60个字符")
    public String visitId;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    public String statusId;

    @Size(max = 320, message = "创建用户长度不能超过320个字符")
    public String createdBy;

    @Size(max = 60, message = "货币单位 ID 长度不能超过60个字符")
    public String currencyUom;

    @Size(max = 60, message = "计费账户 ID 长度不能超过60个字符")
    public String billingAccountId;

    public String needsInventoryIssuance;

    public String isRushOrder;

    @Size(max = 60, message = "内部代码长度不能超过60个字符")
    public String internalCode;

    @Size(max = 60, message = "支付方法 ID 长度不能超过60个字符")
    public String paymentMethodId;

    @Size(max = 60, message = "支付方法类型 ID 长度不能超过60个字符")
    public String paymentMethodTypeId;

    // 订单项列表
    @NotNull(message = "订单项列表不能为空")
    @Valid
    public List<OrderItemCreateRequest> orderItems;

    /**
     * 订单项创建请求 DTO
     */
    public static class OrderItemCreateRequest {

        @NotBlank(message = "订单项序号不能为空")
        @Size(max = 60, message = "订单项序号长度不能超过60个字符")
        public String orderItemSeqId;

        @Size(max = 60, message = "订单项类型 ID 长度不能超过60个字符")
        public String orderItemTypeId;

        @Size(max = 60, message = "产品 ID 长度不能超过60个字符")
        public String productId;

        @Size(max = 60, message = "产品特征 ID 长度不能超过60个字符")
        public String productFeatureId;

        @Size(max = 60, message = "产品目录 ID 长度不能超过60个字符")
        public String prodCatalogId;

        @Size(max = 60, message = "供应商产品 ID 长度不能超过60个字符")
        public String supplierProductId;

        @Size(max = 60, message = "供应商 ID 长度不能超过60个字符")
        public String supplierId;

        @Size(max = 60, message = "配置 ID 长度不能超过60个字符")
        public String configId;

        @Size(max = 60, message = "自动名称长度不能超过60个字符")
        public String autoName;

        @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
        public String statusId;

        public LocalDateTime estimatedShipDate;

        public LocalDateTime estimatedDeliveryDate;

        public LocalDateTime autoCancelDate;

        @Size(max = 60, message = "发货前日期长度不能超过60个字符")
        public String shipBeforeDate;

        @Size(max = 60, message = "发货后日期长度不能超过60个字符")
        public String shipAfterDate;

        // 数量相关字段
        @NotNull(message = "数量不能为空")
        public BigDecimal quantity;

        public BigDecimal selectedAmount;

        @NotNull(message = "单价不能为空")
        public BigDecimal unitPrice;

        public BigDecimal unitListPrice;

        public BigDecimal unitAverageCost;

        public BigDecimal unitRecurringPrice;

        public String isModifiedPrice;

        @Size(max = 60, message = "重复频率单位 ID 长度不能超过60个字符")
        public String recurringFreqUomId;

        public String itemDescription;

        public String comments;
    }
}
