package com.accounting.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Liability {
    private Long id;
    private String name;
    private String type; // CREDIT_CARD, MORTGAGE, CAR_LOAN, PERSONAL_LOAN, OTHER
    private BigDecimal balance;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
}
