package com.example.crm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product 实体类 - 产品信息
 */
@Entity
@Table(name = "product")
public class Product extends PanacheEntityBase {

    @Id
    @NotBlank(message = "产品 ID 不能为空")
    @Size(max = 60, message = "产品 ID 长度不能超过60个字符")
    @Column(name = "product_id", length = 60, nullable = false)
    public String productId;

    @Size(max = 60, message = "产品类型 ID 长度不能超过60个字符")
    @Column(name = "product_type_id", length = 60)
    public String productTypeId;

    @Size(max = 60, message = "主要产品分类 ID 长度不能超过60个字符")
    @Column(name = "primary_product_category_id", length = 60)
    public String primaryProductCategoryId;

    @Size(max = 60, message = "设施 ID 长度不能超过60个字符")
    @Column(name = "facility_id", length = 60)
    public String facilityId;

    @Column(name = "introduction_date")
    public LocalDateTime introductionDate;

    @Column(name = "release_date")
    public LocalDateTime releaseDate;

    @Column(name = "support_discontinuation_date")
    public LocalDateTime supportDiscontinuationDate;

    @Column(name = "sales_discontinuation_date")
    public LocalDateTime salesDiscontinuationDate;

    @Column(name = "sales_disc_when_not_avail", length = 1)
    public String salesDiscWhenNotAvail;

    @Size(max = 255, message = "内部名称长度不能超过255个字符")
    @Column(name = "internal_name", length = 255)
    public String internalName;

    @Size(max = 100, message = "品牌名称长度不能超过100个字符")
    @Column(name = "brand_name", length = 100)
    public String brandName;

    @Size(max = 255, message = "评论长度不能超过255个字符")
    @Column(name = "comments", length = 255)
    public String comments;

    @Size(max = 100, message = "产品名称长度不能超过100个字符")
    @Column(name = "product_name", length = 100)
    public String productName;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    @Column(name = "description", length = 255)
    public String description;

    @Column(name = "long_description", columnDefinition = "text")
    public String longDescription;

    @Size(max = 255, message = "价格详情文本长度不能超过255个字符")
    @Column(name = "price_detail_text", length = 255)
    public String priceDetailText;

    @Size(max = 2000, message = "小图片 URL 长度不能超过2000个字符")
    @Column(name = "small_image_url", length = 2000)
    public String smallImageUrl;

    @Size(max = 2000, message = "中等图片 URL 长度不能超过2000个字符")
    @Column(name = "medium_image_url", length = 2000)
    public String mediumImageUrl;

    @Size(max = 2000, message = "大图片 URL 长度不能超过2000个字符")
    @Column(name = "large_image_url", length = 2000)
    public String largeImageUrl;

    @Size(max = 2000, message = "详情图片 URL 长度不能超过2000个字符")
    @Column(name = "detail_image_url", length = 2000)
    public String detailImageUrl;

    @Size(max = 2000, message = "原始图片 URL 长度不能超过2000个字符")
    @Column(name = "original_image_url", length = 2000)
    public String originalImageUrl;

    @Size(max = 255, message = "详情屏幕长度不能超过255个字符")
    @Column(name = "detail_screen", length = 255)
    public String detailScreen;

    @Size(max = 255, message = "库存消息长度不能超过255个字符")
    @Column(name = "inventory_message", length = 255)
    public String inventoryMessage;

    @Size(max = 60, message = "库存项目类型 ID 长度不能超过60个字符")
    @Column(name = "inventory_item_type_id", length = 60)
    public String inventoryItemTypeId;

    @Column(name = "require_inventory", length = 1)
    public String requireInventory;

    @Size(max = 60, message = "数量单位 ID 长度不能超过60个字符")
    @Column(name = "quantity_uom_id", length = 60)
    public String quantityUomId;

    @Column(name = "quantity_included", precision = 18, scale = 6)
    public BigDecimal quantityIncluded;

    @Column(name = "pieces_included")
    public Integer piecesIncluded;

    @Column(name = "require_amount", length = 1)
    public String requireAmount;

    @Column(name = "fixed_amount", precision = 18, scale = 2)
    public BigDecimal fixedAmount;

    @Column(name = "amount_uom_type_id", length = 60)
    public String amountUomTypeId;

    @Column(name = "weight_uom_id", length = 60)
    public String weightUomId;

    @Column(name = "shipping_weight", precision = 18, scale = 6)
    public BigDecimal shippingWeight;

    @Column(name = "product_weight", precision = 18, scale = 6)
    public BigDecimal productWeight;

    @Column(name = "height_uom_id", length = 60)
    public String heightUomId;

    @Column(name = "product_height", precision = 18, scale = 6)
    public BigDecimal productHeight;

    @Column(name = "shipping_height", precision = 18, scale = 6)
    public BigDecimal shippingHeight;

    @Column(name = "width_uom_id", length = 60)
    public String widthUomId;

    @Column(name = "product_width", precision = 18, scale = 6)
    public BigDecimal productWidth;

    @Column(name = "shipping_width", precision = 18, scale = 6)
    public BigDecimal shippingWidth;

    @Column(name = "depth_uom_id", length = 60)
    public String depthUomId;

    @Column(name = "product_depth", precision = 18, scale = 6)
    public BigDecimal productDepth;

    @Column(name = "shipping_depth", precision = 18, scale = 6)
    public BigDecimal shippingDepth;

    @Column(name = "diameter_uom_id", length = 60)
    public String diameterUomId;

    @Column(name = "product_diameter", precision = 18, scale = 6)
    public BigDecimal productDiameter;

    @Column(name = "product_rating", precision = 18, scale = 6)
    public BigDecimal productRating;

    @Column(name = "rating_type_enum", length = 60)
    public String ratingTypeEnum;

    @Column(name = "returnable", length = 1)
    public String returnable;

    @Column(name = "taxable", length = 1)
    public String taxable;

    @Column(name = "charge_shipping", length = 1)
    public String chargeShipping;

    @Column(name = "auto_create_keywords", length = 1)
    public String autoCreateKeywords;

    @Column(name = "include_in_promotions", length = 1)
    public String includeInPromotions;

    @Column(name = "is_virtual", length = 1)
    public String isVirtual;

    @Column(name = "is_variant", length = 1)
    public String isVariant;

    @Column(name = "virtual_variant_method_enum", length = 60)
    public String virtualVariantMethodEnum;

    @Column(name = "origin_geo_id", length = 60)
    public String originGeoId;

    @Column(name = "requirement_method_enum_id", length = 60)
    public String requirementMethodEnumId;

    @Column(name = "bill_of_material_level")
    public Integer billOfMaterialLevel;

    @Column(name = "reserv_max_persons")
    public Integer reservMaxPersons;

    @Column(name = "reserv2nd_p_p_perc")
    public BigDecimal reserv2ndPPPerc;

    @Column(name = "reserv_nth_p_p_perc")
    public BigDecimal reservNthPPPerc;

    @Column(name = "config_id", length = 60)
    public String configId;

    @Column(name = "created_date")
    public LocalDateTime createdDate;

    @Column(name = "created_by_user_login", length = 320)
    public String createdByUserLogin;

    @Column(name = "last_modified_date")
    public LocalDateTime lastModifiedDate;

    @Column(name = "last_modified_by_user_login", length = 320)
    public String lastModifiedByUserLogin;

    @Column(name = "in_shipment_box", length = 1)
    public String inShipmentBox;

    @Column(name = "default_shipment_box_type_id", length = 60)
    public String defaultShipmentBoxTypeId;

    @Column(name = "lot_id_filled_in", length = 60)
    public String lotIdFilledIn;

    @Column(name = "order_decimal_quantity", length = 1)
    public String orderDecimalQuantity;

    @Column(name = "last_updated_stamp")
    public LocalDateTime lastUpdatedStamp;

    @Column(name = "last_updated_tx_stamp")
    public LocalDateTime lastUpdatedTxStamp;

    @Column(name = "created_stamp")
    public LocalDateTime createdStamp;

    @Column(name = "created_tx_stamp")
    public LocalDateTime createdTxStamp;

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return productName != null ? productName : productId;
    }

    /**
     * 检查产品是否可用
     */
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return (introductionDate == null || introductionDate.isBefore(now) || introductionDate.isEqual(now)) &&
               (salesDiscontinuationDate == null || salesDiscontinuationDate.isAfter(now));
    }

    /**
     * 检查是否需要库存
     */
    public boolean isRequireInventory() {
        return "Y".equals(requireInventory);
    }

    /**
     * 检查是否可退货
     */
    public boolean isReturnable() {
        return "Y".equals(returnable);
    }

    /**
     * 检查是否应税
     */
    public boolean isTaxable() {
        return "Y".equals(taxable);
    }

    /**
     * 检查是否为虚拟产品
     */
    public boolean isVirtual() {
        return "Y".equals(isVirtual);
    }

    /**
     * 检查是否为变体产品
     */
    public boolean isVariant() {
        return "Y".equals(isVariant);
    }
}
