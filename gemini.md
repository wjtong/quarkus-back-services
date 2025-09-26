# CRM 服务端项目

这是一个基于 Quarkus 框架构建的 CRM (客户关系管理) 系统服务端。它提供了一套完整的 RESTful API，用于管理客户数据，项目本身不包含任何前端页面。

## 技术栈

本项目采用以下技术和库构建：

- **框架**: [Quarkus](https://quarkus.io/) - 一个为 GraalVM 和 HotSpot 定制的 Kubernetes 原生 Java 栈。
- **API**: RESTEasy Reactive - 用于构建响应式 RESTful API。
- **JSON 处理**: RESTEasy Reactive Jackson - 提供高效的 JSON 序列化和反序列化。
- **数据库交互**: Hibernate ORM with Panache - 简化了数据库实体和仓库的操作。
- **数据库**: PostgreSQL - 一个强大的开源对象关系数据库。
- **安全**: SmallRye JWT - 基于 JSON Web Token 的声明式安全认证。
- **API 文档**: Quarkus SmallRye OpenAPI - 自动生成符合 OpenAPI 规范的 API 文档。

## 环境准备

在开始之前，请确保您的开发环境中安装了以下软件：

- JDK 17+
- Maven 3.8+ 或 Gradle
- Docker (推荐用于快速启动 PostgreSQL 数据库)
- 一个你喜欢的 IDE (如 IntelliJ IDEA 或 VS Code)

## 项目配置与启动

1.  **克隆项目**
    ```bash
    git clone <your-repository-url>
    cd quarkus-back-services
    ```

2.  **配置数据库连接**

    打开 `src/main/resources/application.properties` 文件，根据您的环境修改 PostgreSQL 数据库的连接信息。

    ```properties
    # PostgreSQL Datasource
    quarkus.datasource.db-kind=postgresql
    quarkus.datasource.username=your-db-username
    quarkus.datasource.password=your-db-password
    quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/crm_db

    # 当应用启动时，根据实体类自动更新数据库表结构
    quarkus.hibernate-orm.database.generation=update
    ```

3.  **启动应用 (开发模式)**

    使用以下命令启动应用。Quarkus 的开发模式支持热加载，您修改代码后无需重启即可看到效果。

    - **使用 Maven:**
      ```bash
      ./mvnw quarkus:dev
      ```
    - **使用 Gradle:**
      ```bash
      ./gradlew quarkusDev
      ```

    应用默认启动在 `8080` 端口。

## API 文档

项目集成了 `quarkus-smallrye-openapi`，它会自动扫描 JAX-RS 注解并生成 OpenAPI 3.0 文档。

当应用启动后，您可以访问以下地址查看和测试 API：

- **Swagger UI**: [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

所有可用的 API 端点、请求参数和响应模型都会在这里详细列出。

## 安全

本项目的 API 受 JWT (JSON Web Token) 保护。客户端在请求受保护的端点时，必须在 HTTP Header 中提供一个有效的 `Authorization: Bearer <token>`。

JWT 的相关配置（例如公钥位置和签发者）也位于 `application.properties` 文件中。

```properties
# SmallRye JWT
mp.jwt.verify.publickey.location=META-INF/resources/publicKey.pem
mp.jwt.verify.issuer=https://your-issuer.com/
```

## 打包与部署

您可以使用标准 Maven 或 Gradle 命令将应用打包成一个可执行的 JAR 文件。

- **使用 Maven:**
  ```bash
  ./mvnw package
  ```
- **使用 Gradle:**
  ```bash
  ./gradlew build
  ```

打包完成后，您可以在 `target/quarkus-app/` 目录下找到 `quarkus-run.jar` 文件，并使用 `java -jar` 命令来运行它。
