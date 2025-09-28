package com.example.crm.ai;

/**
 * CRM AI 服务接口
 * 定义 AI 助手的功能接口
 * 
 * 注意：这是一个简化的接口，用于演示 AI 集成
 * 在实际项目中，您需要根据 LangChain4j 的最新版本调整注解
 */
public interface CrmAiService {
    
    /**
     * 通用聊天功能
     * @param userMessage 用户消息
     * @return AI 响应
     */
    String chat(String userMessage);
    
    /**
     * 客户数据分析
     * @param query 分析查询
     * @return 分析结果
     */
    String analyzeCustomerData(String query);
    
    /**
     * 生成客户报告
     * @param requirements 报告需求
     * @return 生成的报告
     */
    String generateCustomerReport(String requirements);
    
    /**
     * 客户服务支持
     * @param question 客户问题
     * @return 服务响应
     */
    String customerService(String question);
}
