package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.dto.DebugCreatePackageRequest;
import com.example.gpot.dto.ExceptionReportRequest;
import com.example.gpot.dto.FormalPackageExceptionRequest;
import com.example.gpot.entity.ExceptionPackage;
import com.example.gpot.entity.Package;
import com.example.gpot.entity.PackageTemp;
import com.example.gpot.repository.ExceptionPackageRepository;
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

    @Autowired
    private ExceptionPackageRepository exceptionPackageRepository;

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
     * 根据收件人电话查询已入库的包裹列表（终端机查询使用）
     */
    @GetMapping("/packages/phone/{receiverPhone}")
    public ResponseEntity<ApiResponse<List<Package>>> getPackagesByReceiverPhone(@PathVariable String receiverPhone) {
        try {
            List<Package> packages = packageService.getPackagesByReceiverPhone(receiverPhone);
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
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
     * @param request 包含员工ID、仓库ID、货架ID的可选参数
     */
    @PutMapping("/packages/temp/{id}/verification")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificationPackage(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer status = (Integer) request.get("status");
            if (status == null || (status != 1 && status != 2)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("核验状态无效，必须为1（核验成功）或2（核验出错）"));
            }

            Optional<PackageTemp> optionalPackage = packageTempRepository.findById(id);
            if (!optionalPackage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // 如果核验成功，需要执行完整的转移逻辑
            if (status == 1) {
                // 获取员工ID（默认1）
                Long employeeId = 1L;
                if (request.get("employeeId") != null) {
                    employeeId = ((Number) request.get("employeeId")).longValue();
                }

                // 获取仓库ID（可为空；新入库包裹不再默认分配仓库/货架）
                Long warehouseId = null;
                if (request.get("warehouseId") != null) {
                    warehouseId = ((Number) request.get("warehouseId")).longValue();
                }

                // 获取货架ID（可为空）
                Long shelfId = null;
                if (request.get("shelfId") != null) {
                    shelfId = ((Number) request.get("shelfId")).longValue();
                }

                // 获取货架层数（可为空）
                Integer shelfLayer = null;
                if (request.get("shelfLayer") != null) {
                    shelfLayer = ((Number) request.get("shelfLayer")).intValue();
                }

                // 执行核验成功后的转移逻辑
                Map<String, Object> result = packageService.verificationAndTransferPackage(
                    id, employeeId, warehouseId, shelfId, shelfLayer
                );

                return ResponseEntity.ok(ApiResponse.success("核验成功，包裹已入库", result));
            } else {
                // 核验失败，只更新状态
                PackageTemp pkg = optionalPackage.get();
                pkg.setVerificationSuccess(status);
                pkg.setStatus("核验异常");
                pkg.setUpdateTime(LocalDateTime.now());
                packageTempRepository.save(pkg);

                Map<String, Object> result = new HashMap<>();
                result.put("id", pkg.getId());
                result.put("trackingNumber", pkg.getTrackingNumber());
                result.put("verificationSuccess", pkg.getVerificationSuccess());

                return ResponseEntity.ok(ApiResponse.success("已标记为核验出错", result));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("核验过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 报告异常件
     * 将包裹写入异常件表，并从临时快递表中删除
     */
    @PostMapping("/packages/temp/report-exception")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reportException(@RequestBody ExceptionReportRequest request) {
        try {
            // 验证输入
            if (request.getTempPackageId() == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("包裹ID不能为空"));
            }
            if (request.getExceptionType() == null || request.getExceptionType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("异常类型不能为空"));
            }
            // 入库流程改为仅一次核验：只允许 verification 来源
            if (request.getSource() == null || !"verification".equals(request.getSource())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("异常来源无效，必须为 verification"));
            }

            // 查询临时包裹
            Optional<PackageTemp> optionalPackage = packageTempRepository.findById(request.getTempPackageId());
            if (!optionalPackage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PackageTemp pkg = optionalPackage.get();

            // 获取员工ID（默认1）
            Long employeeId = request.getEmployeeId() != null ? request.getEmployeeId() : 1L;

            // 创建异常件记录，保存用户ID
            ExceptionPackage exceptionPkg = new ExceptionPackage(
                pkg.getId(),
                pkg.getTrackingNumber(),
                request.getExceptionType(),
                request.getExceptionReason(),
                employeeId,
                "员工" + employeeId, // 简化处理，实际应该查询员工姓名
                request.getSource(),
                pkg.getUserId() // 保存用户ID
            );
            exceptionPackageRepository.save(exceptionPkg);

            // 从临时表中删除该包裹
            packageTempRepository.delete(pkg);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("exceptionId", exceptionPkg.getId());
            result.put("trackingNumber", exceptionPkg.getTrackingNumber());
            result.put("exceptionType", exceptionPkg.getExceptionType());
            result.put("handleStatus", exceptionPkg.getHandleStatus());

            return ResponseEntity.ok(ApiResponse.success("已记录为核验异常件", result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("报告异常件过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * Debug：直接往正式包裹表写入一条包裹记录
     * 仅供开发调试使用
     */
    @PostMapping("/debug/packages")
    public ResponseEntity<ApiResponse<Package>> debugCreatePackage(@RequestBody DebugCreatePackageRequest request) {
        try {
            // 简单必填校验（核心字段）
            if (request.getSenderName() == null || request.getSenderName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("寄件人姓名不能为空"));
            }
            if (request.getSenderPhone() == null || request.getSenderPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("寄件人电话不能为空"));
            }
            if (request.getReceiverName() == null || request.getReceiverName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("收件人姓名不能为空"));
            }
            if (request.getReceiverPhone() == null || request.getReceiverPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("收件人电话不能为空"));
            }
            if (request.getPackageType() == null || request.getPackageType().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("包裹类型不能为空"));
            }

            Package pkg = packageService.debugCreatePackage(request);
            return ResponseEntity.ok(ApiResponse.success("Debug 包裹创建成功", pkg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Debug 创建包裹过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取所有异常件列表
     */
    @GetMapping("/exception-packages")
    public ResponseEntity<ApiResponse<List<ExceptionPackage>>> getAllExceptionPackages() {
        try {
            List<ExceptionPackage> exceptions = packageService.getAllExceptionPackages();
            return ResponseEntity.ok(ApiResponse.success("查询成功", exceptions));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取用户的所有包裹信息（临时包裹、正式包裹、异常包裹）
     */
    @GetMapping("/packages/user/{userId}/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAllPackages(@PathVariable Long userId) {
        try {
            Map<String, Object> result = packageService.getUserAllPackages(userId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取已入库的包裹列表（供员工B出库使用）
     */
    @GetMapping("/packages/in-stock")
    public ResponseEntity<ApiResponse<List<Package>>> getInStockPackages() {
        try {
            List<Package> packages = packageService.getInStockPackages();
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取待入库的正式包裹列表（package 表中 status=待入库）
     */
    @GetMapping("/packages/pending-inbound")
    public ResponseEntity<ApiResponse<List<Package>>> getPendingInboundPackages() {
        try {
            List<Package> packages = packageService.getPendingFormalPackages();
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取所有包裹列表（员工B查看全部）
     */
    @GetMapping("/packages/all")
    public ResponseEntity<ApiResponse<List<Package>>> getAllPackages() {
        try {
            List<Package> packages = packageService.getAllPackages();
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 出库操作（员工B）
     */
    @PostMapping("/packages/{packageId}/outbound")
    public ResponseEntity<ApiResponse<Map<String, Object>>> outboundPackage(
            @PathVariable Long packageId,
            @RequestBody Map<String, Long> request) {
        try {
            Long outboundEmployeeId = request.get("employeeId");
            if (outboundEmployeeId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("员工ID不能为空"));
            }

            Map<String, Object> result = packageService.outboundPackage(packageId, outboundEmployeeId);
            return ResponseEntity.ok(ApiResponse.success("出库成功", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("出库过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 正式包裹入库操作（从待入库 -> 已入库）
     */
    @PutMapping("/packages/{packageId}/inbound")
    public ResponseEntity<ApiResponse<Map<String, Object>>> inboundFormalPackage(
            @PathVariable Long packageId,
            @RequestBody Map<String, Object> request) {
        try {
            Long employeeId = null;
            if (request.get("employeeId") != null) {
                employeeId = ((Number) request.get("employeeId")).longValue();
            }
            if (employeeId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("员工ID不能为空"));
            }

            Long warehouseId = null;
            if (request.get("warehouseId") != null) {
                warehouseId = ((Number) request.get("warehouseId")).longValue();
            }

            Long shelfId = null;
            if (request.get("shelfId") != null) {
                shelfId = ((Number) request.get("shelfId")).longValue();
            }

            Integer shelfLayer = null;
            if (request.get("shelfLayer") != null) {
                shelfLayer = ((Number) request.get("shelfLayer")).intValue();
            }

            Map<String, Object> result = packageService.inboundFormalPackage(
                    packageId, employeeId, warehouseId, shelfId, shelfLayer
            );
            return ResponseEntity.ok(ApiResponse.success("入库成功", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("入库过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取分配给指定员工的运输中包裹列表（供员工A使用）
     */
    @GetMapping("/packages/transporting/{employeeId}")
    public ResponseEntity<ApiResponse<List<Package>>> getTransportingPackages(@PathVariable Long employeeId) {
        try {
            List<Package> packages = packageService.getTransportingPackagesByEmployee(employeeId);
            return ResponseEntity.ok(ApiResponse.success("查询成功", packages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 送达操作（员工A）
     */
    @PostMapping("/packages/{packageId}/deliver")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deliverPackage(
            @PathVariable Long packageId,
            @RequestBody Map<String, Long> request) {
        try {
            Long deliveryEmployeeId = request.get("employeeId");
            if (deliveryEmployeeId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("员工ID不能为空"));
            }

            Map<String, Object> result = packageService.deliverPackage(packageId, deliveryEmployeeId);
            return ResponseEntity.ok(ApiResponse.success("送达成功", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("送达过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 获取系统统计数据（管理员监控大屏使用）
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStatistics() {
        try {
            Map<String, Object> statistics = packageService.getAdminStatistics();
            return ResponseEntity.ok(ApiResponse.success("查询成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取统计数据失败：" + e.getMessage()));
        }
    }

    /**
     * 获取近几日入库统计（管理员监控大屏使用）
     */
    @GetMapping("/admin/daily-entry-statistics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyEntryStatistics(@RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> statistics = packageService.getDailyEntryStatistics(days);
            return ResponseEntity.ok(ApiResponse.success("查询成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取每日入库统计失败：" + e.getMessage()));
        }
    }

    /**
     * 报告正式包裹异常件
     * 将正式包裹标记为异常，并写入异常件表
     */
    @PostMapping("/packages/{packageId}/report-exception")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reportFormalPackageException(
            @PathVariable Long packageId,
            @RequestBody FormalPackageExceptionRequest request) {
        try {
            if (request.getExceptionType() == null || request.getExceptionType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("异常类型不能为空"));
            }

            Long employeeId = request.getEmployeeId() != null ? request.getEmployeeId() : 1L;
            String source = request.getSource() != null ? request.getSource() : "inbound";

            Map<String, Object> result = packageService.reportFormalPackageException(
                packageId,
                request.getExceptionType(),
                request.getExceptionReason(),
                employeeId,
                source
            );
            return ResponseEntity.ok(ApiResponse.success("已记录为异常件", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("报告异常件过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 用户取件操作（终端机出库使用）
     * 根据快递单号将包裹状态改为已取件
     */
    @PostMapping("/packages/pickup/{trackingNumber}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userPickupPackage(@PathVariable String trackingNumber) {
        try {
            Map<String, Object> result = packageService.userPickupPackage(trackingNumber);
            return ResponseEntity.ok(ApiResponse.success("取件成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("取件失败：" + e.getMessage()));
        }
    }
}