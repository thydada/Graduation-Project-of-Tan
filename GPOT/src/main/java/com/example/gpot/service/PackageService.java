package com.example.gpot.service;

import com.example.gpot.dto.SendPackageRequest;
import com.example.gpot.dto.SendPackageResponse;
import com.example.gpot.entity.Package;
import com.example.gpot.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    /**
     * 创建寄件包裹
     */
    public SendPackageResponse sendPackage(SendPackageRequest request) {
        try {
            // 生成唯一的快递单号
            String trackingNumber = generateTrackingNumber();

            // 创建包裹实体
            Package pkg = new Package(
                trackingNumber,
                request.getSenderName(),
                request.getSenderPhone(),
                request.getSenderAddress(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getReceiverAddress(),
                request.getPackageType(),
                request.getWeight(),
                request.getSize(),
                request.getUserId()
            );

            // 保存到数据库
            Package savedPackage = packageRepository.save(pkg);

            // 返回响应
            return new SendPackageResponse(
                savedPackage.getId(),
                savedPackage.getTrackingNumber(),
                savedPackage.getStatus(),
                savedPackage.getCreateTime(),
                "寄件成功！"
            );

        } catch (Exception e) {
            throw new RuntimeException("寄件失败：" + e.getMessage());
        }
    }

    /**
     * 生成唯一的快递单号
     */
    private String generateTrackingNumber() {
        String trackingNumber;
        do {
            // 生成格式：GPOT + 时间戳 + 随机数
            trackingNumber = "GPOT" + System.currentTimeMillis() +
                           String.format("%03d", (int)(Math.random() * 1000));
        } while (packageRepository.findByTrackingNumber(trackingNumber).isPresent());

        return trackingNumber;
    }

    /**
     * 根据用户ID查询包裹列表
     */
    public List<Package> getPackagesByUserId(Long userId) {
        return packageRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 根据快递单号查询包裹
     */
    public Optional<Package> getPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber);
    }
}