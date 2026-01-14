package com.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "交易汇总信息")
public class SummaryDTO {
    @Schema(description = "总收入", example = "10000.00")
    private BigDecimal totalIncome;

    @Schema(description = "总支出", example = "5000.00")
    private BigDecimal totalExpense;

    @Schema(description = "结余", example = "5000.00")
    private BigDecimal balance;

    public SummaryDTO() {
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpense = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
    }

    public void calculateBalance() {
        this.balance = this.totalIncome.subtract(this.totalExpense);
    }
}
