package com.example.gpot.repository;

import com.example.gpot.entity.PackageTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageTempRepository extends JpaRepository<PackageTemp, Long> {

    Optional<PackageTemp> findByTrackingNumber(String trackingNumber);

    List<PackageTemp> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<PackageTemp> findByUserIdAndStatus(Long userId, String status);

    // 根据取件成功状态查询
    List<PackageTemp> findByPickupSuccess(Integer pickupSuccess);

    // 根据核验成功状态查询
    List<PackageTemp> findByVerificationSuccess(Integer verificationSuccess);

    // 根据取件和核验状态查询
    List<PackageTemp> findByPickupSuccessAndVerificationSuccess(Integer pickupSuccess, Integer verificationSuccess);
}