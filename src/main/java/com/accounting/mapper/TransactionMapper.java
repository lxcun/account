package com.accounting.mapper;

import com.accounting.entity.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TransactionMapper {
    
    void insert(TransactionRecord record);
    
    void update(TransactionRecord record);
    
    void deleteById(Long id);
    
    TransactionRecord selectById(Long id);
    
    List<TransactionRecord> selectAll();
    
    List<TransactionRecord> selectByDateRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    BigDecimal sumByTypeAndDateRange(@Param("type") String type,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
