package com.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志查询条件")
public class OperationLogQueryDTO {
    @Schema(description = "操作人ID", example = "1")
    private Long userId;

    @Schema(description = "模块", example = "ASSET", allowableValues = {"ASSET", "LIABILITY", "TRANSACTION", "OTHER"})
    private String module;

    @Schema(description = "操作类型", example = "CREATE", allowableValues = {"CREATE", "UPDATE", "DELETE", "OTHER"})
    private String operationType;

    @Schema(description = "开始时间", example = "2026-01-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2026-01-31T23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "查询数量限制", example = "100")
    private Integer limit = 100;
}
