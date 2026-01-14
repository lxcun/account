# 个人记账单系统

一个基于 Spring Boot 3.5.6 + React + TypeScript + Ant Design 的全栈记账单应用。

## 技术栈

### 后端
- Spring Boot 3.5.6
- MyBatis 3.0.3
- MySQL 8.0+
- JDK 21
- Maven

### 前端
- React 18
- TypeScript 5
- Ant Design 5
- Vite 6
- React Router 7
- Axios
- Day.js

## 功能特性

- ✅ 记录收入和支出
- ✅ 支持分类管理（餐饮、购物、交通等）
- ✅ 按时间范围查询统计
- ✅ 收支汇总展示
- ✅ 记录的增删改查
- ✅ 响应式设计
- ✅ **资产管理**（现金、银行、支付宝、微信、股票、基金等）
- ✅ **负债管理**（信用卡、房贷、车贷、个人借款等）
- ✅ **交易自动关联资产负债**（交易时自动更新账户余额）

## 快速开始

### 1. 数据库准备

```bash
# 登录 MySQL
mysql -u root -p

# 执行建库建表脚本
source src/main/resources/schema.sql
```

或者手动创建数据库和表（参考 `src/main/resources/schema.sql`）。

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/accounting_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的密码
```

### 3. 启动后端

```bash
# 在项目根目录执行
mvn clean install
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动。

### 4. 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:5173 启动。

**注意**：如果 3000 端口被占用（如 Grafana），前端已配置为使用 5173 端口。

## 项目结构

```
.
├── src/
│   ├── main/
│   │   ├── java/com/accounting/
│   │   │   ├── AccountingApplication.java    # 启动类
│   │   │   ├── controller/                   # 控制器层
│   │   │   ├── service/                      # 服务层
│   │   │   ├── mapper/                       # MyBatis Mapper
│   │   │   ├── entity/                       # 实体类
│   │   │   ├── dto/                          # 数据传输对象
│   │   │   ├── common/                       # 公共类
│   │   │   └── config/                       # 配置类
│   │   └── resources/
│   │       ├── application.yml               # 应用配置
│   │       ├── schema.sql                    # 数据库脚本
│   │       └── mapper/                       # MyBatis XML
│   └── test/                                 # 测试
├── frontend/
│   ├── src/
│   │   ├── api/                              # API 接口
│   │   ├── components/                       # 组件
│   │   ├── pages/                            # 页面
│   │   ├── types/                            # TypeScript 类型
│   │   ├── App.tsx                           # 根组件
│   │   └── main.tsx                          # 入口文件
│   ├── package.json
│   └── vite.config.ts
└── pom.xml                                   # Maven 配置
```

## API 接口

### 交易记录 (/api/transactions)

- `POST /api/transactions` - 创建记录
- `PUT /api/transactions/{id}` - 更新记录
- `DELETE /api/transactions/{id}` - 删除记录
- `GET /api/transactions/{id}` - 获取单条记录
- `GET /api/transactions` - 获取所有记录
- `GET /api/transactions/range` - 按时间范围查询
- `GET /api/transactions/summary` - 获取统计汇总

**交易记录新增字段**：
- `assetId`: 关联的资产ID
- `liabilityId`: 关联的负债ID
- `accountChangeType`: 账户变动类型
  - `ASSET_INCREASE`: 资产增加（收入）
  - `ASSET_DECREASE`: 资产减少（支出）
  - `LIABILITY_INCREASE`: 负债增加（借款）
  - `LIABILITY_DECREASE`: 负债减少（还款）

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

## 开发说明

### 后端开发

- 使用 Lombok 简化代码，需要 IDE 安装 Lombok 插件
- MyBatis 采用 XML 配置方式
- 统一返回格式 `Result<T>`
- 支持跨域访问

### 前端开发

- 使用函数式组件和 Hooks
- Ant Design 组件库
- Axios 统一请求封装
- TypeScript 类型定义
- Vite 开发服务器支持热更新

## 构建部署

### 后端打包

```bash
mvn clean package
java -jar target/accounting-app-1.0.0.jar
```

### 前端打包

```bash
cd frontend
npm run build
# 构建产物在 frontend/dist 目录
```

## License

MIT
