package com.example.gpot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendPackageResponse {

    private Long packageId;
    private String trackingNumber;
    private String status;
    private LocalDateTime createTime;
    private String message;
}