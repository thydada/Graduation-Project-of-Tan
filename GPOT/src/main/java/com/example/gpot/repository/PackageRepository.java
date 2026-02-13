package com.example.gpot.repository;

import com.example.gpot.entity.Package;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 根据用户ID分页查询包裹，支持按快递单号、收件人姓名、收件人电话查询
     */
    @Query("SELECT p FROM Package p WHERE p.userId = :userId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "p.trackingNumber LIKE %:keyword% OR " +
           "p.receiverName LIKE %:keyword% OR " +
           "p.receiverPhone LIKE %:keyword%) " +
           "ORDER BY p.createTime DESC")
    Page<Package> findByUserIdWithKeyword(@Param("userId") Long userId, 
                                          @Param("keyword") String keyword, 
                                          Pageable pageable);

    /**
     * 根据用户ID统计包裹数量，支持按关键词查询
     */
    @Query("SELECT COUNT(p) FROM Package p WHERE p.userId = :userId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "p.trackingNumber LIKE %:keyword% OR " +
           "p.receiverName LIKE %:keyword% OR " +
           "p.receiverPhone LIKE %:keyword%)")
    long countByUserIdWithKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}