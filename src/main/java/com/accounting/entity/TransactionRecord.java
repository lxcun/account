package com.accounting.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRecord {
    private Long id;
    private String type; // INCOME / EXPENSE
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime transactionDate;
    private Long assetId; // 关联的资产ID
    private Long liabilityId; // 关联的负债ID
    private String accountChangeType; // ASSET_INCREASE, ASSET_DECREASE, LIABILITY_INCREASE, LIABILITY_DECREASE
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
}
