package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.dto.DebugCreatePackageRequest;
import com.example.gpot.dto.FormalPackageExceptionRequest;
import com.example.gpot.entity.ExceptionPackage;
import com.example.gpot.entity.Package;
import com.example.gpot.service.PackageService;
import com.example.gpot.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private AiService aiService;

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
     * 获取用户的所有包裹信息（正式包裹、异常包裹）
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
     * 分页获取用户的所有包裹信息（正式包裹、异常包裹），支持查询
     * @param userId 用户ID
     * @param keyword 查询关键词（快递单号、收件人姓名、收件人电话）
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param type 包裹类型：all-全部，formal-正式包裹，exception-异常包裹（默认all）
     */
    @GetMapping("/packages/user/{userId}/all/paged")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAllPackagesWithPagination(
            @PathVariable Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String type) {
        try {
            Map<String, Object> result = packageService.getUserAllPackagesWithPagination(userId, keyword, page, size, type);
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
     * 
     * 【功能已禁用】该功能已被禁用，但为了保持系统完整性未被删除，请勿依赖此功能
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
     * 
     * 【功能已禁用】该功能已被禁用，但为了保持系统完整性未被删除，请勿依赖此功能
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
     * 获取近几日出库统计（管理员监控大屏使用）
     */
    @GetMapping("/admin/daily-outbound-statistics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyOutboundStatistics(@RequestParam(defaultValue = "3") int days) {
        try {
            List<Map<String, Object>> statistics = packageService.getDailyOutboundStatistics(days);
            return ResponseEntity.ok(ApiResponse.success("查询成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取每日出库统计失败：" + e.getMessage()));
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
     * 根据快递单号和取件码将包裹状态改为已取件
     */
    @PostMapping("/packages/pickup/{trackingNumber}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userPickupPackage(
            @PathVariable String trackingNumber,
            @RequestBody Map<String, String> request) {
        try {
            String pickupCode = request.get("pickupCode");
            Map<String, Object> result = packageService.userPickupPackage(trackingNumber, pickupCode);
            return ResponseEntity.ok(ApiResponse.success("取件成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("取件失败：" + e.getMessage()));
        }
    }

    /**
     * 导出运维监控数据到根目录下的GPOT-DATA文件夹
     * 将数据保存为.txt文件到项目根目录/GPOT-DATA文件夹
     */
    @PostMapping("/admin/export-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportAdminData(@RequestBody Map<String, Object> exportData) {
        try {
            // 获取项目根目录
            File rootDir = new File(System.getProperty("user.dir"));
            if (!rootDir.exists()) {
                rootDir = new File(".");
            }
            
            // 创建或获取GPOT-DATA文件夹
            File dataDir = new File(rootDir, "GPOT-DATA");
            if (!dataDir.exists()) {
                if (!dataDir.mkdirs()) {
                    return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("创建GPOT-DATA文件夹失败"));
                }
            }
            
            // 生成文件名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = "GPOT_运维监控数据_" + now.format(formatter) + ".txt";
            File exportFile = new File(dataDir, fileName);
            
            // 获取导出内容
            String content = (String) exportData.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("导出内容不能为空"));
            }
            
            // 写入文件
            try (FileWriter writer = new FileWriter(exportFile, StandardCharsets.UTF_8)) {
                writer.write(content);
            }
            
            // 调用AI服务生成分析
            String aiAnalysis = aiService.generateAnalysis(content);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("fileName", fileName);
            result.put("filePath", exportFile.getAbsolutePath());
            result.put("message", "数据已成功保存到GPOT-DATA文件夹");
            result.put("aiAnalysis", aiAnalysis);
            
            return ResponseEntity.ok(ApiResponse.success("导出成功", result));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("保存文件失败：" + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("导出过程中发生错误：" + e.getMessage()));
        }
    }
}