package com.example.aihou.entity;

import com.example.aihou.enumClass.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class User {


    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在 2 到 20 个字符之间")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间")
    // 注意：数据库中存的密码字段长度通常要长一点，因为加密后的字符串很长（比如 BCrypt 加密后通常是 60 位）
    private String password;

    // 邮箱非必填，但只要填了就必须符合邮箱格式
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    @Size(max = 50, message = "昵称不能超过 50 个字符")
    private String nickname;

    @Size(max = 255, message = "头像地址不能超过 255 个字符")
    private String avatar;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Min(value = 0, message = "性别参数错误")
    @Max(value = 2, message = "性别参数错误")
    private Integer gender;

    private String genderDisplayName;

    private String birthday;

    @Min(value = 1, message = "用户类型参数错误")
    @Max(value = 2, message = "用户类型参数错误")
    private Integer userType;

    private String userTypeDisplayName;

    @Min(value = 0, message = "状态参数错误")
    @Max(value = 1, message = "状态参数错误")
    private Integer status;

    private String statusDisplayName;

    @Size(max = 50, message = "展示名称不能超过 50 个字符")
    private String displayName;

    private String createdAt;

    private String updatedAt;
    public boolean isActive(){
        return UserStatus.NORMAL.getCode().equals(this.status);
    }
}