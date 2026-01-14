package com.accounting.aspect;

import com.accounting.annotation.OperationLog;
import com.accounting.annotation.OperationModule;
import com.accounting.annotation.OperationType;
import com.accounting.security.CustomUserDetails;
import com.accounting.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ObjectMapper objectMapper;
    private final OperationLogService operationLogService;

    @Pointcut("@annotation(com.accounting.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    @Around("operationLogPointcut() && @annotation(operationLogAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, com.accounting.annotation.OperationLog operationLogAnnotation) throws Throwable {
        com.accounting.entity.OperationLog operationLog = new com.accounting.entity.OperationLog();

        try {
            // 获取用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                operationLog.setUserId(userDetails.getId());
                operationLog.setUsername(userDetails.getUsername());
            }

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIp(getClientIp(request));
            }

            // 设置操作信息
            OperationType operationType = operationLogAnnotation.operationType();
            OperationModule module = operationLogAnnotation.module();
            operationLog.setOperationType(operationType != null ? operationType.name() : OperationType.OTHER.name());
            operationLog.setModule(module != null ? module.name() : OperationModule.OTHER.name());
            operationLog.setOperationName(operationLogAnnotation.value());

            // 记录请求数据
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    operationLog.setRequestData(objectMapper.writeValueAsString(args));
                } catch (Exception e) {
                    operationLog.setRequestData(Arrays.toString(args));
                }
            }

            // 执行方法
            Object result = joinPoint.proceed();

            // 记录结果数据
            if (result != null) {
                try {
                    operationLog.setResultData(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    operationLog.setResultData(result.toString());
                }
            }

            operationLog.setStatus("SUCCESS");
            operationLog.setOperationTime(LocalDateTime.now());

            // 保存日志
            saveOperationLog(operationLog);

            return result;
        } catch (Exception e) {
            operationLog.setStatus("FAILED");
            operationLog.setErrorMsg(e.getMessage());
            operationLog.setOperationTime(LocalDateTime.now());
            saveOperationLog(operationLog);
            throw e;
        }
    }

    private void saveOperationLog(com.accounting.entity.OperationLog operationLog) {
        try {
            log.info("操作日志: 用户={}, 模块={}, 操作={}, 状态={}",
                    operationLog.getUsername(),
                    operationLog.getModule(),
                    operationLog.getOperationName(),
                    operationLog.getStatus());
            operationLogService.save(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

