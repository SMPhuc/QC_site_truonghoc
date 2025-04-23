package com.mcp.site_truonghoc.BaoCaoVaoRa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// page_url = https://truonghoc.vinaid.vn/#/
public class BaoCaoVao_Page {
    @FindBy(css = "html > body > div > div > div > div > main > div:nth-of-type(1) > div > div > div:nth-of-type(7)")
    public WebElement BaoCaoVaoRa_MainList;



    @FindBy(xpath = "//*[text() = 'Chọn tất cả']")
    public WebElement Label_PhongBan;
    
    

    @FindBy(name = "date")
    public WebElement inputUndefinedDate;

    @FindBy(xpath = "//*[text() = 'Xuất Excel']")
    public WebElement buttonExcel;

    @FindBy(xpath = "//*[text() = 'Chọn tất cả']")
    public WebElement spanTitle;

    

    public BaoCaoVao_Page(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}