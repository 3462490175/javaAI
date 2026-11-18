package com.example.aihou.service.convert;

import com.example.aihou.DTO.command.UserRegisterCommandDTO;
import com.example.aihou.DTO.response.UserLoginResponseDTO;
import com.example.aihou.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserConvert {
    // 只保留这一个方法
    public static UserLoginResponseDTO.Userinfo convert(User user) {
        if (user == null) {
            return null;
        }
        return UserLoginResponseDTO.Userinfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(user.getGenderDisplayName())
                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // 👈 加上了第二个参数 encodedPassword
    public static User registerCommandToEntity(UserRegisterCommandDTO commandDTO, String encodedPassword) {
        return User.builder()
                .username(commandDTO.getUsername())
                .email(commandDTO.getEmail())
                .password(encodedPassword) //
                .nickname(commandDTO.getNickname())
                .phone(commandDTO.getPhone())
                .gender(commandDTO.getGender())
                .birthday(commandDTO.getBirthday())
                .userType(commandDTO.getUserType())
                .status(1) //
                .build();
    }
    /**
     * 将 User 实体类转换为前端展示的详细响应对象
     * @param user 数据库查出来的用户实体
     * @return 过滤了敏感信息后的展示对象
     */
    public static UserLoginResponseDTO.Userinfo entityToDetailResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserLoginResponseDTO.Userinfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .gender(user.getGender())
                .genderDisplayName(user.getGenderDisplayName()) // 如果数据库没存，这里可能是null
                .birthday(user.getBirthday())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        // ⚠️ 注意：这里故意没有把 password 塞进去，这就是脱敏！
    }
}