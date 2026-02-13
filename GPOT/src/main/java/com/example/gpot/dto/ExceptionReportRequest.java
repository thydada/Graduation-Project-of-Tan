package com.example.gpot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionReportRequest {

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 异常原因
     */
    private String exceptionReason;

    /**
     * 报告员工ID
     */
    private Long employeeId;

    /**
     * 来源：verification（核验异常）
     */
    private String source;
}
