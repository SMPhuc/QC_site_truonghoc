package com.mcp.site_truonghoc.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "src/test/java/com/mcp/site_truonghoc/config/urls.properties";
    private static final Properties properties = new Properties();
    private static String environment;

    static {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
            environment = properties.getProperty("environment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEnvironment() {
        return environment;
    }

    public static void setEnvironment(String env) {
        if (!env.equals("product") && !env.equals("uat")) {
            throw new IllegalArgumentException("Môi trường không hợp lệ. Chỉ chấp nhận 'product' hoặc 'uat'");
        }
        environment = env;
        System.out.println("🔄 Đang sử dụng môi trường: " + environment);
    }

    public static String getBaseUrl() {
        return properties.getProperty(environment + ".baseUrl");
    }

    private static String resolveUrl(String url) {
        if (url == null) return null;
        return url.replace("${${environment}.baseUrl}", getBaseUrl());
    }

    public static String getUrl(String key) {
        String url = properties.getProperty(key);
        if (url == null) {
            System.err.println("⚠️ Không tìm thấy URL cho key: " + key);
            return null;
        }
        return resolveUrl(url);
    }

    // Authentication URLs
    public static String getLoginUrl() {
        return getUrl("auth.loginUrl");
    }

    public static String getLogoutUrl() {
        return getUrl("auth.logoutUrl");
    }

    // Report URLs
    public static String getReportInUrl() {
        return getUrl("report.inUrl");
    }

    public static String getReportOutUrl() {
        return getUrl("report.outUrl");
    }

    public static String getReportAttendanceUrl() {
        return getUrl("report.attendanceUrl");
    }

    // Management URLs
    public static String getManagementStudentUrl() {
        return getUrl("management.studentUrl");
    }

    public static String getManagementTeacherUrl() {
        return getUrl("management.teacherUrl");
    }

    public static String getManagementClassUrl() {
        return getUrl("management.classUrl");
    }

    // Settings URLs
    public static String getSettingsProfileUrl() {
        return getUrl("settings.profileUrl");
    }

    public static String getSettingsPasswordUrl() {
        return getUrl("settings.passwordUrl");
    }
}