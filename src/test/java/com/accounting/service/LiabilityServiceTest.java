package com.accounting.service;

import com.accounting.dto.LiabilityDTO;
import com.accounting.entity.Liability;
import com.accounting.mapper.LiabilityMapper;
import com.accounting.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * LiabilityService 单元测试
 * 测试负债创建、更新、删除以及余额管理逻辑
 */
@ExtendWith(MockitoExtension.class)
class LiabilityServiceTest {

    @Mock
    private LiabilityMapper liabilityMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LiabilityService liabilityService;

    private static final Long USER_ID = 1L;
    private static final Long LIABILITY_ID = 1L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateLiability_WithInitialBalance() {
        // 准备测试数据
        LiabilityDTO dto = new LiabilityDTO();
        dto.setName("招商银行信用卡");
        dto.setType("CREDIT_CARD");
        dto.setBalance(new BigDecimal("5000.00"));

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username("testUser")
                .password("password")
                .roleCode("USER")
                .authorities(null)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 执行测试
        liabilityService.createLiability(dto);

        // 验证：insert 方法被调用
        verify(liabilityMapper).insert(any(Liability.class));
    }

    @Test
    void testCreateLiability_WithoutInitialBalance() {
        // 准备测试数据：没有初始余额
        LiabilityDTO dto = new LiabilityDTO();
        dto.setName("房贷");
        dto.setType("MORTGAGE");

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username("testUser")
                .password("password")
                .roleCode("USER")
                .authorities(null)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 执行测试
        LiabilityDTO result = liabilityService.createLiability(dto);

        // 验证：余额应被设置为 0
        verify(liabilityMapper).insert(argThat(liability ->
                liability.getBalance().equals(BigDecimal.ZERO)
        ));
    }

    @Test
    void testUpdateLiability_Success() {
        // 准备测试数据
        LiabilityDTO dto = new LiabilityDTO();
        dto.setId(LIABILITY_ID);
        dto.setName("工商银行信用卡");
        dto.setType("CREDIT_CARD");
        dto.setBalance(new BigDecimal("8000.00"));

        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username("testUser")
                .password("password")
                .roleCode("USER")
                .authorities(null)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 执行测试
        LiabilityDTO result = liabilityService.updateLiability(dto);

        // 验证
        verify(liabilityMapper).update(any(Liability.class));
        assertEquals("工商银行信用卡", result.getName());
        assertEquals(new BigDecimal("8000.00"), result.getBalance());
    }

    @Test
    void testUpdateLiability_LiabilityNotFound() {
        LiabilityDTO dto = new LiabilityDTO();
        dto.setId(999L);
        dto.setName("不存在的负债");

        when(liabilityMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                liabilityService.updateLiability(dto)
        );
        assertEquals("负债不存在", exception.getMessage());
        verify(liabilityMapper, never()).update(any());
    }

    @Test
    void testDeleteLiability_Success() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试
        liabilityService.deleteLiability(LIABILITY_ID);

        // 验证
        verify(liabilityMapper).deleteById(LIABILITY_ID);
    }

    @Test
    void testDeleteLiability_LiabilityNotFound() {
        when(liabilityMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                liabilityService.deleteLiability(999L)
        );
        assertEquals("负债不存在", exception.getMessage());
        verify(liabilityMapper, never()).deleteById(any());
    }

    @Test
    void testGetAllLiabilities() {
        List<Liability> liabilities = Arrays.asList(
                createLiability(1L, "招商银行信用卡", "CREDIT_CARD", new BigDecimal("5000.00")),
                createLiability(2L, "房贷", "MORTGAGE", new BigDecimal("800000.00")),
                createLiability(3L, "车贷", "CAR_LOAN", new BigDecimal("150000.00"))
        );

        when(liabilityMapper.selectAll()).thenReturn(liabilities);

        // 执行测试
        List<LiabilityDTO> result = liabilityService.getAllLiabilities();

        // 验证
        assertEquals(3, result.size());
        assertEquals("招商银行信用卡", result.get(0).getName());
        assertEquals("房贷", result.get(1).getName());
        assertEquals("车贷", result.get(2).getName());
    }

    @Test
    void testGetAllLiabilities_Empty() {
        when(liabilityMapper.selectAll()).thenReturn(Collections.emptyList());

        // 执行测试
        List<LiabilityDTO> result = liabilityService.getAllLiabilities();

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTotalLiabilities() {
        when(liabilityMapper.sumBalance()).thenReturn(new BigDecimal("965500.00"));

        // 执行测试
        BigDecimal result = liabilityService.getTotalLiabilities();

        // 验证
        assertEquals(new BigDecimal("965500.00"), result);
    }

    @Test
    void testGetTotalLiabilities_Zero() {
        when(liabilityMapper.sumBalance()).thenReturn(BigDecimal.ZERO);

        // 执行测试
        BigDecimal result = liabilityService.getTotalLiabilities();

        // 验证
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testUpdateBalance_Increase() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试：增加负债余额（消费）
        liabilityService.updateBalance(LIABILITY_ID, new BigDecimal("2000.00"));

        // 验证：余额应为 7000
        verify(liabilityMapper).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("7000.00")));
    }

    @Test
    void testUpdateBalance_Decrease() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试：减少负债余额（还款）
        liabilityService.updateBalance(LIABILITY_ID, new BigDecimal("-2000.00"));

        // 验证：余额应为 3000
        verify(liabilityMapper).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("3000.00")));
    }

    @Test
    void testUpdateBalance_InsufficientFunds() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("1000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试并验证异常：还款金额大于当前负债余额
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                liabilityService.updateBalance(LIABILITY_ID, new BigDecimal("-2000.00"))
        );
        assertEquals("负债余额不能为负", exception.getMessage());
        verify(liabilityMapper, never()).updateBalance(any(), any());
    }

    @Test
    void testUpdateBalance_LiabilityNotFound() {
        when(liabilityMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                liabilityService.updateBalance(999L, new BigDecimal("1000.00"))
        );
        assertEquals("负债不存在", exception.getMessage());
        verify(liabilityMapper, never()).updateBalance(any(), any());
    }

    @Test
    void testUpdateBalance_BecomeZero() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("招商银行信用卡");
        existingLiability.setType("CREDIT_CARD");
        existingLiability.setBalance(new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试：负债余额变为 0（还清）
        liabilityService.updateBalance(LIABILITY_ID, new BigDecimal("-5000.00"));

        // 验证：余额变为 0 是允许的（使用 argThat 来匹配值为 0 的 BigDecimal）
        verify(liabilityMapper).updateBalance(eq(LIABILITY_ID), argThat(balance ->
                balance.compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void testUpdateBalance_FromZero() {
        Liability existingLiability = new Liability();
        existingLiability.setId(LIABILITY_ID);
        existingLiability.setName("个人借款");
        existingLiability.setType("PERSONAL_LOAN");
        existingLiability.setBalance(BigDecimal.ZERO);

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(existingLiability);

        // 执行测试：从 0 开始增加负债（借款）
        liabilityService.updateBalance(LIABILITY_ID, new BigDecimal("10000.00"));

        // 验证
        verify(liabilityMapper).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("10000.00")));
    }

    @Test
    void testGetLiabilityById() {
        Liability liability = createLiability(LIABILITY_ID, "招商银行信用卡", "CREDIT_CARD",
                new BigDecimal("5000.00"));

        when(liabilityMapper.selectById(LIABILITY_ID)).thenReturn(liability);

        // 执行测试
        Liability result = liabilityService.getLiabilityById(LIABILITY_ID);

        // 验证
        assertNotNull(result);
        assertEquals(LIABILITY_ID, result.getId());
        assertEquals("招商银行信用卡", result.getName());
        assertEquals("CREDIT_CARD", result.getType());
        assertEquals(new BigDecimal("5000.00"), result.getBalance());
    }

    @Test
    void testGetLiabilityById_NotFound() {
        when(liabilityMapper.selectById(999L)).thenReturn(null);

        // 执行测试
        Liability result = liabilityService.getLiabilityById(999L);

        // 验证
        assertNull(result);
    }

    @Test
    void testConvertToDTO() {
        Liability liability = createLiability(LIABILITY_ID, "招商银行信用卡", "CREDIT_CARD",
                new BigDecimal("5000.00"));

        // 执行测试（通过 getAllLiabilities 方法间接测试）
        List<Liability> liabilities = Collections.singletonList(liability);
        when(liabilityMapper.selectAll()).thenReturn(liabilities);

        List<LiabilityDTO> result = liabilityService.getAllLiabilities();

        // 验证转换是否正确
        assertEquals(1, result.size());
        assertEquals(LIABILITY_ID, result.get(0).getId());
        assertEquals("招商银行信用卡", result.get(0).getName());
        assertEquals("CREDIT_CARD", result.get(0).getType());
        assertEquals(new BigDecimal("5000.00"), result.get(0).getBalance());
    }

    // 辅助方法：创建 Liability
    private Liability createLiability(Long id, String name, String type, BigDecimal balance) {
        Liability liability = new Liability();
        liability.setId(id);
        liability.setName(name);
        liability.setType(type);
        liability.setBalance(balance);
        return liability;
    }
}
