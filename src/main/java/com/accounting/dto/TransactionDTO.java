package com.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "交易记录信息")
public class TransactionDTO {
    @Schema(description = "交易记录ID", example = "1")
    private Long id;

    @NotBlank(message = "类型不能为空")
    @Schema(description = "交易类型", example = "INCOME", allowableValues = {"INCOME", "EXPENSE"}, required = true)
    private String type;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    @Schema(description = "交易金额", example = "1000.00", required = true)
    private BigDecimal amount;

    @NotBlank(message = "分类不能为空")
    @Schema(description = "交易分类", example = "工资", required = true)
    private String category;

    @Schema(description = "交易描述", example = "3月份工资")
    private String description;

    @NotNull(message = "交易日期不能为空")
    @Schema(description = "交易日期时间", example = "2026-01-15T10:30:00", required = true)
    private LocalDateTime transactionDate;

    @Schema(description = "关联的资产ID", example = "1")
    private Long assetId;

    @Schema(description = "关联的负债ID", example = "1")
    private Long liabilityId;

    @Schema(description = "账户变动类型", example = "ASSET_INCREASE", allowableValues = {"ASSET_INCREASE", "ASSET_DECREASE", "LIABILITY_INCREASE", "LIABILITY_DECREASE", "LIABILITY_ONLY_INCREASE"})
    private String accountChangeType;
}
