#!/bin/bash

###############################################################################
# 微服务管理脚本
# 用途：便捷管理 Spring Cloud 微服务的启动、停止、重启、状态查看等操作
# 作者：Claude AI
# 日期：2024-12-25
###############################################################################

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置项
SERVICES=("gateway" "system" "blog")
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOGS_DIR="${BASE_DIR}/logs"
PIDS_DIR="${BASE_DIR}/pids"

# 创建必要的目录
mkdir -p "${PIDS_DIR}"
mkdir -p "${LOGS_DIR}"

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查服务是否存在
check_service_exists() {
    local service=$1
    local jar_file="${BASE_DIR}/${service}/target/${service}-1.0-SNAPSHOT.jar"

    if [ ! -f "$jar_file" ]; then
        print_error "服务 ${service} 的 JAR 文件不存在: ${jar_file}"
        print_info "请先执行 'mvn clean package' 构建项目"
        return 1
    fi
    return 0
}

# 获取服务 PID
get_pid() {
    local service=$1
    local pid_file="${PIDS_DIR}/${service}.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            echo "$pid"
            return 0
        else
            rm -f "$pid_file"
        fi
    fi

    # 通过 jar 文件名查找进程
    local jar_name="${service}-1.0-SNAPSHOT.jar"
    local pid=$(ps aux | grep "$jar_name" | grep -v grep | awk '{print $2}')
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi

    return 1
}

# 检查服务状态
check_status() {
    local service=$1
    local pid=$(get_pid "$service")

    if [ -n "$pid" ]; then
        return 0
    else
        return 1
    fi
}

# 启动单个服务
start_service() {
    local service=$1

    if ! check_service_exists "$service"; then
        return 1
    fi

    if check_status "$service"; then
        local pid=$(get_pid "$service")
        print_warning "服务 ${service} 已经在运行 (PID: ${pid})"
        return 0
    fi

    print_info "正在启动服务: ${service}..."

    local jar_file="${BASE_DIR}/${service}/target/${service}-1.0-SNAPSHOT.jar"
    local log_file="${LOGS_DIR}/${service}/current/${service}.log"
    local pid_file="${PIDS_DIR}/${service}.pid"

    # 确保日志目录存在
    mkdir -p "$(dirname "$log_file")"

    # 启动服务
    nohup java -jar "$jar_file" \
        --spring.profiles.active=dev \
        > /dev/null 2>&1 &

    local pid=$!
    echo $pid > "$pid_file"

    # 等待服务启动
    sleep 3

    if check_status "$service"; then
        print_success "服务 ${service} 启动成功 (PID: ${pid})"
        print_info "日志文件: ${log_file}"
        return 0
    else
        print_error "服务 ${service} 启动失败"
        rm -f "$pid_file"
        return 1
    fi
}

# 停止单个服务
stop_service() {
    local service=$1

    if ! check_status "$service"; then
        print_warning "服务 ${service} 未运行"
        return 0
    fi

    local pid=$(get_pid "$service")
    print_info "正在停止服务: ${service} (PID: ${pid})..."

    # 优雅停止
    kill "$pid" 2>/dev/null

    # 等待最多30秒
    local count=0
    while [ $count -lt 30 ]; do
        if ! ps -p "$pid" > /dev/null 2>&1; then
            break
        fi
        sleep 1
        count=$((count + 1))
    done

    # 如果还在运行，强制停止
    if ps -p "$pid" > /dev/null 2>&1; then
        print_warning "服务未响应，强制停止..."
        kill -9 "$pid" 2>/dev/null
        sleep 1
    fi

    # 清理 PID 文件
    rm -f "${PIDS_DIR}/${service}.pid"

    if ! check_status "$service"; then
        print_success "服务 ${service} 已停止"
        return 0
    else
        print_error "服务 ${service} 停止失败"
        return 1
    fi
}

# 重启单个服务
restart_service() {
    local service=$1
    print_info "正在重启服务: ${service}..."
    stop_service "$service"
    sleep 2
    start_service "$service"
}

# 查看服务状态
status_service() {
    local service=$1

    if check_status "$service"; then
        local pid=$(get_pid "$service")
        local uptime=$(ps -p "$pid" -o etime= | tr -d ' ')
        local mem=$(ps -p "$pid" -o rss= | awk '{printf "%.2f MB", $1/1024}')
        local cpu=$(ps -p "$pid" -o %cpu= | tr -d ' ')

        echo -e "${GREEN}●${NC} ${service}"
        echo "  PID: ${pid}"
        echo "  运行时间: ${uptime}"
        echo "  内存使用: ${mem}"
        echo "  CPU使用率: ${cpu}%"
    else
        echo -e "${RED}●${NC} ${service}"
        echo "  状态: 未运行"
    fi
}

# 查看服务日志
logs_service() {
    local service=$1
    local lines=${2:-50}

    local log_file="${LOGS_DIR}/${service}/current/${service}.log"

    if [ ! -f "$log_file" ]; then
        print_error "日志文件不存在: ${log_file}"
        return 1
    fi

    print_info "显示 ${service} 最后 ${lines} 行日志:"
    echo "----------------------------------------"
    tail -n "$lines" "$log_file"
}

# 实时查看服务日志
tail_logs() {
    local service=$1
    local log_file="${LOGS_DIR}/${service}/current/${service}.log"

    if [ ! -f "$log_file" ]; then
        print_error "日志文件不存在: ${log_file}"
        return 1
    fi

    print_info "实时监控 ${service} 日志 (Ctrl+C 退出):"
    tail -f "$log_file"
}

# 启动所有服务
start_all() {
    print_info "启动所有服务..."
    for service in "${SERVICES[@]}"; do
        start_service "$service"
        echo ""
    done
}

# 停止所有服务
stop_all() {
    print_info "停止所有服务..."
    # 反向停止，先停止依赖少的服务
    for ((i=${#SERVICES[@]}-1; i>=0; i--)); do
        stop_service "${SERVICES[$i]}"
        echo ""
    done
}

# 重启所有服务
restart_all() {
    print_info "重启所有服务..."
    stop_all
    sleep 3
    start_all
}

# 查看所有服务状态
status_all() {
    echo "=========================================="
    echo "          微服务运行状态"
    echo "=========================================="
    for service in "${SERVICES[@]}"; do
        status_service "$service"
        echo ""
    done
}

# 构建项目
build_project() {
    print_info "开始构建项目..."
    cd "$BASE_DIR"

    if [ -f "pom.xml" ]; then
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            print_success "项目构建成功"
        else
            print_error "项目构建失败"
            return 1
        fi
    else
        print_error "未找到 pom.xml 文件"
        return 1
    fi
}

# 清理日志
clean_logs() {
    local days=${1:-7}
    print_info "清理 ${days} 天前的归档日志..."

    for service in "${SERVICES[@]}"; do
        local archive_dir="${LOGS_DIR}/${service}/archive"
        if [ -d "$archive_dir" ]; then
            local count=$(find "$archive_dir" -name "*.log" -mtime +${days} | wc -l)
            if [ $count -gt 0 ]; then
                find "$archive_dir" -name "*.log" -mtime +${days} -delete
                print_success "已清理 ${service} 的 ${count} 个日志文件"
            else
                print_info "${service} 没有需要清理的日志"
            fi
        fi
    done
}

# 显示帮助信息
show_help() {
    cat << EOF
微服务管理脚本使用说明

用法: $0 <命令> [服务名] [选项]

命令:
  start <service>      启动指定服务
  stop <service>       停止指定服务
  restart <service>    重启指定服务
  status <service>     查看指定服务状态
  logs <service> [n]   查看指定服务最后n行日志 (默认50行)
  tail <service>       实时查看指定服务日志

  start-all            启动所有服务
  stop-all             停止所有服务
  restart-all          重启所有服务
  status-all           查看所有服务状态

  build                构建项目
  clean-logs [days]    清理指定天数前的日志 (默认7天)

  help                 显示此帮助信息

可用服务:
$(for s in "${SERVICES[@]}"; do echo "  - $s"; done)

示例:
  $0 start gateway              # 启动网关服务
  $0 stop system                # 停止系统服务
  $0 restart blog               # 重启博客服务
  $0 status gateway             # 查看网关状态
  $0 logs gateway 100           # 查看网关最后100行日志
  $0 tail gateway               # 实时查看网关日志
  $0 start-all                  # 启动所有服务
  $0 status-all                 # 查看所有服务状态
  $0 build                      # 构建项目
  $0 clean-logs 7               # 清理7天前的日志

EOF
}

# 主函数
main() {
    local command=$1
    local service=$2
    local option=$3

    case "$command" in
        start)
            if [ -z "$service" ]; then
                print_error "请指定要启动的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            start_service "$service"
            ;;
        stop)
            if [ -z "$service" ]; then
                print_error "请指定要停止的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            stop_service "$service"
            ;;
        restart)
            if [ -z "$service" ]; then
                print_error "请指定要重启的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            restart_service "$service"
            ;;
        status)
            if [ -z "$service" ]; then
                print_error "请指定要查看的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            status_service "$service"
            ;;
        logs)
            if [ -z "$service" ]; then
                print_error "请指定要查看日志的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            logs_service "$service" "${option:-50}"
            ;;
        tail)
            if [ -z "$service" ]; then
                print_error "请指定要监控日志的服务"
                echo "可用服务: ${SERVICES[*]}"
                exit 1
            fi
            tail_logs "$service"
            ;;
        start-all)
            start_all
            ;;
        stop-all)
            stop_all
            ;;
        restart-all)
            restart_all
            ;;
        status-all)
            status_all
            ;;
        build)
            build_project
            ;;
        clean-logs)
            clean_logs "${service:-7}"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "未知命令: $command"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
if [ $# -eq 0 ]; then
    show_help
    exit 0
fi

main "$@"