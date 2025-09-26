package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderHeader 实体类 - 订单头信息
 * 对应 OFBiz 中的 order_header 表
 */
@Entity
@Table(name = "order_header")
public class OrderHeader extends PanacheEntityBase {

    @Id
    @NotBlank(message = "订单 ID 不能为空")
    @Size(max = 60, message = "订单 ID 长度不能超过60个字符")
    @Column(name = "order_id", length = 60, nullable = false)
    public String orderId;

    @Size(max = 60, message = "订单类型 ID 长度不能超过60个字符")
    @Column(name = "order_type_id", length = 60)
    public String orderTypeId;

    @Size(max = 60, message = "订单名称长度不能超过60个字符")
    @Column(name = "order_name", length = 60)
    public String orderName;

    @Size(max = 60, message = "外部 ID 长度不能超过60个字符")
    @Column(name = "external_id", length = 60)
    public String externalId;

    @Size(max = 60, message = "销售渠道枚举 ID 长度不能超过60个字符")
    @Column(name = "sales_channel_enum_id", length = 60)
    public String salesChannelEnumId;

    @Column(name = "order_date")
    public LocalDateTime orderDate;

    @Size(max = 1, message = "优先级长度不能超过1个字符")
    @Column(name = "priority", length = 1)
    public String priority;

    @Column(name = "entry_date")
    public LocalDateTime entryDate;

    @Column(name = "pick_sheet_printed_date")
    public LocalDateTime pickSheetPrintedDate;

    @Size(max = 60, message = "访问代码长度不能超过60个字符")
    @Column(name = "visit_id", length = 60)
    public String visitId;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    @Column(name = "status_id", length = 60)
    public String statusId;

    @Column(name = "created_by", length = 320)
    public String createdBy;

    @Column(name = "first_attempt_order_id", length = 60)
    public String firstAttemptOrderId;

    @Size(max = 60, message = "货币单位 ID 长度不能超过60个字符")
    @Column(name = "currency_uom", length = 60)
    public String currencyUom;

    @Size(max = 60, message = "同步状态 ID 长度不能超过60个字符")
    @Column(name = "sync_status_id", length = 60)
    public String syncStatusId;

    @Size(max = 60, message = "计费账户 ID 长度不能超过60个字符")
    @Column(name = "billing_account_id", length = 60)
    public String billingAccountId;

    @Size(max = 60, message = "原始货币单位 ID 长度不能超过60个字符")
    @Column(name = "origin_currency_uom", length = 60)
    public String originCurrencyUom;

    @Column(name = "origin_order_id", length = 60)
    public String originOrderId;

    @Column(name = "needs_inventory_issuance", length = 1)
    public String needsInventoryIssuance;

    @Column(name = "is_rush_order", length = 1)
    public String isRushOrder;

    @Size(max = 60, message = "内部代码长度不能超过60个字符")
    @Column(name = "internal_code", length = 60)
    public String internalCode;

    @Column(name = "remaining_sub_total", precision = 18, scale = 2)
    public BigDecimal remainingSubTotal;

    @Column(name = "grand_total", precision = 18, scale = 2)
    public BigDecimal grandTotal;

    @Column(name = "is_viewed", length = 1)
    public String isViewed;

    @Size(max = 60, message = "发票 ID 长度不能超过60个字符")
    @Column(name = "invoice_id", length = 60)
    public String invoiceId;

    @Size(max = 60, message = "发票类型 ID 长度不能超过60个字符")
    @Column(name = "invoice_type_id", length = 60)
    public String invoiceTypeId;

    @Column(name = "invoice_date")
    public LocalDateTime invoiceDate;

    @Size(max = 60, message = "发票消息长度不能超过60个字符")
    @Column(name = "invoice_message", length = 60)
    public String invoiceMessage;

    @Size(max = 60, message = "发票状态 ID 长度不能超过60个字符")
    @Column(name = "invoice_status_id", length = 60)
    public String invoiceStatusId;

    @Size(max = 60, message = "支付方法 ID 长度不能超过60个字符")
    @Column(name = "payment_method_id", length = 60)
    public String paymentMethodId;

    @Size(max = 60, message = "支付方法类型 ID 长度不能超过60个字符")
    @Column(name = "payment_method_type_id", length = 60)
    public String paymentMethodTypeId;

    @Size(max = 60, message = "支付状态 ID 长度不能超过60个字符")
    @Column(name = "payment_status_id", length = 60)
    public String paymentStatusId;

    @Column(name = "payment_date")
    public LocalDateTime paymentDate;

    @Column(name = "payment_ref_num", length = 60)
    public String paymentRefNum;

    @Column(name = "payment_gateway_response", columnDefinition = "text")
    public String paymentGatewayResponse;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    // 关联的订单项（一对多关系）
    @OneToMany(mappedBy = "orderHeader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<OrderItem> orderItems;

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

    /**
     * 获取订单项总数
     */
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    /**
     * 计算订单项总金额
     */
    public BigDecimal calculateTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(item -> item.unitPrice != null && item.quantity != null ? 
                    item.unitPrice.multiply(item.quantity) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
