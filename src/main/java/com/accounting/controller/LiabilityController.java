package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.LiabilityDTO;
import com.accounting.service.LiabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/liabilities")
@Tag(name = "负债管理", description = "负债的增删改查及汇总功能")
public class LiabilityController {

    private final LiabilityService liabilityService;

    public LiabilityController(LiabilityService liabilityService) {
        this.liabilityService = liabilityService;
    }

    /**
     * 创建负债 - 仅领导角色可访问
     */
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "创建负债", description = "创建新的负债账户（如信用卡、贷款等）")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<LiabilityDTO> create(@RequestBody LiabilityDTO liabilityDTO) {
        try {
            LiabilityDTO result = liabilityService.createLiability(liabilityDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新负债 - 仅领导角色可访问
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "更新负债", description = "更新指定的负债信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "负债不存在")
    })
    public Result<LiabilityDTO> update(
            @Parameter(description = "负债ID") @PathVariable Long id,
            @RequestBody LiabilityDTO liabilityDTO) {
        try {
            liabilityDTO.setId(id);
            LiabilityDTO result = liabilityService.updateLiability(liabilityDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除负债 - 仅领导角色可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "删除负债", description = "删除指定的负债账户")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "负债不存在")
    })
    public Result<Void> delete(@Parameter(description = "负债ID") @PathVariable Long id) {
        try {
            liabilityService.deleteLiability(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有负债 - 所有角色可访问
     */
    @GetMapping
    @Operation(summary = "获取所有负债", description = "获取系统中所有的负债列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public Result<List<LiabilityDTO>> list() {
        try {
            List<LiabilityDTO> liabilities = liabilityService.getAllLiabilities();
            return Result.success(liabilities);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取负债汇总 - 所有角色可访问
     */
    @GetMapping("/summary")
    @Operation(summary = "获取负债汇总", description = "统计所有负债的总余额")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "统计成功")
    })
    public Result<Map<String, BigDecimal>> summary() {
        try {
            BigDecimal total = liabilityService.getTotalLiabilities();
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("totalLiabilities", total);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
