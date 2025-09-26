# JWT Token 认证调试指南

## 🎉 测试结果

**所有测试都通过了！** ✅
- **BusinessResourceTest**: 3 个测试通过
- **JwtAuthenticationTest**: 12 个测试通过  
- **RsqlQueryTest**: 9 个测试通过
- **总计**: 24 个测试，0 个失败，0 个错误

## 🔍 JWT Token 认证调试步骤

### 1. 检查 JWT 配置

您的当前配置：
```properties
# JWT 验证配置
mp.jwt.verify.issuer=crm-service
mp.jwt.verify.audiences=crm-api
mp.jwt.verify.publickey.location=META-INF/resources/publicKey.pem

# JWT 签名配置
smallrye.jwt.sign.key.location=privateKey-pkcs8.pem
smallrye.jwt.sign.algorithm=RS256

# JWT 自定义配置
jwt.issuer=crm-service
jwt.expiration.time=3600
```

### 2. 生成测试 Token

使用您的 JwtConfig 生成测试 Token：

```java
@Inject
JwtConfig jwtConfig;

// 生成包含正确角色的 Token
String token = jwtConfig.generateToken("testuser", Set.of("USER", "ADMIN"));
System.out.println("Generated Token: " + token);
```

### 3. 验证 Token 内容

将生成的 Token 在 [jwt.io](https://jwt.io) 上解码，检查：

- **iss**: 应该是 `crm-service`
- **aud**: 应该包含 `crm-api`
- **groups**: 应该包含 `["USER", "ADMIN"]` 等角色
- **exp**: 过期时间（当前时间 + 3600 秒）
- **alg**: 应该是 `RS256`

### 4. 测试 API 访问

```bash
# 使用生成的 Token 测试 API
curl -H "Authorization: Bearer <your_token>" \
     http://localhost:8080/products

# 测试 RSQL 查询
curl -H "Authorization: Bearer <your_token>" \
     "http://localhost:8080/products/query?q=productName==test"
```

### 5. 常见 401 错误原因

#### 5.1 Token 格式错误
```bash
# ❌ 错误格式
Authorization: Bearer<token>  # 缺少空格
Authorization: Bearer <token>  # 多余空格
Authorization: <token>        # 缺少 Bearer

# ✅ 正确格式
Authorization: Bearer <token>
```

#### 5.2 Token 内容错误
- **iss 不匹配**: Token 中的 `iss` 必须是 `crm-service`
- **aud 不匹配**: Token 中的 `aud` 必须包含 `crm-api`
- **缺少 groups**: Token 中必须包含 `groups` 声明
- **Token 过期**: 检查 `exp` 时间戳

#### 5.3 权限不足 (403 vs 401)
- **401 Unauthorized**: Token 无效、过期或格式错误
- **403 Forbidden**: Token 有效但缺少所需角色

### 6. 调试日志

在 `application.properties` 中添加调试日志：

```properties
# JWT 调试日志
quarkus.log.category."io.smallrye.jwt".level=DEBUG
quarkus.log.category."io.quarkus.security".level=DEBUG
quarkus.log.category."org.eclipse.microprofile.jwt".level=DEBUG
```

### 7. 测试用例验证

我们的测试用例验证了：

✅ **JWT 配置正确**
- issuer: `crm-service`
- expiration: `3600` 秒
- 算法: `RS256`

✅ **Token 生成正常**
- 格式正确（3 个部分，用点分隔）
- 包含正确的角色信息
- 刷新 Token 生成正常

✅ **不同角色 Token**
- USER 角色 Token
- ADMIN 角色 Token  
- 多角色 Token
- 空角色 Token

### 8. 快速诊断命令

```bash
# 1. 检查公钥文件是否存在
ls -la src/main/resources/META-INF/resources/publicKey.pem

# 2. 检查私钥文件是否存在  
ls -la src/main/resources/META-INF/resources/privateKey-pkcs8.pem

# 3. 运行 JWT 测试
./mvnw test -Dtest=JwtAuthenticationTest

# 4. 运行所有测试
./mvnw test
```

### 9. 示例 Token 结构

正确的 JWT Token 应该包含：

```json
{
  "iss": "crm-service",
  "sub": "testuser", 
  "groups": ["USER", "ADMIN"],
  "iat": 1632672000,
  "exp": 1632675600,
  "aud": "crm-api"
}
```

### 10. 解决 401 的步骤

1. **确认 Token 生成**: 使用 JwtConfig 生成新 Token
2. **验证 Token 内容**: 在 jwt.io 上解码检查
3. **检查请求格式**: 确保 `Authorization: Bearer <token>`
4. **验证角色权限**: 确保 Token 包含所需角色
5. **检查时间同步**: 确保系统时间正确
6. **查看调试日志**: 启用 JWT 调试日志

## 🎯 总结

您的 JWT 认证系统已经通过所有测试，配置正确。如果仍然遇到 401 错误，请：

1. 使用 JwtConfig 生成新的测试 Token
2. 在 jwt.io 上验证 Token 内容
3. 确保请求头格式正确
4. 检查 Token 是否包含正确的角色

需要我帮您生成一个测试 Token 或进一步调试吗？
