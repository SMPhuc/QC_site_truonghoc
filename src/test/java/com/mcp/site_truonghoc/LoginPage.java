package com.mcp.site_truonghoc;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// page_url = https://truonghoc.vinaid.vn/#/login
public class LoginPage {
    @FindBy(css = "input[id='username']")
    public WebElement inputUsername;

    @FindBy(css = "input[id='password']")
    public WebElement inputPassword;

    @FindBy(css = "button[type='submit']")
    public WebElement button;

    @FindBy(css = "button[title='Hồ sơ']")
    public WebElement button2;

    @FindBy(css = "li[class*='logout']")
    public WebElement liLogout;
    
    

    public LoginPage(ChromeDriver driver) {
        PageFactory.initElements(driver, this);
    }

}
