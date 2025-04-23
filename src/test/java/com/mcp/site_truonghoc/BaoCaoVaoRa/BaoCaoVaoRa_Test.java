package com.mcp.site_truonghoc.BaoCaoVaoRa;

import com.mcp.site_truonghoc.LoginMethod;
import com.mcp.site_truonghoc.config.ConfigManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BaoCaoVaoRa_Test {
    private ChromeDriver driver;
    private BaoCaoVaoRa_Page baocaocaora_Page;
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
        System.out.println("🌐 Base URL: " + ConfigManager.getBaseUrl());
        System.out.println("===========================\n");

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
            System.out.println("===========================\n");
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
                            "application/vnd.ms-excel,application/x-excel,application/x-msexcel," +
                            "application/octet-stream");

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
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--start-maximized");

            // Thêm tham số để đảm bảo tải file trong headless mode
            options.addArguments("--disable-gpu-sandbox");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-browser-side-navigation");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");
            options.addArguments("--disable-site-isolation-trials");
            options.addArguments("--disable-features=DownloadBubble,DownloadBubbleV2");
            options.addArguments("--disable-features=DownloadNotification");
            options.addArguments("--disable-features=DownloadBubble");
            options.addArguments("--disable-features=DownloadBubbleV2");
            options.addArguments("--disable-features=DownloadNotification");
            options.addArguments("--disable-features=DownloadBubble");
            options.addArguments("--disable-features=DownloadBubbleV2");
            options.addArguments("--disable-features=DownloadNotification");

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

            baocaocaora_Page = new BaoCaoVaoRa_Page(driver);
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

    @Test(priority = 4, description = "Xuất báo cáo Excel với đầy đủ các bước")
    public void testBaoCaoVaoRa_FullSteps() throws InterruptedException {
        try {
            System.out.println("\n=== TEST CASE 4: XUẤT BÁO CÁO ĐẦY ĐỦ CÁC BƯỚC ===");
            
            // 1. Đăng nhập
            System.out.println("\n1️⃣ ĐĂNG NHẬP HỆ THỐNG");
            LoginMethod.login(driver);
            Thread.sleep(2000);
            System.out.println("✅ Đăng nhập thành công");

            // 2. Truy cập trang báo cáo
            System.out.println("\n2️⃣ TRUY CẬP TRANG BÁO CÁO");
            System.out.println("➡️ Điều hướng đến trang báo cáo vào/ra");
            System.out.println("🌐 URL báo cáo: " + ConfigManager.getReportInUrl());
            driver.get(ConfigManager.getReportInUrl());
            Thread.sleep(2000);
            System.out.println("✅ Đã vào trang báo cáo");

            // 3. Chọn ngày bắt đầu và kết thúc
            System.out.println("\n3️⃣ CHỌN NGÀY BÁO CÁO");
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.println("➡️ Nhập ngày bắt đầu: " + currentDate);
            baocaocaora_Page.inputUndefinedFromDate.clear();
            baocaocaora_Page.inputUndefinedFromDate.sendKeys(currentDate);
            Thread.sleep(1000);
            
            System.out.println("➡️ Nhập ngày kết thúc: " + currentDate);
            baocaocaora_Page.inputUndefinedDate.clear();
            baocaocaora_Page.inputUndefinedDate.sendKeys(currentDate);
            Thread.sleep(2000);
            System.out.println("✅ Đã nhập ngày báo cáo");

            // 4. Chọn phòng ban
            System.out.println("\n4️⃣ CHỌN PHÒNG BAN");
            System.out.println("➡️ Click vào label Chọn tất cả");
            baocaocaora_Page.label.click();
            Thread.sleep(1500);
            System.out.println("✅ Đã chọn phòng ban");

            // 5. Chọn sự kiện điểm danh sáng
            System.out.println("\n5️⃣ CHỌN SỰ KIỆN ĐIỂM DANH SÁNG");
            System.out.println("➡️ Click vào sự kiện điểm danh sáng");
            baocaocaora_Page.DiemDanhSang.click();
            Thread.sleep(1500);
            System.out.println("✅ Đã chọn sự kiện điểm danh sáng");

            // 6. Xuất Excel
            System.out.println("\n6️⃣ XUẤT BÁO CÁO EXCEL");
            System.out.println("➡️ Click nút xuất Excel");
            baocaocaora_Page.spanExcel.click();
            System.out.println("✅ Đã click nút xuất Excel");
            Thread.sleep(3000);

            // 7. Kiểm tra file tải về
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

    private void executeTest(String testDate) throws InterruptedException {
        System.out.println("\n1️⃣ ĐĂNG NHẬP HỆ THỐNG");
        LoginMethod.login(driver);
        Thread.sleep(2000);
        System.out.println("✅ Đăng nhập thành công");

        System.out.println("\n2️⃣ TRUY CẬP TRANG BÁO CÁO");
        System.out.println("➡️ Điều hướng đến trang báo cáo vào/ra");
        System.out.println("🌐 URL báo cáo: " + ConfigManager.getReportInUrl());
        driver.get(ConfigManager.getReportInUrl());
        Thread.sleep(2000);
        System.out.println("✅ Đã vào trang báo cáo");

        System.out.println("\n3️⃣ CHỌN PHÒNG BAN");
        System.out.println("➡️ Click vào label Chọn tất cả");
        baocaocaora_Page.label.click();
        Thread.sleep(1500);
        System.out.println("✅ Đã chọn phòng ban");

        System.out.println("\n4️⃣ NHẬP NGÀY BÁO CÁO");
        System.out.println("➡️ Xóa dữ liệu cũ và nhập ngày: " + testDate);
        baocaocaora_Page.inputUndefinedDate.clear();
        baocaocaora_Page.inputUndefinedDate.sendKeys(testDate);
        Thread.sleep(3000);
        System.out.println("✅ Đã nhập ngày báo cáo");

        System.out.println("\n5️⃣ XUẤT BÁO CÁO EXCEL");
        System.out.println("➡️ Click nút xuất Excel");
        baocaocaora_Page.spanExcel.click();
        System.out.println("✅ Đã click nút xuất Excel");
        Thread.sleep(3000);

        System.out.println("\n6️⃣ KIỂM TRA FILE TẢI VỀ");
        System.out.println("➡️ Thư mục tải về: " + downloadPath);

        verifyFileDownload();
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