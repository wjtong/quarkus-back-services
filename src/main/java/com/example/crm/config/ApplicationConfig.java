package com.example.crm.config;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

/**
 * JAX-RS 应用配置类
 * 定义 REST API 的基础路径
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    // 基础 API 路径设置为 /api
    // 所有 REST 资源将在此路径下访问
}
