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
}