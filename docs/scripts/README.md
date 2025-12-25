# 微服务管理脚本使用手册

## 简介

`microservice.sh` 是一个用于便捷管理 Spring Cloud 微服务的 Shell 脚本，支持服务的启动、停止、重启、状态查看、日志管理等操作。

## 安装

1. 将 `microservice.sh` 放置到项目根目录（与 pom.xml 同级）

2. 添加执行权限：
```bash
chmod +x microservice.sh
```

3. （可选）创建软链接，方便全局使用：
```bash
sudo ln -s $(pwd)/microservice.sh /usr/local/bin/ms
# 之后可以直接使用 ms 命令
```

## 基本用法

```bash
./microservice.sh <命令> [服务名] [选项]
```

## 命令列表

### 单个服务操作

#### 启动服务
```bash
./microservice.sh start <服务名>

# 示例
./microservice.sh start gateway
./microservice.sh start system
./microservice.sh start blog
```

#### 停止服务
```bash
./microservice.sh stop <服务名>

# 示例
./microservice.sh stop gateway
```

#### 重启服务
```bash
./microservice.sh restart <服务名>

# 示例
./microservice.sh restart system
```

#### 查看服务状态
```bash
./microservice.sh status <服务名>

# 示例
./microservice.sh status gateway

# 输出示例：
# ● gateway
#   PID: 12345
#   运行时间: 01:23:45
#   内存使用: 512.34 MB
#   CPU使用率: 2.5%
```

### 批量操作

#### 启动所有服务
```bash
./microservice.sh start-all
```

#### 停止所有服务
```bash
./microservice.sh stop-all
```

#### 重启所有服务
```bash
./microservice.sh restart-all
```

#### 查看所有服务状态
```bash
./microservice.sh status-all

# 输出示例：
# ==========================================
#           微服务运行状态
# ==========================================
# ● gateway
#   PID: 12345
#   运行时间: 01:23:45
#   内存使用: 512.34 MB
#   CPU使用率: 2.5%
#
# ● system
#   PID: 12346
#   ...
```

### 日志管理

#### 查看最近的日志
```bash
./microservice.sh logs <服务名> [行数]

# 示例
./microservice.sh logs gateway        # 查看最后50行（默认）
./microservice.sh logs gateway 100    # 查看最后100行
./microservice.sh logs system 200     # 查看最后200行
```

#### 实时查看日志
```bash
./microservice.sh tail <服务名>

# 示例
./microservice.sh tail gateway
# 按 Ctrl+C 退出监控
```

### 项目管理

#### 构建项目
```bash
./microservice.sh build

# 等价于执行：mvn clean package -DskipTests
```

#### 清理归档日志
```bash
./microservice.sh clean-logs [天数]

# 示例
./microservice.sh clean-logs        # 清理7天前的日志（默认）
./microservice.sh clean-logs 30     # 清理30天前的日志
```

### 帮助信息

```bash
./microservice.sh help
# 或
./microservice.sh --help
# 或
./microservice.sh -h
```

## 使用场景

### 场景1：首次部署

```bash
# 1. 构建项目
./microservice.sh build

# 2. 启动所有服务
./microservice.sh start-all

# 3. 查看服务状态
./microservice.sh status-all
```

### 场景2：更新某个服务

```bash
# 1. 重新构建
./microservice.sh build

# 2. 重启特定服务
./microservice.sh restart gateway

# 3. 查看日志确认启动成功
./microservice.sh logs gateway 50
```

### 场景3：排查问题

```bash
# 1. 查看服务状态
./microservice.sh status gateway

# 2. 查看最近日志
./microservice.sh logs gateway 200

# 3. 实时监控日志
./microservice.sh tail gateway
```

### 场景4：日常维护

```bash
# 1. 查看所有服务状态
./microservice.sh status-all

# 2. 清理过期日志
./microservice.sh clean-logs 7

# 3. 重启某个服务（如内存占用过高）
./microservice.sh restart system
```

### 场景5：服务器重启后恢复

```bash
# 重启所有服务
./microservice.sh start-all
```

## 目录结构

脚本会自动创建和管理以下目录：

```
项目根目录/
├── microservice.sh           # 管理脚本
├── pids/                     # PID 文件目录
│   ├── gateway.pid
│   ├── system.pid
│   └── blog.pid
├── logs/                     # 日志目录（由logback管理）
│   ├── gateway/
│   │   ├── current/
│   │   └── archive/
│   ├── system/
│   └── blog/
└── [服务模块]/
    └── target/
        └── [服务名]-1.0-SNAPSHOT.jar
```

## 功能特性

### 1. 智能进程管理
- 自动记录和管理 PID
- 优雅停止服务（30秒超时后强制停止）
- 自动检测服务运行状态
- 防止重复启动

### 2. 详细的状态信息
- PID
- 运行时间
- 内存占用
- CPU 使用率

### 3. 灵活的日志查看
- 查看指定行数的日志
- 实时监控日志输出
- 日志自动归档管理

### 4. 批量操作
- 一键启动/停止/重启所有服务
- 统一查看所有服务状态

### 5. 友好的用户界面
- 彩色输出（成功/警告/错误）
- 详细的提示信息
- 进度反馈

## 配置说明

### 修改服务列表

编辑脚本中的 `SERVICES` 数组：

```bash
SERVICES=("gateway" "system" "blog" "order" "product")
```

### 修改 JVM 参数

找到脚本中的启动命令部分，添加 JVM 参数：

```bash
nohup java -jar "$jar_file" \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    --spring.profiles.active=dev \
    > /dev/null 2>&1 &
```

### 指定配置文件

修改启动命令中的 profile：

```bash
--spring.profiles.active=prod  # 使用生产环境配置
```

## 常见问题

### Q1: 服务启动失败？

**A:** 检查以下几点：
1. JAR 包是否存在：`ls -l */target/*.jar`
2. 查看日志：`./microservice.sh logs <服务名> 100`
3. 检查端口是否被占用：`netstat -tulpn | grep <端口>`
4. 确认依赖服务（如 Nacos）是否正常运行

### Q2: 无法停止服务？

**A:**
```bash
# 查看进程
ps aux | grep java

# 手动强制停止
kill -9 <PID>

# 清理 PID 文件
rm -f pids/<服务名>.pid
```

### Q3: 日志文件不存在？

**A:**
- 确保服务至少启动过一次
- 检查 logback-spring.xml 配置是否正确
- 查看应用是否有写入日志目录的权限

### Q4: 内存不足？

**A:** 修改脚本中的 JVM 参数，减少堆内存：
```bash
-Xms256m -Xmx512m
```

## 最佳实践

### 1. 服务启动顺序

建议按以下顺序启动：
```bash
./microservice.sh start gateway    # 先启动网关
./microservice.sh start system     # 再启动核心服务
./microservice.sh start blog       # 最后启动业务服务
```

或者直接使用：
```bash
./microservice.sh start-all  # 脚本会按定义顺序启动
```

### 2. 定期维护

创建定时任务清理日志：
```bash
# 编辑 crontab
crontab -e

# 添加任务：每周日凌晨2点清理30天前的日志
0 2 * * 0 /path/to/microservice.sh clean-logs 30
```

### 3. 监控脚本

创建监控脚本，定期检查服务状态：
```bash
#!/bin/bash
# monitor.sh
while true; do
    /path/to/microservice.sh status-all
    sleep 300  # 每5分钟检查一次
done
```

### 4. 开机自启动

创建 systemd 服务：
```bash
# /etc/systemd/system/microservices.service
[Unit]
Description=Spring Cloud Microservices
After=network.target

[Service]
Type=forking
User=youruser
WorkingDirectory=/path/to/project
ExecStart=/path/to/microservice.sh start-all
ExecStop=/path/to/microservice.sh stop-all
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

启用服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable microservices
sudo systemctl start microservices
```

## 安全建议

1. **权限控制**：确保脚本只能被授权用户执行
```bash
chmod 750 microservice.sh
chown youruser:yourgroup microservice.sh
```

2. **生产环境**：不要在生产环境中使用 `-DskipTests` 构建

3. **日志敏感信息**：确保日志中不包含密码等敏感信息

4. **防火墙**：合理配置防火墙规则，只开放必要端口

## 扩展功能

### 添加健康检查

可以在脚本中添加健康检查功能：

```bash
health_check() {
    local service=$1
    local port=$2
    
    curl -s http://localhost:${port}/actuator/health | grep -q "UP"
    if [ $? -eq 0 ]; then
        print_success "${service} 健康检查通过"
    else
        print_error "${service} 健康检查失败"
    fi
}
```

### 集成监控告警

配合监控工具（如 Prometheus）使用，在服务异常时发送告警。

## 总结

这个脚本提供了完整的微服务管理功能，适合开发和测试环境使用。在生产环境中，建议配合容器编排工具（如 Kubernetes）或服务管理工具（如 Supervisor）使用。