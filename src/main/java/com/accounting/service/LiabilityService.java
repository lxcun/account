package com.accounting.service;

import com.accounting.annotation.OperationLog;
import com.accounting.annotation.OperationModule;
import com.accounting.annotation.OperationType;
import com.accounting.dto.LiabilityDTO;
import com.accounting.entity.Liability;
import com.accounting.mapper.LiabilityMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiabilityService {

    private final LiabilityMapper liabilityMapper;

    public LiabilityService(LiabilityMapper liabilityMapper) {
        this.liabilityMapper = liabilityMapper;
    }

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
     * 创建负债
     */
    @Transactional
    @OperationLog(value = "创建负债", operationType = OperationType.CREATE, module = OperationModule.LIABILITY)
    public LiabilityDTO createLiability(LiabilityDTO liabilityDTO) {
        Liability liability = new Liability();
        BeanUtils.copyProperties(liabilityDTO, liability);
        if (liability.getBalance() == null) {
            liability.setBalance(BigDecimal.ZERO);
        }
        Long currentUserId = getCurrentUserId();
        liability.setCreateBy(currentUserId);
        liability.setUpdateBy(currentUserId);
        liabilityMapper.insert(liability);
        liabilityDTO.setId(liability.getId());
        return liabilityDTO;
    }

    /**
     * 更新负债
     */
    @Transactional
    @OperationLog(value = "更新负债", operationType = OperationType.UPDATE, module = OperationModule.LIABILITY)
    public LiabilityDTO updateLiability(LiabilityDTO liabilityDTO) {
        Liability existLiability = liabilityMapper.selectById(liabilityDTO.getId());
        if (existLiability == null) {
            throw new RuntimeException("负债不存在");
        }

        Liability liability = new Liability();
        BeanUtils.copyProperties(liabilityDTO, liability);
        Long currentUserId = getCurrentUserId();
        liability.setUpdateBy(currentUserId);
        liabilityMapper.update(liability);
        return liabilityDTO;
    }

    /**
     * 删除负债
     */
    @Transactional
    @OperationLog(value = "删除负债", operationType = OperationType.DELETE, module = OperationModule.LIABILITY)
    public void deleteLiability(Long liabilityId) {
        Liability liability = liabilityMapper.selectById(liabilityId);
        if (liability == null) {
            throw new RuntimeException("负债不存在");
        }
        liabilityMapper.deleteById(liabilityId);
    }

    /**
     * 获取所有负债
     */
    public List<LiabilityDTO> getAllLiabilities() {
        List<Liability> liabilities = liabilityMapper.selectAll();
        return liabilities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取负债总额
     */
    public BigDecimal getTotalLiabilities() {
        return liabilityMapper.sumBalance();
    }

    /**
     * 更新负债余额（内部方法，供交易使用）
     */
    @Transactional
    public void updateBalance(Long liabilityId, BigDecimal amount) {
        Liability liability = liabilityMapper.selectById(liabilityId);
        if (liability == null) {
            throw new RuntimeException("负债不存在");
        }
        BigDecimal newBalance = liability.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("负债余额不能为负");
        }
        liabilityMapper.updateBalance(liabilityId, newBalance);
    }

    /**
     * 获取负债信息
     */
    public Liability getLiabilityById(Long liabilityId) {
        return liabilityMapper.selectById(liabilityId);
    }

    private LiabilityDTO convertToDTO(Liability liability) {
        LiabilityDTO dto = new LiabilityDTO();
        BeanUtils.copyProperties(liability, dto);
        return dto;
    }
}
