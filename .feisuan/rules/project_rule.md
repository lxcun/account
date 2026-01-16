
# 开发规范指南
为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、技术栈要求

- **主框架**：Spring Boot 3.5.6
- **语言版本**：Java 21
- **核心依赖**：
  - `spring-boot-starter-web`
  - `spring-boot-starter-aop`
  - `mybatis-spring-boot-starter` (3.0.3)
  - `spring-boot-starter-security`
  - `lombok`
  - `jjwt-api` (0.11.5)
  - `springdoc-openapi-starter-webmvc-ui` (2.3.0)

## 二、项目环境信息

- **操作系统**：Mac OS X
- **工作区路径**：/Users/cora/Documents/project/temp/account
- **项目名称**：Accounting Application
- **项目描述**：个人记账单应用
- **代码作者**：cora
- **当前时间**：2026-01-16 09:18:17

## 三、目录结构

```
account/
├── frontend/
│   └── src/
│       ├── api/          # 前端API接口
│       ├── components/   # 前端组件
│       ├── pages/        # 前端页面
│       ├── types/        # TypeScript类型定义
│       └── utils/        # 前端工具函数
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── accounting/
    │   │           ├── annotation/    # 自定义注解
    │   │           ├── aspect/        # AOP切面
    │   │           ├── common/        # 通用类
    │   │           ├── config/        # 配置类
    │   │           ├── controller/    # 控制器层
    │   │           ├── dto/          # 数据传输对象
    │   │           ├── entity/       # 实体类
    │   │           ├── mapper/       # MyBatis Mapper接口
    │   │           ├── security/     # 安全相关
    │   │           ├── service/      # 服务层
    │   │           └── util/         # 工具类
    │   └── resources/
    │       ├── mapper/              # MyBatis XML映射文件
    │       └── migration/           # 数据库迁移脚本
    └── test/
        └── java/
            └── com/                 # 测试代码
```

## 四、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 不得直接访问数据库，必须通过 Service 层调用                  |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 必须通过 Mapper 层访问数据库；返回 DTO 而非 Entity（除非必要） |
| **Mapper**     | 数据库访问与持久化操作             | 使用 MyBatis；XML映射文件放在 `resources/mapper` 目录下      |
| **Entity**     | 映射数据库表结构                   | 不得直接返回给前端（需转换为 DTO）；包名统一为 `entity`         |

### 接口与实现分离

- 所有接口实现类需放在接口所在包下的 `impl` 子包中。

## 五、安全与性能规范

### 输入校验

- 使用 `@Valid` 与 JSR-303 校验注解（如 `@NotBlank`, `@Size` 等）
  - 注意：Spring Boot 3.x 中校验注解位于 `jakarta.validation.constraints.*`

- 禁止手动拼接 SQL 字符串，防止 SQL 注入攻击。

### 事务管理

- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，影响性能。

### JWT安全

- 所有需要认证的API接口必须使用JWT进行身份验证
- JWT密钥配置在`application.yml`中，不得硬编码在代码中
- JWT令牌过期时间默认为86400000毫秒（24小时）

## 六、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserServiceImpl`     |
| 方法/变量  | lowerCamelCase       | `saveUser()`          |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |

### 注释规范

- 所有类、方法、字段需添加 **Javadoc** 注释。
- 注释使用中文编写，确保代码可读性。

### 类型命名规范（阿里巴巴风格）

| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `UserDTO`    |
| DO   | 数据库实体对象               | `UserDO`     |
| BO   | 业务逻辑封装对象             | `UserBO`     |
| VO   | 视图展示对象                 | `UserVO`     |
| Query| 查询参数封装对象             | `UserQuery`  |

### 实体类简化工具

- 使用 Lombok 注解替代手动编写 getter/setter/构造方法：
  - `@Data`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor`

## 七、扩展性与日志规范

### 接口优先原则

- 所有业务逻辑通过接口定义（如 `UserService`），具体实现放在 `impl` 包中（如 `UserServiceImpl`）。

### 日志记录

- 使用 `@Slf4j` 注解代替 `System.out.println`
- 日志级别配置在 `application.yml` 中，默认 `com.accounting` 包为 debug 级别

## 八、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |

## 九、数据库规范

### MyBatis配置

- XML映射文件统一放在 `resources/mapper` 目录下
- 启用驼峰命名转换：`map-underscore-to-camel-case: true`
- 开发环境使用 `StdOutImpl` 作为日志实现，生产环境应替换为其他实现

### 数据库连接

- 默认连接 `localhost:3306/accounting_db`
- 数据库用户名：`root`
- 数据库密码：`123456`（生产环境应使用更安全的密码）
