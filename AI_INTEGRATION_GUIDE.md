# AI 集成指南 - LangChain4j

本指南介绍如何在 CRM 项目中集成 LangChain4j 提供 AI Agent 功能。

## 1. 依赖配置

在 `pom.xml` 中添加 LangChain4j 相关依赖：

```xml
<!-- LangChain4j 核心依赖 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.34.0</version>
</dependency>

<!-- OpenAI 集成 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.34.0</version>
</dependency>

<!-- 向量数据库集成 (可选) -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
    <version>0.34.0</version>
</dependency>

<!-- 内存向量存储 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings-store-in-memory</artifactId>
    <version>0.34.0</version>
</dependency>
```

## 2. 配置文件

在 `application.properties` 中添加 AI 相关配置：

```properties
# LangChain4j OpenAI 配置
langchain4j.openai.api-key=${OPENAI_API_KEY:your-openai-api-key}
langchain4j.openai.base-url=${OPENAI_BASE_URL:https://api.openai.com/v1}
langchain4j.openai.model=${OPENAI_MODEL:gpt-3.5-turbo}
langchain4j.openai.temperature=${OPENAI_TEMPERATURE:0.7}
langchain4j.openai.max-tokens=${OPENAI_MAX_TOKENS:1000}

# AI Agent 配置
ai.agent.enabled=${AI_AGENT_ENABLED:true}
ai.agent.max-conversation-history=${AI_AGENT_MAX_HISTORY:10}
ai.agent.timeout=${AI_AGENT_TIMEOUT:30}
```

## 3. AI 服务接口定义

创建 AI 服务接口：

```java
package com.example.crm.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.AiService;

@AiService
public interface CrmAiService {
    
    @SystemMessage("你是一个专业的CRM系统助手，帮助用户管理客户关系。")
    String chat(@UserMessage String userMessage);
    
    @SystemMessage("分析客户数据并提供业务洞察。")
    String analyzeCustomerData(@UserMessage String query);
    
    @SystemMessage("生成客户报告和建议。")
    String generateCustomerReport(@UserMessage String requirements);
}
```

## 4. AI 工具类

创建 AI 工具类，让 AI 能够访问 CRM 数据：

```java
package com.example.crm.ai;

import com.example.crm.entity.Party;
import com.example.crm.entity.OrderHeader;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CrmTools {
    
    @Tool("获取客户信息")
    public String getCustomerInfo(String customerId) {
        Party customer = Party.findById(Long.parseLong(customerId));
        if (customer != null) {
            return String.format("客户ID: %s, 姓名: %s, 状态: %s", 
                customer.id, customer.partyName, customer.status);
        }
        return "未找到客户信息";
    }
    
    @Tool("获取客户订单")
    public String getCustomerOrders(String customerId) {
        List<OrderHeader> orders = OrderHeader.find("partyId", Long.parseLong(customerId)).list();
        return String.format("客户 %s 共有 %d 个订单", customerId, orders.size());
    }
    
    @Tool("搜索客户")
    public String searchCustomers(String keyword) {
        List<Party> customers = Party.find("partyName like ?1", "%" + keyword + "%").list();
        return String.format("找到 %d 个匹配的客户", customers.size());
    }
}
```

## 5. AI 服务实现

创建 AI 服务实现类：

```java
package com.example.crm.ai;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolSpecification;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CrmAiServiceImpl {
    
    @Inject
    CrmTools crmTools;
    
    @ConfigProperty(name = "langchain4j.openai.api-key")
    String apiKey;
    
    @ConfigProperty(name = "langchain4j.openai.model", defaultValue = "gpt-3.5-turbo")
    String model;
    
    private CrmAiService aiService;
    
    public void init() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.fromString(model))
            .temperature(0.7)
            .maxTokens(1000)
            .build();
            
        this.aiService = AiServices.builder(CrmAiService.class)
            .chatLanguageModel(chatModel)
            .tools(crmTools)
            .build();
    }
    
    public String chat(String message) {
        if (aiService == null) {
            init();
        }
        return aiService.chat(message);
    }
    
    public String analyzeCustomerData(String query) {
        if (aiService == null) {
            init();
        }
        return aiService.analyzeCustomerData(query);
    }
    
    public String generateCustomerReport(String requirements) {
        if (aiService == null) {
            init();
        }
        return aiService.generateCustomerReport(requirements);
    }
}
```

## 6. AI REST 资源

创建 AI 相关的 REST API：

```java
package com.example.crm.resource;

import com.example.crm.ai.CrmAiServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/ai")
@Tag(name = "AI Agent", description = "AI 智能助手相关接口")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {
    
    @Inject
    CrmAiServiceImpl aiService;
    
    @POST
    @Path("/chat")
    @Operation(summary = "AI 聊天", description = "与 AI 助手进行对话")
    @RolesAllowed("USER")
    public Response chat(@QueryParam("message") String message) {
        try {
            String response = aiService.chat(message);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("AI 服务暂时不可用").build();
        }
    }
    
    @POST
    @Path("/analyze")
    @Operation(summary = "数据分析", description = "使用 AI 分析客户数据")
    @RolesAllowed("USER")
    public Response analyze(@QueryParam("query") String query) {
        try {
            String response = aiService.analyzeCustomerData(query);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("数据分析失败").build();
        }
    }
    
    @POST
    @Path("/report")
    @Operation(summary = "生成报告", description = "使用 AI 生成客户报告")
    @RolesAllowed("USER")
    public Response generateReport(@QueryParam("requirements") String requirements) {
        try {
            String response = aiService.generateCustomerReport(requirements);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("报告生成失败").build();
        }
    }
}
```

## 7. 环境变量配置

创建 `.env` 文件（用于开发环境）：

```bash
# OpenAI API 配置
OPENAI_API_KEY=your-actual-openai-api-key
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
OPENAI_MAX_TOKENS=1000

# AI Agent 配置
AI_AGENT_ENABLED=true
AI_AGENT_MAX_HISTORY=10
AI_AGENT_TIMEOUT=30
```

## 8. 测试 AI 功能

创建测试类验证 AI 功能：

```java
package com.example.crm;

import com.example.crm.ai.CrmAiServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AiServiceTest {
    
    @Inject
    CrmAiServiceImpl aiService;
    
    @Test
    public void testChat() {
        String response = aiService.chat("你好，请介绍一下 CRM 系统");
        System.out.println("AI 响应: " + response);
    }
    
    @Test
    public void testCustomerAnalysis() {
        String response = aiService.analyzeCustomerData("分析一下我们的客户分布情况");
        System.out.println("分析结果: " + response);
    }
}
```

## 9. 部署注意事项

1. **API 密钥安全**: 确保在生产环境中使用环境变量或密钥管理服务
2. **成本控制**: 监控 AI API 调用次数和成本
3. **性能优化**: 实现适当的缓存和超时机制
4. **错误处理**: 为 AI 服务添加完善的错误处理和降级策略
5. **合规性**: 确保 AI 生成的内容符合业务合规要求

## 10. 扩展功能

- **向量数据库**: 集成 Chroma、Pinecone 等向量数据库
- **RAG 检索**: 实现检索增强生成功能
- **多模态**: 支持图像和文档处理
- **流式响应**: 实现实时流式 AI 响应
- **自定义模型**: 支持本地部署的模型

通过以上配置，您的 CRM 系统就具备了完整的 AI Agent 功能，可以提供智能客服、数据分析、内容生成等 AI 服务。
