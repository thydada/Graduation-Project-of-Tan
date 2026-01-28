package com.example.gpot.repository;

import com.example.gpot.entity.ExceptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionPackageRepository extends JpaRepository<ExceptionPackage, Long> {

    /**
     * 根据临时包裹ID查询异常记录
     */
    List<ExceptionPackage> findByTempPackageId(Long tempPackageId);

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
}
