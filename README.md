# KnowChain 启动指南

## 一、环境准备

### 1 数据库

- 下载并安装 MySQL 数据库。
- 确保 MySQL 服务正在运行。

### 2 Java

#### 2.1 安装 Java

- 下载并安装 Java 17 或更高版本。
- 配置 JAVA_HOME 环境变量，指向 Java 安装目录。

### 3 项目依赖

#### 3.1 安装 Maven

- 下载并安装 Apache Maven。
- 配置 MAVEN_HOME 环境变量，指向 Maven 安装目录。

## 二、项目启动

### 1. 数据库初始化

```bash
# 执行数据库初始化脚本
init-database.bat
```

### 2. 启动后端

```bash
# 启动后端服务
start-backend.bat
```

### 3. 启动前端

```bash
# 启动前端服务
start-frontend.bat
```

### 4.macos可使用`.sh`脚本

### 数据库配置

**配置文件：** `backend\KnowChain\src\main\resources\application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/knowchain
    username: root
    password: 1234
```

### 测试账户

| 用户名 | 密码 | 邮箱 |
|--------|------|------|
| admin123 | admin123 | admin123@knowchain.com |
| test123 | test123 | test123@knowchain.com |

**注意：** 密码长度要求不少于6位字符。

### 文件存储结构

KnowChain 使用以下目录结构来存储用户文件（位于项目根目录）：

```
KnowChain/                    # 项目根目录
├── docs/                     # 文档存储根目录
│   └── users/
│       ├── 1/                # 用户ID为1的文件
│       │   ├── 文档1.txt
│       │   └── 项目文件夹/
│       │       └── 项目文件.md
│       ├── 2/                # 用户ID为2的文件
│       │   └── 用户2文档.txt
│       └── ...
├── backend/                  # 后端代码
├── frontend/                 # 前端代码
└── ...
```

**路径规则：**

- 用户文件根目录：`{项目根目录}/docs/users/{userId}/`
- 文件夹不存储URL路径，仅作为组织结构
- 文件存储相对路径：`docs/users/{userId}/[父文件夹路径/]文件名`

**注意事项：**

- 文件创建时会自动创建所需的目录结构
- 支持多级文件夹嵌套
- 文件路径使用相对路径存储，便于部署迁移
- 所有用户文件统一存储在项目根目录的docs文件夹中

### 端口信息

- 后端服务：http://localhost:8080
- 前端服务：http://localhost:5173 (默认Vite端口)

### 故障排除

#### 1. 数据库连接失败

- 确保MySQL服务器正在运行
- 检查数据库用户名和密码
- 确认数据库 `knowchain` 已创建

#### 2. 端口占用

- 检查8080端口是否被占用
- 可以修改application.yml中的server.port

#### 3. Maven/Java路径问题

- 确认JDK和Maven路径配置正确
- 检查环境变量设置

#### 4. 依赖下载失败

- 检查网络连接
- 清理Maven缓存：`mvn clean`
