package com.example.gpot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "package_temp")
public class PackageTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String trackingNumber;

    @Column(length = 50)
    private String senderName;

    @Column(length = 20)
    private String senderPhone;

    @Column(length = 200)
    private String senderAddress;

    @Column(length = 50)
    private String receiverName;

    @Column(length = 20)
    private String receiverPhone;

    @Column(length = 200)
    private String receiverAddress;

    @Column(length = 20)
    private String packageType;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(length = 50)
    private String size;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT '待入库'")
    private String status;

    @Column
    private Long warehouseId;

    @Column(name = "shelf_id")
    private Long shelfId;

    @Column(name = "shelf_layer")
    private Integer shelfLayer;

    @Column
    private Long entryEmployeeId;

    @Column
    private LocalDateTime entryTime;

    @Column
    private Long userId;

    @Column
    private LocalDateTime pickupDeadline;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer pickupSuccess;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer verificationSuccess;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    // 自定义构造函数 - 用户寄件时使用
    public PackageTemp(String trackingNumber, String senderName, String senderPhone, String senderAddress,
                      String receiverName, String receiverPhone, String receiverAddress, String packageType,
                      BigDecimal weight, String size, Long userId) {
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.senderAddress = senderAddress;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.packageType = packageType;
        this.weight = weight;
        this.size = size;
        this.userId = userId;
        this.status = "待入库";
        this.pickupSuccess = 0; // 默认未取件
        this.verificationSuccess = 0; // 默认未核验
        this.createTime = LocalDateTime.now();
    }
}