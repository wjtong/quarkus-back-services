# 协作任务处理系统使用指南

## 系统概述

本系统实现了一个基于AI Agent的智能协作任务处理系统，支持多种业务流程的自动化处理，包括报销申请、请假申请、采购申请等。系统通过AI Agent模拟不同角色的员工，根据预定义的业务规则自动处理任务并流转到下一个步骤。

## 核心特性

- **智能工作流**: 支持复杂的工作流定义，包括条件判断、角色权限、金额阈值等
- **AI Agent协作**: 每个员工都有对应的AI Agent，能够智能处理分配的任务
- **自动流转**: 根据业务规则自动将任务流转到下一个处理步骤
- **实时监控**: 提供工作流执行状态的实时监控和历史追踪
- **灵活配置**: 工作流类型和步骤完全可配置，支持运行时更新

## 系统架构

### 核心实体

1. **WorkflowType**: 工作流类型定义（如报销申请、请假申请等）
2. **WorkflowInstance**: 工作流实例（具体的申请记录）
3. **WorkflowStep**: 工作流步骤定义（审批流程中的每个步骤）
4. **WorkflowExecution**: 工作流执行记录（每个步骤的执行情况）
5. **AiAgent**: AI Agent定义（代表每个员工的AI助手）

### 服务层

1. **WorkflowService**: 工作流核心服务，处理任务创建、执行、流转
2. **AiAgentService**: AI Agent服务，处理AI Agent的任务分配和执行
3. **DataInitializationService**: 数据初始化服务，创建示例数据

## 使用示例

### 1. 发起报销申请

```bash
curl -X POST http://localhost:8080/api/workflow/expense/apply \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "employeeId": "EMP_001",
    "expenseType": "差旅费",
    "amount": 800.00,
    "reason": "出差北京参加会议",
    "description": "往返机票和住宿费用",
    "department": "技术部",
    "project": "产品研发"
  }'
```

### 2. 查询申请状态

```bash
curl -X GET http://localhost:8080/api/workflow/expense/status/{workflowInstanceId} \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. 获取执行历史

```bash
curl -X GET http://localhost:8080/api/workflow/expense/history/{workflowInstanceId} \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 报销申请流程说明

### 审批规则

根据申请金额，系统会自动选择不同的审批路径：

1. **金额 < 500元**: 部门经理 → 总监 → 完成
2. **500元 ≤ 金额 < 1000元**: 部门经理 → 总监 → 财务 → 完成
3. **金额 ≥ 1000元**: 部门经理 → 总监 → 财务 → 总经理 → 完成

### 角色说明

- **普通员工**: 发起申请
- **部门经理**: 第一级审批
- **总监**: 第二级审批
- **财务**: 财务审核（金额≥500元时）
- **总经理**: 最终审批（金额≥1000元时）

## API端点

### 工作流管理

- `GET /api/workflow/types` - 获取工作流类型列表
- `GET /api/workflow/types/{id}` - 获取工作流类型详情
- `GET /api/workflow/types/{id}/steps` - 获取工作流步骤
- `POST /api/workflow/instances` - 创建工作流实例
- `GET /api/workflow/instances/{id}` - 获取工作流实例
- `GET /api/workflow/instances/{id}/executions` - 获取执行历史

### AI Agent管理

- `GET /api/ai-agent/agents` - 获取AI Agent列表
- `GET /api/ai-agent/agents/{id}` - 获取AI Agent详情
- `GET /api/ai-agent/agents/{id}/statistics` - 获取AI Agent统计信息
- `GET /api/ai-agent/agents/by-role/{role}` - 根据角色获取AI Agent
- `GET /api/ai-agent/agents/{id}/tasks` - 获取AI Agent任务列表

### 报销申请专用

- `POST /api/workflow/expense/apply` - 发起报销申请
- `GET /api/workflow/expense/status/{id}` - 查询报销申请状态
- `GET /api/workflow/expense/history/{id}` - 获取报销申请历史

## 配置说明

### 数据库配置

系统使用PostgreSQL数据库，配置在`application.properties`中：

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=your-username
quarkus.datasource.password=your-password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/crm_db
```

### AI服务配置

```properties
# LangChain4j 配置
langchain4j.openai.api-key=${OPENAI_API_KEY:your-openai-api-key}
langchain4j.openai.model=${OPENAI_MODEL:gpt-3.5-turbo}
langchain4j.openai.temperature=${OPENAI_TEMPERATURE:0.7}

# 工作流配置
workflow.enabled=${WORKFLOW_ENABLED:true}
workflow.max-concurrent-instances=${WORKFLOW_MAX_INSTANCES:100}
workflow.default-timeout=${WORKFLOW_DEFAULT_TIMEOUT:3600}
```

## 扩展开发

### 添加新的工作流类型

1. 在数据库中创建新的`WorkflowType`记录
2. 定义相应的`WorkflowStep`步骤
3. 创建专用的REST API资源类
4. 更新AI Agent的职责和能力描述

### 自定义业务规则

工作流的业务规则通过JSON格式存储在数据库中，支持：

- 金额阈值判断
- 角色权限控制
- 条件分支逻辑
- 超时和重试机制

### AI Agent定制

每个AI Agent可以配置：

- 个性特征
- 沟通风格
- 知识库内容
- 处理能力
- 响应时间

## 监控和日志

系统提供完整的监控和日志功能：

- 工作流执行状态实时监控
- AI Agent性能统计
- 任务处理历史记录
- 错误和异常日志

## 注意事项

1. 系统启动时会自动初始化示例数据
2. AI Agent需要有效的API密钥才能正常工作
3. 工作流规则支持运行时更新，无需重启服务
4. 所有API都需要JWT认证
5. 系统支持并发处理多个工作流实例

## 故障排除

### 常见问题

1. **AI Agent无响应**: 检查API密钥配置和网络连接
2. **工作流卡住**: 检查步骤条件和AI Agent状态
3. **数据初始化失败**: 检查数据库连接和权限
4. **编译错误**: 确保所有依赖都正确配置

### 日志查看

```bash
# 查看应用日志
tail -f logs/quarkus.log

# 查看特定组件的日志
grep "WorkflowService" logs/quarkus.log
grep "AiAgentService" logs/quarkus.log
```

## 技术支持

如有问题，请查看：

1. 系统日志文件
2. API文档 (http://localhost:8080/q/swagger-ui)
3. 数据库表结构和数据
4. 配置文件设置
