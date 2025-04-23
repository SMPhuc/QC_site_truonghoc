package com.mcp.site_truonghoc;

import com.mcp.site_truonghoc.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginMethod {
    // Tài khoản mặc định
    private static final String DEFAULT_USERNAME = "admin@64";
    private static final String DEFAULT_PASSWORD = "admin";

    // Selectors
    private static final String USERNAME_SELECTOR = "input[id='username']";
    private static final String PASSWORD_SELECTOR = "input[id='password']";
    private static final String LOGIN_BUTTON_SELECTOR = "button[type='submit']";
    private static final String ERROR_MESSAGE_SELECTOR = ".error-message, .alert-danger, .text-danger, .ant-message-error, .ant-alert-error";

    // Phương thức đăng nhập mặc định
    public static void login(WebDriver driver) throws InterruptedException {
        System.out.println("\n1️⃣ ĐĂNG NHẬP HỆ THỐNG");
        System.out.println("➡️ Thực hiện đăng nhập với tài khoản: " + DEFAULT_USERNAME);
        
        try {
            System.out.println("🌐 Truy cập trang đăng nhập: " + ConfigManager.getLoginUrl());
            driver.get(ConfigManager.getLoginUrl());
            
            // Đợi trang load xong
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            
            // Đợi và nhập tên đăng nhập
            System.out.println("⏳ Đang đợi trường tên đăng nhập...");
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(USERNAME_SELECTOR)));
            usernameField.clear();
            usernameField.sendKeys(DEFAULT_USERNAME);
            System.out.println("✅ Đã nhập tên đăng nhập: " + DEFAULT_USERNAME);
            
            // Đợi và nhập mật khẩu
            System.out.println("⏳ Đang đợi trường mật khẩu...");
            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(PASSWORD_SELECTOR)));
            passwordField.clear();
            passwordField.sendKeys(DEFAULT_PASSWORD);
            System.out.println("✅ Đã nhập mật khẩu");
            
            // Đợi và click nút đăng nhập
            System.out.println("⏳ Đang đợi nút đăng nhập...");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(LOGIN_BUTTON_SELECTOR)));
            loginButton.click();
            System.out.println("✅ Đã click nút đăng nhập");
            
            // Đợi 2 giây để đăng nhập hoàn tất
            Thread.sleep(2000);
            
            // Kiểm tra thông báo lỗi
            try {
                WebElement errorMessage = driver.findElement(By.cssSelector(ERROR_MESSAGE_SELECTOR));
                if (errorMessage.isDisplayed()) {
                    String errorText = errorMessage.getText();
                    System.out.println("❌ Đăng nhập thất bại: " + errorText);
                    throw new RuntimeException("Đăng nhập thất bại: " + errorText);
                }
            } catch (Exception e) {
                // Không tìm thấy thông báo lỗi, đăng nhập thành công
                System.out.println("✅ Đăng nhập thành công");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi trong quá trình đăng nhập: " + e.getMessage());
            throw e;
        }
    }

    // Phương thức đăng nhập với tài khoản tùy chỉnh (nếu cần)
    public static void login(WebDriver driver, String username, String password) throws InterruptedException {
        System.out.println("\n1️⃣ ĐĂNG NHẬP HỆ THỐNG");
        System.out.println("➡️ Thực hiện đăng nhập với tài khoản: " + username);
        
        try {
            System.out.println("🌐 Truy cập trang đăng nhập: " + ConfigManager.getLoginUrl());
            driver.get(ConfigManager.getLoginUrl());
            
            // Đợi trang load xong
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            
            // Đợi và nhập tên đăng nhập
            System.out.println("⏳ Đang đợi trường tên đăng nhập...");
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(USERNAME_SELECTOR)));
            usernameField.clear();
            usernameField.sendKeys(username);
            System.out.println("✅ Đã nhập tên đăng nhập: " + username);
            
            // Đợi và nhập mật khẩu
            System.out.println("⏳ Đang đợi trường mật khẩu...");
            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(PASSWORD_SELECTOR)));
            passwordField.clear();
            passwordField.sendKeys(password);
            System.out.println("✅ Đã nhập mật khẩu");
            
            // Đợi và click nút đăng nhập
            System.out.println("⏳ Đang đợi nút đăng nhập...");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(LOGIN_BUTTON_SELECTOR)));
            loginButton.click();
            System.out.println("✅ Đã click nút đăng nhập");
            
            // Đợi 2 giây để đăng nhập hoàn tất
            Thread.sleep(2000);
            
            // Kiểm tra thông báo lỗi
            try {
                WebElement errorMessage = driver.findElement(By.cssSelector(ERROR_MESSAGE_SELECTOR));
                if (errorMessage.isDisplayed()) {
                    String errorText = errorMessage.getText();
                    System.out.println("❌ Đăng nhập thất bại: " + errorText);
                    throw new RuntimeException("Đăng nhập thất bại: " + errorText);
                }
            } catch (Exception e) {
                // Không tìm thấy thông báo lỗi, đăng nhập thành công
                System.out.println("✅ Đăng nhập thành công");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi trong quá trình đăng nhập: " + e.getMessage());
            throw e;
        }
    }
}
