package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.dto.SendPackageRequest;
import com.example.gpot.dto.SendPackageResponse;
import com.example.gpot.entity.Package;
import com.example.gpot.entity.PackageTemp;
import com.example.gpot.repository.PackageTempRepository;
import com.example.gpot.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private PackageTempRepository packageTempRepository;

    /**
     * 用户寄件接口
     */
    @PostMapping("/send-package")
    public ResponseEntity<ApiResponse<SendPackageResponse>> sendPackage(@RequestBody SendPackageRequest request) {
        try {
            // 验证输入
            if (request.getSenderName() == null || request.getSenderName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("寄件人姓名不能为空"));
            }
            if (request.getSenderPhone() == null || request.getSenderPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("寄件人电话不能为空"));
            }
            if (request.getSenderAddress() == null || request.getSenderAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("寄件人地址不能为空"));
            }
            if (request.getReceiverName() == null || request.getReceiverName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("收件人姓名不能为空"));
            }
            if (request.getReceiverPhone() == null || request.getReceiverPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("收件人电话不能为空"));
            }
            if (request.getReceiverAddress() == null || request.getReceiverAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("收件人地址不能为空"));
            }
            if (request.getPackageType() == null || request.getPackageType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("包裹类型不能为空"));
            }
            if (request.getWeight() == null || request.getWeight().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("包裹重量必须大于0"));
            }
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户ID不能为空"));
            }

            // 调用服务创建包裹
            SendPackageResponse response = packageService.sendPackage(request);
            return ResponseEntity.ok(ApiResponse.success("寄件成功", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("寄件过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 根据用户ID查询包裹列表
     */
    @GetMapping("/packages/user/{userId}")
    public ResponseEntity<ApiResponse<List<Package>>> getPackagesByUserId(@PathVariable Long userId) {
        try {
            List<Package> packages = packageService.getPackagesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 根据快递单号查询包裹
     */
    @GetMapping("/packages/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<Package>> getPackageByTrackingNumber(@PathVariable String trackingNumber) {
        try {
            Optional<Package> pkg = packageService.getPackageByTrackingNumber(trackingNumber);
            if (pkg.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("查询成功", pkg.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 查询所有待取件的临时快递（pickup_success=0）
     */
    @GetMapping("/packages/temp/pending")
    public ResponseEntity<ApiResponse<List<PackageTemp>>> getPendingPackages() {
        try {
            List<PackageTemp> packages = packageTempRepository.findByPickupSuccess(0);
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 审核快递取件情况
     * @param id 快递ID
     * @param status 审核状态：1-已取件，2-取件出错
     */
    @PutMapping("/packages/temp/{id}/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyPackage(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            if (status == null || (status != 1 && status != 2)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("审核状态无效，必须为1（已取件）或2（取件出错）"));
            }

            Optional<PackageTemp> optionalPackage = packageTempRepository.findById(id);
            if (!optionalPackage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PackageTemp pkg = optionalPackage.get();
            pkg.setPickupSuccess(status);
            pkg.setUpdateTime(LocalDateTime.now());
            packageTempRepository.save(pkg);

            Map<String, Object> result = new HashMap<>();
            result.put("id", pkg.getId());
            result.put("trackingNumber", pkg.getTrackingNumber());
            result.put("pickupSuccess", pkg.getPickupSuccess());

            String message = status == 1 ? "已标记为已取件" : "已标记为取件出错";
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("审核过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 查询所有待核验的临时快递（verification_success=0）
     */
    @GetMapping("/packages/temp/verification-pending")
    public ResponseEntity<ApiResponse<List<PackageTemp>>> getVerificationPendingPackages() {
        try {
            List<PackageTemp> packages = packageTempRepository.findByVerificationSuccess(0);
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 核验快递
     * @param id 快递ID
     * @param status 核验状态：1-核验成功，2-核验出错
     */
    @PutMapping("/packages/temp/{id}/verification")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificationPackage(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            if (status == null || (status != 1 && status != 2)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("核验状态无效，必须为1（核验成功）或2（核验出错）"));
            }

            Optional<PackageTemp> optionalPackage = packageTempRepository.findById(id);
            if (!optionalPackage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PackageTemp pkg = optionalPackage.get();
            pkg.setVerificationSuccess(status);
            pkg.setUpdateTime(LocalDateTime.now());
            packageTempRepository.save(pkg);

            Map<String, Object> result = new HashMap<>();
            result.put("id", pkg.getId());
            result.put("trackingNumber", pkg.getTrackingNumber());
            result.put("verificationSuccess", pkg.getVerificationSuccess());

            String message = status == 1 ? "已核验成功" : "已标记为核验出错";
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("核验过程中发生错误：" + e.getMessage()));
        }
    }
}