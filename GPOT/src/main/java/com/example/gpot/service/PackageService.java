package com.example.gpot.service;

import com.example.gpot.dto.SendPackageRequest;
import com.example.gpot.dto.SendPackageResponse;
import com.example.gpot.entity.Employee;
import com.example.gpot.entity.ExceptionPackage;
import com.example.gpot.entity.Package;
import com.example.gpot.entity.PackageEntry;
import com.example.gpot.entity.PackageOutbound;
import com.example.gpot.entity.PackageTemp;
import com.example.gpot.repository.EmployeeRepository;
import com.example.gpot.repository.ExceptionPackageRepository;
import com.example.gpot.repository.PackageEntryRepository;
import com.example.gpot.repository.PackageOutboundRepository;
import com.example.gpot.repository.PackageRepository;
import com.example.gpot.repository.PackageTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackageTempRepository packageTempRepository;

    @Autowired
    private PackageEntryRepository packageEntryRepository;

    @Autowired
    private ExceptionPackageRepository exceptionPackageRepository;

    @Autowired
    private PackageOutboundRepository packageOutboundRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * 获取所有异常件列表，按报告时间倒序排列
     */
    public List<ExceptionPackage> getAllExceptionPackages() {
        return exceptionPackageRepository.findAllByOrderByReportTimeDesc();
    }

    /**
     * 创建寄件包裹 - 先保存到临时表
     */
    public SendPackageResponse sendPackage(SendPackageRequest request) {
        try {
            // 生成唯一的快递单号
            String trackingNumber = generateTrackingNumber();

            // 创建临时包裹实体
            PackageTemp tempPackage = new PackageTemp(
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

            // 保存到临时表
            PackageTemp savedTempPackage = packageTempRepository.save(tempPackage);

            // 返回响应
            return new SendPackageResponse(
                savedTempPackage.getId(),
                savedTempPackage.getTrackingNumber(),
                savedTempPackage.getStatus(),
                savedTempPackage.getCreateTime(),
                "寄件成功！包裹已提交，等待取件和核验。"
            );

        } catch (Exception e) {
            throw new RuntimeException("寄件失败：" + e.getMessage());
        }
    }

    /**
     * 生成唯一的快递单号 - 检查临时表和正式表
     */
    private String generateTrackingNumber() {
        String trackingNumber;
        do {
            // 生成格式：GPOT + 时间戳 + 随机数
            trackingNumber = "GPOT" + System.currentTimeMillis() +
                           String.format("%03d", (int)(Math.random() * 1000));
        } while (packageRepository.findByTrackingNumber(trackingNumber).isPresent() ||
                 packageTempRepository.findByTrackingNumber(trackingNumber).isPresent());

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

    /**
     * 根据用户ID查询临时包裹列表
     */
    public List<PackageTemp> getTempPackagesByUserId(Long userId) {
        return packageTempRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 根据快递单号查询临时包裹
     */
    public Optional<PackageTemp> getTempPackageByTrackingNumber(String trackingNumber) {
        return packageTempRepository.findByTrackingNumber(trackingNumber);
    }

    /**
     * 将临时包裹转移到正式表（取件和核验都成功后调用）
     */
    public Package transferTempPackageToFormal(Long tempPackageId) {
        Optional<PackageTemp> tempPackageOpt = packageTempRepository.findById(tempPackageId);
        if (!tempPackageOpt.isPresent()) {
            throw new RuntimeException("临时包裹不存在");
        }

        PackageTemp tempPackage = tempPackageOpt.get();

        // 检查是否满足转移条件
        if (tempPackage.getPickupSuccess() != 1 || tempPackage.getVerificationSuccess() != 1) {
            throw new RuntimeException("包裹未完成取件或核验，无法转移到正式表");
        }

        // 创建正式包裹
        Package formalPackage = new Package();
        formalPackage.setTrackingNumber(tempPackage.getTrackingNumber());
        formalPackage.setSenderName(tempPackage.getSenderName());
        formalPackage.setSenderPhone(tempPackage.getSenderPhone());
        formalPackage.setSenderAddress(tempPackage.getSenderAddress());
        formalPackage.setReceiverName(tempPackage.getReceiverName());
        formalPackage.setReceiverPhone(tempPackage.getReceiverPhone());
        formalPackage.setReceiverAddress(tempPackage.getReceiverAddress());
        formalPackage.setPackageType(tempPackage.getPackageType());
        formalPackage.setWeight(tempPackage.getWeight());
        formalPackage.setSize(tempPackage.getSize());
        formalPackage.setStatus(tempPackage.getStatus());
        formalPackage.setWarehouseId(tempPackage.getWarehouseId());
        formalPackage.setShelfId(tempPackage.getShelfId());
        formalPackage.setEntryEmployeeId(tempPackage.getEntryEmployeeId());
        formalPackage.setEntryTime(tempPackage.getEntryTime());
        formalPackage.setUserId(tempPackage.getUserId());
        formalPackage.setPickupDeadline(tempPackage.getPickupDeadline());

        // 保存到正式表
        Package savedFormalPackage = packageRepository.save(formalPackage);

        // 删除临时表中的记录
        packageTempRepository.delete(tempPackage);

        return savedFormalPackage;
    }

    /**
     * 更新临时包裹的取件状态
     */
    public void updatePickupSuccess(Long tempPackageId, boolean success) {
        Optional<PackageTemp> tempPackageOpt = packageTempRepository.findById(tempPackageId);
        if (tempPackageOpt.isPresent()) {
            PackageTemp tempPackage = tempPackageOpt.get();
            tempPackage.setPickupSuccess(success ? 1 : 0);
            packageTempRepository.save(tempPackage);
        }
    }

    /**
     * 更新临时包裹的核验状态
     */
    public void updateVerificationSuccess(Long tempPackageId, boolean success) {
        Optional<PackageTemp> tempPackageOpt = packageTempRepository.findById(tempPackageId);
        if (tempPackageOpt.isPresent()) {
            PackageTemp tempPackage = tempPackageOpt.get();
            tempPackage.setVerificationSuccess(success ? 1 : 0);
            packageTempRepository.save(tempPackage);
        }
    }

    /**
     * 核验成功后转移包裹到正式表并创建入库记录
     * @param tempPackageId 临时包裹ID
     * @param employeeId 操作员工ID
     * @param warehouseId 仓库ID（默认1）
     * @param shelfId 货架ID（默认1）
     * @return 核验结果信息
     */
    @Transactional
    public Map<String, Object> verificationAndTransferPackage(Long tempPackageId, Long employeeId, Long warehouseId, Long shelfId) {
        // 1. 查询临时包裹
        Optional<PackageTemp> tempPackageOpt = packageTempRepository.findById(tempPackageId);
        if (!tempPackageOpt.isPresent()) {
            throw new RuntimeException("临时包裹不存在");
        }

        PackageTemp tempPackage = tempPackageOpt.get();

        // 3. 更新核验状态为成功
        tempPackage.setVerificationSuccess(1);
        // 更新status字段
        if (tempPackage.getPickupSuccess() == 1) {
            tempPackage.setStatus("审核完成");
        } else {
            tempPackage.setStatus("待取件");
        }
        tempPackage.setUpdateTime(java.time.LocalDateTime.now());
        packageTempRepository.save(tempPackage);

        // 4. 创建正式包裹并保存到package表
        Package formalPackage = new Package();
        formalPackage.setTrackingNumber(tempPackage.getTrackingNumber());
        formalPackage.setSenderName(tempPackage.getSenderName());
        formalPackage.setSenderPhone(tempPackage.getSenderPhone());
        formalPackage.setSenderAddress(tempPackage.getSenderAddress());
        formalPackage.setReceiverName(tempPackage.getReceiverName());
        formalPackage.setReceiverPhone(tempPackage.getReceiverPhone());
        formalPackage.setReceiverAddress(tempPackage.getReceiverAddress());
        formalPackage.setPackageType(tempPackage.getPackageType());
        formalPackage.setWeight(tempPackage.getWeight());
        formalPackage.setSize(tempPackage.getSize());
        formalPackage.setStatus("已入库");
        formalPackage.setWarehouseId(warehouseId);
        formalPackage.setShelfId(shelfId);
        formalPackage.setEntryEmployeeId(employeeId);
        formalPackage.setEntryTime(java.time.LocalDateTime.now());
        formalPackage.setUserId(tempPackage.getUserId());
        formalPackage.setPickupDeadline(tempPackage.getPickupDeadline());
        formalPackage.setCreateTime(java.time.LocalDateTime.now());

        Package savedPackage = packageRepository.save(formalPackage);

        // 5. 创建入库记录到package_entry表
        PackageEntry entryRecord = new PackageEntry(
            savedPackage.getId(),
            employeeId,
            warehouseId,
            shelfId,
            "扫码录入",
            "核验成功后自动入库"
        );
        packageEntryRepository.save(entryRecord);

        // 6. 从临时表删除该条记录
        packageTempRepository.delete(tempPackage);

        // 7. 返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("packageId", savedPackage.getId());
        result.put("trackingNumber", savedPackage.getTrackingNumber());
        result.put("status", savedPackage.getStatus());
        result.put("warehouseId", warehouseId);
        result.put("shelfId", shelfId);
        result.put("entryTime", savedPackage.getEntryTime());
        result.put("message", "核验成功，包裹已入库");

        return result;
    }

    /**
     * 获取用户的所有包裹信息（临时包裹、正式包裹、异常包裹）
     */
    public Map<String, Object> getUserAllPackages(Long userId) {
        // 1. 获取用户的临时包裹
        List<PackageTemp> tempPackages = getTempPackagesByUserId(userId);

        // 2. 获取用户的正式包裹
        List<Package> formalPackages = getPackagesByUserId(userId);

        // 3. 直接通过用户ID获取用户的异常包裹
        List<ExceptionPackage> exceptionPackages = exceptionPackageRepository.findByUserIdOrderByReportTimeDesc(userId);

        // 构建返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("tempPackages", tempPackages);
        result.put("formalPackages", formalPackages);
        result.put("exceptionPackages", exceptionPackages);

        return result;
    }

    /**
     * 获取已入库的包裹列表（供员工B出库使用）
     */
    public List<Package> getInStockPackages() {
        return packageRepository.findByStatusOrderByCreateTimeDesc("已入库");
    }

    /**
     * 出库操作：将包裹状态改为运输中，创建出库记录，随机分配给员工A
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

        // 3. 随机选择一个部门A的员工
        List<Employee> departmentAEmployees = employeeRepository.findByDepartment("A");
        if (departmentAEmployees.isEmpty()) {
            throw new RuntimeException("没有可用的派送员工（部门A）");
        }

        // 随机选择
        Employee deliveryEmployee = departmentAEmployees.get((int)(Math.random() * departmentAEmployees.size()));

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
     * 获取分配给指定员工的运输中包裹列表（供员工A使用）
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
}