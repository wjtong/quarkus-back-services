#!/bin/bash

# CRM 服务开发环境启动脚本

echo "🚀 启动 CRM 服务开发环境..."

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker"
    exit 1
fi

# 启动 PostgreSQL 数据库
echo "📦 启动 PostgreSQL 数据库..."
docker-compose up -d postgres

# 等待数据库启动
echo "⏳ 等待数据库启动..."
sleep 10

# 检查数据库是否就绪
until docker-compose exec postgres pg_isready -U crm_user -d crm_db; do
    echo "⏳ 等待数据库连接..."
    sleep 2
done

echo "✅ 数据库已就绪"

# 启动 Quarkus 应用
echo "🔥 启动 Quarkus 应用..."
./mvnw quarkus:dev
