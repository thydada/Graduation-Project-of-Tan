package com.example.gpot.repository;

import com.example.gpot.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByTrackingNumber(String trackingNumber);

    List<Package> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Package> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据状态查询包裹列表
     */
    List<Package> findByStatusOrderByCreateTimeDesc(String status);

    /**
     * 根据派送员工ID和状态查询包裹列表
     */
    List<Package> findByDeliveryEmployeeIdAndStatusOrderByCreateTimeDesc(Long deliveryEmployeeId, String status);
}