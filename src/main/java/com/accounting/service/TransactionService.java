package com.accounting.service;

import com.accounting.annotation.OperationLog;
import com.accounting.annotation.OperationModule;
import com.accounting.annotation.OperationType;
import com.accounting.dto.SummaryDTO;
import com.accounting.dto.TransactionDTO;
import com.accounting.entity.TransactionRecord;
import com.accounting.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AssetService assetService;
    private final LiabilityService liabilityService;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.accounting.security.CustomUserDetails) {
            com.accounting.security.CustomUserDetails userDetails =
                (com.accounting.security.CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }

    /**
     * 创建交易记录并更新资产/负债
     */
    @Transactional
    @OperationLog(value = "创建交易记录", operationType = OperationType.CREATE, module = OperationModule.TRANSACTION)
    public TransactionRecord create(TransactionDTO dto) {
        TransactionRecord record = new TransactionRecord();
        BeanUtils.copyProperties(dto, record);
        Long currentUserId = getCurrentUserId();
        record.setCreateBy(currentUserId);
        record.setUpdateBy(currentUserId);

        // 先插入记录
        transactionMapper.insert(record);

        // 再处理账户变动
        processAccountChange(record);

        return record;
    }

    /**
     * 更新交易记录并调整资产/负债
     */
    @Transactional
    @OperationLog(value = "更新交易记录", operationType = OperationType.UPDATE, module = OperationModule.TRANSACTION)
    public TransactionRecord update(Long id, TransactionDTO dto) {
        TransactionRecord oldRecord = transactionMapper.selectById(id);
        if (oldRecord == null) {
            throw new RuntimeException("记录不存在");
        }

        // 先回退旧的账户变动
        revertAccountChange(oldRecord);

        // 应用新的数据
        TransactionRecord newRecord = new TransactionRecord();
        BeanUtils.copyProperties(dto, newRecord);
        newRecord.setId(id);
        Long currentUserId = getCurrentUserId();
        newRecord.setUpdateBy(currentUserId);

        // 处理新的账户变动
        processAccountChange(newRecord);

        transactionMapper.update(newRecord);
        return newRecord;
    }

    /**
     * 删除交易记录并回退资产/负债变动
     */
    @Transactional
    @OperationLog(value = "删除交易记录", operationType = OperationType.DELETE, module = OperationModule.TRANSACTION)
    public void delete(Long id) {
        TransactionRecord record = transactionMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }

        // 回退账户变动
        revertAccountChange(record);

        transactionMapper.deleteById(id);
    }
    
    public TransactionRecord getById(Long id) {
        return transactionMapper.selectById(id);
    }
    
    public List<TransactionRecord> getAll() {
        return transactionMapper.selectAll();
    }
    
    public List<TransactionRecord> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionMapper.selectByDateRange(startDate, endDate);
    }
    
    public SummaryDTO getSummary(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalIncome = transactionMapper.sumByTypeAndDateRange("INCOME", startDate, endDate);
        BigDecimal totalExpense = transactionMapper.sumByTypeAndDateRange("EXPENSE", startDate, endDate);
        
        SummaryDTO summary = new SummaryDTO();
        summary.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        summary.setTotalExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO);
        summary.calculateBalance();
        
        return summary;
    }
    
    /**
     * 处理账户变动（资产/负债）
     */
    private void processAccountChange(TransactionRecord record) {
        if (record.getAccountChangeType() == null) {
            System.out.println("警告: accountChangeType 为空");
            return;
        }
        
        System.out.println("处理账户变动 - 类型: " + record.getAccountChangeType() + 
                          ", 资产ID: " + record.getAssetId() + 
                          ", 负债ID: " + record.getLiabilityId() + 
                          ", 金额: " + record.getAmount());
        
        switch (record.getAccountChangeType()) {
            case "ASSET_INCREASE":
                // 收入增加资产
                if (record.getAssetId() != null) {
                    System.out.println("执行资产增加: ID=" + record.getAssetId() + ", 金额=" + record.getAmount());
                    assetService.updateBalance(record.getAssetId(), record.getAmount());
                } else {
                    System.out.println("警告: ASSET_INCREASE 但 assetId 为空");
                }
                break;
                
            case "ASSET_DECREASE":
                // 支出减少资产
                if (record.getAssetId() != null) {
                    System.out.println("执行资产减少: ID=" + record.getAssetId() + ", 金额=" + record.getAmount());
                    assetService.updateBalance(record.getAssetId(), record.getAmount().negate());
                } else {
                    System.out.println("警告: ASSET_DECREASE 但 assetId 为空");
                }
                break;
                
            case "LIABILITY_INCREASE":
                // 借款增加负债（同时增加资产）
                if (record.getLiabilityId() != null) {
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount());
                }
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount());
                }
                break;
                
            case "LIABILITY_ONLY_INCREASE":
                // 信用卡消费：只增加负债，不增加资产
                if (record.getLiabilityId() != null) {
                    System.out.println("执行负债增加(仅): ID=" + record.getLiabilityId() + ", 金额=" + record.getAmount());
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount());
                }
                break;
                
            case "LIABILITY_DECREASE":
                // 还款减少负债（同时减少资产）
                if (record.getLiabilityId() != null) {
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount().negate());
                }
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount().negate());
                }
                break;
        }
    }
    
    /**
     * 回退账户变动
     */
    private void revertAccountChange(TransactionRecord record) {
        if (record.getAccountChangeType() == null) {
            return;
        }
        
        switch (record.getAccountChangeType()) {
            case "ASSET_INCREASE":
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount().negate());
                }
                break;
                
            case "ASSET_DECREASE":
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount());
                }
                break;
                
            case "LIABILITY_INCREASE":
                if (record.getLiabilityId() != null) {
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount().negate());
                }
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount().negate());
                }
                break;
                
            case "LIABILITY_ONLY_INCREASE":
                // 回退信用卡消费：只减少负债
                if (record.getLiabilityId() != null) {
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount().negate());
                }
                break;
                
            case "LIABILITY_DECREASE":
                if (record.getLiabilityId() != null) {
                    liabilityService.updateBalance(record.getLiabilityId(), record.getAmount());
                }
                if (record.getAssetId() != null) {
                    assetService.updateBalance(record.getAssetId(), record.getAmount());
                }
                break;
        }
    }
}
