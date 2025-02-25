package com.example.demo.constant;

public class AppConstants {
    public static final String[] GRADES = {
        "初一", "初二", "初三",
        "高一", "高二", "高三"
    };

    public static final String[] SUBJECTS = {
        "数学", "语文", "外语", "物理", "历史",
        "地理", "生物", "化学", "体育", "艺术", "政治"
    };

    public static final class ValidationMessages {
        public static final String USERNAME_NOT_BLANK = "用户名不能为空";
        public static final String PASSWORD_NOT_BLANK = "密码不能为空";
        public static final String EMAIL_INVALID = "邮箱格式不正确";
        public static final String PHONE_INVALID = "手机号必须为11位数字";
    }

    public static final class SecurityConstants {
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";
        public static final long TOKEN_EXPIRATION = 864_000_000; // 10 days
    }
} 