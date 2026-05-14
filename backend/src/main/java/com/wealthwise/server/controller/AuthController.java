package com.wealthwise.server.controller;

import com.wealthwise.server.common.JwtUtil;
import com.wealthwise.server.common.Result;
import com.wealthwise.server.dto.LoginRequest;
import com.wealthwise.server.dto.RegisterRequest;
import com.wealthwise.server.entity.User;
import com.wealthwise.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request.getUsername(), request.getPassword());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            String token = jwtUtil.generateToken(user.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }
}
