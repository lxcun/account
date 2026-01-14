package com.accounting.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Asset {
    private Long id;
    private String name;
    private String type; // CASH, BANK, ALIPAY, WECHAT, STOCK, FUND, OTHER
    private BigDecimal balance;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
}
