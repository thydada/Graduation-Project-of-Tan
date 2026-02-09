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

    /**
     * 根据货架ID和层数查询包裹数量
     */
    long countByShelfIdAndShelfLayerAndStatusNot(Long shelfId, Integer shelfLayer, String status);

    boolean existsByPickupCode(String pickupCode);

    /**
     * 根据收件人电话查询已入库的包裹列表
     */
    List<Package> findByReceiverPhoneAndStatusOrderByEntryTimeDesc(String receiverPhone, String status);
}