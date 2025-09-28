package com.example.crm.ai;

import com.example.crm.entity.Party;
import com.example.crm.entity.OrderHeader;
import com.example.crm.entity.Product;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * CRM AI 工具类
 * 为 AI 提供访问 CRM 数据的能力
 */
@ApplicationScoped
public class CrmTools {
    
    /**
     * 获取客户信息
     * @param customerId 客户ID
     * @return 客户信息
     */
    @Tool("根据客户ID获取客户详细信息")
    public String getCustomerInfo(String customerId) {
        try {
            Party customer = Party.findById(customerId);
            if (customer != null) {
                return String.format("客户信息 - ID: %s, 姓名: %s, 状态: %s, 创建时间: %s", 
                    customer.partyId, 
                    customer.partyName != null ? customer.partyName : "未设置",
                    customer.statusId != null ? customer.statusId : "未知",
                    customer.createdDate != null ? customer.createdDate.toString() : "未知");
            }
            return "未找到ID为 " + customerId + " 的客户";
        } catch (Exception e) {
            return "查询客户信息时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取客户订单
     * @param customerId 客户ID
     * @return 客户订单信息
     */
    @Tool("获取指定客户的所有订单信息")
    public String getCustomerOrders(String customerId) {
        try {
            // 注意：这里需要根据实际的数据库关系来查询
            // 由于 OrderHeader 中没有直接的 partyId 字段，这里使用一个示例查询
            List<OrderHeader> orders = OrderHeader.listAll(); // 简化查询，实际应该根据业务逻辑查询
            if (orders.isEmpty()) {
                return "客户 " + customerId + " 暂无订单";
            }
            
            StringBuilder result = new StringBuilder();
            result.append("客户 ").append(customerId).append(" 的订单信息:\n");
            for (OrderHeader order : orders) {
                result.append("- 订单ID: ").append(order.orderId)
                      .append(", 状态: ").append(order.statusId != null ? order.statusId : "未知")
                      .append(", 金额: ").append(order.grandTotal != null ? order.grandTotal : "0")
                      .append(", 创建时间: ").append(order.orderDate != null ? order.orderDate.toString() : "未知")
                      .append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "查询客户订单时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 搜索客户
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @Tool("根据关键词搜索客户")
    public String searchCustomers(String keyword) {
        try {
            List<Party> customers = Party.find("partyName like ?1", "%" + keyword + "%").list();
            if (customers.isEmpty()) {
                return "未找到包含关键词 '" + keyword + "' 的客户";
            }
            
            StringBuilder result = new StringBuilder();
            result.append("找到 ").append(customers.size()).append(" 个匹配的客户:\n");
            for (Party customer : customers) {
                result.append("- ID: ").append(customer.partyId)
                      .append(", 姓名: ").append(customer.partyName != null ? customer.partyName : "未设置")
                      .append(", 状态: ").append(customer.statusId != null ? customer.statusId : "未知")
                      .append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "搜索客户时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取产品信息
     * @param productId 产品ID
     * @return 产品信息
     */
    @Tool("根据产品ID获取产品详细信息")
    public String getProductInfo(String productId) {
        try {
            Product product = Product.findById(productId);
            if (product != null) {
                return String.format("产品信息 - ID: %s, 名称: %s, 描述: %s, 创建时间: %s", 
                    product.productId,
                    product.productName != null ? product.productName : "未设置",
                    product.description != null ? product.description : "未设置",
                    product.createdDate != null ? product.createdDate.toString() : "未知");
            }
            return "未找到ID为 " + productId + " 的产品";
        } catch (Exception e) {
            return "查询产品信息时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取系统统计信息
     * @return 系统统计
     */
    @Tool("获取CRM系统的统计信息")
    public String getSystemStatistics() {
        try {
            long customerCount = Party.count();
            long orderCount = OrderHeader.count();
            long productCount = Product.count();
            
            return String.format("CRM系统统计信息:\n" +
                "- 客户总数: %d\n" +
                "- 订单总数: %d\n" +
                "- 产品总数: %d", 
                customerCount, orderCount, productCount);
        } catch (Exception e) {
            return "获取系统统计信息时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取最近订单
     * @param limit 限制数量
     * @return 最近订单列表
     */
    @Tool("获取最近的订单列表")
    public String getRecentOrders(String limit) {
        try {
            int limitNum = Integer.parseInt(limit);
            List<OrderHeader> orders = OrderHeader.find("order by orderDate desc").page(0, limitNum).list();
            
            if (orders.isEmpty()) {
                return "暂无订单数据";
            }
            
            StringBuilder result = new StringBuilder();
            result.append("最近 ").append(limitNum).append(" 个订单:\n");
            for (OrderHeader order : orders) {
                result.append("- 订单ID: ").append(order.orderId)
                      .append(", 订单名称: ").append(order.orderName != null ? order.orderName : "未设置")
                      .append(", 金额: ").append(order.grandTotal != null ? order.grandTotal : "0")
                      .append(", 状态: ").append(order.statusId != null ? order.statusId : "未知")
                      .append(", 日期: ").append(order.orderDate != null ? order.orderDate.toString() : "未知")
                      .append("\n");
            }
            return result.toString();
        } catch (NumberFormatException e) {
            return "限制数量格式错误，请输入有效的数字";
        } catch (Exception e) {
            return "获取最近订单时发生错误: " + e.getMessage();
        }
    }
}
