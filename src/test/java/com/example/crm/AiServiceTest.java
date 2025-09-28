package com.example.crm;

import com.example.crm.ai.CrmAiServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 * AI 服务测试类
 * 测试 AI Agent 功能
 */
@QuarkusTest
public class AiServiceTest {
    
    @Inject
    CrmAiServiceImpl aiService;
    
    @Test
    public void testAiServiceStatus() {
        String status = aiService.getStatus();
        System.out.println("AI 服务状态: " + status);
        assert status != null && !status.isEmpty();
    }
    
    @Test
    public void testChat() {
        String response = aiService.chat("你好，请介绍一下 CRM 系统");
        System.out.println("AI 聊天响应: " + response);
        assert response != null && !response.isEmpty();
    }
    
    @Test
    public void testCustomerAnalysis() {
        String response = aiService.analyzeCustomerData("分析一下我们的客户分布情况");
        System.out.println("数据分析响应: " + response);
        assert response != null && !response.isEmpty();
    }
    
    @Test
    public void testReportGeneration() {
        String response = aiService.generateCustomerReport("生成月度客户报告");
        System.out.println("报告生成响应: " + response);
        assert response != null && !response.isEmpty();
    }
    
    @Test
    public void testCustomerService() {
        String response = aiService.customerService("如何创建新客户？");
        System.out.println("客户服务响应: " + response);
        assert response != null && !response.isEmpty();
    }
}
