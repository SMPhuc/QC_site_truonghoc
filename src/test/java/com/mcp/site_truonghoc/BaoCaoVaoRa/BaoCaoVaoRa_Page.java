package com.mcp.site_truonghoc.BaoCaoVaoRa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// page_url = https://truonghoc.vinaid.vn/#/
public class BaoCaoVaoRa_Page {
    @FindBy(css = "input[name='fromDate']")
    public WebElement inputUndefinedFromDate;

    @FindBy(css = "input[id*='toDate']")
    public WebElement inputUndefinedDate;

    @FindBy(xpath = "//*[text() = 'Chọn tất cả']")
    public WebElement label;

    @FindBy(name = "eventId")
    public WebElement inputEvent;

    @FindBy(css = "html > body > div:nth-of-type(2) > div > ul > li:nth-of-type(6) > div > div > strong")
    public WebElement DiemDanhSang;

    @FindBy(xpath = "//*[text() = 'Xuất Excel']")
    public WebElement spanExcel;
    public BaoCaoVaoRa_Page(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}
