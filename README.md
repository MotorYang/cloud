# SpringCloud 微服务系统

## 项目简介

这是一个基于 SpringCloud 的微服务架构项目，采用前后端分离设计，提供完整的用户认证、权限管理等功能。项目使用 PostgreSQL 数据库、MyBatis Plus 持久层框架，以及 Nacos 作为服务注册与发现中心。

## 技术栈

### 核心框架
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0
- **JDK**: 17

### 数据库相关
- **PostgreSQL**: 42.7.1
- **MyBatis Plus**: 3.5.15
- **Druid**: 1.2.20 (数据库连接池)

### 安全认证
- **JWT**: 0.12.3 (JSON Web Token)
- **Easy Captcha**: 1.6.2 (验证码)

### 工具类库
- **Hutool**: 5.8.24
- **Lombok**: 1.18.30
- **FastJSON**: 2.0.43
- **MapStruct**: 1.6.3
- **Apache Commons**: Lang3, Collections4, IO

### API 文档
- **Knife4j**: 4.4.0 (基于 OpenAPI 3.0)

## 项目结构

```
cloud
├── common          # 公共模块
│   ├── constant    # 常量定义
│   ├── domain      # 公共领域对象
│   ├── exception   # 异常处理
│   └── utils       # 工具类
│
├── gateway         # 网关服务
│   ├── config      # 配置类
│   ├── filter      # 过滤器
│   ├── handler     # 处理器
│   └── properties  # 配置属性
│
├── security        # 安全模块
│   ├── config      # 安全配置
│   ├── domain      # 安全领域对象
│   ├── filter      # 安全过滤器
│   ├── handler     # 安全处理器
│   ├── interceptor # 拦截器
│   ├── properties  # 配置属性
│   ├── service     # 安全服务
│   └── utils       # 安全工具类
│
├── system          # 系统服务
│   ├── auth        # 认证模块
│   │   ├── controller  # 认证控制器
│   │   ├── entity      # 认证实体
│   │   ├── mapper      # 数据访问层
│   │   ├── service     # 业务逻辑层
│   │   └── vo          # 视图对象
│   │
│   ├── user        # 用户管理模块
│   │   ├── controller  # 用户控制器
│   │   ├── converter   # 对象转换器
│   │   ├── dto         # 数据传输对象
│   │   ├── entity      # 用户实体
│   │   ├── mapper      # 数据访问层
│   │   ├── service     # 业务逻辑层
│   │   └── vo          # 视图对象
│   │
│   ├── role        # 角色管理模块
│   │   ├── converter   # 对象转换器
│   │   ├── dto         # 数据传输对象
│   │   ├── entity      # 角色实体
│   │   ├── mapper      # 数据访问层
│   │   └── vo          # 视图对象
│   │
│   └── config      # 系统配置
│
└── docs            # 项目文档
    └── sql         # 数据库脚本
```

## 模块说明

### common (公共模块)
提供项目中所有模块共享的常量、工具类、异常定义等基础功能。

### gateway (网关服务)
- 统一网关入口，负责请求路由和转发
- JWT 令牌验证
- 接口访问控制
- 防止直接访问后端服务

### security (安全模块)
- JWT 认证与授权
- Token 刷新机制
- 安全拦截器和过滤器
- 统一的安全配置

### system (系统服务)
- **auth**: 用户登录、注册、登出功能
- **user**: 用户信息管理
- **role**: 角色权限管理

## 核心特性

1. **微服务架构**: 采用 SpringCloud 微服务架构，模块化设计，易于扩展
2. **服务注册与发现**: 使用 Nacos 实现服务的注册与发现
3. **统一网关**: 所有请求统一通过网关访问，提供安全验证和路由转发
4. **JWT 认证**: 基于 JWT 的无状态认证，支持 Token 刷新机制
5. **前后端分离**: 纯 API 接口，无服务端跳转，适合前后端分离开发
6. **分层设计**: 严格的分层架构 (Entity/DTO/VO/Mapper/Service/Controller)
7. **API 文档**: 集成 Knife4j，自动生成 API 文档

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- PostgreSQL 数据库
- Nacos 服务注册中心

### 构建项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd cloud

# 编译打包
mvn clean package
```

### 运行服务

1. 启动 Nacos 服务注册中心
2. 配置数据库连接信息
3. 按顺序启动各个微服务模块

## 配置说明

项目支持多环境配置：
- **dev**: 开发环境 (默认)
- **prod**: 生产环境

可通过 Maven Profile 切换环境：
```bash
mvn clean package -P prod
```

## 开发规范

1. 所有实体类使用 Lombok 简化代码
2. 使用 MapStruct 进行对象转换
3. 统一的异常处理机制
4. RESTful API 设计规范
5. 代码注释完整，符合 JavaDoc 标准

## 联系方式

- 作者: motoryang
- GitHub: https://github.com/motoryang