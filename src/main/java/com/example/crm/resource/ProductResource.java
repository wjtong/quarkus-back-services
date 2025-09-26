package com.example.crm.resource;

import com.example.crm.entity.Product;
import com.example.crm.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product REST API 资源类
 * 提供产品管理的 RESTful 接口
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "产品管理", description = "产品信息的增删改查操作")
public class ProductResource {

    @Inject
    ProductService productService;

    /**
     * 获取所有产品
     */
    @GET
    @RolesAllowed({"USER", "ADMIN", "MANAGER", "SALES"})
    @Operation(summary = "获取所有产品", description = "返回系统中所有产品的信息，支持 RSQL 查询")
    @APIResponse(responseCode = "200", description = "成功获取产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getAllProducts(@QueryParam("q") String rsqlQuery,
                                  @QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<Product> products;
            if (rsqlQuery != null && !rsqlQuery.trim().isEmpty()) {
                // 验证 RSQL 查询
                if (!productService.isValidRsqlQuery(rsqlQuery)) {
                    String error = productService.getRsqlParseError(rsqlQuery);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                            .build();
                }
                
                if (page >= 0 && size > 0) {
                    products = productService.queryProductsWithPagination(rsqlQuery, page, size);
                } else {
                    products = productService.queryProducts(rsqlQuery);
                }
            } else {
                if (page >= 0 && size > 0) {
                    products = productService.getAllProducts().stream()
                            .skip(page * size)
                            .limit(size)
                            .collect(java.util.stream.Collectors.toList());
                } else {
                    products = productService.getAllProducts();
                }
            }
            return Response.ok(products).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 根据ID获取产品
     */
    @GET
    @Path("/{productId}")
    @Operation(summary = "根据ID获取产品", description = "根据产品ID获取特定产品的详细信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "成功获取产品信息",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "404", description = "产品不存在")
    })
    public Response getProductById(@PathParam("productId") String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            return Response.ok(product.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"产品不存在，ID: " + productId + "\"}")
                    .build();
        }
    }

    /**
     * 创建新产品
     */
    @POST
    @Operation(summary = "创建新产品", description = "创建新的产品记录")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "产品创建成功",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效或产品ID已存在")
    })
    public Response createProduct(@Valid Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return Response.status(Response.Status.CREATED).entity(createdProduct).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 更新产品信息
     */
    @PUT
    @Path("/{productId}")
    @Operation(summary = "更新产品信息", description = "更新指定产品的信息")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "产品信息更新成功",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "400", description = "请求数据无效"),
            @APIResponse(responseCode = "404", description = "产品不存在")
    })
    public Response updateProduct(@PathParam("productId") String productId, @Valid Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return Response.ok(updatedProduct).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * 删除产品
     */
    @DELETE
    @Path("/{productId}")
    @Operation(summary = "删除产品", description = "删除指定的产品记录")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "产品删除成功"),
            @APIResponse(responseCode = "404", description = "产品不存在")
    })
    public Response deleteProduct(@PathParam("productId") String productId) {
        boolean deleted = productService.deleteProduct(productId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"产品不存在，ID: " + productId + "\"}")
                    .build();
        }
    }

    /**
     * 搜索产品
     */
    @GET
    @Path("/search")
    @Operation(summary = "搜索产品", description = "根据名称、描述或品牌搜索产品")
    @APIResponse(responseCode = "200", description = "搜索成功",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response searchProducts(@QueryParam("name") String name) {
        List<Product> products = productService.searchProductsByName(name);
        return Response.ok(products).build();
    }

    /**
     * 根据类型获取产品列表
     */
    @GET
    @Path("/type/{productTypeId}")
    @Operation(summary = "根据类型获取产品", description = "根据产品类型获取产品列表")
    @APIResponse(responseCode = "200", description = "成功获取产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsByType(@PathParam("productTypeId") String productTypeId) {
        List<Product> products = productService.getProductsByType(productTypeId);
        return Response.ok(products).build();
    }

    /**
     * 根据分类获取产品列表
     */
    @GET
    @Path("/category/{categoryId}")
    @Operation(summary = "根据分类获取产品", description = "根据产品分类获取产品列表")
    @APIResponse(responseCode = "200", description = "成功获取产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsByCategory(@PathParam("categoryId") String categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return Response.ok(products).build();
    }

    /**
     * 根据品牌获取产品列表
     */
    @GET
    @Path("/brand/{brandName}")
    @Operation(summary = "根据品牌获取产品", description = "根据品牌名称获取产品列表")
    @APIResponse(responseCode = "200", description = "成功获取产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsByBrand(@PathParam("brandName") String brandName) {
        List<Product> products = productService.getProductsByBrand(brandName);
        return Response.ok(products).build();
    }

    /**
     * 获取可用产品列表
     */
    @GET
    @Path("/available")
    @Operation(summary = "获取可用产品", description = "获取所有可用的产品")
    @APIResponse(responseCode = "200", description = "成功获取可用产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return Response.ok(products).build();
    }

    /**
     * 获取虚拟产品列表
     */
    @GET
    @Path("/virtual")
    @Operation(summary = "获取虚拟产品", description = "获取所有虚拟产品")
    @APIResponse(responseCode = "200", description = "成功获取虚拟产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getVirtualProducts() {
        List<Product> products = productService.getVirtualProducts();
        return Response.ok(products).build();
    }

    /**
     * 获取变体产品列表
     */
    @GET
    @Path("/variants")
    @Operation(summary = "获取变体产品", description = "获取所有变体产品")
    @APIResponse(responseCode = "200", description = "成功获取变体产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getVariantProducts() {
        List<Product> products = productService.getVariantProducts();
        return Response.ok(products).build();
    }

    /**
     * 获取需要库存的产品列表
     */
    @GET
    @Path("/inventory-required")
    @Operation(summary = "获取需要库存的产品", description = "获取所有需要库存管理的产品")
    @APIResponse(responseCode = "200", description = "成功获取需要库存的产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsRequiringInventory() {
        List<Product> products = productService.getProductsRequiringInventory();
        return Response.ok(products).build();
    }

    /**
     * 获取可退货产品列表
     */
    @GET
    @Path("/returnable")
    @Operation(summary = "获取可退货产品", description = "获取所有可退货的产品")
    @APIResponse(responseCode = "200", description = "成功获取可退货产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getReturnableProducts() {
        List<Product> products = productService.getReturnableProducts();
        return Response.ok(products).build();
    }

    /**
     * 获取应税产品列表
     */
    @GET
    @Path("/taxable")
    @Operation(summary = "获取应税产品", description = "获取所有应税的产品")
    @APIResponse(responseCode = "200", description = "成功获取应税产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getTaxableProducts() {
        List<Product> products = productService.getTaxableProducts();
        return Response.ok(products).build();
    }

    /**
     * 根据重量范围获取产品
     */
    @GET
    @Path("/weight-range")
    @Operation(summary = "根据重量范围获取产品", description = "根据产品重量范围获取产品列表")
    @APIResponse(responseCode = "200", description = "成功获取产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsByWeightRange(@QueryParam("minWeight") BigDecimal minWeight, 
                                           @QueryParam("maxWeight") BigDecimal maxWeight) {
        List<Product> products = productService.getProductsByWeightRange(minWeight, maxWeight);
        return Response.ok(products).build();
    }

    /**
     * 获取最近创建的产品
     */
    @GET
    @Path("/recent")
    @Operation(summary = "获取最近创建的产品", description = "获取最近创建的产品列表")
    @APIResponse(responseCode = "200", description = "成功获取最近创建的产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getRecentlyCreatedProducts(@QueryParam("limit") @DefaultValue("10") int limit) {
        List<Product> products = productService.getRecentlyCreatedProducts(limit);
        return Response.ok(products).build();
    }

    /**
     * 获取即将停产的产品
     */
    @GET
    @Path("/discontinuing")
    @Operation(summary = "获取即将停产的产品", description = "获取即将停产的产品列表")
    @APIResponse(responseCode = "200", description = "成功获取即将停产的产品列表",
            content = @Content(schema = @Schema(implementation = Product.class)))
    public Response getProductsDiscontinuingSoon(@QueryParam("days") @DefaultValue("30") int days) {
        List<Product> products = productService.getProductsDiscontinuingSoon(days);
        return Response.ok(products).build();
    }

    /**
     * RSQL 查询产品
     */
    @GET
    @Path("/query")
    @Operation(summary = "RSQL 查询产品", description = "使用 RSQL 语法进行灵活查询")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @APIResponse(responseCode = "400", description = "RSQL 查询语法错误")
    })
    public Response queryProducts(@QueryParam("q") String rsqlQuery,
                                 @QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("20") int size,
                                 @QueryParam("count") @DefaultValue("false") boolean returnCount) {
        try {
            if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"RSQL 查询参数 q 不能为空\"}")
                        .build();
            }

            // 验证 RSQL 查询
            if (!productService.isValidRsqlQuery(rsqlQuery)) {
                String error = productService.getRsqlParseError(rsqlQuery);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"无效的 RSQL 查询: " + error + "\"}")
                        .build();
            }

            List<Product> products;
            if (page >= 0 && size > 0) {
                products = productService.queryProductsWithPagination(rsqlQuery, page, size);
            } else {
                products = productService.queryProducts(rsqlQuery);
            }

            if (returnCount) {
                long totalCount = productService.queryProductsCount(rsqlQuery);
                java.util.Map<String, Object> result = new java.util.HashMap<>();
                result.put("data", products);
                result.put("total", totalCount);
                result.put("page", page);
                result.put("size", size);
                return Response.ok(result).build();
            } else {
                return Response.ok(products).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
