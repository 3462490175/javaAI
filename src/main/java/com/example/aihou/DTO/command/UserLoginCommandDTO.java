package com.example.aihou.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginCommandDTO {
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max=20,message="输入过长")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(max=20,min=6,message="密码在6到20个字符之间")
    private String password;
}
