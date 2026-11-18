package com.example.aihou.DTO.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterCommandDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在 2 到 20 个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    // 邮箱非必填，但只要填了就必须符合格式
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    // 手机号非必填，只要填了就必须符合格式
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 50, message = "昵称不能超过 50 个字符")
    private String nickname;

    // 选填，如果不填默认给 0 (未知)
    @Min(value = 0, message = "性别参数错误")
    @Max(value = 2, message = "性别参数错误")
    private Integer gender = 0;

    // 选填，默认普通用户
    @Min(value = 1, message = "用户类型参数错误")
    @Max(value = 2, message = "用户类型参数错误")
    private Integer userType = 1;

    // 👇 下面是根据你的实体类补充的字段（选填，注册时用户可以传，也可以不传）

    @Size(max = 255, message = "头像地址不能超过 255 个字符")
    private String avatar;

    // 生日格式：yyyy-MM-dd
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "生日格式必须为 yyyy-MM-dd")
    private String birthday;
}