package com.example.gpot.service;

import com.example.gpot.dto.DebugCreatePackageRequest;
import com.example.gpot.entity.Employee;
import com.example.gpot.entity.ExceptionPackage;
import com.example.gpot.entity.Package;
import com.example.gpot.entity.PackageEntry;
import com.example.gpot.entity.PackageOutbound;
import com.example.gpot.repository.AdminRepository;
import com.example.gpot.repository.EmployeeRepository;
import com.example.gpot.repository.ExceptionPackageRepository;
import com.example.gpot.repository.PackageEntryRepository;
import com.example.gpot.repository.PackageOutboundRepository;
import com.example.gpot.repository.PackageRepository;
import com.example.gpot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackageEntryRepository packageEntryRepository;

    @Autowired
    private ExceptionPackageRepository exceptionPackageRepository;

    @Autowired
    private PackageOutboundRepository packageOutboundRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private MessageService messageService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] PICKUP_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private String generatePickupCode(Long shelfId, Integer shelfLayer) {
        String prefix = String.valueOf(shelfId) + "-" + String.valueOf(shelfLayer) + "-";
        for (int i = 0; i < 20; i++) {
            StringBuilder sb = new StringBuilder(6);
            for (int j = 0; j < 6; j++) {
                sb.append(PICKUP_CODE_CHARS[SECURE_RANDOM.nextInt(PICKUP_CODE_CHARS.length)]);
            }
            String code = prefix + sb.toString();
            if (!packageRepository.existsByPickupCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("生成取件码失败");
    }

    /**
     * 获取所有异常件列表，按报告时间倒序排列
     */
    public List<ExceptionPackage> getAllExceptionPackages() {
        return exceptionPackageRepository.findAllByOrderByReportTimeDesc();
    }

    // ======================== 用户/包裹查询 ========================

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

    /**
     * 根据收件人电话查询已入库的包裹列表
     */
    public List<Package> getPackagesByReceiverPhone(String receiverPhone) {
        return packageRepository.findByReceiverPhoneAndStatusOrderByEntryTimeDesc(receiverPhone, "已入库");
    }


    // ======================== 货架分配相关 ========================

    /**
     * 根据包裹大小自动分配货架和层数
     * @param size 包裹尺寸（格式：长x宽x高，单位cm）
     * @param weight 包裹重量（kg）
     * @param warehouseId 仓库ID
     * @return 包含shelfId和shelfLayer的Map
     */
    private Map<String, Object> allocateShelfAndLayer(String size, BigDecimal weight, Long warehouseId) {
        // 判断包裹大小
        boolean isLargePackage = false;
        
        // 解析尺寸
        if (size != null && !size.trim().isEmpty()) {
            try {
                String[] dimensions = size.split("x");
                if (dimensions.length == 3) {
                    double length = Double.parseDouble(dimensions[0].trim());
                    double width = Double.parseDouble(dimensions[1].trim());
                    double height = Double.parseDouble(dimensions[2].trim());
                    double volume = length * width * height; // 体积（cm³）
                    
                    // 如果体积大于50000 cm³（约0.05立方米）或重量大于5kg，使用大货架
                    if (volume > 50000 || (weight != null && weight.compareTo(new BigDecimal("5")) > 0)) {
                        isLargePackage = true;
                    }
                }
            } catch (Exception e) {
                // 解析失败，默认使用普通货架
            }
        } else if (weight != null && weight.compareTo(new BigDecimal("5")) > 0) {
            // 如果没有尺寸信息，仅根据重量判断
            isLargePackage = true;
        }
        
        Long selectedShelfId;
        Integer selectedLayer;
        
        if (isLargePackage) {
            // 使用大货架（货架4）
            selectedShelfId = 4L;
            selectedLayer = findAvailableLayer(selectedShelfId, warehouseId, 5); // 大货架每层5容量
        } else {
            // 使用普通货架（货架1、2、3），选择使用量最少的货架
            selectedShelfId = findBestNormalShelf(warehouseId);
            selectedLayer = findAvailableLayer(selectedShelfId, warehouseId, 10); // 普通货架每层10容量
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("shelfId", selectedShelfId);
        result.put("shelfLayer", selectedLayer);
        return result;
    }
    
    /**
     * 查找最佳普通货架（使用量最少的）
     */
    private Long findBestNormalShelf(Long warehouseId) {
        List<Long> normalShelfIds = List.of(1L, 2L, 3L);
        Long bestShelfId = 1L;
        int minCount = Integer.MAX_VALUE;
        
        for (Long shelfId : normalShelfIds) {
            int totalCount = 0;
            for (int layer = 1; layer <= 4; layer++) {
                long count = packageRepository.countByShelfIdAndShelfLayerAndStatusNot(
                    shelfId, layer, "已取件");
                totalCount += count;
            }
            
            if (totalCount < minCount) {
                minCount = totalCount;
                bestShelfId = shelfId;
            }
        }
        
        return bestShelfId;
    }
    
    /**
     * 查找指定货架的可用层数
     * @param shelfId 货架ID
     * @param warehouseId 仓库ID
     * @param layerCapacity 每层容量
     * @return 可用层数（1-4）
     */
    private Integer findAvailableLayer(Long shelfId, Long warehouseId, int layerCapacity) {
        int bestLayer = 1;
        int minCount = Integer.MAX_VALUE;
        
        for (int layer = 1; layer <= 4; layer++) {
            long count = packageRepository.countByShelfIdAndShelfLayerAndStatusNot(
                shelfId, layer, "已取件");
            
            if (count < layerCapacity && count < minCount) {
                minCount = (int) count;
                bestLayer = layer;
            }
        }
        
        // 如果所有层都满了，返回第一层
        return bestLayer;
    }


    // ======================== 用户包裹总览 ========================

    /**
     * 获取用户的所有包裹信息（正式包裹、异常包裹）
     */
    public Map<String, Object> getUserAllPackages(Long userId) {
        // 1. 获取用户的正式包裹
        List<Package> formalPackages = getPackagesByUserId(userId);

        // 2. 直接通过用户ID获取用户的异常包裹
        List<ExceptionPackage> exceptionPackages = exceptionPackageRepository.findByUserIdOrderByReportTimeDesc(userId);

        // 构建返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("formalPackages", formalPackages);
        result.put("exceptionPackages", exceptionPackages);

        return result;
    }

    /**
     * 分页获取用户的所有包裹信息（正式包裹、异常包裹），支持查询
     * @param userId 用户ID
     * @param keyword 查询关键词（快递单号、收件人姓名、收件人电话）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param type 包裹类型：all-全部，formal-正式包裹，exception-异常包裹
     * @return 分页结果
     */
    public Map<String, Object> getUserAllPackagesWithPagination(Long userId, String keyword, int page, int size, String type) {
        // 处理空字符串关键词
        String searchKeyword = (keyword != null && keyword.trim().isEmpty()) ? null : keyword;
        
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> result = new HashMap<>();

        if ("formal".equals(type)) {
            // 只查询正式包裹
            Page<Package> formalPage = packageRepository.findByUserIdWithKeyword(userId, searchKeyword, pageable);
            result.put("formalPackages", formalPage.getContent());
            result.put("exceptionPackages", new ArrayList<>());
            result.put("totalElements", formalPage.getTotalElements());
            result.put("totalPages", formalPage.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);
        } else if ("exception".equals(type)) {
            // 只查询异常包裹
            Page<ExceptionPackage> exceptionPage = exceptionPackageRepository.findByUserIdWithKeyword(userId, searchKeyword, pageable);
            result.put("formalPackages", new ArrayList<>());
            result.put("exceptionPackages", exceptionPage.getContent());
            result.put("totalElements", exceptionPage.getTotalElements());
            result.put("totalPages", exceptionPage.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);
        } else {
            // 查询全部：需要合并正式包裹和异常包裹
            // 由于需要合并两种类型，我们需要分别查询然后合并
            // 为了简化，我们分别查询所有数据，然后在内存中分页
            List<Package> allFormalPackages = packageRepository.findByUserIdOrderByCreateTimeDesc(userId);
            List<ExceptionPackage> allExceptionPackages = exceptionPackageRepository.findByUserIdOrderByReportTimeDesc(userId);
            
            // 应用关键词过滤
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String lowerKeyword = searchKeyword.toLowerCase();
                allFormalPackages = allFormalPackages.stream()
                    .filter(p -> p.getTrackingNumber().toLowerCase().contains(lowerKeyword) ||
                                (p.getReceiverName() != null && p.getReceiverName().toLowerCase().contains(lowerKeyword)) ||
                                (p.getReceiverPhone() != null && p.getReceiverPhone().contains(searchKeyword)))
                    .collect(Collectors.toList());
                allExceptionPackages = allExceptionPackages.stream()
                    .filter(e -> e.getTrackingNumber().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            }
            
            // 合并并排序
            List<Map<String, Object>> allPackages = new ArrayList<>();
            for (Package pkg : allFormalPackages) {
                Map<String, Object> pkgMap = new HashMap<>();
                pkgMap.put("id", pkg.getId());
                pkgMap.put("trackingNumber", pkg.getTrackingNumber());
                pkgMap.put("receiverName", pkg.getReceiverName());
                pkgMap.put("receiverPhone", pkg.getReceiverPhone());
                pkgMap.put("receiverAddress", pkg.getReceiverAddress());
                pkgMap.put("packageType", pkg.getPackageType());
                pkgMap.put("weight", pkg.getWeight());
                pkgMap.put("status", pkg.getStatus());
                pkgMap.put("createTime", pkg.getCreateTime());
                pkgMap.put("source", "formal");
                allPackages.add(pkgMap);
            }
            for (ExceptionPackage exPkg : allExceptionPackages) {
                Map<String, Object> pkgMap = new HashMap<>();
                pkgMap.put("id", exPkg.getId());
                pkgMap.put("trackingNumber", exPkg.getTrackingNumber());
                pkgMap.put("receiverName", "-");
                pkgMap.put("receiverPhone", "-");
                pkgMap.put("receiverAddress", "-");
                pkgMap.put("packageType", "-");
                pkgMap.put("weight", "-");
                pkgMap.put("status", "异常：" + (exPkg.getExceptionType() != null ? exPkg.getExceptionType() : "未知"));
                pkgMap.put("createTime", exPkg.getReportTime());
                pkgMap.put("reportTime", exPkg.getReportTime());
                pkgMap.put("exceptionType", exPkg.getExceptionType());
                pkgMap.put("exceptionReason", exPkg.getExceptionReason());
                pkgMap.put("source", "exception");
                allPackages.add(pkgMap);
            }
            
            // 按时间倒序排序
            allPackages.sort((a, b) -> {
                LocalDateTime timeA = (LocalDateTime) (a.get("createTime") != null ? a.get("createTime") : a.get("reportTime"));
                LocalDateTime timeB = (LocalDateTime) (b.get("createTime") != null ? b.get("createTime") : b.get("reportTime"));
                if (timeA == null && timeB == null) return 0;
                if (timeA == null) return 1;
                if (timeB == null) return -1;
                return timeB.compareTo(timeA);
            });
            
            // 内存分页
            int total = allPackages.size();
            int start = page * size;
            int end = Math.min(start + size, total);
            List<Map<String, Object>> pagedPackages = start < total ? allPackages.subList(start, end) : new ArrayList<>();
            
            // 分离正式包裹和异常包裹
            List<Package> formalPackages = new ArrayList<>();
            List<ExceptionPackage> exceptionPackages = new ArrayList<>();
            for (Map<String, Object> pkgMap : pagedPackages) {
                if ("formal".equals(pkgMap.get("source"))) {
                    // 从原始列表中查找对应的Package对象
                    Long pkgId = ((Number) pkgMap.get("id")).longValue();
                    Optional<Package> pkgOpt = packageRepository.findById(pkgId);
                    pkgOpt.ifPresent(formalPackages::add);
                } else {
                    // 从原始列表中查找对应的ExceptionPackage对象
                    Long exPkgId = ((Number) pkgMap.get("id")).longValue();
                    Optional<ExceptionPackage> exPkgOpt = exceptionPackageRepository.findById(exPkgId);
                    exPkgOpt.ifPresent(exceptionPackages::add);
                }
            }
            
            result.put("formalPackages", formalPackages);
            result.put("exceptionPackages", exceptionPackages);
            result.put("totalElements", (long) total);
            result.put("totalPages", (int) Math.ceil((double) total / size));
            result.put("currentPage", page);
            result.put("pageSize", size);
        }

        return result;
    }

    // ======================== 员工端操作：出库/运输 ========================

    /**
     * 获取已入库的包裹列表（供员工出库使用）
     */
    public List<Package> getInStockPackages() {
        return packageRepository.findByStatusOrderByCreateTimeDesc("已入库");
    }

    /**
     * 获取所有包裹列表
     */
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    /**
     * 获取所有待入库的正式包裹列表（供员工在入库页面操作）
     */
    public List<Package> getPendingFormalPackages() {
        return packageRepository.findByStatusOrderByCreateTimeDesc("待入库");
    }

    /**
     * 出库操作：将包裹状态改为运输中，创建出库记录，随机分配派送员工
     */
    @Transactional
    public Map<String, Object> outboundPackage(Long packageId, Long outboundEmployeeId) {
        // 1. 查询包裹
        Optional<Package> packageOpt = packageRepository.findById(packageId);
        if (!packageOpt.isPresent()) {
            throw new RuntimeException("包裹不存在");
        }

        Package pkg = packageOpt.get();

        // 2. 检查包裹状态
        if (!"已入库".equals(pkg.getStatus())) {
            throw new RuntimeException("只有已入库的包裹才能出库");
        }

        // 3. 员工不再区分部门A/B：随机选择任意员工作为派送员工
        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            throw new RuntimeException("没有可用的派送员工");
        }
        Employee deliveryEmployee = allEmployees.get((int)(Math.random() * allEmployees.size()));

        // 4. 更新包裹状态和派送员工
        pkg.setStatus("运输中");
        pkg.setDeliveryEmployeeId(deliveryEmployee.getId());
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        packageRepository.save(pkg);

        // 5. 创建出库记录
        PackageOutbound outbound = new PackageOutbound();
        outbound.setPackageId(packageId);
        outbound.setOutboundEmployeeId(outboundEmployeeId);
        outbound.setDeliveryEmployeeId(deliveryEmployee.getId());
        outbound.setOutboundTime(java.time.LocalDateTime.now());
        PackageOutbound savedOutbound = packageOutboundRepository.save(outbound);

        // 6. 返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("outboundId", savedOutbound.getId());
        result.put("packageId", pkg.getId());
        result.put("trackingNumber", pkg.getTrackingNumber());
        result.put("status", pkg.getStatus());
        result.put("deliveryEmployeeId", deliveryEmployee.getId());
        result.put("deliveryEmployeeName", deliveryEmployee.getRealName());
        result.put("outboundTime", savedOutbound.getOutboundTime());

        return result;
    }

    /**
     * 获取分配给指定员工的运输中包裹列表
     */
    public List<Package> getTransportingPackagesByEmployee(Long employeeId) {
        return packageRepository.findByDeliveryEmployeeIdAndStatusOrderByCreateTimeDesc(employeeId, "运输中");
    }

    /**
     * 送达操作：将包裹状态改为待取件
     */
    @Transactional
    public Map<String, Object> deliverPackage(Long packageId, Long deliveryEmployeeId) {
        // 1. 查询包裹
        Optional<Package> packageOpt = packageRepository.findById(packageId);
        if (!packageOpt.isPresent()) {
            throw new RuntimeException("包裹不存在");
        }

        Package pkg = packageOpt.get();

        // 2. 检查包裹状态和派送员工
        if (!"运输中".equals(pkg.getStatus())) {
            throw new RuntimeException("只有运输中的包裹才能标记为送达");
        }

        if (!deliveryEmployeeId.equals(pkg.getDeliveryEmployeeId())) {
            throw new RuntimeException("该包裹不是分配给您的，无法操作");
        }

        // 3. 更新包裹状态
        pkg.setStatus("待取件");
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        packageRepository.save(pkg);

        // 4. 返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("packageId", pkg.getId());
        result.put("trackingNumber", pkg.getTrackingNumber());
        result.put("status", pkg.getStatus());

        return result;
    }

    /**
     * 用户取件操作：根据快递单号将包裹状态改为已取件（终端机出库使用）
     */
    @Transactional
    public Map<String, Object> userPickupPackage(String trackingNumber) {
        // 1. 根据快递单号查询包裹
        Optional<Package> packageOpt = packageRepository.findByTrackingNumber(trackingNumber);
        if (!packageOpt.isPresent()) {
            throw new RuntimeException("包裹不存在");
        }

        Package pkg = packageOpt.get();

        // 2. 检查包裹状态
        if (!"已入库".equals(pkg.getStatus()) && !"待取件".equals(pkg.getStatus())) {
            throw new RuntimeException("只有已入库或待取件的包裹才能取件");
        }

        // 3. 更新包裹状态
        pkg.setStatus("已取件");
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        packageRepository.save(pkg);

        // 4. 返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("packageId", pkg.getId());
        result.put("trackingNumber", pkg.getTrackingNumber());
        result.put("status", pkg.getStatus());
        result.put("receiverName", pkg.getReceiverName());
        result.put("pickupCode", pkg.getPickupCode());

        return result;
    }

    /**
     * 员工对正式包裹执行入库操作（从待入库 -> 已入库）
     */
    @Transactional
    public Map<String, Object> inboundFormalPackage(Long packageId, Long employeeId, Long warehouseId,
                                                    Long shelfId, Integer shelfLayer) {
        Optional<Package> packageOpt = packageRepository.findById(packageId);
        if (!packageOpt.isPresent()) {
            throw new RuntimeException("包裹不存在");
        }

        Package pkg = packageOpt.get();
        if (!"待入库".equals(pkg.getStatus())) {
            throw new RuntimeException("只有待入库状态的包裹才能执行入库操作");
        }

        if (warehouseId == null) {
            warehouseId = 1L;
        }

        // 自动/手动分配货架与层数
        if (shelfId == null) {
            Map<String, Object> allocation = allocateShelfAndLayer(
                    pkg.getSize(),
                    pkg.getWeight(),
                    warehouseId
            );
            shelfId = ((Number) allocation.get("shelfId")).longValue();
            if (shelfLayer == null) {
                shelfLayer = (Integer) allocation.get("shelfLayer");
            }
        } else {
            if (shelfLayer == null) {
                shelfLayer = findAvailableLayer(
                        shelfId,
                        warehouseId,
                        shelfId == 4L ? 5 : 10
                );
            }
        }

        String pickupCode = generatePickupCode(shelfId, shelfLayer);

        // 更新正式包裹信息
        pkg.setStatus("已入库");
        pkg.setWarehouseId(warehouseId);
        pkg.setShelfId(shelfId);
        pkg.setShelfLayer(shelfLayer);
        pkg.setPickupCode(pickupCode);
        pkg.setEntryEmployeeId(employeeId);
        pkg.setEntryTime(java.time.LocalDateTime.now());
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        Package savedPackage = packageRepository.save(pkg);

        // 记录入库流水
        PackageEntry entryRecord = new PackageEntry(
                savedPackage.getId(),
                employeeId,
                warehouseId,
                shelfId,
                shelfLayer,
                "扫码录入",
                "正式包裹入库（待入库 -> 已入库）"
        );
        packageEntryRepository.save(entryRecord);

        // 发送取件提醒消息（如果用户ID存在）
        if (savedPackage.getUserId() != null) {
            try {
                messageService.sendPickupNotification(
                    savedPackage.getUserId(),
                    savedPackage.getTrackingNumber(),
                    pickupCode,
                    shelfId,
                    shelfLayer,
                    warehouseId,
                    "employee",
                    employeeId
                );
            } catch (Exception e) {
                // 消息发送失败不影响入库流程
                System.err.println("发送取件提醒消息失败: " + e.getMessage());
            }
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("packageId", savedPackage.getId());
        result.put("trackingNumber", savedPackage.getTrackingNumber());
        result.put("status", savedPackage.getStatus());
        result.put("warehouseId", warehouseId);
        result.put("shelfId", shelfId);
        result.put("shelfLayer", shelfLayer);
        result.put("pickupCode", pickupCode);
        result.put("entryTime", savedPackage.getEntryTime());
        result.put("message", "包裹已入库");
        return result;
    }

    // ======================== Debug 调试：直接创建正式包裹 ========================

    /**
     * 生成唯一的快递单号 - 检查正式表
     */
    private String generateDebugTrackingNumber() {
        String trackingNumber;
        do {
            trackingNumber = "DBG" + System.currentTimeMillis() +
                    String.format("%03d", (int) (Math.random() * 1000));
        } while (packageRepository.findByTrackingNumber(trackingNumber).isPresent());
        return trackingNumber;
    }

    /**
     * Debug 调试：直接往 package 表写入一条包裹记录
     */
    public Package debugCreatePackage(DebugCreatePackageRequest request) {
        Package pkg = new Package();
        pkg.setTrackingNumber(generateDebugTrackingNumber());
        pkg.setSenderName(request.getSenderName());
        pkg.setSenderPhone(request.getSenderPhone());
        pkg.setSenderAddress(request.getSenderAddress());
        pkg.setReceiverName(request.getReceiverName());
        pkg.setReceiverPhone(request.getReceiverPhone());
        pkg.setReceiverAddress(request.getReceiverAddress());
        pkg.setPackageType(request.getPackageType());
        pkg.setWeight(request.getWeight());
        pkg.setSize(request.getSize());
        pkg.setUserId(request.getUserId());
        pkg.setStatus(request.getStatus() == null || request.getStatus().trim().isEmpty()
                ? "待入库"
                : request.getStatus().trim());
        pkg.setCreateTime(java.time.LocalDateTime.now());
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        return packageRepository.save(pkg);
    }

    /**
     * 报告正式包裹异常件
     * 将正式包裹标记为异常，并写入异常件表
     */
    @Transactional
    public Map<String, Object> reportFormalPackageException(Long packageId, String exceptionType,
                                                           String exceptionReason, Long employeeId, String source) {
        Optional<Package> packageOpt = packageRepository.findById(packageId);
        if (!packageOpt.isPresent()) {
            throw new RuntimeException("包裹不存在");
        }

        Package pkg = packageOpt.get();

        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        String employeeName = employeeOpt.isPresent() ? employeeOpt.get().getRealName() : "员工" + employeeId;

        ExceptionPackage exceptionPkg = new ExceptionPackage();
        exceptionPkg.setPackageId(packageId);
        exceptionPkg.setTrackingNumber(pkg.getTrackingNumber());
        exceptionPkg.setExceptionType(exceptionType);
        exceptionPkg.setExceptionReason(exceptionReason);
        exceptionPkg.setReportEmployeeId(employeeId);
        exceptionPkg.setReportEmployeeName(employeeName);
        exceptionPkg.setHandleStatus("待处理");
        exceptionPkg.setSource(source);
        exceptionPkg.setUserId(pkg.getUserId());
        exceptionPkg.setReportTime(java.time.LocalDateTime.now());
        exceptionPkg.setUpdateTime(java.time.LocalDateTime.now());

        ExceptionPackage savedException = exceptionPackageRepository.save(exceptionPkg);

        pkg.setStatus("异常");
        pkg.setUpdateTime(java.time.LocalDateTime.now());
        packageRepository.save(pkg);

        Map<String, Object> result = new HashMap<>();
        result.put("exceptionId", savedException.getId());
        result.put("packageId", packageId);
        result.put("trackingNumber", savedException.getTrackingNumber());
        result.put("exceptionType", savedException.getExceptionType());
        result.put("handleStatus", savedException.getHandleStatus());

        return result;
    }

    /**
     * 获取管理员统计数据
     */
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalPackages = packageRepository.count();
        long pendingInbound = packageRepository.findByStatusOrderByCreateTimeDesc("待入库").size();
        long inStock = packageRepository.findByStatusOrderByCreateTimeDesc("已入库").size();
        long inTransit = packageRepository.findByStatusOrderByCreateTimeDesc("运输中").size();
        long delivered = packageRepository.findByStatusOrderByCreateTimeDesc("已取件").size();
        long exception = packageRepository.findByStatusOrderByCreateTimeDesc("异常").size();

        long totalExceptionPackages = exceptionPackageRepository.count();
        long pendingException = exceptionPackageRepository.findByHandleStatusOrderByReportTimeDesc("待处理").size();
        long processingException = exceptionPackageRepository.findByHandleStatusOrderByReportTimeDesc("处理中").size();
        long completedException = exceptionPackageRepository.findByHandleStatusOrderByReportTimeDesc("已处理").size();

        long totalUsers = userRepository.count();
        long totalEmployees = employeeRepository.count();
        long totalAdmins = adminRepository.count();

        long totalEntries = packageEntryRepository.count();
        long totalOutbounds = packageOutboundRepository.count();

        stats.put("totalPackages", totalPackages);
        stats.put("pendingInbound", pendingInbound);
        stats.put("inStock", inStock);
        stats.put("inTransit", inTransit);
        stats.put("delivered", delivered);
        stats.put("exception", exception);
        stats.put("totalExceptionPackages", totalExceptionPackages);
        stats.put("pendingException", pendingException);
        stats.put("processingException", processingException);
        stats.put("completedException", completedException);
        stats.put("totalUsers", totalUsers);
        stats.put("totalEmployees", totalEmployees);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalEntries", totalEntries);
        stats.put("totalOutbounds", totalOutbounds);

        return stats;
    }

    /**
     * 获取近几日入库统计
     */
    public List<Map<String, Object>> getDailyEntryStatistics(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        List<PackageEntry> entries = packageEntryRepository.findAll();
        
        Map<String, Long> dailyCounts = entries.stream()
            .filter(entry -> {
                LocalDateTime entryTime = entry.getEntryTime();
                if (entryTime == null) return false;
                LocalDate entryDate = entryTime.toLocalDate();
                return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
            })
            .collect(Collectors.groupingBy(
                entry -> entry.getEntryTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateLabel = date.format(DateTimeFormatter.ofPattern("MM/dd"));
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateStr);
            dayData.put("dateLabel", dateLabel);
            dayData.put("count", dailyCounts.getOrDefault(dateStr, 0L));
            result.add(dayData);
        }
        
        return result;
    }
}