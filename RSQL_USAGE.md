# RSQL 查询功能使用指南

## 概述

本项目已集成 RSQL (RESTful Service Query Language) 查询功能，允许通过 URL 参数进行灵活的数据库查询。RSQL 是一种基于 FIQL (Feed Item Query Language) 的查询语言，支持复杂的查询条件组合。

## 支持的操作符

### 比较操作符

| 操作符 | 说明 | 示例 |
|--------|------|------|
| `==` | 等于 | `productName==test` |
| `!=` | 不等于 | `statusId!=INACTIVE` |
| `>` | 大于 | `price>100` |
| `>=` | 大于等于 | `createdDate>=2024-01-01` |
| `<` | 小于 | `price<1000` |
| `<=` | 小于等于 | `createdDate<=2024-12-31` |
| `=gt=` | 大于 | `price=gt=100` |
| `=ge=` | 大于等于 | `createdDate=ge=2024-01-01` |
| `=lt=` | 小于 | `price=lt=1000` |
| `=le=` | 小于等于 | `createdDate=le=2024-12-31` |
| `=in=` | 在列表中 | `statusId=in=(ACTIVE,NEW)` |
| `=out=` | 不在列表中 | `statusId=out=(INACTIVE,DELETED)` |
| `=like=` | 模糊匹配 | `productName=like=*acme*` |
| `=notlike=` | 不匹配 | `productName=notlike=*test*` |

### 逻辑操作符

| 操作符 | 说明 | 示例 |
|--------|------|------|
| `;` | AND (与) | `productName==*test*;isVirtual==Y` |
| `,` | OR (或) | `productName==*test*,isVirtual==Y` |

## API 端点

### 产品查询

#### 1. 获取所有产品（支持 RSQL 查询）
```
GET /products?q={rsqlQuery}&page={page}&size={size}
```

#### 2. 专门的 RSQL 查询端点
```
GET /products/query?q={rsqlQuery}&page={page}&size={size}&count={returnCount}
```

### Party 查询

#### 1. 获取所有 Party（支持 RSQL 查询）
```
GET /parties?q={rsqlQuery}&page={page}&size={size}
```

#### 2. 专门的 RSQL 查询端点
```
GET /parties/query?q={rsqlQuery}&page={page}&size={size}&count={returnCount}
```

## 查询示例

### 产品查询示例

#### 1. 基本查询
```bash
# 查询名称为 "test" 的产品
GET /products/query?q=productName==test

# 查询虚拟产品
GET /products/query?q=isVirtual==Y

# 查询价格大于 100 的产品
GET /products/query?q=productWeight=gt=100
```

#### 2. 复合查询
```bash
# 查询名称包含 "acme" 且为虚拟产品的产品
GET /products/query?q=productName=like=*acme*;isVirtual==Y

# 查询状态为 ACTIVE 或 NEW 的产品
GET /products/query?q=statusId=in=(ACTIVE,NEW)

# 查询创建日期在 2024 年的产品
GET /products/query?q=createdDate=ge=2024-01-01;createdDate=le=2024-12-31
```

#### 3. 分页查询
```bash
# 查询第 1 页，每页 10 条记录
GET /products/query?q=isVirtual==Y&page=0&size=10

# 查询并返回总数
GET /products/query?q=isVirtual==Y&page=0&size=10&count=true
```

### Party 查询示例

#### 1. 基本查询
```bash
# 查询名称为 "acme" 的 Party
GET /parties/query?q=partyName==acme

# 查询个人类型的 Party
GET /parties/query?q=partyTypeId==PERSON

# 查询状态为 ACTIVE 的 Party
GET /parties/query?q=statusId==ACTIVE
```

#### 2. 复合查询
```bash
# 查询名称包含 "test" 且状态为 ACTIVE 的 Party
GET /parties/query?q=partyName=like=*test*;statusId==ACTIVE

# 查询个人或组织类型的 Party
GET /parties/query?q=partyTypeId=in=(PERSON,PARTY_GROUP)

# 查询创建日期在 2024 年的 Party
GET /parties/query?q=createdDate=ge=2024-01-01;createdDate=le=2024-12-31
```

## 响应格式

### 普通查询响应
```json
[
  {
    "productId": "PROD001",
    "productName": "Test Product",
    "isVirtual": "Y",
    "createdDate": "2024-01-01T00:00:00"
  }
]
```

### 带分页信息的响应（count=true）
```json
{
  "data": [
    {
      "productId": "PROD001",
      "productName": "Test Product",
      "isVirtual": "Y",
      "createdDate": "2024-01-01T00:00:00"
    }
  ],
  "total": 100,
  "page": 0,
  "size": 10
}
```

## 错误处理

### 无效的 RSQL 查询
```json
{
  "error": "无效的 RSQL 查询: Unknown operator: =invalid="
}
```

### 缺少查询参数
```json
{
  "error": "RSQL 查询参数 q 不能为空"
}
```

## 注意事项

1. **字段名称**: 查询中使用的字段名称必须与实体类中的字段名称完全匹配
2. **数据类型**: 系统会自动尝试解析查询参数的数据类型（字符串、数字、日期等）
3. **通配符**: 在 `=like=` 操作中，`*` 会被转换为 SQL 的 `%` 通配符
4. **日期格式**: 支持的日期格式为 `yyyy-MM-dd` 和 `yyyy-MM-dd HH:mm:ss`
5. **URL 编码**: 在 URL 中使用 RSQL 查询时，特殊字符需要进行 URL 编码

## 支持的实体字段

### Product 实体主要字段
- `productId` - 产品 ID
- `productName` - 产品名称
- `productTypeId` - 产品类型 ID
- `primaryProductCategoryId` - 主要产品分类 ID
- `brandName` - 品牌名称
- `isVirtual` - 是否为虚拟产品 (Y/N)
- `isVariant` - 是否为变体产品 (Y/N)
- `requireInventory` - 是否需要库存 (Y/N)
- `returnable` - 是否可退货 (Y/N)
- `taxable` - 是否应税 (Y/N)
- `productWeight` - 产品重量
- `createdDate` - 创建日期
- `lastModifiedDate` - 最后修改日期

### Party 实体主要字段
- `partyId` - Party ID
- `partyName` - Party 名称
- `partyTypeId` - Party 类型 ID
- `statusId` - 状态 ID
- `description` - 描述
- `createdDate` - 创建日期
- `lastModifiedDate` - 最后修改日期

## 性能建议

1. **索引**: 确保经常查询的字段有适当的数据库索引
2. **分页**: 对于大量数据的查询，建议使用分页功能
3. **查询复杂度**: 避免过于复杂的查询条件，可能影响性能
4. **缓存**: 对于频繁查询的数据，考虑使用缓存机制
