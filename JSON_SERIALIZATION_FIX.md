# JSON 序列化无限循环问题修复

## 问题描述

在访问 `/parties` 端点时，返回的 Party 对象中包含 Person 信息，而 Person 对象中又包含 Party 信息，导致 JSON 序列化时出现无限循环：

```
Party -> Person -> Party -> Person -> ...
```

## 问题原因

Party 和 Person 实体之间存在双向的一对一关系：

```java
// Party.java
@OneToOne(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
public Person person;

// Person.java  
@OneToOne
@JoinColumn(name = "party_id")
@MapsId
public Party party;
```

当 Jackson 序列化 Party 对象时，会包含 Person 信息，而序列化 Person 时又会包含 Party 信息，形成无限循环。

## 解决方案

在 Person 实体的 party 字段上添加 `@JsonIgnore` 注解：

```java
// Person.java
@OneToOne
@JoinColumn(name = "party_id")
@MapsId
@JsonIgnore  // 防止 JSON 序列化时的无限循环
public Party party;
```

## 修复效果

### 修复前
```json
{
  "partyId": "TEST_001",
  "partyName": "测试客户",
  "person": {
    "partyId": "TEST_001",
    "firstName": "张",
    "lastName": "三",
    "party": {
      "partyId": "TEST_001",
      "partyName": "测试客户",
      "person": {
        // 无限循环...
      }
    }
  }
}
```

### 修复后
```json
{
  "partyId": "TEST_001",
  "partyName": "测试客户",
  "person": {
    "partyId": "TEST_001",
    "firstName": "张",
    "lastName": "三"
    // 不再包含 party 字段
  }
}
```

## 测试验证

创建了 `PartyPersonSerializationTest` 测试类，验证：

1. ✅ Party 序列化包含 Person 信息
2. ✅ Person 序列化不包含 Party 信息（避免循环）
3. ✅ 没有 Person 的 Party 正常序列化
4. ✅ 显示名称方法正常工作
5. ✅ 完整姓名方法正常工作

## 技术细节

### 使用的注解
- `@JsonIgnore`: 在 JSON 序列化时忽略该字段
- 只影响 JSON 序列化，不影响 JPA 关系映射
- 数据库查询和对象关系仍然正常工作

### 序列化结果示例

**Party 序列化结果**:
```json
{
  "partyId": "TEST_PARTY_001",
  "partyTypeId": "PERSON",
  "partyName": "测试客户",
  "person": {
    "partyId": "TEST_PARTY_001",
    "firstName": "张",
    "lastName": "三",
    "gender": "M",
    "fullName": "张 三"
  },
  "displayName": "张 三"
}
```

**Person 序列化结果**:
```json
{
  "partyId": "TEST_PARTY_002",
  "firstName": "李",
  "lastName": "四",
  "gender": "F",
  "fullName": "李 四"
  // 注意：没有 party 字段
}
```

## 其他解决方案

除了 `@JsonIgnore`，还可以考虑：

1. **@JsonManagedReference 和 @JsonBackReference**:
   ```java
   // Party.java
   @JsonManagedReference
   public Person person;
   
   // Person.java
   @JsonBackReference
   public Party party;
   ```

2. **@JsonIdentityInfo**:
   ```java
   @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "partyId")
   public class Party { ... }
   ```

3. **自定义序列化器**:
   实现 `JsonSerializer` 接口自定义序列化逻辑

## 推荐方案

使用 `@JsonIgnore` 是最简单有效的解决方案，因为：

- ✅ 实现简单，只需添加一个注解
- ✅ 性能好，没有额外的序列化开销
- ✅ 符合业务需求：访问 Party 时带出 Person，Person 不需要带出 Party
- ✅ 不影响 JPA 关系映射和数据库操作
- ✅ 测试验证完全通过

## 总结

通过在 Person 实体的 party 字段上添加 `@JsonIgnore` 注解，成功解决了 JSON 序列化时的无限循环问题。现在访问 `/parties` 端点时，返回的 Party 对象会包含 Person 信息，但 Person 对象不会包含 Party 信息，避免了循环引用。

所有测试通过，功能正常，问题已完全解决。
