package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem DTO 类
 * 用于订单项数据传输
 */
public class OrderItemDto {

    @NotBlank(message = "订单 ID 不能为空")
    @Size(max = 60, message = "订单 ID 长度不能超过60个字符")
    public String orderId;

    @NotBlank(message = "订单项序号不能为空")
    @Size(max = 60, message = "订单项序号长度不能超过60个字符")
    public String orderItemSeqId;

    @Size(max = 60, message = "外部 ID 长度不能超过60个字符")
    public String externalId;

    @Size(max = 60, message = "订单项类型 ID 长度不能超过60个字符")
    public String orderItemTypeId;

    @Size(max = 60, message = "订单项分组序号长度不能超过60个字符")
    public String orderItemGroupSeqId;

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

    @Size(max = 60, message = "供应商协议 ID 长度不能超过60个字符")
    public String supplierAgreementId;

    @Size(max = 60, message = "协议项目序号长度不能超过60个字符")
    public String agreementItemSeqId;

    @Size(max = 60, message = "协议 ID 长度不能超过60个字符")
    public String agreementId;

    @Size(max = 60, message = "协议序号长度不能超过60个字符")
    public String agreementSeqId;

    @Size(max = 60, message = "购物车 ID 长度不能超过60个字符")
    public String shoppingListId;

    @Size(max = 60, message = "购物车项目序号长度不能超过60个字符")
    public String shoppingListItemSeqId;

    @Size(max = 60, message = "报价 ID 长度不能超过60个字符")
    public String quoteId;

    @Size(max = 60, message = "报价项目序号长度不能超过60个字符")
    public String quoteItemSeqId;

    @Size(max = 60, message = "配置 ID 长度不能超过60个字符")
    public String configId;

    @Size(max = 60, message = "自动名称长度不能超过60个字符")
    public String autoName;

    @Size(max = 60, message = "需求 ID 长度不能超过60个字符")
    public String requirementId;

    @Size(max = 60, message = "需求序号长度不能超过60个字符")
    public String requirementSeqId;

    @Size(max = 60, message = "需求承诺 ID 长度不能超过60个字符")
    public String requirementCommitmentId;

    @Size(max = 60, message = "需求承诺序号长度不能超过60个字符")
    public String requirementCommitmentSeqId;

    @Size(max = 60, message = "对应 PO ID 长度不能超过60个字符")
    public String correspondingPoId;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    public String statusId;

    @Size(max = 60, message = "同步状态 ID 长度不能超过60个字符")
    public String syncStatusId;

    public LocalDateTime estimatedShipDate;

    public LocalDateTime estimatedDeliveryDate;

    public LocalDateTime autoCancelDate;

    public LocalDateTime dontCancelSetDate;

    @Size(max = 320, message = "不取消设置用户登录名长度不能超过320个字符")
    public String dontCancelSetUserLogin;

    @Size(max = 60, message = "发货前日期长度不能超过60个字符")
    public String shipBeforeDate;

    @Size(max = 60, message = "发货后日期长度不能超过60个字符")
    public String shipAfterDate;

    public LocalDateTime cancelBackOrderDate;

    @Size(max = 60, message = "覆盖账户 ID 长度不能超过60个字符")
    public String overrideGlAccountId;

    @Size(max = 60, message = "销售机会 ID 长度不能超过60个字符")
    public String salesOpportunityId;

    @Size(max = 320, message = "修改用户登录 ID 长度不能超过320个字符")
    public String changeByUserLoginId;

    // 数量相关字段
    public BigDecimal quantity;

    public BigDecimal cancelQuantity;

    public BigDecimal selectedAmount;

    public BigDecimal unitPrice;

    public BigDecimal unitListPrice;

    public BigDecimal unitAverageCost;

    public BigDecimal unitRecurringPrice;

    public String isModifiedPrice;

    @Size(max = 60, message = "重复频率单位 ID 长度不能超过60个字符")
    public String recurringFreqUomId;

    public String itemDescription;

    public String comments;

    // 计算字段
    public BigDecimal totalPrice;
    public BigDecimal remainingQuantity;
    public boolean fullyCancelled;

    /**
     * 获取订单项显示名称
     */
    public String getDisplayName() {
        return autoName != null ? autoName : (orderId + "-" + orderItemSeqId);
    }

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
     * 计算剩余数量
     */
    public BigDecimal getRemainingQuantity() {
        if (quantity != null && cancelQuantity != null) {
            return quantity.subtract(cancelQuantity);
        }
        return quantity != null ? quantity : BigDecimal.ZERO;
    }

    /**
     * 检查是否已完全取消
     */
    public boolean isFullyCancelled() {
        return getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * 检查是否为修改价格
     */
    public boolean isModifiedPrice() {
        return "Y".equals(isModifiedPrice);
    }
}
