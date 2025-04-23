package com.mcp.site_truonghoc;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginPageTest {
    private ChromeDriver driver;
    private LoginPage loginPage;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://truonghoc.vinaid.vn/#/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage = new LoginPage(driver);
    }
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginPage() throws InterruptedException {
        //Thao tac dang nhap

        for (int i = 1; i <= 3; i++){
        System.out.println("Lan "+ i +" : Dang nhap");

        try{

            loginPage.inputUsername.sendKeys("phuc@27");
            Thread.sleep(1500);
            loginPage.inputPassword.sendKeys("156823479");
            Thread.sleep(1000);
            loginPage.button.click();
            System.out.println("Dang nhap thanh cong");
            Thread.sleep(3000);


            performLogout();
            System.out.println("Dang xuat thanh cong" + "lan " + i);
            }
            catch (TimeoutException e){
                System.err.println("Time out " + e.getMessage() + " lan: " + i);
                assert false : "Dang nhap that bai";
            }
        }
    }

    private void performLogout() {
        //Hang dong dang xuat
        try {
            loginPage.button2.click();
            Thread.sleep(1000);
            loginPage.liLogout.click();
            Thread.sleep(1000);

            wait.until(ExpectedConditions.urlContains("/#"));
        }
        catch (TimeoutException | InterruptedException e) {
            throw new RuntimeException("Khong the dang xuat: " + e.getMessage());
        }
    }
}
