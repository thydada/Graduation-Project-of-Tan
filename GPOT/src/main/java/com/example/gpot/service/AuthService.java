package com.example.gpot.service;

import com.example.gpot.entity.Admin;
import com.example.gpot.entity.Employee;
import com.example.gpot.entity.User;
import com.example.gpot.repository.AdminRepository;
import com.example.gpot.repository.EmployeeRepository;
import com.example.gpot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 管理员登录验证
     */
    public Admin loginAdmin(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsernameAndPassword(username, password);
        return adminOpt.orElse(null);
    }

    /**
     * 员工登录验证
     */
    public Employee loginEmployee(String username, String password) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsernameAndPassword(username, password);
        return employeeOpt.orElse(null);
    }

    /**
     * 用户登录验证
     */
    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(username, password);
        return userOpt.orElse(null);
    }

    /**
     * 根据用户名检查管理员是否存在
     */
    public boolean adminExists(String username) {
        return adminRepository.findByUsername(username).isPresent();
    }

    /**
     * 根据用户名检查员工是否存在
     */
    public boolean employeeExists(String username) {
        return employeeRepository.findByUsername(username).isPresent();
    }

    /**
     * 根据用户名检查用户是否存在
     */
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 用户注册
     */
    public User registerUser(String username, String password, String realName) {
        // 检查用户名是否已存在
        if (userExists(username)) {
            return null;
        }

        // 创建新用户
        User newUser = new User(username, password, realName);
        return userRepository.save(newUser);
    }
}