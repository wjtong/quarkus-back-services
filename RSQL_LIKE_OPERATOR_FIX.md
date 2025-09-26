# RSQL Like 操作符修复

## 问题描述

使用 RSQL 进行查询时，报错：
```
"error": "无效的 RSQL 查询: cz.jirutka.rsql.parser.UnknownOperatorException: Unknown operator: =like="
```

## 问题原因

RSQL 解析器默认不支持 `=like=` 和 `=notlike=` 操作符。这些操作符需要手动配置到 RSQL 解析器中。

## 解决方案

在 `RsqlUtil.java` 中配置自定义操作符：

```java
private static final RSQLParser RSQL_PARSER = new RSQLParser(
    Set.of(
        RSQLOperators.EQUAL,
        RSQLOperators.NOT_EQUAL,
        RSQLOperators.GREATER_THAN,
        RSQLOperators.GREATER_THAN_OR_EQUAL,
        RSQLOperators.LESS_THAN,
        RSQLOperators.LESS_THAN_OR_EQUAL,
        RSQLOperators.IN,
        RSQLOperators.NOT_IN,
        new ComparisonOperator("=like=", true),      // 新增
        new ComparisonOperator("=notlike=", true)    // 新增
    )
);
```

## 修复效果

### 修复前
```bash
# ❌ 会报错
GET /products/query?q=productName=like=*acme*
# 错误: Unknown operator: =like=
```

### 修复后
```bash
# ✅ 正常工作
GET /products/query?q=productName=like=*acme*
# 返回包含 "acme" 的产品
```

## 支持的操作符

现在支持以下 Like 操作符：

| 操作符 | 描述 | 示例 | 生成的 SQL |
|--------|------|------|------------|
| `=like=` | 模糊匹配 | `name=like=*John*` | `name LIKE '%John%'` |
| `=like=` | 前缀匹配 | `name=like=John*` | `name LIKE 'John%'` |
| `=like=` | 后缀匹配 | `name=like=*John` | `name LIKE '%John'` |
| `=like=` | 精确匹配 | `name=like=John` | `name LIKE 'John'` |
| `=notlike=` | 不匹配 | `name=notlike=*test*` | `name NOT LIKE '%test%'` |

## 测试验证

创建了 `RsqlLikeOperatorTest` 测试类，验证了：

1. ✅ `=like=` 操作符基本功能
2. ✅ `=notlike=` 操作符基本功能
3. ✅ 前缀匹配 (`test*`)
4. ✅ 后缀匹配 (`*test`)
5. ✅ 精确匹配 (`test`)
6. ✅ 与 AND 操作符结合
7. ✅ 与 OR 操作符结合
8. ✅ Party 名称的 like 查询
9. ✅ 复杂的 like 查询
10. ✅ 操作符验证功能

## 使用示例

### 产品查询
```bash
# 查询产品名称包含 "acme" 的产品
GET /products/query?q=productName=like=*acme*

# 查询产品名称以 "test" 开头的产品
GET /products/query?q=productName=like=test*

# 查询产品名称以 "demo" 结尾的产品
GET /products/query?q=productName=like=*demo

# 查询产品名称不包含 "test" 的产品
GET /products/query?q=productName=notlike=*test*
```

### Party 查询
```bash
# 查询 Party 名称包含 "acme" 的 Party
GET /parties/query?q=partyName=like=*acme*

# 查询 Party 名称以 "test" 开头的 Party
GET /parties/query?q=partyName=like=test*

# 查询 Party 名称不包含 "demo" 的 Party
GET /parties/query?q=partyName=notlike=*demo*
```

### 复合查询
```bash
# 查询名称包含 "acme" 且为虚拟产品的产品
GET /products/query?q=productName=like=*acme*;isVirtual==Y

# 查询 Party 名称包含 "acme" 且类型为 PERSON 的 Party
GET /parties/query?q=partyName=like=*acme*;partyTypeId==PERSON

# 查询产品名称以 "test" 开头或以 "demo" 结尾的产品
GET /products/query?q=productName=like=test*,productName=like=*demo
```

## 通配符说明

- `*` 表示任意字符序列
- `*acme*` 匹配包含 "acme" 的字符串
- `acme*` 匹配以 "acme" 开头的字符串
- `*acme` 匹配以 "acme" 结尾的字符串
- `acme` 精确匹配 "acme"

## 性能考虑

- Like 查询通常比精确匹配慢
- 建议在相关字段上创建索引
- 避免以 `%` 开头的查询（如 `%acme`），这会阻止索引使用

## 测试结果

**所有测试通过** ✅
- **RsqlLikeOperatorTest**: 10 个测试通过
- **所有测试总计**: 39 个测试，0 个失败，0 个错误

## 总结

通过在 RSQL 解析器中配置自定义操作符，成功解决了 `=like=` 和 `=notlike=` 操作符不被识别的问题。现在可以正常使用这些操作符进行模糊匹配查询，大大增强了 RSQL 查询的灵活性。

修复后的功能完全向后兼容，不会影响现有的查询功能。
