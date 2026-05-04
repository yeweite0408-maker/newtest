# GameMall 游戏商城

一款基于 **Vue 3 + Spring Boot 3** 的前后端分离精品游戏购物网站，展示 20 款 Steam 热门游戏。

**项目演示账号：** 管理员 `admin / admin` | 普通用户 `test / 123456`

---

## 目录

1. [项目简介](#项目简介)
2. [功能概览](#功能概览)
3. [技术栈](#技术栈)
4. [环境准备 —— 必读](#环境准备--必读)
5. [快速启动](#快速启动)
6. [项目结构](#项目结构)
7. [API 接口文档](#api-接口文档)
8. [H2 数据库控制台](#h2-数据库控制台)
9. [商品列表](#商品列表)

---

## 项目简介

GameMall 是一个前后端分离的游戏购物商城，适用于期末项目展示。系统使用 H2 内嵌数据库，无需额外安装数据库软件，真正的开箱即用。

**核心功能流程：** 注册/登录 → 浏览游戏 → 加入购物车 → 管理员后台管理

---

## 功能概览

### 用户端
| 功能 | 说明 |
|------|------|
| 用户注册/登录 | 新用户注册后自动登录，JWT 身份认证 |
| 游戏商城首页 | 展示 20 款 Steam 热门游戏，含封面图、价格、开发商 |
| 商品搜索 | 按游戏名称关键字搜索 |
| 分类筛选 | 按游戏类型（FPS、MOBA、开放世界等）筛选 |
| 商品详情 | 查看游戏介绍、价格、库存、开发商 |
| 加入购物车 | 选择数量后加入购物车 |
| 购物车管理 | 修改数量、删除、清空、价格合计 |

### 管理员端
| 功能 | 说明 |
|------|------|
| 数据概览 | 统计用户数、商品数、商品总价值 |
| 用户管理 | 查看所有注册用户及其角色 |
| 商品管理 | 编辑/删除已有商品 |
| 添加商品 | 填写名称、分类、价格、描述等 |
| 图片上传 | 上传商品图片至服务器 |

---

## 技术栈

### 后端
- **Spring Boot 3.2** — 应用框架
- **Spring Security** — BCrypt 密码加密
- **MyBatis** — 数据库 ORM
- **H2 Database** — 内嵌数据库（MySQL 兼容模式，零配置）
- **JWT (jjwt 0.12)** — 无状态身份认证

### 前端
- **Vue 3** — 前端框架（Composition API）
- **Vue Router 4** — 路由管理
- **Pinia** — 状态管理
- **Element Plus** — UI 组件库
- **Axios** — HTTP 请求
- **Vite 5** — 构建工具

---

## 环境准备（必读）

### 需要安装的软件

#### 1. JDK 17+（必须）

后端运行需要 Java 环境。

**下载地址：** https://www.oracle.com/java/technologies/downloads/

选择适合你系统的版本：
- Windows → 下载 `x64 Installer`（约 170MB）
- 下载后双击安装，一路点"下一步"
- 安装完成后**重启电脑**

**验证安装：** 打开命令提示符（Win+R → 输入 `cmd` → 回车），输入：
```bash
java -version
```
看到版本号为 17 或更高即表示成功。

#### 2. Node.js（已安装 ✓）

你的电脑已安装 Node.js v22.14.0，无需重复安装。

#### 3. Maven（无需安装）

项目已内置 Maven Wrapper（`mvnw.cmd`），会自动下载所需版本，无需手动安装 Maven。

---

## 快速启动

### 方式一：一键启动（推荐）

确保已安装 JDK 17+ 后：

1. **双击** `start.bat`
2. 脚本会自动：
   - 启动后端（首次会下载 Maven 和依赖，约 1-3 分钟）
   - 启动前端
   - 打开浏览器 http://localhost:3000

### 方式二：分别启动

打开两个终端窗口：

**终端 1 —— 启动后端：**
```bash
cd backend
mvnw.cmd spring-boot:run
```
等待出现 `Started MallApplication` 即启动成功。

**终端 2 —— 启动前端：**
```bash
cd frontend
npm install
npm run dev
```
看到 `http://localhost:3000` 即启动成功。

### 首次启动说明

- 后端第一次启动较慢（Maven 下载依赖 + 编译），之后会快很多
- 数据库和 20 款游戏数据会在启动时自动创建，无需任何手动操作
- 看到日志中出现 `20 Steam games seeded.` 表示数据初始化完成

---

## 项目结构

```
shopping-mall/
│
├── start.bat                    # 一键启动脚本（Windows）
├── start.sh                     # 一键启动脚本（Mac/Linux）
├── README.md                    # 项目文档
│
├── backend/                     # Spring Boot 后端
│   ├── pom.xml                  # Maven 依赖配置
│   ├── mvnw.cmd                 # Maven Wrapper（无需装 Maven）
│   ├── .mvn/wrapper/            # Maven Wrapper 文件
│   └── src/main/
│       ├── java/com/mall/
│       │   ├── MallApplication.java       # 启动入口
│       │   ├── config/
│       │   │   ├── SecurityConfig.java    # Spring Security + CORS
│       │   │   ├── WebConfig.java         # 拦截器配置
│       │   │   └── DataInitializer.java   # 数据初始化（自动建账号+游戏）
│       │   ├── controller/
│       │   │   ├── AuthController.java    # 登录/注册
│       │   │   ├── ProductController.java # 商品查询
│       │   │   ├── CartController.java    # 购物车 CRUD
│       │   │   └── AdminController.java   # 管理后台接口
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Product.java
│       │   │   └── CartItem.java
│       │   ├── mapper/
│       │   │   ├── UserMapper.java
│       │   │   ├── ProductMapper.java
│       │   │   └── CartMapper.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   ├── ProductService.java
│       │   │   └── CartService.java
│       │   ├── util/JwtUtil.java          # JWT 令牌工具
│       │   └── dto/
│       │       ├── LoginRequest.java
│       │       ├── RegisterRequest.java
│       │       └── Result.java            # 统一响应格式
│       └── resources/
│           ├── application.yml            # 应用配置
│           ├── schema.sql                 # 数据库表结构
│           └── data.sql                   # 数据说明
│
├── frontend/                    # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js           # 代理配置
│   ├── index.html
│   └── src/
│       ├── main.js              # 入口
│       ├── App.vue
│       ├── api/index.js         # Axios 封装 + 所有 API 接口
│       ├── router/index.js      # 路由 + 导航守卫
│       ├── stores/user.js       # Pinia 用户状态
│       ├── assets/style.css     # 全局样式
│       ├── components/Navbar.vue
│       └── views/
│           ├── Login.vue
│           ├── Register.vue
│           ├── Home.vue
│           ├── ProductDetail.vue
│           ├── Cart.vue
│           └── Admin.vue
│
└── uploads/                     # 上传的图片（自动创建）
```

---

## API 接口文档

### 公开接口（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/register` | 用户注册 |
| GET | `/api/products` | 商品列表（支持 `?keyword=` 搜索） |
| GET | `/api/products/{id}` | 商品详情 |

**登录请求示例：**
```json
POST /api/auth/login
{
    "username": "admin",
    "password": "admin"
}
```

**登录响应示例：**
```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "user": {
            "id": 1,
            "username": "admin",
            "role": "admin"
        }
    }
}
```

### 需要登录（请求头带 Token）

在请求头中添加：`Authorization: Bearer <token>`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/cart` | 购物车列表 |
| POST | `/api/cart` | 添加到购物车 |
| PUT | `/api/cart/{id}` | 修改数量 |
| DELETE | `/api/cart/{id}` | 删除购物车项 |
| DELETE | `/api/cart` | 清空购物车 |

### 管理员接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/users` | 用户列表 |
| GET | `/api/admin/products` | 商品管理列表 |
| POST | `/api/admin/products` | 添加商品 |
| PUT | `/api/admin/products/{id}` | 编辑商品 |
| DELETE | `/api/admin/products/{id}` | 删除商品 |
| POST | `/api/admin/upload` | 上传图片 |

---

## H2 数据库控制台

启动后端后，访问 http://localhost:8080/h2-console

连接配置：
- **JDBC URL:** `jdbc:h2:file:./data/game_mall`
- **用户名:** `sa`
- **密码:** （留空）

---

## 商品列表

项目内置 20 款 Steam 游戏数据：

| # | 游戏名称 | 价格 | 类型 | 开发商 |
|---|---------|------|------|--------|
| 1 | Dota 2 | 免费 | MOBA | Valve |
| 2 | Counter-Strike 2 | 免费 | FPS | Valve |
| 3 | PUBG: BATTLEGROUNDS | ¥98 | 大逃杀 | KRAFTON |
| 4 | Apex Legends | 免费 | 大逃杀 | Respawn |
| 5 | Rust | ¥116 | 生存 | Facepunch |
| 6 | Grand Theft Auto V | ¥118 | 开放世界 | Rockstar |
| 7 | ELDEN RING | ¥298 | 动作RPG | FromSoftware |
| 8 | Cyberpunk 2077 | ¥299 | 开放世界RPG | CDPR |
| 9 | Red Dead Redemption 2 | ¥249 | 开放世界 | Rockstar |
| 10 | Baldur's Gate 3 | ¥298 | CRPG | Larian |
| 11 | Hogwarts Legacy | ¥298 | 开放世界RPG | Avalanche |
| 12 | Call of Duty: MW III | ¥469 | FPS | Sledgehammer |
| 13 | EA SPORTS FC 24 | ¥249 | 体育 | EA Canada |
| 14 | Rainbow Six Siege | ¥88 | 战术FPS | Ubisoft |
| 15 | The Witcher 3 | ¥127 | 动作RPG | CDPR |
| 16 | Civilization VI | ¥199 | 策略 | Firaxis |
| 17 | Stardew Valley | ¥48 | 模拟经营 | ConcernedApe |
| 18 | Left 4 Dead 2 | ¥42 | FPS | Valve |
| 19 | Team Fortress 2 | 免费 | FPS | Valve |
| 20 | Palworld | ¥108 | 开放世界生存 | Pocketpair |

---

## 常见问题

### Q: 双击 start.bat 闪退怎么办？
A: 大概率是 Java 没安装。请先安装 JDK 17+，然后重启电脑，再双击 `start.bat`。

### Q: "前端页面能打开但没数据"？
A: 后端还没启动好。等待后端窗口出现 `Started MallApplication` 字样后刷新前端页面。

### Q: 如何重置数据？
A: 删除 `backend/data/` 文件夹，重启后端即可重新初始化。

### Q: 后端端口被占用？
A: 修改 `backend/src/main/resources/application.yml` 中的 `server.port` 值。
