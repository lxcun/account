package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.SummaryDTO;
import com.accounting.dto.TransactionDTO;
import com.accounting.entity.TransactionRecord;
import com.accounting.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "交易记录管理", description = "交易记录的增删改查及统计功能")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 创建交易记录 - 仅领导角色可访问
     */
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "创建交易记录", description = "创建新的交易记录，同时更新关联的资产或负债余额")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<TransactionRecord> create(@Valid @RequestBody TransactionDTO dto) {
        return Result.success(transactionService.create(dto));
    }

    /**
     * 更新交易记录 - 仅领导角色可访问
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "更新交易记录", description = "更新指定的交易记录，同时调整关联的资产或负债余额")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    public Result<TransactionRecord> update(
            @Parameter(description = "交易记录ID") @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto) {
        return Result.success(transactionService.update(id, dto));
    }

    /**
     * 删除交易记录 - 仅领导角色可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "删除交易记录", description = "删除指定的交易记录，同时回退关联的资产或负债余额变动")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    public Result<Void> delete(@Parameter(description = "交易记录ID") @PathVariable Long id) {
        transactionService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID获取交易记录 - 所有角色可访问
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取交易记录详情", description = "根据ID获取指定的交易记录详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    public Result<TransactionRecord> getById(@Parameter(description = "交易记录ID") @PathVariable Long id) {
        return Result.success(transactionService.getById(id));
    }

    /**
     * 获取所有交易记录 - 所有角色可访问
     */
    @GetMapping
    @Operation(summary = "获取所有交易记录", description = "获取系统中所有的交易记录，按交易日期倒序排列")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public Result<List<TransactionRecord>> getAll() {
        return Result.success(transactionService.getAll());
    }

    /**
     * 按日期范围获取交易记录 - 所有角色可访问
     */
    @GetMapping("/range")
    @Operation(summary = "按日期范围查询交易记录", description = "根据指定的日期范围查询交易记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "日期参数错误")
    })
    public Result<List<TransactionRecord>> getByDateRange(
            @Parameter(description = "开始日期时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return Result.success(transactionService.getByDateRange(startDate, endDate));
    }

    /**
     * 获取交易汇总 - 所有角色可访问
     */
    @GetMapping("/summary")
    @Operation(summary = "获取交易汇总", description = "统计指定日期范围内的收入总额、支出总额和结余")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "统计成功"),
            @ApiResponse(responseCode = "400", description = "日期参数错误")
    })
    public Result<SummaryDTO> getSummary(
            @Parameter(description = "开始日期时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return Result.success(transactionService.getSummary(startDate, endDate));
    }
}
