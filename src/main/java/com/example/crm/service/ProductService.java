package com.example.crm.service;

import com.example.crm.entity.Product;
import com.example.crm.util.RsqlUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * Product 服务类
 * 提供产品相关的业务逻辑
 */
@ApplicationScoped
public class ProductService {

    @Inject
    QueryService queryService;

    /**
     * 获取所有产品
     */
    public List<Product> getAllProducts() {
        return Product.listAll();
    }

    /**
     * 根据ID获取产品
     */
    public Optional<Product> getProductById(String productId) {
        return Product.findByIdOptional(productId);
    }

    /**
     * 根据名称搜索产品
     */
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        
        String searchPattern = "%" + name.toLowerCase() + "%";
        return Product.find("LOWER(productName) LIKE ?1 OR LOWER(description) LIKE ?1 OR LOWER(brandName) LIKE ?1", 
                searchPattern).list();
    }

    /**
     * 根据类型获取产品列表
     */
    public List<Product> getProductsByType(String productTypeId) {
        return Product.find("productTypeId", productTypeId).list();
    }

    /**
     * 根据分类获取产品列表
     */
    public List<Product> getProductsByCategory(String categoryId) {
        return Product.find("primaryProductCategoryId", categoryId).list();
    }

    /**
     * 根据品牌获取产品列表
     */
    public List<Product> getProductsByBrand(String brandName) {
        return Product.find("brandName", brandName).list();
    }

    /**
     * 创建新产品
     */
    @Transactional
    public Product createProduct(@Valid Product product) {
        // 检查产品 ID 是否已存在
        if (getProductById(product.productId).isPresent()) {
            throw new IllegalArgumentException("产品 ID 已存在: " + product.productId);
        }
        
        product.persist();
        return product;
    }

    /**
     * 更新产品信息
     */
    @Transactional
    public Product updateProduct(String productId, @Valid Product productData) {
        Product product = Product.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("产品不存在，ID: " + productId);
        }

        // 更新产品信息
        product.productTypeId = productData.productTypeId;
        product.primaryProductCategoryId = productData.primaryProductCategoryId;
        product.facilityId = productData.facilityId;
        product.productName = productData.productName;
        product.description = productData.description;
        product.longDescription = productData.longDescription;
        product.brandName = productData.brandName;
        product.internalName = productData.internalName;
        product.comments = productData.comments;
        product.requireInventory = productData.requireInventory;
        product.returnable = productData.returnable;
        product.taxable = productData.taxable;
        product.chargeShipping = productData.chargeShipping;
        product.includeInPromotions = productData.includeInPromotions;
        product.isVirtual = productData.isVirtual;
        product.isVariant = productData.isVariant;

        return product;
    }

    /**
     * 删除产品
     */
    @Transactional
    public boolean deleteProduct(String productId) {
        Product product = Product.findById(productId);
        if (product == null) {
            return false;
        }
        product.delete();
        return true;
    }

    /**
     * 获取可用产品列表
     */
    public List<Product> getAvailableProducts() {
        return Product.find("introductionDate <= CURRENT_TIMESTAMP AND (salesDiscontinuationDate IS NULL OR salesDiscontinuationDate > CURRENT_TIMESTAMP)").list();
    }

    /**
     * 获取虚拟产品列表
     */
    public List<Product> getVirtualProducts() {
        return Product.find("isVirtual = 'Y'").list();
    }

    /**
     * 获取变体产品列表
     */
    public List<Product> getVariantProducts() {
        return Product.find("isVariant = 'Y'").list();
    }

    /**
     * 获取需要库存的产品列表
     */
    public List<Product> getProductsRequiringInventory() {
        return Product.find("requireInventory = 'Y'").list();
    }

    /**
     * 获取可退货产品列表
     */
    public List<Product> getReturnableProducts() {
        return Product.find("returnable = 'Y'").list();
    }

    /**
     * 获取应税产品列表
     */
    public List<Product> getTaxableProducts() {
        return Product.find("taxable = 'Y'").list();
    }

    /**
     * 根据重量范围获取产品
     */
    public List<Product> getProductsByWeightRange(java.math.BigDecimal minWeight, java.math.BigDecimal maxWeight) {
        if (minWeight == null && maxWeight == null) {
            return getAllProducts();
        }
        
        if (minWeight == null) {
            return Product.find("productWeight <= ?1", maxWeight).list();
        }
        
        if (maxWeight == null) {
            return Product.find("productWeight >= ?1", minWeight).list();
        }
        
        return Product.find("productWeight >= ?1 AND productWeight <= ?2", minWeight, maxWeight).list();
    }

    /**
     * 获取最近创建的产品
     */
    public List<Product> getRecentlyCreatedProducts(int limit) {
        return Product.find("ORDER BY createdDate DESC").page(0, limit).list();
    }

    /**
     * 获取即将停产的产品
     */
    public List<Product> getProductsDiscontinuingSoon(int days) {
        java.time.LocalDateTime futureDate = java.time.LocalDateTime.now().plusDays(days);
        return Product.find("salesDiscontinuationDate IS NOT NULL AND salesDiscontinuationDate <= ?1", futureDate).list();
    }

    /**
     * 使用 RSQL 查询产品
     * 
     * @param rsqlQuery RSQL 查询字符串，例如：
     *                  - productName==*acme* (名称包含 acme)
     *                  - isVirtual==Y (虚拟产品)
     *                  - createdDate>=2024-01-01 (创建日期大于等于 2024-01-01)
     *                  - productName==*test*;isVirtual==Y (产品名称包含 test 且为虚拟产品)
     * @return 查询结果列表
     */
    public List<Product> queryProducts(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return getAllProducts();
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return getAllProducts();
            }

            return Product.find(queryResult.getQuery(), queryResult.getParamsArray()).list();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 RSQL 查询产品并支持分页
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 查询结果列表
     */
    public List<Product> queryProductsWithPagination(String rsqlQuery, int page, int size) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return getAllProducts().stream()
                    .skip(page * size)
                    .limit(size)
                    .collect(java.util.stream.Collectors.toList());
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return getAllProducts().stream()
                        .skip(page * size)
                        .limit(size)
                        .collect(java.util.stream.Collectors.toList());
            }

            return Product.find(queryResult.getQuery(), queryResult.getParamsArray())
                    .page(page, size).list();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 RSQL 查询产品总数
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 查询结果总数
     */
    public long queryProductsCount(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return Product.count();
        }

        try {
            RsqlUtil.QueryResult queryResult = queryService.parseRsqlQuery(rsqlQuery);
            
            if (queryResult.getQuery().trim().isEmpty()) {
                return Product.count();
            }

            return Product.find(queryResult.getQuery(), queryResult.getParamsArray()).count();
        } catch (Exception e) {
            throw new IllegalArgumentException("RSQL 查询计数失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证 RSQL 查询字符串
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 是否有效
     */
    public boolean isValidRsqlQuery(String rsqlQuery) {
        return queryService.isValidRsqlQuery(rsqlQuery);
    }

    /**
     * 获取 RSQL 查询的解析错误信息
     * 
     * @param rsqlQuery RSQL 查询字符串
     * @return 错误信息，如果没有错误则返回 null
     */
    public String getRsqlParseError(String rsqlQuery) {
        return queryService.getRsqlParseError(rsqlQuery);
    }
}
