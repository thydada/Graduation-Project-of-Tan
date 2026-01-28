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
@Table(name = "package_outbound")
public class PackageOutbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, comment = "包裹ID")
    private Long packageId;

    @Column(nullable = false, comment = "出库员工ID（员工B）")
    private Long outboundEmployeeId;

    @Column(nullable = false, comment = "派送员工ID（员工A）")
    private Long deliveryEmployeeId;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime outboundTime;

    @Column(length = 200, comment = "备注")
    private String remarks;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime;
}
