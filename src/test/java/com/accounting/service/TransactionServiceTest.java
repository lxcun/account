package com.accounting.service;

import com.accounting.dto.SummaryDTO;
import com.accounting.dto.TransactionDTO;
import com.accounting.entity.TransactionRecord;
import com.accounting.mapper.TransactionMapper;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TransactionService 单元测试
 * 测试交易记录创建、更新、删除以及账户余额变动逻辑
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AssetService assetService;

    @Mock
    private LiabilityService liabilityService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private static final Long USER_ID = 1L;
    private static final Long ASSET_ID = 1L;
    private static final Long LIABILITY_ID = 1L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateTransaction_AssetIncrease() {
        // 准备测试数据
        TransactionDTO dto = createTransactionDTO(
                "INCOME",
                new BigDecimal("1000.00"),
                "工资",
                ASSET_ID,
                null,
                "ASSET_INCREASE"
        );

        // 模拟 SecurityContext 返回用户ID
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
        TransactionRecord result = transactionService.create(dto);

        // 验证
        verify(transactionMapper).insert(any(TransactionRecord.class));
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("1000.00")));
        assertEquals(USER_ID, result.getCreateBy());
        assertEquals(USER_ID, result.getUpdateBy());
    }

    @Test
    void testCreateTransaction_AssetDecrease() {
        // 准备测试数据
        TransactionDTO dto = createTransactionDTO(
                "EXPENSE",
                new BigDecimal("500.00"),
                "餐饮",
                ASSET_ID,
                null,
                "ASSET_DECREASE"
        );

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
        TransactionRecord result = transactionService.create(dto);

        // 验证
        verify(transactionMapper).insert(any(TransactionRecord.class));
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("-500.00")));
    }

    @Test
    void testCreateTransaction_LiabilityIncrease() {
        // 准备测试数据：借款
        TransactionDTO dto = createTransactionDTO(
                "INCOME",
                new BigDecimal("5000.00"),
                "借款",
                ASSET_ID,
                LIABILITY_ID,
                "LIABILITY_INCREASE"
        );

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
        transactionService.create(dto);

        // 验证：借款同时增加负债和资产
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("5000.00")));
        verify(liabilityService).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("5000.00")));
    }

    @Test
    void testCreateTransaction_LiabilityOnlyIncrease() {
        // 准备测试数据：信用卡消费
        TransactionDTO dto = createTransactionDTO(
                "EXPENSE",
                new BigDecimal("1000.00"),
                "购物",
                null,
                LIABILITY_ID,
                "LIABILITY_ONLY_INCREASE"
        );

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
        transactionService.create(dto);

        // 验证：信用卡消费只增加负债
        verify(liabilityService).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("1000.00")));
        verify(assetService, never()).updateBalance(any(), any());
    }

    @Test
    void testCreateTransaction_LiabilityDecrease() {
        // 准备测试数据：还款
        TransactionDTO dto = createTransactionDTO(
                "EXPENSE",
                new BigDecimal("2000.00"),
                "信用卡还款",
                ASSET_ID,
                LIABILITY_ID,
                "LIABILITY_DECREASE"
        );

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
        transactionService.create(dto);

        // 验证：还款同时减少负债和资产
        verify(liabilityService).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("-2000.00")));
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("-2000.00")));
    }

    @Test
    void testCreateTransaction_WithNullAccountChangeType() {
        // 准备测试数据：accountChangeType 为 null
        TransactionDTO dto = createTransactionDTO(
                "INCOME",
                new BigDecimal("1000.00"),
                "工资",
                ASSET_ID,
                null,
                null
        );

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username("testUser")
                .password("password")
                .roleCode("USER")
                .authorities(null)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 执行测试 - 不应抛出异常，但也不会更新账户
        TransactionRecord result = transactionService.create(dto);

        // 验证：只插入记录，不更新账户
        verify(transactionMapper).insert(any(TransactionRecord.class));
        verify(assetService, never()).updateBalance(any(), any());
        verify(liabilityService, never()).updateBalance(any(), any());
    }

    @Test
    void testUpdateTransaction_AssetChange() {
        // 准备原始记录
        TransactionRecord oldRecord = new TransactionRecord();
        oldRecord.setId(1L);
        oldRecord.setType("INCOME");
        oldRecord.setAmount(new BigDecimal("1000.00"));
        oldRecord.setAccountChangeType("ASSET_INCREASE");
        oldRecord.setAssetId(ASSET_ID);

        // 准备更新数据
        TransactionDTO dto = createTransactionDTO(
                "EXPENSE",
                new BigDecimal("800.00"),
                "餐饮",
                ASSET_ID,
                null,
                "ASSET_DECREASE"
        );

        when(transactionMapper.selectById(1L)).thenReturn(oldRecord);
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
        TransactionRecord result = transactionService.update(1L, dto);

        // 验证：先回退旧的变动，再应用新的变动
        // 回退：资产减少 1000
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("-1000.00")));
        // 应用新变动：资产减少 800
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("-800.00")));
        verify(transactionMapper).update(any(TransactionRecord.class));
    }

    @Test
    void testUpdateTransaction_RecordNotFound() {
        TransactionDTO dto = createTransactionDTO(
                "INCOME",
                new BigDecimal("1000.00"),
                "工资",
                ASSET_ID,
                null,
                "ASSET_INCREASE"
        );

        when(transactionMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> transactionService.update(1L, dto));
    }

    @Test
    void testDeleteTransaction() {
        // 准备要删除的记录
        TransactionRecord record = new TransactionRecord();
        record.setId(1L);
        record.setType("INCOME");
        record.setAmount(new BigDecimal("1000.00"));
        record.setAccountChangeType("ASSET_INCREASE");
        record.setAssetId(ASSET_ID);

        when(transactionMapper.selectById(1L)).thenReturn(record);

        // 执行测试
        transactionService.delete(1L);

        // 验证：回退账户变动并删除记录
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("-1000.00")));
        verify(transactionMapper).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_LiabilityDecrease() {
        // 准备要删除的记录：还款记录
        TransactionRecord record = new TransactionRecord();
        record.setId(1L);
        record.setType("EXPENSE");
        record.setAmount(new BigDecimal("2000.00"));
        record.setAccountChangeType("LIABILITY_DECREASE");
        record.setAssetId(ASSET_ID);
        record.setLiabilityId(LIABILITY_ID);

        when(transactionMapper.selectById(1L)).thenReturn(record);

        // 执行测试
        transactionService.delete(1L);

        // 验证：回退还款操作 = 增加负债和资产
        verify(liabilityService).updateBalance(eq(LIABILITY_ID), eq(new BigDecimal("2000.00")));
        verify(assetService).updateBalance(eq(ASSET_ID), eq(new BigDecimal("2000.00")));
    }

    @Test
    void testDeleteTransaction_RecordNotFound() {
        when(transactionMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> transactionService.delete(1L));
    }

    @Test
    void testGetById() {
        TransactionRecord record = new TransactionRecord();
        record.setId(1L);
        record.setType("INCOME");
        record.setAmount(new BigDecimal("1000.00"));

        when(transactionMapper.selectById(1L)).thenReturn(record);

        // 执行测试
        TransactionRecord result = transactionService.getById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("INCOME", result.getType());
        assertEquals(new BigDecimal("1000.00"), result.getAmount());
    }

    @Test
    void testGetAll() {
        List<TransactionRecord> records = Arrays.asList(
                createRecord(1L, "INCOME", new BigDecimal("1000.00")),
                createRecord(2L, "EXPENSE", new BigDecimal("500.00"))
        );

        when(transactionMapper.selectAll()).thenReturn(records);

        // 执行测试
        List<TransactionRecord> result = transactionService.getAll();

        // 验证
        assertEquals(2, result.size());
    }

    @Test
    void testGetByDateRange() {
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 31, 23, 59);

        List<TransactionRecord> records = Collections.singletonList(
                createRecord(1L, "INCOME", new BigDecimal("1000.00"))
        );

        when(transactionMapper.selectByDateRange(startDate, endDate)).thenReturn(records);

        // 执行测试
        List<TransactionRecord> result = transactionService.getByDateRange(startDate, endDate);

        // 验证
        assertEquals(1, result.size());
        verify(transactionMapper).selectByDateRange(startDate, endDate);
    }

    @Test
    void testGetSummary() {
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 31, 23, 59);

        when(transactionMapper.sumByTypeAndDateRange("INCOME", startDate, endDate))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionMapper.sumByTypeAndDateRange("EXPENSE", startDate, endDate))
                .thenReturn(new BigDecimal("3000.00"));

        // 执行测试
        SummaryDTO result = transactionService.getSummary(startDate, endDate);

        // 验证
        assertEquals(new BigDecimal("5000.00"), result.getTotalIncome());
        assertEquals(new BigDecimal("3000.00"), result.getTotalExpense());
        assertEquals(new BigDecimal("2000.00"), result.getBalance());
    }

    @Test
    void testGetSummary_WithNullValues() {
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 31, 23, 59);

        when(transactionMapper.sumByTypeAndDateRange("INCOME", startDate, endDate))
                .thenReturn(null);
        when(transactionMapper.sumByTypeAndDateRange("EXPENSE", startDate, endDate))
                .thenReturn(null);

        // 执行测试
        SummaryDTO result = transactionService.getSummary(startDate, endDate);

        // 验证：null 值应被处理为 ZERO
        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpense());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void testGetCurrentUserId_NotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        // 执行测试 - 注意：getCurrentUserId 是私有方法，我们只能通过间接测试
        // 当没有认证用户时，createById 和 updateBy 应该是 null
        TransactionDTO dto = createTransactionDTO(
                "INCOME",
                new BigDecimal("1000.00"),
                "工资",
                ASSET_ID,
                null,
                "ASSET_INCREASE"
        );

        TransactionRecord result = transactionService.create(dto);

        assertNull(result.getCreateBy());
        assertNull(result.getUpdateBy());
    }

    // 辅助方法：创建 TransactionDTO
    private TransactionDTO createTransactionDTO(String type, BigDecimal amount, String category,
                                                 Long assetId, Long liabilityId, String accountChangeType) {
        TransactionDTO dto = new TransactionDTO();
        dto.setType(type);
        dto.setAmount(amount);
        dto.setCategory(category);
        dto.setDescription("测试描述");
        dto.setTransactionDate(LocalDateTime.now());
        dto.setAssetId(assetId);
        dto.setLiabilityId(liabilityId);
        dto.setAccountChangeType(accountChangeType);
        return dto;
    }

    // 辅助方法：创建 TransactionRecord
    private TransactionRecord createRecord(Long id, String type, BigDecimal amount) {
        TransactionRecord record = new TransactionRecord();
        record.setId(id);
        record.setType(type);
        record.setAmount(amount);
        record.setCategory("测试分类");
        record.setTransactionDate(LocalDateTime.now());
        return record;
    }
}
