package com.mcp.site_truonghoc;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginMethod {
    public static void login(WebDriver driver, String username, String password) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://truonghoc.vinaid.vn/#/login");

        WebElement inputUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        inputUsername.clear();
        inputUsername.sendKeys(username);

        WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        inputPassword.clear();
        inputPassword.sendKeys(password);
        Thread.sleep(2000);
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        loginButton.click();

        // Đợi đăng nhập thành công
        wait.until(ExpectedConditions.urlContains("/#"));
    }
}
