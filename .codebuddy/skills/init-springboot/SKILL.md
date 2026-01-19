---
name: init-springboot
description: Initialize Spring Boot Maven project with standard structure, dependencies, and configurations
version: "1.0.0"
updated: "2025-01-14"
---

# Init Spring Boot

快速初始化标准的 Spring Boot Maven 项目，包含完整的目录结构、基础配置和最佳实践。

## Features

### 自动创建的内容

1. **标准目录结构**
   ```
   src/main/java/com/example/demo/
   ├── controller/
   ├── service/
   ├── config/
   ├── common/
   ├── pojo/
   │   ├── bo/
   │   ├── entity/
   │   ├── query/
   │   └── vo/
   ├── mapper/
   ├── module/
   └── DemoApplication.java
   ```

2. **Maven 配置** (`pom.xml`)
   - Spring Boot 3.2.x
   - Java 21
   - 常用依赖：Web, Validation, Lombok, MySQL, MyBatis-Plus
   - 可选依赖：JWT, OAuth2

3. **应用配置** (`application.yml`)
   - 服务器端口配置
   - 数据库连接配置
   - MybatisPlus 配置
   - 日志配置

4. **主启动类** (`DemoApplication.java`)
   - `@SpringBootApplication` 注解
   - 标准 main 方法

5. **Git 忽略文件** (`.gitignore`)
   - Maven target/
   - IDEA 配置
   - 日志文件

6. **可选配置**
   - JWT 安全配置（SecurityConfig）
   - CORS 跨域配置
   - 全局异常处理

## Usage

调用此 skill 时，可以指定以下参数：

### 参数说明

| 参数 | 类型 | 必需 | 默认值 | 说明 |
|------|------|------|------|------|
| `group_id` | string | ✗ | com.example | 项目 groupId |
| `artifact_id` | string | ✗ | demo | 项目 artifactId |
| `package_name` | string | ✗ | com.example.demo | 主包名 |
| `include_jwt` | boolean | ✗ | true | 是否包含 JWT 安全配置 |
| `include_cors` | boolean | ✗ | true | 是否包含 CORS 配置 |
| `database` | enum | ✗ | mysql | 数据库类型: h2, mysql, postgresql |

### 调用示例

```
User: "创建一个新的 Spring Boot 项目"
→ 使用默认配置创建项目

User: "创建 Spring Boot 项目，使用 MySQL 数据库"
→ 创建项目，配置 MySQL 数据库

User: "创建 Spring Boot 项目，包名为 com.myapp.api"
→ 创建项目，使用自定义包名
```

## Code Templates

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- H2 Database (for development) -->
        <!--
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        -->

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.5</version>
        </dependency>

        <!-- JWT (if include_jwt is true) -->
        <!--
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        -->

        <!-- Spring Boot DevTools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: demo
  # Database Configuration
  datasource:
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis-Plus Configuration
mybatis-plus:
  # Mapper XML文件路径
  mapper-locations: classpath:mapper/*.xml
  # 实体类扫描路径
  type-aliases-package: com.example.demo.pojo.entity
  configuration:
    # 驼峰转下划线
    map-underscore-to-camel-case: true
    # 日志输出
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      # 主键类型 (AUTO 自增)
      id-type: auto
      # 逻辑删除字段
      logic-delete-field: isDeleted
      # 逻辑删除值 (默认 1)
      logic-delete-value: 1
      # 逻辑未删除值 (默认 0)
      logic-not-delete-value: 0
# Logging
logging:
  level:
    root: INFO
    com.example.demo: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

# JWT Configuration
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation2024
  expiration: 24h  # 支持: 30m(30分钟), 2h(2小时), 7d(7天)等格式
```

### Main Application Class

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### SecurityConfig (Optional, if include_jwt is true)

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

### .gitignore

```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.DS_Store

# Logs
logs/
*.log

# Application
application-local.yml
application-local.properties
```

## Examples

### Example 1: Create basic project

**User**: "创建一个 Spring Boot 项目"

**Output**:
- 创建标准项目结构
- 使用默认配置（MySQL 数据库、包含 JWT 和 CORS）
- 包名：com.example.demo

### Example 2: Create project with MySQL

**User**: "创建 Spring Boot 项目，使用 MySQL 数据库"

**Output**:
- 项目结构同上
- application.yml 配置 MySQL 连接
- pom.xml 包含 MySQL 驱动依赖

### Example 3: Create project with custom package

**User**: "创建 Spring Boot 项目，包名为 com.mycompany.ecommerce"

**Output**:
- 项目结构：src/main/java/com/mycompany/ecommerce/
- 所有类文件使用自定义包名

## Next Steps

项目创建后，您可以：

1. **启动项目**: `mvn spring-boot:run`
2. **添加实体类**: 在 `pojo/entity/` 包下创建实体类
3. **添加 Mapper**: 在 `mapper/` 包下创建持久层接口
4. **实现业务逻辑**: 在 `service/` 包下添加服务类
5. **创建 API**: 在 `controller/` 包下添加控制器
6. **添加配置**: 在 `config/` 包下添加配置类（如 SecurityConfig）

## Common Customizations

### 修改端口
在 `application.yml` 中修改 `server.port`

### 切换到生产数据库
1. 在 `pom.xml` 添加对应数据库驱动
2. 在 `application.yml` 修改 `spring.datasource` 配置

### 添加 Swagger/OpenAPI
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2025-01-14 | Initial release |
