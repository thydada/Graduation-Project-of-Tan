package com.example.gpot.repository;

import com.example.gpot.entity.PackageOutbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageOutboundRepository extends JpaRepository<PackageOutbound, Long> {

    /**
     * 根据包裹ID查询出库记录
     */
    List<PackageOutbound> findByPackageId(Long packageId);

    /**
     * 根据派送员工ID查询出库记录
     */
    List<PackageOutbound> findByDeliveryEmployeeIdOrderByOutboundTimeDesc(Long deliveryEmployeeId);

    /**
     * 根据出库员工ID查询出库记录
     */
    List<PackageOutbound> findByOutboundEmployeeIdOrderByOutboundTimeDesc(Long outboundEmployeeId);
}
