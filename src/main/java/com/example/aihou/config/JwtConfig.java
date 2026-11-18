package com.example.aihou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component // 必须交给 Spring 容器管理
@ConfigurationProperties(prefix = "jwt") // 👈 核心注解：告诉 Spring 去配置文件里找 jwt 开头的配置
public class JwtConfig {

    private String secret;
    private Long expiration;
    private Long refreshExpiration; // yml 里叫 refresh-expiration，这里自动转驼峰命名
    private String issuer;
    private String header;
    private String prefix;
}