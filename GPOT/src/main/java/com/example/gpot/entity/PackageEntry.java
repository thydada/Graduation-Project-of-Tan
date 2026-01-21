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
@Table(name = "package_entry")
public class PackageEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, comment = "包裹ID")
    private Long packageId;

    @Column(nullable = false, comment = "操作员工ID")
    private Long employeeId;

    @Column(nullable = false, comment = "仓库ID")
    private Long warehouseId;

    @Column(nullable = false, comment = "货架ID")
    private Long shelfId;

    @Column(length = 20, comment = "入库方式(扫码录入,自动分拣)")
    private String entryMethod;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", comment = "入库时间")
    private LocalDateTime entryTime;

    @Column(length = 200, comment = "备注")
    private String remarks;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", comment = "创建时间")
    private LocalDateTime createTime;

    // 自定义构造函数
    public PackageEntry(Long packageId, Long employeeId, Long warehouseId, Long shelfId,
                        String entryMethod, String remarks) {
        this.packageId = packageId;
        this.employeeId = employeeId;
        this.warehouseId = warehouseId;
        this.shelfId = shelfId;
        this.entryMethod = entryMethod;
        this.remarks = remarks;
        this.entryTime = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }
}
