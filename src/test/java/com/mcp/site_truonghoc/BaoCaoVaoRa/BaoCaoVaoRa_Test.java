package com.mcp.site_truonghoc.BaoCaoVaoRa;

import com.mcp.site_truonghoc.LoginMethod;
import com.mcp.site_truonghoc.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BaoCaoVaoRa_Test {
    private WebDriver driver;
    private WebDriverWait wait;
    private BaoCaoVaoRa_Page baoCaoVaoRaPage;

    private final String downloadPath = getDownloadPath();

    private String getDownloadPath() {
        String path;
        boolean isCI = false;

        // Kiểm tra các dấu hiệu của môi trường CI
        String ciEnv = System.getenv("CI");
        String githubActions = System.getenv("GITHUB_ACTIONS");
        String runnerTemp = System.getenv("RUNNER_TEMP");
        String runnerWork = System.getenv("RUNNER_WORKSPACE");

        if (ciEnv != null && ciEnv.equals("true") ||
                githubActions != null && githubActions.equals("true") ||
                runnerTemp != null) {
            isCI = true;
        }

        if (isCI) {
            // Trên CI (GitHub Actions)
            if (runnerWork != null) {
                path = runnerWork + "/QC_site_truonghoc/downloads";
            } else {
                path = "/home/runner/work/QC_site_truonghoc/QC_site_truonghoc/downloads";
            }
            System.out.println("🔄 Đang chạy trong môi trường CI");
            System.out.println("CI: " + ciEnv);
            System.out.println("GITHUB_ACTIONS: " + githubActions);
            System.out.println("RUNNER_WORKSPACE: " + runnerWork);
        } else {
            // Trên local
            path = System.getProperty("user.dir") + File.separator + "downloads";
            System.out.println("🔄 Đang chạy trong môi trường local");
            System.out.println("Thư mục dự án: " + System.getProperty("user.dir"));
        }

        System.out.println("📁 Đường dẫn thư mục tải về: " + path);
        return path;
    }

    @BeforeClass
    public void setup() {
        String osName = System.getProperty("os.name").toLowerCase();
        String projectDir = System.getProperty("user.dir");
        String chromeDriverPath;
        
        if (osName.contains("windows")) {
            chromeDriverPath = projectDir + File.separator + "chromedriver-win64" + 
                File.separator + "135.0.7049.95" + File.separator + "chromedriver.exe";
        } else {
            chromeDriverPath = projectDir + File.separator + "chromedriver-linux64" + 
                File.separator + "chromedriver";
        }
        
        System.out.println("Sử dụng ChromeDriver tại: " + chromeDriverPath);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        
        // Khởi tạo ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--safebrowsing-disable-download-protection");
        options.addArguments("--disable-web-security");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless=new"); // Chạy ở chế độ không hiển thị
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        
        // Cấu hình download
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("browser.download.folderList", 2);
        prefs.put("browser.download.manager.showWhenStarting", false);
        prefs.put("browser.helperApps.neverAsk.saveToDisk", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet," +
            "application/vnd.ms-excel,application/x-excel,application/x-msexcel," +
            "application/octet-stream");
        options.setExperimentalOption("prefs", prefs);
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baoCaoVaoRaPage = new BaoCaoVaoRa_Page(driver);
    }

    @BeforeMethod
    public void setUp() {
        // Không cần khởi tạo lại ChromeDriver
        System.out.println("\n🔄 Đang chuẩn bị test case...");
    }

    @AfterMethod
    public void tearDown() {
        try {
            // Đăng xuất trước khi kết thúc test case
            System.out.println("\n🔄 Đang đăng xuất...");
            performLogout();
            System.out.println("✅ Đã đăng xuất thành công");
        } catch (Exception e) {
            System.out.println("⚠️ Không thể đăng xuất: " + e.getMessage());
        }
        System.out.println("\n🔄 Đã hoàn thành test case");
    }

    private void performLogout() {
        try {
            // Click vào nút hồ sơ
            WebElement profileButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title='Hồ sơ']")));
            profileButton.click();
            Thread.sleep(1000);

            // Click vào nút đăng xuất
            WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[class*='logout']")));
            logoutButton.click();
            Thread.sleep(1000);

            // Đợi cho đến khi URL chứa "/#"
            wait.until(ExpectedConditions.urlContains("/#"));
        } catch (Exception e) {
            throw new RuntimeException("Không thể đăng xuất: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("\n=== KẾT THÚC TẤT CẢ TEST CASE ===");
        if (driver != null) {
            driver.quit();
        }
        cleanDownloadDirectory();
    }

    private void cleanDownloadDirectory() {
        File downloadDir = new File(downloadPath);
        if (downloadDir.exists()) {
            File[] files = downloadDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".xlsx") ||
                            name.toLowerCase().endsWith(".xls"));
            if (files != null) {
                for (File file : files) {
                    if(file.delete()) {
                        System.out.println("✅ Đã xóa file: " + file.getName());
                    } else {
                        System.out.println("⚠️ Không thể xóa file: " + file.getName());
                        // Thử đổi tên file nếu không xóa được
                        File renamedFile = new File(file.getParent(), "old_" + file.getName());
                        if(file.renameTo(renamedFile)) {
                            System.out.println("✅ Đã đổi tên file: " + file.getName() + " -> " + renamedFile.getName());
                        }
                    }
                }
            }
        }
    }

    @Test(priority = 1, description = "Xuất báo cáo Excel với ngày trong tương lai")
    public void testBaoCaoVaoRa_NgayTuongLai() throws InterruptedException {
        try {
            System.out.println("\n=== TEST CASE 1: XUẤT BÁO CÁO NGÀY TƯƠNG LAI ===");
            // Lấy ngày hiện tại + 2 ngày
            String futureDate = java.time.LocalDate.now().plusDays(2).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            executeTest(futureDate);
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH KIỂM THỬ");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 2, description = "Xuất báo cáo Excel với ngày hiện tại")
    public void testBaoCaoVaoRa_NgayHienTai() throws InterruptedException {
        try {
            System.out.println("\n=== TEST CASE 2: XUẤT BÁO CÁO NGÀY HIỆN TẠI ===");
            // Lấy ngày hiện tại
            String currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            executeTest(currentDate);
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH KIỂM THỬ");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test(priority = 3, description = "Xuất báo cáo Excel với ngày trong quá khứ")
    public void testBaoCaoVaoRa_NgayQuaKhu() throws InterruptedException {
        try {
            System.out.println("\n=== TEST CASE 3: XUẤT BÁO CÁO NGÀY QUÁ KHỨ ===");
            // Lấy ngày hiện tại - 2 ngày
            String pastDate = java.time.LocalDate.now().minusDays(2).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            executeTest(pastDate);
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH KIỂM THỬ");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void executeTest(String testDate) throws InterruptedException {
        try {

            LoginMethod.login(driver);
            Thread.sleep(2000);


            System.out.println("\n2️⃣ TRUY CẬP TRANG BÁO CÁO");
            System.out.println("➡️ Điều hướng đến trang báo cáo vào/ra");
            System.out.println("🌐 URL báo cáo: " + ConfigManager.getUrl("report.inOutUrl"));
            driver.get(ConfigManager.getUrl("report.inOutUrl"));
            Thread.sleep(2000);
            System.out.println("✅ Đã vào trang báo cáo");

            System.out.println("\n3️⃣ CHỌN PHÒNG BAN");
            System.out.println("➡️ Click vào label Chọn tất cả");
            baoCaoVaoRaPage.label.click();
            Thread.sleep(1500);
            System.out.println("✅ Đã chọn phòng ban");

            System.out.println("\n4️⃣ NHẬP NGÀY BÁO CÁO");
            System.out.println("➡️ Xóa dữ liệu cũ và nhập ngày: " + testDate);
            baoCaoVaoRaPage.inputUndefinedDate.clear();
            baoCaoVaoRaPage.inputUndefinedDate.sendKeys(testDate);
            Thread.sleep(3000);
            System.out.println("✅ Đã nhập ngày báo cáo");

            System.out.println("\n5️⃣ CHỌN SỰ KIỆN ĐIỂM DANH SÁNG");
            System.out.println("➡️ Click vào input sự kiện");
            baoCaoVaoRaPage.inputEvent.click();
            Thread.sleep(1000);
            System.out.println("➡️ Click vào sự kiện điểm danh sáng");
            baoCaoVaoRaPage.DiemDanhSang.click();
            Thread.sleep(1500);
            System.out.println("✅ Đã chọn sự kiện điểm danh sáng");

            System.out.println("\n6️⃣ XUẤT BÁO CÁO EXCEL");
            System.out.println("➡️ Click nút xuất Excel");
            baoCaoVaoRaPage.spanExcel.click();
            System.out.println("✅ Đã click nút xuất Excel");
            Thread.sleep(3000);

            System.out.println("\n7️⃣ KIỂM TRA FILE TẢI VỀ");
            System.out.println("➡️ Thư mục tải về: " + downloadPath);

            verifyFileDownload();
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH KIỂM THỬ");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void verifyFileDownload() throws InterruptedException {
        // Kiểm tra môi trường CI
        boolean isCI = false;
        String ciEnv = System.getenv("CI");
        String githubActions = System.getenv("GITHUB_ACTIONS");

        if (ciEnv != null && ciEnv.equals("true") ||
                githubActions != null && githubActions.equals("true")) {
            isCI = true;
            System.out.println("🔄 Đang chạy trong môi trường CI");
            return;
        }

        boolean fileDownloaded = false;
        File downloadedFile = null;
        int maxAttempts = 10;
        int waitTime = 2000;

        for (int i = 0; i < maxAttempts && !fileDownloaded; i++) {
            Thread.sleep(waitTime);
            System.out.println("⏳ Kiểm tra lần " + (i + 1) + "/" + maxAttempts);

            File[] files = new File(downloadPath).listFiles(
                    (dir, name) -> name.toLowerCase().endsWith(".xlsx") ||
                            name.toLowerCase().endsWith(".xls"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.length() > 0) {
                        fileDownloaded = true;
                        downloadedFile = file;
                        System.out.println("✅ Tìm thấy file Excel: " + file.getName() + " (" + file.length() + " bytes)");
                        break;
                    } else {
                        System.out.println("⚠️ File rỗng: " + file.getName());
                    }
                }
            }

            if (!fileDownloaded) {
                System.out.println("📁 Nội dung thư mục tải về:");
                File[] allFiles = new File(downloadPath).listFiles();
                if (allFiles != null) {
                    for (File file : allFiles) {
                        System.out.println("   - " + file.getName() + " (" + file.length() + " bytes)");
                    }
                } else {
                    System.out.println("   - Thư mục trống");
                }
            }
        }

        if (!fileDownloaded || downloadedFile == null) {
            System.err.println("❌ Không tìm thấy file Excel sau " + maxAttempts + " lần kiểm tra");
            System.err.println("📁 Nội dung thư mục tải về:");
            File[] allFiles = new File(downloadPath).listFiles();
            if (allFiles != null) {
                for (File file : allFiles) {
                    System.err.println("   - " + file.getName() + " (" + file.length() + " bytes)");
                }
            }
            throw new RuntimeException("Không tìm thấy file Excel được tải về");
        }

        System.out.println("✅ Tải file thành công: " + downloadedFile.getName());
        System.out.println("📊 Kích thước file: " + downloadedFile.length() + " bytes");

        Thread.sleep(2000);

        System.out.println("\n=== KẾT THÚC TEST CASE - THÀNH CÔNG ===\n");
    }
} 