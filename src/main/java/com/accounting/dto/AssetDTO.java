package com.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "资产信息")
public class AssetDTO {
    @Schema(description = "资产ID", example = "1")
    private Long id;

    @Schema(description = "资产名称", example = "招商银行储蓄卡", required = true)
    private String name;

    @Schema(description = "资产类型", example = "CASH", allowableValues = {"CASH", "BANK", "ALIPAY", "WECHAT", "STOCK", "FUND", "OTHER"}, required = true)
    private String type;

    @Schema(description = "资产余额", example = "10000.00")
    private BigDecimal balance;

    @Schema(description = "资产描述", example = "主要工资卡")
    private String description;
}
