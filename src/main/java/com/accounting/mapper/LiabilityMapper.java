package com.accounting.mapper;

import com.accounting.entity.Liability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LiabilityMapper {
    
    int insert(Liability liability);
    
    int update(Liability liability);
    
    int deleteById(@Param("id") Long id);
    
    Liability selectById(@Param("id") Long id);
    
    List<Liability> selectAll();
    
    BigDecimal sumBalance();
    
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);
}
