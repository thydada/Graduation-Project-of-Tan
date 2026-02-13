package com.example.gpot.repository;

import com.example.gpot.entity.ExceptionPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionPackageRepository extends JpaRepository<ExceptionPackage, Long> {

    /**
     * 根据快递单号查询异常记录
     */
    List<ExceptionPackage> findByTrackingNumber(String trackingNumber);

    /**
     * 根据处理状态查询异常记录
     */
    List<ExceptionPackage> findByHandleStatusOrderByReportTimeDesc(String handleStatus);

    /**
     * 根据报告员工ID查询异常记录
     */
    List<ExceptionPackage> findByReportEmployeeIdOrderByReportTimeDesc(Long reportEmployeeId);

    /**
     * 查询所有异常记录，按报告时间倒序排列
     */
    List<ExceptionPackage> findAllByOrderByReportTimeDesc();

    /**
     * 根据用户ID查询异常记录，按报告时间倒序排列
     */
    List<ExceptionPackage> findByUserIdOrderByReportTimeDesc(Long userId);

    /**
     * 根据用户ID分页查询异常记录，支持按快递单号查询
     */
    @Query("SELECT e FROM ExceptionPackage e WHERE e.userId = :userId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "e.trackingNumber LIKE %:keyword%) " +
           "ORDER BY e.reportTime DESC")
    Page<ExceptionPackage> findByUserIdWithKeyword(@Param("userId") Long userId, 
                                                   @Param("keyword") String keyword, 
                                                   Pageable pageable);

    /**
     * 根据用户ID统计异常记录数量，支持按关键词查询
     */
    @Query("SELECT COUNT(e) FROM ExceptionPackage e WHERE e.userId = :userId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "e.trackingNumber LIKE %:keyword%)")
    long countByUserIdWithKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
