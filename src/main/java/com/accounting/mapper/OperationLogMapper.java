package com.accounting.mapper;

import com.accounting.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (user_id, username, real_name, operation_type, module, " +
            "operation_name, description, target_id, target_name, request_data, result_data, " +
            "ip, status, error_msg, operation_time) " +
            "VALUES (#{userId}, #{username}, #{realName}, #{operationType}, #{module}, " +
            "#{operationName}, #{description}, #{targetId}, #{targetName}, #{requestData}, " +
            "#{resultData}, #{ip}, #{status}, #{errorMsg}, #{operationTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperationLog operationLog);

    @Select("SELECT * FROM operation_log WHERE id = #{id}")
    OperationLog selectById(Long id);

    @Select("SELECT * FROM operation_log ORDER BY operation_time DESC LIMIT #{limit}")
    List<OperationLog> selectRecent(@Param("limit") int limit);

    @Select("SELECT * FROM operation_log WHERE user_id = #{userId} ORDER BY operation_time DESC")
    List<OperationLog> selectByUserId(Long userId);

    @Select("SELECT * FROM operation_log WHERE module = #{module} ORDER BY operation_time DESC")
    List<OperationLog> selectByModule(String module);

    @Select("SELECT * FROM operation_log WHERE operation_type = #{operationType} ORDER BY operation_time DESC")
    List<OperationLog> selectByOperationType(String operationType);

    @Select("SELECT * FROM operation_log " +
            "WHERE operation_time >= #{startTime} AND operation_time <= #{endTime} " +
            "ORDER BY operation_time DESC")
    List<OperationLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM operation_log " +
            "WHERE user_id = #{userId} AND module = #{module} " +
            "ORDER BY operation_time DESC")
    List<OperationLog> selectByUserIdAndModule(@Param("userId") Long userId,
                                                 @Param("module") String module);

    @Select("SELECT * FROM operation_log " +
            "WHERE user_id = #{userId} AND operation_type = #{operationType} " +
            "ORDER BY operation_time DESC")
    List<OperationLog> selectByUserIdAndOperationType(@Param("userId") Long userId,
                                                        @Param("operationType") String operationType);

    @Select("SELECT COUNT(*) FROM operation_log")
    Long count();

    @Select("SELECT COUNT(*) FROM operation_log WHERE user_id = #{userId}")
    Long countByUserId(Long userId);
}
