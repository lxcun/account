package com.accounting.service;

import com.accounting.entity.OperationLog;
import com.accounting.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public void save(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
            log.info("操作日志保存成功: 用户={}, 操作={}", operationLog.getUsername(), operationLog.getOperationName());
        } catch (Exception e) {
            log.error("保存操作日志失败: " + e.getMessage(), e);
        }
    }

    public OperationLog getById(Long id) {
        return operationLogMapper.selectById(id);
    }

    public List<OperationLog> getRecentLogs(int limit) {
        return operationLogMapper.selectRecent(limit);
    }

    public List<OperationLog> getLogsByUserId(Long userId) {
        return operationLogMapper.selectByUserId(userId);
    }

    public List<OperationLog> getLogsByModule(String module) {
        return operationLogMapper.selectByModule(module);
    }

    public List<OperationLog> getLogsByOperationType(String operationType) {
        return operationLogMapper.selectByOperationType(operationType);
    }

    public List<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogMapper.selectByTimeRange(startTime, endTime);
    }

    public List<OperationLog> getLogsByUserIdAndModule(Long userId, String module) {
        return operationLogMapper.selectByUserIdAndModule(userId, module);
    }

    public List<OperationLog> getLogsByUserIdAndOperationType(Long userId, String operationType) {
        return operationLogMapper.selectByUserIdAndOperationType(userId, operationType);
    }

    public Long getTotalCount() {
        return operationLogMapper.count();
    }

    public Long getCountByUserId(Long userId) {
        return operationLogMapper.countByUserId(userId);
    }
}
