package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.OperationLogQueryDTO;
import com.accounting.entity.OperationLog;
import com.accounting.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@Tag(name = "操作日志管理", description = "系统操作日志的查询功能，仅领导角色可访问")
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 根据ID获取操作日志 - 仅领导角色可访问
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据ID获取操作日志", description = "根据日志ID获取详细的操作日志信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "日志不存在")
    })
    public Result<OperationLog> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        return Result.success(operationLogService.getById(id));
    }

    /**
     * 获取最近操作日志 - 仅领导角色可访问
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "获取最近操作日志", description = "获取最近N条操作日志，默认100条")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getRecentLogs(
            @Parameter(description = "返回数量限制，默认100") @RequestParam(defaultValue = "100") int limit) {
        return Result.success(operationLogService.getRecentLogs(limit));
    }

    /**
     * 根据用户ID查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据用户ID查询操作日志", description = "查询指定用户的所有操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(operationLogService.getLogsByUserId(userId));
    }

    /**
     * 根据模块查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/module/{module}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据模块查询操作日志", description = "查询指定模块的所有操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByModule(@Parameter(description = "模块名称（ASSET/LIABILITY/TRANSACTION）") @PathVariable String module) {
        return Result.success(operationLogService.getLogsByModule(module));
    }

    /**
     * 根据操作类型查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/operation-type/{operationType}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据操作类型查询操作日志", description = "查询指定操作类型的所有操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByOperationType(@Parameter(description = "操作类型（CREATE/UPDATE/DELETE）") @PathVariable String operationType) {
        return Result.success(operationLogService.getLogsByOperationType(operationType));
    }

    /**
     * 根据时间范围查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/time-range")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据时间范围查询操作日志", description = "查询指定时间范围内的操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "日期参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByTimeRange(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(operationLogService.getLogsByTimeRange(startTime, endTime));
    }

    /**
     * 根据用户ID和模块查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/user/{userId}/module/{module}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据用户ID和模块查询操作日志", description = "查询指定用户在指定模块的所有操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByUserIdAndModule(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "模块名称") @PathVariable String module) {
        return Result.success(operationLogService.getLogsByUserIdAndModule(userId, module));
    }

    /**
     * 根据用户ID和操作类型查询操作日志 - 仅领导角色可访问
     */
    @GetMapping("/user/{userId}/operation-type/{operationType}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "根据用户ID和操作类型查询操作日志", description = "查询指定用户的指定操作类型的所有操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> getLogsByUserIdAndOperationType(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "操作类型") @PathVariable String operationType) {
        return Result.success(operationLogService.getLogsByUserIdAndOperationType(userId, operationType));
    }

    /**
     * 获取操作日志总数 - 仅领导角色可访问
     */
    @GetMapping("/total-count")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "获取操作日志总数", description = "获取系统中操作日志的总数量")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<Long> getTotalCount() {
        return Result.success(operationLogService.getTotalCount());
    }

    /**
     * 获取指定用户的操作日志数量 - 仅领导角色可访问
     */
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "获取指定用户的操作日志数量", description = "统计指定用户的操作日志数量")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<Long> getCountByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(operationLogService.getCountByUserId(userId));
    }

    /**
     * 复合查询操作日志 - 仅领导角色可访问
     */
    @PostMapping("/query")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "复合查询操作日志", description = "根据多个条件组合查询操作日志")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<List<OperationLog>> query(@RequestBody OperationLogQueryDTO queryDTO) {
        List<OperationLog> result;

        if (queryDTO.getUserId() != null && queryDTO.getModule() != null) {
            result = operationLogService.getLogsByUserIdAndModule(queryDTO.getUserId(), queryDTO.getModule());
        } else if (queryDTO.getUserId() != null && queryDTO.getOperationType() != null) {
            result = operationLogService.getLogsByUserIdAndOperationType(queryDTO.getUserId(), queryDTO.getOperationType());
        } else if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            result = operationLogService.getLogsByTimeRange(queryDTO.getStartTime(), queryDTO.getEndTime());
        } else if (queryDTO.getUserId() != null) {
            result = operationLogService.getLogsByUserId(queryDTO.getUserId());
        } else if (queryDTO.getModule() != null) {
            result = operationLogService.getLogsByModule(queryDTO.getModule());
        } else if (queryDTO.getOperationType() != null) {
            result = operationLogService.getLogsByOperationType(queryDTO.getOperationType());
        } else {
            result = operationLogService.getRecentLogs(queryDTO.getLimit());
        }

        return Result.success(result);
    }
}
