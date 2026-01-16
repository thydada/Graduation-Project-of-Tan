package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.dto.LoginRequest;
import com.example.gpot.dto.LoginResponse;
import com.example.gpot.dto.RegisterRequest;
import com.example.gpot.entity.Admin;
import com.example.gpot.entity.Employee;
import com.example.gpot.entity.User;
import com.example.gpot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    /**
     * REST API登录接口
     */
    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String userType = loginRequest.getUserType();
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            switch (userType) {
                case "admin":
                    Admin admin = authService.loginAdmin(username, password);
                    if (admin != null) {
                        LoginResponse response = new LoginResponse(true, "管理员登录成功！",
                            userType, admin.getId(), admin.getRealName(), admin.getUsername(), admin);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;

                case "employee":
                    Employee employee = authService.loginEmployee(username, password);
                    if (employee != null) {
                        LoginResponse response = new LoginResponse(true, "员工登录成功！",
                            userType, employee.getId(), employee.getRealName(), employee.getUsername(), employee);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;

                case "user":
                    User user = authService.loginUser(username, password);
                    if (user != null) {
                        LoginResponse response = new LoginResponse(true, "用户登录成功！",
                            userType, user.getId(), user.getRealName(), user.getUsername(), user);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;
            }

            return ResponseEntity.badRequest()
                .body(ApiResponse.error(userType + "用户名或密码错误！"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("登录过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * REST API注册接口
     */
    @PostMapping("/api/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String username = registerRequest.getUsername();
            String password = registerRequest.getPassword();
            String realName = registerRequest.getRealName();

            // 验证输入
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名不能为空"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("密码不能为空"));
            }
            if (realName == null || realName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("真实姓名不能为空"));
            }

            // 检查用户名是否已存在
            if (authService.userExists(username)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名已存在"));
            }

            // 注册用户
            User newUser = authService.registerUser(username, password, realName);
            if (newUser != null) {
                LoginResponse response = new LoginResponse(true, "注册成功！",
                    "user", newUser.getId(), newUser.getRealName(), newUser.getUsername(), newUser);
                return ResponseEntity.ok(ApiResponse.success("注册成功", response));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("注册失败"));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("注册过程中发生错误：" + e.getMessage()));
        }
    }

}