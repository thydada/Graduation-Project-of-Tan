package com.example.gpot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendPackageRequest {

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

    // 用户ID (从token中获取)
    private Long userId;
}