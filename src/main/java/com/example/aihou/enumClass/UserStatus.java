package com.example.aihou.enumClass;



import lombok.Getter;

@Getter
public enum UserStatus {


    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    private final Integer code;
    private final String description;

    // 构造方法
    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 💡 企业级常用方法：根据 code 获取对应的中文描述
     * @param code 状态码（0 或 1）
     * @return 中文描述
     */
    public static String getDescriptionByCode(Integer code) {
        if (code == null) {
            return "未知";
        }
        // 遍历所有枚举项
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知";
    }
}