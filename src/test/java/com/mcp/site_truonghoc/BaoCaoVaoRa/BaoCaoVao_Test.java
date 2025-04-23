package com.mcp.site_truonghoc.BaoCaoVaoRa;

import com.mcp.site_truonghoc.LoginMethod;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Test(groups = "baocaovao")
public class BaoCaoVao_Test {
    private ChromeDriver driver;
    private BaoCaoVao_Page baocaocao_Page;
    private WebDriverWait wait;

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
    public void setUpClass() {
        System.out.println("\n=== CHUẨN BỊ MÔI TRƯỜNG TEST ===");
        
        // Kiểm tra và tạo thư mục downloads nếu chưa tồn tại
        File downloadDir = new File(downloadPath);
        if (!downloadDir.exists()) {
            System.out.println("Thư mục tải về chưa tồn tại, đang tạo mới...");
            if (downloadDir.mkdirs()) {
                System.out.println("✅ Đã tạo thư mục tải về thành công");
            } else {
                System.err.println("❌ Không thể tạo thư mục tải về");
                throw new RuntimeException("Không thể tạo thư mục tải về: " + downloadPath);
            }
        } else {
            System.out.println("✅ Thư mục tải về đã tồn tại");
        }
        
        cleanDownloadDirectory();
    }

    @BeforeMethod
    public void setUp() {
        System.out.println("\n🔄 Khởi tạo trình duyệt mới...");
        initializeChromeDriver();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("\n=== KẾT THÚC TẤT CẢ TEST CASE ===");
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
                    }
                }
            }
        }
    }

    private void initializeChromeDriver() {
        try {
            System.out.println("Cấu hình Chrome Driver với đường dẫn tải về: " + downloadPath);
            
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("browser.download.folderList", 2);
        prefs.put("browser.download.manager.showWhenStarting", false);
        prefs.put("browser.helperApps.neverAsk.saveToDisk", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet," +
            "application/vnd.ms-excel,application/x-excel,application/x-msexcel");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--safebrowsing-disable-download-protection");
        options.addArguments("--disable-web-security");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            
            // Xác định môi trường chạy
            boolean isCI = false;
            String osName = System.getProperty("os.name").toLowerCase();
            String ciEnv = System.getenv("CI");
            String githubActions = System.getenv("GITHUB_ACTIONS");
            String runnerTemp = System.getenv("RUNNER_TEMP");
            
            System.out.println("=== THÔNG TIN MÔI TRƯỜNG ===");
            System.out.println("Hệ điều hành: " + osName);
            System.out.println("CI: " + ciEnv);
            System.out.println("GITHUB_ACTIONS: " + githubActions);
            System.out.println("RUNNER_TEMP: " + runnerTemp);
            
            if ((ciEnv != null && ciEnv.equals("true")) || 
                (githubActions != null && githubActions.equals("true")) ||
                (runnerTemp != null)) {
                isCI = true;
                System.out.println("🔄 Đang chạy trong môi trường CI");
            } else {
                System.out.println("🔄 Đang chạy trong môi trường local");
            }
            
            // Xác định đường dẫn ChromeDriver
            String chromeDriverPath;
            if (isCI) {
                // Trên CI
                chromeDriverPath = "/usr/local/bin/chromedriver";
                System.out.println("Sử dụng ChromeDriver tại: " + chromeDriverPath);
            } else {
                // Trên local
                String projectDir = System.getProperty("user.dir");
                if (osName.contains("windows")) {
                    chromeDriverPath = projectDir + File.separator + "chromedriver-win64" + 
                                    File.separator + "135.0.7049.95" + File.separator + "chromedriver.exe";
                } else {
                    chromeDriverPath = projectDir + File.separator + "chromedriver-linux64" + 
                                    File.separator + "chromedriver";
                }
                System.out.println("Sử dụng ChromeDriver tại: " + chromeDriverPath);
            }
            
            File chromeDriverFile = new File(chromeDriverPath);
            if (chromeDriverFile.exists()) {
                System.out.println("✅ Tìm thấy ChromeDriver tại: " + chromeDriverPath);
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            } else {
                System.err.println("❌ Không tìm thấy ChromeDriver tại: " + chromeDriverPath);
                throw new RuntimeException("Không tìm thấy ChromeDriver");
            }

            // Xác định Chrome binary
            String chromeBinary = null;
            if (isCI) {
                // Trên CI
                chromeBinary = "/usr/bin/google-chrome";
                System.out.println("Sử dụng Chrome tại: " + chromeBinary);
            } else {
                // Trên local
                String[] possiblePaths;
                if (osName.contains("windows")) {
                    possiblePaths = new String[] {
                        System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\Application\\chrome.exe",
                        System.getenv("PROGRAMFILES") + "\\Google\\Chrome\\Application\\chrome.exe",
                        System.getenv("PROGRAMFILES(X86)") + "\\Google\\Chrome\\Application\\chrome.exe",
                        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"
                    };
                } else {
                    possiblePaths = new String[] {
                        "/usr/bin/google-chrome",
                        "/usr/bin/chromium-browser",
                        "/usr/bin/chromium"
                    };
                }
                
                for (String path : possiblePaths) {
                    if (path != null) {
                        File chromeFile = new File(path);
                        if (chromeFile.exists()) {
                            chromeBinary = path;
                            System.out.println("✅ Tìm thấy Chrome tại: " + chromeBinary);
                            break;
                        }
                    }
                }
            }
            
            if (chromeBinary == null) {
                System.err.println("❌ Không tìm thấy Chrome trong các đường dẫn mặc định");
                throw new RuntimeException("Không tìm thấy Chrome browser. Vui lòng cài đặt Chrome.");
            }
            
            System.out.println("Thiết lập Chrome binary: " + chromeBinary);
            options.setBinary(chromeBinary);

            System.out.println("Khởi tạo Chrome Driver...");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        baocaocao_Page = new BaoCaoVao_Page(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            System.out.println("✅ Khởi tạo Chrome Driver thành công");
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo Chrome Driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo Chrome Driver", e);
        }
    }

    @Test(priority = 1, description = "Xuất báo cáo Excel với ngày trong tương lai")
    public void testBaoCaoVaoRa_NgayTuongLai() throws InterruptedException {
        try {
            System.out.println("\n=== TEST CASE 1: XUẤT BÁO CÁO NGÀY TƯƠNG LAI ===");
            executeTest("23-04-2025");
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
            executeTest(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
            executeTest("01-01-2024");
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH KIỂM THỬ");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void executeTest(String testDate) throws InterruptedException {
        System.out.println("\n1️⃣ ĐĂNG NHẬP HỆ THỐNG");
        System.out.println("➡️ Thực hiện đăng nhập với tài khoản: phuc@27");
        LoginMethod.login(driver, "phuc@27", "156823479");
        Thread.sleep(2000);
        System.out.println("✅ Đăng nhập thành công");
        
        System.out.println("\n2️⃣ TRUY CẬP TRANG BÁO CÁO");
        System.out.println("➡️ Điều hướng đến trang báo cáo vào/ra");
        driver.get("https://truonghoc.vinaid.vn/#/inReport");
        Thread.sleep(2000);
        System.out.println("✅ Đã vào trang báo cáo");
        
        System.out.println("\n3️⃣ CHỌN PHÒNG BAN");
        System.out.println("➡️ Click vào label Chọn tất cả");
        baocaocao_Page.Label_PhongBan.click();
        Thread.sleep(1000);
        System.out.println("✅ Đã chọn phòng ban");
        
        System.out.println("\n4️⃣ NHẬP NGÀY BÁO CÁO");
        System.out.println("➡️ Xóa dữ liệu cũ và nhập ngày: " + testDate);
        baocaocao_Page.inputUndefinedDate.clear();
        baocaocao_Page.inputUndefinedDate.sendKeys(testDate);
        Thread.sleep(3000);
        System.out.println("✅ Đã nhập ngày báo cáo");

        System.out.println("\n5️⃣ XUẤT BÁO CÁO EXCEL");
        System.out.println("➡️ Click nút xuất Excel");
        baocaocao_Page.buttonExcel.click();
        System.out.println("✅ Đã click nút xuất Excel");
        
        System.out.println("\n6️⃣ KIỂM TRA FILE TẢI VỀ");
        System.out.println("➡️ Thư mục tải về: " + downloadPath);

        verifyFileDownload();
    }

    private void verifyFileDownload() throws InterruptedException {
        boolean fileDownloaded = false;
        File downloadedFile = null;
        int maxAttempts = 30;

        for (int i = 0; i < maxAttempts && !fileDownloaded; i++) {
            Thread.sleep(2000);
            File[] files = new File(downloadPath).listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".xlsx") || 
                             name.toLowerCase().endsWith(".xls"));
            
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.length() > 0) {
                        fileDownloaded = true;
                        downloadedFile = file;
                        break;
                    }
                }
            }
            
            if (!fileDownloaded) {
                System.out.println("⏳ Lần kiểm tra thứ " + (i + 1) + "/" + maxAttempts);
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
        
        System.out.println("\n=== KẾT THÚC TEST CASE - THÀNH CÔNG ===\n");
    }
}
