# AI 配置指南

本指南说明如何配置 CRM 系统中的 AI Agent 功能。

## 环境变量配置

创建 `.env` 文件（用于开发环境）或设置系统环境变量：

```bash
# OpenAI API 配置
OPENAI_API_KEY=your-actual-openai-api-key
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
OPENAI_MAX_TOKENS=1000

# 其他 AI 服务提供商配置（可选）
# ANTHROPIC_API_KEY=your-anthropic-api-key
# GOOGLE_API_KEY=your-google-api-key

# 向量数据库配置（可选）
EMBEDDING_STORE_TYPE=in-memory
EMBEDDING_DIMENSION=1536

# AI Agent 配置
AI_AGENT_ENABLED=true
AI_AGENT_MAX_HISTORY=10
AI_AGENT_TIMEOUT=30
```

## 配置说明

### OpenAI 配置
- `OPENAI_API_KEY`: 您的 OpenAI API 密钥
- `OPENAI_BASE_URL`: API 基础 URL（默认：https://api.openai.com/v1）
- `OPENAI_MODEL`: 使用的模型（默认：gpt-3.5-turbo）
- `OPENAI_TEMPERATURE`: 响应随机性（0-1，默认：0.7）
- `OPENAI_MAX_TOKENS`: 最大令牌数（默认：1000）

### AI Agent 配置
- `AI_AGENT_ENABLED`: 是否启用 AI 功能（默认：true）
- `AI_AGENT_MAX_HISTORY`: 最大对话历史记录数（默认：10）
- `AI_AGENT_TIMEOUT`: 请求超时时间（秒，默认：30）

## 安全注意事项

1. **API 密钥安全**：
   - 永远不要将 API 密钥提交到版本控制系统
   - 使用环境变量或密钥管理服务
   - 定期轮换 API 密钥

2. **生产环境配置**：
   - 使用专用的生产环境 API 密钥
   - 设置适当的速率限制
   - 监控 API 使用量和成本

3. **数据隐私**：
   - 确保敏感数据不会发送到外部 AI 服务
   - 实施数据脱敏和过滤机制
   - 遵守相关数据保护法规

## 测试配置

运行以下命令测试 AI 配置：

```bash
# 运行 AI 服务测试
./mvnw test -Dtest=AiServiceTest

# 检查 AI 服务状态
curl -X GET "http://localhost:8080/api/ai/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 故障排除

### 常见问题

1. **API 密钥无效**：
   - 检查 API 密钥是否正确
   - 确认 API 密钥有足够的权限
   - 验证 API 密钥是否过期

2. **网络连接问题**：
   - 检查网络连接
   - 确认防火墙设置
   - 验证代理配置

3. **服务不可用**：
   - 检查 AI 服务状态
   - 查看应用日志
   - 验证配置参数

### 日志调试

启用详细日志记录：

```properties
# 在 application.properties 中添加
quarkus.log.category."com.example.crm.ai".level=DEBUG
```

## 性能优化

1. **缓存策略**：
   - 实现响应缓存
   - 使用 Redis 或内存缓存
   - 设置合理的缓存过期时间

2. **并发控制**：
   - 限制并发请求数量
   - 实现请求队列
   - 使用连接池

3. **成本控制**：
   - 监控 API 调用次数
   - 设置使用限制
   - 优化提示词长度

## 扩展功能

### 支持更多 AI 提供商

1. **Anthropic Claude**：
   ```properties
   langchain4j.anthropic.api-key=${ANTHROPIC_API_KEY}
   langchain4j.anthropic.model=claude-3-sonnet-20240229
   ```

2. **Google Gemini**：
   ```properties
   langchain4j.google.api-key=${GOOGLE_API_KEY}
   langchain4j.google.model=gemini-pro
   ```

3. **本地模型**：
   ```properties
   langchain4j.local.model.path=/path/to/local/model
   langchain4j.local.model.type=ollama
   ```

### 向量数据库集成

1. **Chroma**：
   ```properties
   langchain4j.embedding.store.type=chroma
   langchain4j.embedding.store.host=localhost
   langchain4j.embedding.store.port=8000
   ```

2. **Pinecone**：
   ```properties
   langchain4j.embedding.store.type=pinecone
   langchain4j.embedding.store.api-key=${PINECONE_API_KEY}
   langchain4j.embedding.store.environment=${PINECONE_ENVIRONMENT}
   ```

通过以上配置，您的 CRM 系统就具备了完整的 AI Agent 功能，可以提供智能客服、数据分析、内容生成等 AI 服务。
