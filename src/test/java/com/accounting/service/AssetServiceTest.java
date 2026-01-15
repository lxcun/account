package com.accounting.service;

import com.accounting.dto.AssetDTO;
import com.accounting.entity.Asset;
import com.accounting.mapper.AssetMapper;
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
 * AssetService 单元测试
 * 测试资产创建、更新、删除以及余额管理逻辑
 */
@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AssetService assetService;

    private static final Long USER_ID = 1L;
    private static final Long ASSET_ID = 1L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateAsset_WithInitialBalance() {
        // 准备测试数据
        AssetDTO dto = new AssetDTO();
        dto.setName("招商银行");
        dto.setType("BANK");
        dto.setBalance(new BigDecimal("10000.00"));

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
        AssetDTO result = assetService.createAsset(dto);

        // 验证：insert 方法被调用
        verify(assetMapper).insert(any(Asset.class));
    }

    @Test
    void testCreateAsset_WithoutInitialBalance() {
        // 准备测试数据：没有初始余额
        AssetDTO dto = new AssetDTO();
        dto.setName("支付宝");
        dto.setType("ALIPAY");

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
        AssetDTO result = assetService.createAsset(dto);

        // 验证：余额应被设置为 0
        verify(assetMapper).insert(argThat(asset ->
                asset.getBalance().equals(BigDecimal.ZERO)
        ));
    }

    @Test
    void testUpdateAsset_Success() {
        // 准备测试数据
        AssetDTO dto = new AssetDTO();
        dto.setId(ASSET_ID);
        dto.setName("工商银行");
        dto.setType("BANK");
        dto.setBalance(new BigDecimal("15000.00"));

        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("10000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);
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
        AssetDTO result = assetService.updateAsset(dto);

        // 验证
        verify(assetMapper).update(any(Asset.class));
        assertEquals("工商银行", result.getName());
        assertEquals(new BigDecimal("15000.00"), result.getBalance());
    }

    @Test
    void testUpdateAsset_AssetNotFound() {
        AssetDTO dto = new AssetDTO();
        dto.setId(999L);
        dto.setName("不存在的资产");

        when(assetMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                assetService.updateAsset(dto)
        );
        assertEquals("资产不存在", exception.getMessage());
        verify(assetMapper, never()).update(any());
    }

    @Test
    void testDeleteAsset_Success() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("10000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试
        assetService.deleteAsset(ASSET_ID);

        // 验证
        verify(assetMapper).deleteById(ASSET_ID);
    }

    @Test
    void testDeleteAsset_AssetNotFound() {
        when(assetMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                assetService.deleteAsset(999L)
        );
        assertEquals("资产不存在", exception.getMessage());
        verify(assetMapper, never()).deleteById(any());
    }

    @Test
    void testGetAllAssets() {
        List<Asset> assets = Arrays.asList(
                createAsset(1L, "招商银行", "BANK", new BigDecimal("10000.00")),
                createAsset(2L, "支付宝", "ALIPAY", new BigDecimal("5000.00")),
                createAsset(3L, "微信", "WECHAT", new BigDecimal("3000.00"))
        );

        when(assetMapper.selectAll()).thenReturn(assets);

        // 执行测试
        List<AssetDTO> result = assetService.getAllAssets();

        // 验证
        assertEquals(3, result.size());
        assertEquals("招商银行", result.get(0).getName());
        assertEquals("支付宝", result.get(1).getName());
        assertEquals("微信", result.get(2).getName());
    }

    @Test
    void testGetAllAssets_Empty() {
        when(assetMapper.selectAll()).thenReturn(Collections.emptyList());

        // 执行测试
        List<AssetDTO> result = assetService.getAllAssets();

        // 验证
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTotalAssets() {
        when(assetMapper.sumBalance()).thenReturn(new BigDecimal("18000.00"));

        // 执行测试
        BigDecimal result = assetService.getTotalAssets();

        // 验证
        assertEquals(new BigDecimal("18000.00"), result);
    }

    @Test
    void testGetTotalAssets_Zero() {
        when(assetMapper.sumBalance()).thenReturn(BigDecimal.ZERO);

        // 执行测试
        BigDecimal result = assetService.getTotalAssets();

        // 验证
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testUpdateBalance_Increase() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("10000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试：增加余额
        assetService.updateBalance(ASSET_ID, new BigDecimal("5000.00"));

        // 验证：余额应为 15000
        verify(assetMapper).updateBalance(eq(ASSET_ID), eq(new BigDecimal("15000.00")));
    }

    @Test
    void testUpdateBalance_Decrease() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("10000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试：减少余额
        assetService.updateBalance(ASSET_ID, new BigDecimal("-3000.00"));

        // 验证：余额应为 7000
        verify(assetMapper).updateBalance(eq(ASSET_ID), eq(new BigDecimal("7000.00")));
    }

    @Test
    void testUpdateBalance_InsufficientFunds() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("1000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试并验证异常：余额不足
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                assetService.updateBalance(ASSET_ID, new BigDecimal("-2000.00"))
        );
        assertEquals("资产余额不足", exception.getMessage());
        verify(assetMapper, never()).updateBalance(any(), any());
    }

    @Test
    void testUpdateBalance_AssetNotFound() {
        when(assetMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                assetService.updateBalance(999L, new BigDecimal("1000.00"))
        );
        assertEquals("资产不存在", exception.getMessage());
        verify(assetMapper, never()).updateBalance(any(), any());
    }

    @Test
    void testUpdateBalance_BecomeZero() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("招商银行");
        existingAsset.setType("BANK");
        existingAsset.setBalance(new BigDecimal("1000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试：余额变为 0
        assetService.updateBalance(ASSET_ID, new BigDecimal("-1000.00"));

        // 验证：余额变为 0 是允许的（使用 argThat 来匹配值为 0 的 BigDecimal）
        verify(assetMapper).updateBalance(eq(ASSET_ID), argThat(balance ->
                balance.compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void testUpdateBalance_FromZero() {
        Asset existingAsset = new Asset();
        existingAsset.setId(ASSET_ID);
        existingAsset.setName("现金");
        existingAsset.setType("CASH");
        existingAsset.setBalance(BigDecimal.ZERO);

        when(assetMapper.selectById(ASSET_ID)).thenReturn(existingAsset);

        // 执行测试：从 0 开始增加余额
        assetService.updateBalance(ASSET_ID, new BigDecimal("500.00"));

        // 验证
        verify(assetMapper).updateBalance(eq(ASSET_ID), eq(new BigDecimal("500.00")));
    }

    @Test
    void testGetAssetById() {
        Asset asset = createAsset(ASSET_ID, "招商银行", "BANK", new BigDecimal("10000.00"));

        when(assetMapper.selectById(ASSET_ID)).thenReturn(asset);

        // 执行测试
        Asset result = assetService.getAssetById(ASSET_ID);

        // 验证
        assertNotNull(result);
        assertEquals(ASSET_ID, result.getId());
        assertEquals("招商银行", result.getName());
        assertEquals("BANK", result.getType());
        assertEquals(new BigDecimal("10000.00"), result.getBalance());
    }

    @Test
    void testGetAssetById_NotFound() {
        when(assetMapper.selectById(999L)).thenReturn(null);

        // 执行测试
        Asset result = assetService.getAssetById(999L);

        // 验证
        assertNull(result);
    }

    @Test
    void testConvertToDTO() {
        Asset asset = createAsset(ASSET_ID, "招商银行", "BANK", new BigDecimal("10000.00"));

        // 执行测试（通过 createAsset 方法间接测试）
        List<Asset> assets = Collections.singletonList(asset);
        when(assetMapper.selectAll()).thenReturn(assets);

        List<AssetDTO> result = assetService.getAllAssets();

        // 验证转换是否正确
        assertEquals(1, result.size());
        assertEquals(ASSET_ID, result.get(0).getId());
        assertEquals("招商银行", result.get(0).getName());
        assertEquals("BANK", result.get(0).getType());
        assertEquals(new BigDecimal("10000.00"), result.get(0).getBalance());
    }

    // 辅助方法：创建 Asset
    private Asset createAsset(Long id, String name, String type, BigDecimal balance) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(name);
        asset.setType(type);
        asset.setBalance(balance);
        return asset;
    }
}
