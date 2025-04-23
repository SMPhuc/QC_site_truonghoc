package com.mcp.site_truonghoc.BaoCaoVaoRa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

// page_url = https://truonghoc.vinaid.vn/#/
public class BaoCaoVaoRa_Page {


    public BaoCaoVaoRa_Page(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}