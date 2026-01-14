package com.accounting.service;

import com.accounting.annotation.OperationLog;
import com.accounting.annotation.OperationModule;
import com.accounting.annotation.OperationType;
import com.accounting.dto.AssetDTO;
import com.accounting.entity.Asset;
import com.accounting.mapper.AssetMapper;
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
public class AssetService {

    private final AssetMapper assetMapper;

    public AssetService(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
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
     * 创建资产
     */
    @Transactional
    @OperationLog(value = "创建资产", operationType = OperationType.CREATE, module = OperationModule.ASSET)
    public AssetDTO createAsset(AssetDTO assetDTO) {
        Asset asset = new Asset();
        BeanUtils.copyProperties(assetDTO, asset);
        if (asset.getBalance() == null) {
            asset.setBalance(BigDecimal.ZERO);
        }
        Long currentUserId = getCurrentUserId();
        asset.setCreateBy(currentUserId);
        asset.setUpdateBy(currentUserId);
        assetMapper.insert(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }

    /**
     * 更新资产
     */
    @Transactional
    @OperationLog(value = "更新资产", operationType = OperationType.UPDATE, module = OperationModule.ASSET)
    public AssetDTO updateAsset(AssetDTO assetDTO) {
        Asset existAsset = assetMapper.selectById(assetDTO.getId());
        if (existAsset == null) {
            throw new RuntimeException("资产不存在");
        }

        Asset asset = new Asset();
        BeanUtils.copyProperties(assetDTO, asset);
        Long currentUserId = getCurrentUserId();
        asset.setUpdateBy(currentUserId);
        assetMapper.update(asset);
        return assetDTO;
    }

    /**
     * 删除资产
     */
    @Transactional
    @OperationLog(value = "删除资产", operationType = OperationType.DELETE, module = OperationModule.ASSET)
    public void deleteAsset(Long assetId) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        assetMapper.deleteById(assetId);
    }

    /**
     * 获取所有资产
     */
    public List<AssetDTO> getAllAssets() {
        List<Asset> assets = assetMapper.selectAll();
        return assets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取资产总额
     */
    public BigDecimal getTotalAssets() {
        return assetMapper.sumBalance();
    }

    /**
     * 更新资产余额（内部方法，供交易使用）
     */
    @Transactional
    public void updateBalance(Long assetId, BigDecimal amount) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        BigDecimal newBalance = asset.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("资产余额不足");
        }
        assetMapper.updateBalance(assetId, newBalance);
    }

    /**
     * 获取资产信息
     */
    public Asset getAssetById(Long assetId) {
        return assetMapper.selectById(assetId);
    }

    private AssetDTO convertToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);
        return dto;
    }
}
