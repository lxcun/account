package com.accounting.mapper;

import com.accounting.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AssetMapper {
    
    int insert(Asset asset);
    
    int update(Asset asset);
    
    int deleteById(@Param("id") Long id);
    
    Asset selectById(@Param("id") Long id);
    
    List<Asset> selectAll();
    
    BigDecimal sumBalance();
    
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);
}
