package com.accounting.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Long userId; // 操作人ID
    private String username; // 操作人用户名
    private String realName; // 操作人真实姓名
    private String operationType; // 操作类型: CREATE, UPDATE, DELETE
    private String module; // 模块: ASSET, LIABILITY, TRANSACTION
    private String operationName; // 操作名称: 创建资产, 更新资产等
    private String description; // 操作描述
    private Long targetId; // 目标记录ID
    private String targetName; // 目标记录名称(便于查看)
    private String requestData; // 请求数据(JSON格式)
    private String resultData; // 结果数据(JSON格式)
    private String ip; // 操作IP
    private String status; // 状态: SUCCESS, FAILED
    private String errorMsg; // 错误信息
    private LocalDateTime operationTime; // 操作时间
}
