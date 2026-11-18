package com.example.aihou.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aihou.DTO.command.UserLoginCommandDTO;
import com.example.aihou.DTO.command.UserRegisterCommandDTO;
import com.example.aihou.DTO.response.UserLoginResponseDTO;
import com.example.aihou.common.Result;
import com.example.aihou.entity.User;
import com.example.aihou.exception.BusinessException;
import com.example.aihou.mapper.UserMapper;
import com.example.aihou.service.convert.UserConvert;
import com.example.aihou.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 登录
    public Result<UserLoginResponseDTO> login(UserLoginCommandDTO commandDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, commandDTO.getUsername())
                .or()
                .eq(User::getEmail, commandDTO.getUsername());

        User user = userMapper.selectOne(queryWrapper);
        System.out.println("查询到的用户：" + user);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String inputpassword = commandDTO.getPassword().trim();
        if (!passwordEncoder.matches(inputpassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (!user.isActive()) {
            throw new BusinessException("用户已被禁用");
        }


        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        System.out.println("生成的 Token：" + token);

        UserLoginResponseDTO.Userinfo userinfo = UserConvert.convert(user);

        UserLoginResponseDTO responseDTO = UserLoginResponseDTO.builder()
                .token(token)
                .roleType(String.valueOf(user.getUserType()))
                .userInfo(userinfo)
                .build();

        return Result.success(responseDTO);
    }

    // 注册
    public UserLoginResponseDTO.Userinfo register(UserRegisterCommandDTO commandDTO) {
        System.out.println(JSONUtil.parseObj(commandDTO));
        if (!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, commandDTO.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException("该用户名已被注册");
        }

        if (commandDTO.getEmail() != null && !commandDTO.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, commandDTO.getEmail());
            if (userMapper.selectCount(emailWrapper) > 0) {
                throw new BusinessException("该邮箱已被注册");
            }
        }

        String password = commandDTO.getPassword().trim();
        String encodedPassword = passwordEncoder.encode(password);
        User user = UserConvert.registerCommandToEntity(commandDTO, encodedPassword);
        userMapper.insert(user);

        return UserConvert.entityToDetailResponse(user);
    }

    // 👈 这里的点删掉了
    public UserLoginResponseDTO.Userinfo getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }
}