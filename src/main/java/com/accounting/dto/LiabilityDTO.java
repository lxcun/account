package com.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "负债信息")
public class LiabilityDTO {
    @Schema(description = "负债ID", example = "1")
    private Long id;

    @Schema(description = "负债名称", example = "招商银行信用卡", required = true)
    private String name;

    @Schema(description = "负债类型", example = "CREDIT_CARD", allowableValues = {"CREDIT_CARD", "MORTGAGE", "CAR_LOAN", "PERSONAL_LOAN", "OTHER"}, required = true)
    private String type;

    @Schema(description = "负债余额", example = "5000.00")
    private BigDecimal balance;

    @Schema(description = "负债描述", example = "日常消费信用卡")
    private String description;
}
