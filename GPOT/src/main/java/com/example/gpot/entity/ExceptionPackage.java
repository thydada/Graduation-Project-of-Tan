package com.example.gpot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exception_package")
public class ExceptionPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, comment = "包裹ID（临时包裹ID）")
    private Long tempPackageId;

    @Column(nullable = false, length = 50, comment = "快递单号")
    private String trackingNumber;

    @Column(nullable = false, length = 50, comment = "异常类型")
    private String exceptionType;

    @Column(length = 200, comment = "异常原因")
    private String exceptionReason;

    @Column(nullable = false, comment = "报告员工ID")
    private Long reportEmployeeId;

    @Column(length = 50, comment = "报告员工姓名")
    private String reportEmployeeName;

    @Column(comment = "处理员工ID")
    private Long handleEmployeeId;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT '待处理'")
    private String handleStatus;

    @Column(length = 200, comment = "处理结果")
    private String handleResult;

    @Column(length = 20, comment = "异常来源（pickup:取件异常, verification:核验异常）")
    private String source;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime reportTime;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime handleTime;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    // 自定义构造函数
    public ExceptionPackage(Long tempPackageId, String trackingNumber, String exceptionType,
                           String exceptionReason, Long reportEmployeeId, String reportEmployeeName, String source) {
        this.tempPackageId = tempPackageId;
        this.trackingNumber = trackingNumber;
        this.exceptionType = exceptionType;
        this.exceptionReason = exceptionReason;
        this.reportEmployeeId = reportEmployeeId;
        this.reportEmployeeName = reportEmployeeName;
        this.handleStatus = "待处理";
        this.source = source;
        this.reportTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
