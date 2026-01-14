package com.accounting.controller;

import com.accounting.common.Result;
import com.accounting.dto.AssetDTO;
import com.accounting.service.AssetService;
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
@RequestMapping("/api/assets")
@Tag(name = "资产管理", description = "资产的增删改查及汇总功能")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * 创建资产 - 仅领导角色可访问
     */
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "创建资产", description = "创建新的资产账户")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问")
    })
    public Result<AssetDTO> create(@RequestBody AssetDTO assetDTO) {
        try {
            AssetDTO result = assetService.createAsset(assetDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新资产 - 仅领导角色可访问
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "更新资产", description = "更新指定的资产信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "资产不存在")
    })
    public Result<AssetDTO> update(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @RequestBody AssetDTO assetDTO) {
        try {
            assetDTO.setId(id);
            AssetDTO result = assetService.updateAsset(assetDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除资产 - 仅领导角色可访问
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(summary = "删除资产", description = "删除指定的资产账户")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅领导角色可访问"),
            @ApiResponse(responseCode = "404", description = "资产不存在")
    })
    public Result<Void> delete(@Parameter(description = "资产ID") @PathVariable Long id) {
        try {
            assetService.deleteAsset(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有资产 - 所有角色可访问
     */
    @GetMapping
    @Operation(summary = "获取所有资产", description = "获取系统中所有的资产列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public Result<List<AssetDTO>> list() {
        try {
            List<AssetDTO> assets = assetService.getAllAssets();
            return Result.success(assets);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取资产汇总 - 所有角色可访问
     */
    @GetMapping("/summary")
    @Operation(summary = "获取资产汇总", description = "统计所有资产的总余额")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "统计成功")
    })
    public Result<Map<String, BigDecimal>> summary() {
        try {
            BigDecimal total = assetService.getTotalAssets();
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("totalAssets", total);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
