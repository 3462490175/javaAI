package com.example.aihou.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aihou.DTO.command.UserLoginCommandDTO;
import com.example.aihou.DTO.command.UserRegisterCommandDTO;
import com.example.aihou.DTO.response.UserLoginResponseDTO;
import com.example.aihou.common.Result;
import com.example.aihou.common.ResultCode;
import com.example.aihou.exception.BusinessException;
import com.example.aihou.mapper.UserMapper;
import com.example.aihou.service.UserService;
import com.example.aihou.service.convert.UserConvert;
import com.example.aihou.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")

public class User {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")

    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO) {
        return userService.login(commandDTO);
    }

    @PostMapping("/register")
    // 用户注册接口

    public Result<UserLoginResponseDTO.Userinfo> register(@Valid @RequestBody UserRegisterCommandDTO commandDTO) {
        UserLoginResponseDTO.Userinfo result = userService.register(commandDTO);
        return Result.success(result);

    }

    @GetMapping("/current")
    public Result<UserLoginResponseDTO.Userinfo> getCurrentUser() {
        // 如何从token中解析出用户的id
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = jwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        UserLoginResponseDTO.Userinfo result = userService.getUserById(userId);
        return Result.success(result);
    }
    }
