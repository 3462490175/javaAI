package com.example.aihou.DTO.response;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class UserLoginResponseDTO {
    private String token;
    private String roleType;
    private Userinfo userInfo;
    @Builder
    @Data
    public static class Userinfo {
        private long id;
        private String username;
        private String email;

        private String nickname;
        private String avatar;
        private String phone;
        private Integer gender;
        private String genderDisplayName;
        private String birthday;
        private Integer userType;
        private String userTypeDisplayName;
        private Integer status;
        private String statusDisplayName;
        private String displayName;
        private String createdAt;
        private String updatedAt;

    }
}
