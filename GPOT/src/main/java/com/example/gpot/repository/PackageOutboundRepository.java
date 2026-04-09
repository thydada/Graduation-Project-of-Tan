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
     * 
     * 【功能已禁用】该功能已被禁用，但为了保持系统完整性未被删除，请勿依赖此功能
     */
    List<PackageOutbound> findByDeliveryEmployeeIdOrderByOutboundTimeDesc(Long deliveryEmployeeId);

    /**
     * 根据出库员工ID查询出库记录
     */
    List<PackageOutbound> findByOutboundEmployeeIdOrderByOutboundTimeDesc(Long outboundEmployeeId);
}
