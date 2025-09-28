# AI Agent 系统使用指南

本指南介绍如何使用 CRM 系统中的 AI Agent 功能，为每个员工创建专属的 AI 助手。

## 系统概述

AI Agent 系统为企业员工提供专属的 AI 助手，每个 AI Agent 都具有：
- **个性化配置**：根据员工职位、部门、职责定制
- **权限控制**：基于员工角色的数据访问权限
- **任务管理**：自动执行和跟踪工作任务
- **对话记录**：完整的交互历史和学习能力
- **性能监控**：实时统计和优化建议

## 核心概念

### 1. AI Agent
每个员工的专属 AI 助手，具有独特的：
- **身份信息**：Agent ID、名称、所属员工
- **职位信息**：职位、部门、职责描述
- **能力配置**：技能、权限、工作范围
- **个性特征**：沟通风格、性格特征
- **学习能力**：知识库、学习率、置信度阈值

### 2. 任务系统
AI Agent 可以执行各种类型的任务：
- **数据分析**：客户数据分析和洞察
- **报告生成**：自动生成业务报告
- **客户服务**：处理客户咨询和问题
- **内容创作**：生成营销内容和文档
- **流程处理**：自动化业务流程

### 3. 对话管理
记录和管理 AI Agent 与用户的交互：
- **对话历史**：完整的交互记录
- **情感分析**：用户情感和满意度
- **意图识别**：理解用户真实需求
- **反馈收集**：持续改进服务质量

## 快速开始

### 1. 创建 AI Agent

为员工创建专属 AI Agent：

```bash
curl -X POST "http://localhost:8080/api/ai-agents/create" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "EMP_001",
    "agentName": "张三的专属助手",
    "position": "销售经理",
    "department": "销售部",
    "responsibilities": "负责客户关系管理、销售数据分析、客户咨询支持",
    "permissions": "READ_CUSTOMER,WRITE_ORDER,ANALYZE_SALES_DATA"
  }'
```

### 2. 配置 Agent 能力

设置 Agent 的详细能力：

```bash
curl -X PUT "http://localhost:8080/api/ai-agents/AGENT_12345678" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "capabilities": "客户数据分析,销售报告生成,客户咨询支持",
    "personalityTraits": "专业、友好、高效",
    "communicationStyle": "正式但亲切，注重细节"
  }'
```

### 3. 分配任务

为 Agent 分配工作任务：

```bash
curl -X POST "http://localhost:8080/api/ai-agents/AGENT_12345678/tasks" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "taskTitle": "分析本月销售数据",
    "taskDescription": "分析销售部门的月度业绩数据，生成分析报告",
    "taskType": "DATA_ANALYSIS",
    "inputData": "销售数据文件路径",
    "priority": 3
  }'
```

### 4. 监控任务执行

查看任务执行状态：

```bash
curl -X GET "http://localhost:8080/api/ai-agents/AGENT_12345678/tasks" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## API 参考

### AI Agent 管理

#### 创建 Agent
```http
POST /api/ai-agents/create
```

**参数：**
- `employeeId`: 员工 ID（必需）
- `agentName`: Agent 名称（必需）
- `position`: 职位（必需）
- `department`: 部门（必需）
- `responsibilities`: 职责描述
- `permissions`: 权限列表

#### 获取 Agent 列表
```http
GET /api/ai-agents
```

#### 根据员工获取 Agent
```http
GET /api/ai-agents/employee/{employeeId}
```

#### 根据部门获取 Agent
```http
GET /api/ai-agents/department/{department}
```

#### 更新 Agent
```http
PUT /api/ai-agents/{agentId}
```

#### 切换 Agent 状态
```http
PUT /api/ai-agents/{agentId}/status?active=true
```

### 任务管理

#### 分配任务
```http
POST /api/ai-agents/{agentId}/tasks
```

**参数：**
- `taskTitle`: 任务标题（必需）
- `taskDescription`: 任务描述
- `taskType`: 任务类型（必需）
- `inputData`: 输入数据
- `priority`: 优先级（1-10）

#### 开始任务
```http
PUT /api/ai-agent-tasks/{taskId}/start
```

#### 完成任务
```http
PUT /api/ai-agent-tasks/{taskId}/complete
```

**参数：**
- `outputData`: 输出数据
- `resultSummary`: 结果摘要
- `confidenceScore`: 置信度分数
- `qualityRating`: 质量评分

#### 任务失败
```http
PUT /api/ai-agent-tasks/{taskId}/fail
```

**参数：**
- `errorMessage`: 错误消息

### 对话管理

#### 开始对话
```http
POST /api/ai-agent-conversations/start
```

#### 添加 AI 响应
```http
POST /api/ai-agent-conversations/{conversationId}/response
```

#### 添加反馈
```http
POST /api/ai-agent-conversations/{conversationId}/feedback
```

## 配置指南

### 1. 权限配置

为不同职位的员工配置适当的权限：

```json
{
  "销售代表": [
    "READ_CUSTOMER",
    "WRITE_ORDER",
    "VIEW_SALES_DATA"
  ],
  "销售经理": [
    "READ_CUSTOMER",
    "WRITE_ORDER",
    "ANALYZE_SALES_DATA",
    "MANAGE_TEAM"
  ],
  "数据分析师": [
    "READ_ALL_DATA",
    "ANALYZE_DATA",
    "GENERATE_REPORT",
    "EXPORT_DATA"
  ]
}
```

### 2. 能力配置

根据工作需求配置 Agent 能力：

```json
{
  "销售类": [
    "客户关系管理",
    "销售数据分析",
    "客户咨询支持",
    "销售报告生成"
  ],
  "市场类": [
    "市场分析",
    "内容创作",
    "活动策划",
    "竞品分析"
  ],
  "客服类": [
    "客户咨询",
    "问题解决",
    "投诉处理",
    "满意度调查"
  ]
}
```

### 3. 个性特征配置

设置 Agent 的沟通风格：

```json
{
  "正式型": {
    "communicationStyle": "正式、专业、准确",
    "personalityTraits": "严谨、可靠、高效"
  },
  "友好型": {
    "communicationStyle": "亲切、耐心、理解",
    "personalityTraits": "温暖、包容、支持"
  },
  "高效型": {
    "communicationStyle": "简洁、直接、快速",
    "personalityTraits": "果断、专注、结果导向"
  }
}
```

## 最佳实践

### 1. Agent 设计原则

- **职责明确**：每个 Agent 应该有清晰的工作范围
- **权限最小化**：只授予必要的权限
- **能力匹配**：能力配置要与实际工作需求匹配
- **持续优化**：根据使用反馈不断改进

### 2. 任务分配策略

- **优先级管理**：合理设置任务优先级
- **负载均衡**：避免单个 Agent 过载
- **任务类型匹配**：根据 Agent 能力分配任务
- **监控执行**：实时跟踪任务执行状态

### 3. 对话管理

- **上下文保持**：维护对话的连续性
- **情感识别**：理解用户情感状态
- **反馈收集**：积极收集用户反馈
- **持续学习**：基于对话历史改进

### 4. 性能监控

- **响应时间**：监控 Agent 响应速度
- **成功率**：跟踪任务完成率
- **用户满意度**：收集用户评价
- **资源使用**：监控系统资源消耗

## 故障排除

### 常见问题

1. **Agent 创建失败**
   - 检查员工 ID 是否存在
   - 确认权限配置正确
   - 验证必填字段完整

2. **任务执行失败**
   - 检查 Agent 状态是否激活
   - 确认权限是否足够
   - 验证输入数据格式

3. **对话响应异常**
   - 检查 AI 服务配置
   - 确认网络连接正常
   - 验证 API 密钥有效

### 日志调试

启用详细日志记录：

```properties
# 在 application.properties 中添加
quarkus.log.category."com.example.crm.ai".level=DEBUG
quarkus.log.category."com.example.crm.service".level=DEBUG
```

### 性能优化

1. **数据库优化**
   - 为常用查询添加索引
   - 定期清理历史数据
   - 优化查询语句

2. **缓存策略**
   - 缓存 Agent 配置信息
   - 缓存常用查询结果
   - 使用 Redis 缓存

3. **并发控制**
   - 限制并发任务数量
   - 实现任务队列
   - 使用连接池

## 扩展功能

### 1. 多语言支持
- 支持多种语言的对话
- 自动语言检测
- 本地化响应

### 2. 集成外部系统
- CRM 系统集成
- 邮件系统集成
- 文档管理系统集成

### 3. 高级分析
- 用户行为分析
- 性能趋势分析
- 预测性分析

### 4. 移动端支持
- 移动应用集成
- 推送通知
- 离线功能

通过以上配置和使用，您的企业就可以为每个员工提供专属的 AI Agent 服务，大大提高工作效率和客户服务质量。
