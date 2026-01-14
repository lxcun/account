# 个人记账单系统 - 项目上下文

## 项目概述

这是一个基于 Spring Boot 3.5.6 + React + TypeScript + Ant Design 的全栈记账单应用，用于个人财务管理。系统支持记录收入和支出，管理资产和负债，并提供完整的操作日志审计功能。

### 核心技术栈

**后端**
- Spring Boot 3.5.6
- MyBatis 3.0.3
- MySQL 8.0+
- JDK 21
- Spring Security + JWT 认证
- Maven

**前端**
- React 18
- TypeScript 5
- Ant Design 5
- Vite 6
- React Router 7
- Axios
- Day.js

### 主要功能

- 交易记录管理（收入/支出）
- 资产管理（现金、银行、支付宝、微信、股票、基金等）
- 负债管理（信用卡、房贷、车贷、个人借款等）
- 交易自动关联资产负债（交易时自动更新账户余额）
- 按时间范围查询统计
- 收支汇总展示
- 操作日志审计（仅领导角色可查看）

## 项目结构

```
.
├── src/main/
│   ├── java/com/accounting/
│   │   ├── AccountingApplication.java    # Spring Boot 启动类
│   │   ├── annotation/                   # 自定义注解
│   │   │   ├── OperationLog.java         # 操作日志注解
│   │   │   ├── OperationModule.java      # 模块枚举
│   │   │   └── OperationType.java        # 操作类型枚举
│   │   ├── aspect/                       # AOP 切面
│   │   │   └── OperationLogAspect.java   # 操作日志切面
│   │   ├── common/                       # 公共类
│   │   │   └── Result.java               # 统一返回格式
│   │   ├── config/                       # 配置类
│   │   │   ├── SecurityConfig.java       # Spring Security 配置
│   │   │   └── WebConfig.java            # Web 配置
│   │   ├── controller/                   # 控制器层
│   │   │   ├── AssetController.java
│   │   │   ├── AuthController.java
│   │   │   ├── LiabilityController.java
│   │   │   ├── OperationLogController.java
│   │   │   └── TransactionController.java
│   │   ├── dto/                          # 数据传输对象
│   │   │   ├── AssetDTO.java
│   │   │   ├── LiabilityDTO.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── OperationLogQueryDTO.java
│   │   │   ├── SummaryDTO.java
│   │   │   ├── TransactionDTO.java
│   │   │   └── UserDTO.java
│   │   ├── entity/                       # 实体类
│   │   │   ├── Asset.java
│   │   │   ├── Liability.java
│   │   │   ├── OperationLog.java
│   │   │   ├── Role.java
│   │   │   ├── TransactionRecord.java
│   │   │   ├── User.java
│   │   │   └── UserRole.java
│   │   ├── mapper/                       # MyBatis Mapper 接口
│   │   │   ├── AssetMapper.java
│   │   │   ├── LiabilityMapper.java
│   │   │   ├── OperationLogMapper.java
│   │   │   ├── RoleMapper.java
│   │   │   ├── TransactionMapper.java
│   │   │   └── UserMapper.java
│   │   ├── security/                     # 安全相关
│   │   │   ├── CustomUserDetails.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── service/                      # 服务层
│   │   │   ├── AssetService.java
│   │   │   ├── AuthService.java
│   │   │   ├── LiabilityService.java
│   │   │   ├── OperationLogService.java
│   │   │   ├── TransactionService.java
│   │   │   └── UserService.java
│   │   └── util/                         # 工具类
│   │       ├── JwtUtil.java
│   │       └── PasswordEncoderUtil.java
│   └── resources/
│       ├── application.yml               # 应用配置
│       ├── schema.sql                    # 数据库初始化脚本
│       ├── mapper/                       # MyBatis XML 映射文件
│       │   ├── AssetMapper.xml
│       │   ├── LiabilityMapper.xml
│       │   └── TransactionMapper.xml
│       └── migration/                    # 数据库迁移脚本
│           ├── V1.0__add_user_auth_tables.sql
│           ├── V1.1__insert_test_users.sql
│           ├── V1.2__fix_password.sql
│           └── V1.3__add_operation_log_table.sql
├── frontend/
│   ├── src/
│   │   ├── api/                          # API 接口定义
│   │   │   └── index.ts                  # Axios 配置和 API 方法
│   │   ├── components/                   # React 组件
│   │   │   └── Layout.tsx                # 布局组件
│   │   ├── pages/                        # 页面组件
│   │   │   ├── AssetManagement.tsx       # 资产管理
│   │   │   ├── LiabilityManagement.tsx   # 负债管理
│   │   │   ├── Login.tsx                 # 登录页
│   │   │   ├── OperationLogList.tsx      # 操作日志列表
│   │   │   ├── RecordList.tsx            # 交易记录
│   │   │   └── Statistics.tsx            # 统计页
│   │   ├── types/                        # TypeScript 类型定义
│   │   │   └── index.ts
│   │   ├── utils/                        # 工具函数
│   │   │   └── auth.ts                   # 认证工具
│   │   ├── App.tsx                       # 根组件
│   │   ├── main.tsx                      # 入口文件
│   │   └── index.css                     # 全局样式
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
└── pom.xml                               # Maven 配置
```

## 构建和运行

### 数据库准备

```bash
# 登录 MySQL
mysql -u root -p

# 执行建库建表脚本
source src/main/resources/schema.sql

# 执行迁移脚本（按版本顺序）
source src/main/resources/migration/V1.0__add_user_auth_tables.sql
source src/main/resources/migration/V1.1__insert_test_users.sql
source src/main/resources/migration/V1.2__fix_password.sql
source src/main/resources/migration/V1.3__add_operation_log_table.sql
```

### 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/accounting_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的密码
```

### 启动后端

```bash
# 在项目根目录执行
mvn clean install
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动。

### 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:5173 启动。

### 打包部署

**后端打包**
```bash
mvn clean package
java -jar target/accounting-app-1.0.0.jar
```

**前端打包**
```bash
cd frontend
npm run build
# 构建产物在 frontend/dist 目录
```

## API 接口

### 认证接口 (/api/auth)
- `POST /api/auth/login` - 用户登录

### 交易记录 (/api/transactions)
- `POST /api/transactions` - 创建记录
- `PUT /api/transactions/{id}` - 更新记录
- `DELETE /api/transactions/{id}` - 删除记录
- `GET /api/transactions/{id}` - 获取单条记录
- `GET /api/transactions` - 获取所有记录
- `GET /api/transactions/range` - 按时间范围查询
- `GET /api/transactions/summary` - 获取统计汇总

### 资产管理 (/api/assets)
- `POST /api/assets` - 创建资产
- `PUT /api/assets/{id}` - 更新资产
- `DELETE /api/assets/{id}` - 删除资产
- `GET /api/assets` - 获取所有资产
- `GET /api/assets/summary` - 获取资产汇总

### 负债管理 (/api/liabilities)
- `POST /api/liabilities` - 创建负债
- `PUT /api/liabilities/{id}` - 更新负债
- `DELETE /api/liabilities/{id}` - 删除负债
- `GET /api/liabilities` - 获取所有负债
- `GET /api/liabilities/summary` - 获取负债汇总

### 操作日志 (/api/operation-logs) - 仅领导角色
- `GET /api/operation-logs/recent` - 获取最近操作日志
- `GET /api/operation-logs/{id}` - 根据ID获取操作日志
- `GET /api/operation-logs/user/{userId}` - 根据用户ID查询
- `GET /api/operation-logs/module/{module}` - 根据模块查询
- `GET /api/operation-logs/operation-type/{operationType}` - 根据操作类型查询
- `GET /api/operation-logs/time-range` - 根据时间范围查询
- `POST /api/operation-logs/query` - 复合查询

## 开发约定

### 后端开发约定

1. **代码结构**
   - 采用分层架构：Controller → Service → Mapper
   - DTO 用于数据传输，Entity 用于数据库映射
   - 统一返回格式 `Result<T>`（code, message, data）

2. **操作日志**
   - 使用 `@OperationLog` 注解标记需要记录日志的 Service 方法
   - 操作类型：CREATE, UPDATE, DELETE, OTHER
   - 模块：ASSET, LIABILITY, TRANSACTION, OTHER
   - 系统自动从 SecurityContext 获取当前用户信息

3. **权限控制**
   - 使用 `@PreAuthorize` 注解进行方法级权限控制
   - 角色定义：USER（普通用户）、LEADER（领导）
   - 操作日志接口仅限 LEADER 角色访问

4. **MyBatis 配置**
   - 使用 XML 映射文件，位于 `src/main/resources/mapper/`
   - 启用驼峰命名转换（map-underscore-to-camel-case）
   - 开启 SQL 日志输出

5. **Lombok 使用**
   - 使用 `@Data`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor` 简化实体类
   - 需要安装 Lombok 插件

### 前端开发约定

1. **技术栈**
   - 使用函数式组件和 Hooks
   - Ant Design 组件库
   - TypeScript 严格模式
   - React Router 7 路由管理

2. **代码结构**
   - API 统一封装在 `src/api/index.ts`
   - 类型定义在 `src/types/index.ts`
   - 工具函数在 `src/utils/`
   - 页面组件在 `src/pages/`
   - 通用组件在 `src/components/`

3. **认证和授权**
   - JWT Token 存储在 localStorage
   - Axios 拦截器自动添加 Authorization header
   - 401 错误自动跳转到登录页
   - 路由守卫保护需要认证的页面

4. **UI/UX**
   - 使用 Ant Design 组件保持一致性
   - 响应式设计，支持移动端
   - 操作日志页面仅对 LEADER 角色显示菜单项
   - 使用彩色标签区分模块、操作类型和状态

5. **状态管理**
   - 使用 React Hooks 进行本地状态管理
   - 使用 Ant Design Form 处理表单
   - 使用 Ant Design message 进行提示

## 测试用户

系统预置了测试用户（见 `V1.1__insert_test_users.sql`）：

| 用户名 | 密码 | 角色 | 真实姓名 |
|--------|------|------|----------|
| admin | admin123 | LEADER | 管理员 |
| user1 | user123 | USER | 张三 |
| user2 | user123 | USER | 李四 |

## 重要配置

### JWT 配置 (application.yml)
```yaml
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000  # 24小时（毫秒）
```

### 服务器端口
- 后端：8080
- 前端开发服务器：5173

## 注意事项

1. **数据库迁移**
   - 按版本顺序执行迁移脚本
   - V1.3 添加了操作日志表，必须执行

2. **权限控制**
   - 操作日志功能仅对 LEADER 角色开放
   - 前端根据用户角色动态显示菜单

3. **跨域配置**
   - 后端已配置 CORS，允许前端跨域访问
   - 生产环境需要调整允许的源地址

4. **密码加密**
   - 使用 BCrypt 加密存储密码
   - 密码长度至少 6 位

5. **前端端口**
   - 使用 5173 端口避免与其他服务（如 Grafana 3000）冲突