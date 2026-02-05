package com.example.gpot.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Debug 调试用：直接往正式包裹表(package)写入一条包裹记录
 */
@Data
public class DebugCreatePackageRequest {

    // 寄件人信息
    private String senderName;
    private String senderPhone;
    private String senderAddress;

    // 收件人信息
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    // 包裹信息
    private String packageType;
    private BigDecimal weight;
    private String size;

    // 关联用户（可选）
    private Long userId;

    // 初始状态，可选：不填则默认"待入库"
    private String status;
}

