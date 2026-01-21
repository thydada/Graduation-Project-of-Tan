package com.example.gpot.repository;

import com.example.gpot.entity.PackageEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageEntryRepository extends JpaRepository<PackageEntry, Long> {

    /**
     * 根据包裹ID查询入库记录
     */
    List<PackageEntry> findByPackageId(Long packageId);

    /**
     * 根据员工ID查询入库记录
     */
    List<PackageEntry> findByEmployeeIdOrderByEntryTimeDesc(Long employeeId);

    /**
     * 根据仓库ID查询入库记录
     */
    List<PackageEntry> findByWarehouseIdOrderByEntryTimeDesc(Long warehouseId);

    /**
     * 根据货架ID查询入库记录
     */
    List<PackageEntry> findByShelfIdOrderByEntryTimeDesc(Long shelfId);
}
