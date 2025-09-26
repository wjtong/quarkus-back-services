package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem 实体类 - 订单项信息
 * 对应 OFBiz 中的 order_item 表
 */
@Entity
@Table(name = "order_item")
public class OrderItem extends PanacheEntityBase {

    @Id
    @NotBlank(message = "订单 ID 不能为空")
    @Size(max = 60, message = "订单 ID 长度不能超过60个字符")
    @Column(name = "order_id", length = 60, nullable = false)
    public String orderId;

    @Id
    @NotBlank(message = "订单项序号不能为空")
    @Size(max = 60, message = "订单项序号长度不能超过60个字符")
    @Column(name = "order_item_seq_id", length = 60, nullable = false)
    public String orderItemSeqId;

    @Size(max = 60, message = "外部 ID 长度不能超过60个字符")
    @Column(name = "external_id", length = 60)
    public String externalId;

    @Size(max = 60, message = "订单项类型 ID 长度不能超过60个字符")
    @Column(name = "order_item_type_id", length = 60)
    public String orderItemTypeId;

    @Size(max = 60, message = "订单项分组序号长度不能超过60个字符")
    @Column(name = "order_item_group_seq_id", length = 60)
    public String orderItemGroupSeqId;

    @Size(max = 60, message = "产品 ID 长度不能超过60个字符")
    @Column(name = "product_id", length = 60)
    public String productId;

    @Size(max = 60, message = "产品特征 ID 长度不能超过60个字符")
    @Column(name = "product_feature_id", length = 60)
    public String productFeatureId;

    @Size(max = 60, message = "产品特征类型 ID 长度不能超过60个字符")
    @Column(name = "prod_catalog_id", length = 60)
    public String prodCatalogId;

    @Size(max = 60, message = "供应商产品 ID 长度不能超过60个字符")
    @Column(name = "supplier_product_id", length = 60)
    public String supplierProductId;

    @Size(max = 60, message = "供应商 ID 长度不能超过60个字符")
    @Column(name = "supplier_id", length = 60)
    public String supplierId;

    @Size(max = 60, message = "供应商协议 ID 长度不能超过60个字符")
    @Column(name = "supplier_agreement_id", length = 60)
    public String supplierAgreementId;

    @Size(max = 60, message = "协议项目序号长度不能超过60个字符")
    @Column(name = "agreement_item_seq_id", length = 60)
    public String agreementItemSeqId;

    @Size(max = 60, message = "协议 ID 长度不能超过60个字符")
    @Column(name = "agreement_id", length = 60)
    public String agreementId;

    @Size(max = 60, message = "协议序号长度不能超过60个字符")
    @Column(name = "agreement_seq_id", length = 60)
    public String agreementSeqId;

    @Size(max = 60, message = "购物车项目 ID 长度不能超过60个字符")
    @Column(name = "shopping_list_id", length = 60)
    public String shoppingListId;

    @Size(max = 60, message = "购物车项目序号长度不能超过60个字符")
    @Column(name = "shopping_list_item_seq_id", length = 60)
    public String shoppingListItemSeqId;

    @Size(max = 60, message = "报价 ID 长度不能超过60个字符")
    @Column(name = "quote_id", length = 60)
    public String quoteId;

    @Size(max = 60, message = "报价项目序号长度不能超过60个字符")
    @Column(name = "quote_item_seq_id", length = 60)
    public String quoteItemSeqId;

    @Size(max = 60, message = "配置 ID 长度不能超过60个字符")
    @Column(name = "config_id", length = 60)
    public String configId;

    @Size(max = 60, message = "自动名称长度不能超过60个字符")
    @Column(name = "auto_name", length = 60)
    public String autoName;

    @Size(max = 60, message = "需求 ID 长度不能超过60个字符")
    @Column(name = "requirement_id", length = 60)
    public String requirementId;

    @Size(max = 60, message = "需求序号长度不能超过60个字符")
    @Column(name = "requirement_seq_id", length = 60)
    public String requirementSeqId;

    @Size(max = 60, message = "需求承诺 ID 长度不能超过60个字符")
    @Column(name = "requirement_commitment_id", length = 60)
    public String requirementCommitmentId;

    @Size(max = 60, message = "需求承诺序号长度不能超过60个字符")
    @Column(name = "requirement_commitment_seq_id", length = 60)
    public String requirementCommitmentSeqId;

    @Size(max = 60, message = "对应 PO ID 长度不能超过60个字符")
    @Column(name = "corresponding_po_id", length = 60)
    public String correspondingPoId;

    @Size(max = 60, message = "状态 ID 长度不能超过60个字符")
    @Column(name = "status_id", length = 60)
    public String statusId;

    @Size(max = 60, message = "同步状态 ID 长度不能超过60个字符")
    @Column(name = "sync_status_id", length = 60)
    public String syncStatusId;

    @Column(name = "estimated_ship_date")
    public LocalDateTime estimatedShipDate;

    @Column(name = "estimated_delivery_date")
    public LocalDateTime estimatedDeliveryDate;

    @Column(name = "auto_cancel_date")
    public LocalDateTime autoCancelDate;

    @Column(name = "dont_cancel_set_date")
    public LocalDateTime dontCancelSetDate;

    @Column(name = "dont_cancel_set_user_login", length = 320)
    public String dontCancelSetUserLogin;

    @Size(max = 60, message = "发货前检查 ID 长度不能超过60个字符")
    @Column(name = "ship_before_date", length = 60)
    public String shipBeforeDate;

    @Size(max = 60, message = "发货后检查 ID 长度不能超过60个字符")
    @Column(name = "ship_after_date", length = 60)
    public String shipAfterDate;

    @Column(name = "cancel_back_order_date")
    public LocalDateTime cancelBackOrderDate;

    @Size(max = 60, message = "覆盖账户 ID 长度不能超过60个字符")
    @Column(name = "override_gl_account_id", length = 60)
    public String overrideGlAccountId;

    @Size(max = 60, message = "销售机会 ID 长度不能超过60个字符")
    @Column(name = "sales_opportunity_id", length = 60)
    public String salesOpportunityId;

    @Column(name = "change_by_user_login_id", length = 320)
    public String changeByUserLoginId;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    // 数量相关字段
    @Column(name = "quantity", precision = 18, scale = 6)
    public BigDecimal quantity;

    @Column(name = "cancel_quantity", precision = 18, scale = 6)
    public BigDecimal cancelQuantity;

    @Column(name = "selected_amount", precision = 18, scale = 2)
    public BigDecimal selectedAmount;

    @Column(name = "unit_price", precision = 18, scale = 2)
    public BigDecimal unitPrice;

    @Column(name = "unit_list_price", precision = 18, scale = 2)
    public BigDecimal unitListPrice;

    @Column(name = "unit_average_cost", precision = 18, scale = 2)
    public BigDecimal unitAverageCost;

    @Column(name = "unit_recurring_price", precision = 18, scale = 2)
    public BigDecimal unitRecurringPrice;

    @Column(name = "is_modified_price", length = 1)
    public String isModifiedPrice;

    @Column(name = "recurring_freq_uom_id", length = 60)
    public String recurringFreqUomId;

    @Column(name = "item_description", columnDefinition = "text")
    public String itemDescription;

    @Column(name = "comments", columnDefinition = "text")
    public String comments;


    // 关联的订单头（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @JsonIgnore
    public OrderHeader orderHeader;

    // 关联的产品（多对一关系）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    public Product product;

    /**
     * 获取订单项显示名称
     */
    public String getDisplayName() {
        if (product != null) {
            return product.getDisplayName();
        }
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
     * 计算取消数量
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
