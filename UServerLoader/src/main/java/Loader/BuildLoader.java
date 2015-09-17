package Loader;

import Properties.Configuration;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildLoader {
    private static Logger log = LoggerFactory.getLogger(BuildLoader.class);

    private String URL_PROD = "https://update.tpproject.ru:8443/TPPUpdateServer/login.do";
    private String URL_TEST = "https://update.tpproject.ru:18443/TPPUpdateServer/login.do";

    private Configuration property;
    private Process chrome;
    private WebDriver driver;
    private WebElement element;
    private String urlString;
    private File[] buildsFiles;
    private String majorVersion;
    private String minorVersion;
    private String description;
    private long fileSize;


    public BuildLoader() {
        log.info("BuildLoader has been started.");
        startStuff();
    }

    private void startStuff() {
        log.info("Read configuration from file 'Configuration.xml'");
        property = ConfigFactory.create(Configuration.class);
        urlString = "";
        if (property.server().equals("production")) urlString = URL_PROD;
        if (property.server().equals("test")) urlString = URL_TEST;
        majorVersion = "";
        minorVersion = "";
        description = property.description();

        buildsFiles = new File("build").listFiles();
        if (buildsFiles == null) {
            close();
            throw new AssertionError("Error occurs while reading files to upload!");
        }

        File jar = null;
        for (File file : buildsFiles) {
            if (file.getName().contains("jar")) {
                jar = file;
            }
        }
        if (jar == null) {
            log.error("Can`t find *.jar file!");
            System.exit(-1);
        }
        fileSize = jar.length() / 1024;

        /**
         * Сортировка по последнему символу (для того, чтобы jad грузился перед jar)
         */
        Arrays.sort(buildsFiles, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        String name1 = o1.getName();
                        String name2 = o2.getName();
                        return name1.charAt(name1.length() - 1) - name2.charAt(name2.length() - 1);
                    }
                }
        );

        log.info("Finding if the loading build already exist.");
        if (isBuildExist()) {
            log.info("Loading build is already exist! Now we just set the core link.");
            setCoreLink();
            close();
        } else {
            log.info("Build is new, so add a new build to brunch.");
            startLoad();
            setCoreLink();
            close();
        }
    }

    private boolean isBuildExist() {
        String buildToLoad = null;

        Pattern pattern = Pattern.compile("([0-9].[0-9]+|[0-9]+)");
        Matcher matcher = pattern.matcher(property.description());
        if (matcher.find()) {
            buildToLoad = matcher.group();
        }
        if (buildToLoad == null) {
            String jadFileName = buildsFiles[0].getName();
            matcher = pattern.matcher(jadFileName);
            if (matcher.find()) {
                buildToLoad = matcher.group();
            }
        }
        if (buildToLoad == null) {
            log.info("Can`t find build number in description (Configuration.xml).");
            log.info("Do you really want to proceed? ('Y' or 'N')");
            System.out.print("> ");
            Scanner s = new Scanner(System.in);
            String q = s.next();
            if (q.toLowerCase().equals("n")) {
                System.exit(0);
            }
            log.info("Type the build number in this case:");
            System.out.print("> ");
            buildToLoad = s.next();
            if (buildToLoad.contains(".")) {
                buildToLoad = buildToLoad.substring(buildToLoad.indexOf(".") + 1);
            }
            description += " " + buildToLoad;
        }


        driverConnection();
        enterSite();
        try {
            driver.findElement(By.linkText(">>")).click();
        } catch (Exception e) {
            driver.findElement(By.linkText(">>")).click();
        }

        WebElement table;
        WebElement tbody;
        WebElement element;
        List<WebElement> rows;

        table = driver.findElements(By.tagName("table")).get(1);
        tbody = table.findElement(By.tagName("tbody"));
        rows = tbody.findElements(By.tagName("tr"));
        int rowCount = rows.size() - 1;
        while (true) {
            table = driver.findElements(By.tagName("table")).get(1);
            try {
                tbody = table.findElement(By.tagName("tbody"));
            } catch (Exception e) {
                tbody = driver.findElements(By.tagName("table")).get(1).findElement(By.tagName("tbody"));
            }
            rows = tbody.findElements(By.tagName("tr"));

            WebElement row = rows.get(rowCount);
            List<WebElement> columns = row.findElements(By.tagName("td"));
            WebElement cell = columns.get(3);

            matcher = pattern.matcher(cell.getText());
            while (matcher.find()) {
                if (matcher.group().equals(buildToLoad)) {
                    WebElement cellNum = columns.get(2);
                    String buildStr = cellNum.getText();
                    String[] lowHi = buildStr.split("\\.");
                    majorVersion = lowHi[0];
                    minorVersion = lowHi[1];

                    WebElement cellClickable = columns.get(0);
                    cellClickable.findElement(By.tagName("a")).click();

                    for (WebElement col : driver.findElements(By.tagName("td"))) {
                        String content;
                        try {
                            content = col.getText();
                        } catch (Exception e) {
                            content = col.getText();
                        }
                        if (content.matches("[0-9]+\\s(kb)")) {
                            String candidateSize = content.substring(0, content.indexOf(" "));
                            if (candidateSize.equals(fileSize + "")) {
                                return true;
                            }
                        }
                    }
                    driver.navigate().back();
                }
            }
            if (--rowCount <= 0) {
                try {
                    rowCount = 19;
                    element = driver.findElement(By.linkText("<"));
                    element.click();
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    return false;
                }
            }
        }
    }

    private void driverConnection() {
        try {
            chrome = new ProcessBuilder("res\\chromedriver.exe").start();
            log.info("Chromium driver was launched.");
        } catch (IOException ioe) {
            close();
            throw new AssertionError("'chromedriver' not found!");
        }

        try {
            driver = new DriverAdapter(new URL("http://localhost:9515"), DesiredCapabilities.chrome());
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            close();
            System.exit(-1);
        }

    }

    private void enterSite() {
        log.info("Open URL.");
        driver.get(urlString);

        log.info("Entering user login/password.");
        element = driver.findElement(By.name("user"));
        element.sendKeys(property.user());
        element = driver.findElement(By.name("password"));
        element.sendKeys(property.password());
        element.submit();
        if (driver.getTitle().equals("Вход в систему")) {
            close();
            throw new AssertionError("Incorrect login/password!");
        }
        log.info("Going to all brunches.");
        element = driver.findElement(By.linkText("Ветви ядра"));
        element.click();

        log.info("Trying to find brunch " + property.branch());
        boolean isFind = clickTableElemenet(property.branch());
        if (!isFind) {
            close();
            System.exit(-1);
        }
    }

    private void startLoad() {
        element = driver.findElement(By.linkText(">>"));
        element.click();

        WebElement table = driver.findElements(By.tagName("table")).get(1);
        WebElement tbody;
        try {
            tbody = table.findElement(By.tagName("tbody"));
        } catch (Exception e) {
            tbody = table.findElement(By.tagName("tbody"));
        }
        List<WebElement> trs = tbody.findElements(By.tagName("tr"));
        WebElement lastTr = trs.get(trs.size() - 1);
        WebElement lastBuildElement = lastTr.findElements(By.tagName("td")).get(2);
        String[] buildsVersion = lastBuildElement.getText().split("\\.");
        majorVersion = buildsVersion[0];
        minorVersion = buildsVersion[1];
        log.info("Found last brunch version: " + majorVersion + "." + minorVersion);
        element = driver.findElement(By.linkText("Добавить сборку ядра"));
        element.click();

        minorVersion = (Integer.valueOf(minorVersion) + 1) + "";
        log.info("Add new build.");
        log.info("\t\tBuild name: " + property.buildName());
        log.info("\t\tDescription: " + description);
        log.info("\t\tVersion: " + majorVersion + "." + minorVersion);
        element = driver.findElement(By.name("name"));
        element.sendKeys(property.buildName());

        element = driver.findElement(By.name("description"));
        element.sendKeys(description);

        element = driver.findElement(By.name("majorVersion"));
        element.sendKeys(majorVersion + "");

        element = driver.findElement(By.name("minorVersion"));
        element.sendKeys(minorVersion + "");
        element.submit();

        for (WebElement lastBuild : driver.findElements(By.linkText(property.buildName()))) {
            element = lastBuild;
        }

        element.click();

        element = driver.findElement(By.name("platformId")).findElements(By.tagName("option")).get(1);
        element.click();

        for (File file : buildsFiles) {
            if (file.isDirectory()) continue;
            log.info("Upload file: " + file.getName());
            element = driver.findElement(By.name("uploadFile"));
            element.sendKeys(file.getAbsolutePath());
            element.submit();
        }

        element = driver.findElement(By.name("defaultFileId"));
        element.click();

        for (WebElement input : driver.findElements(By.tagName("input"))) {
            if (input.getAttribute("value").equals("Сохранить сборку")) {
                input.click();
                break;
            }
        }

        if (property.server().equals("production")) {
            element = driver.findElement(By.name("TPGK"));
            element.sendKeys("07.14.045-03");

            for (WebElement input : driver.findElements(By.tagName("input"))) {
                if (input.getAttribute("value").equals("Утвердить сборку")) {
                    input.click();
                    break;
                }
            }
        }
        log.info("Upload completed.");
    }

    private void setCoreLink() {
        log.info("Set the core link to build " + majorVersion + "." + minorVersion);
        log.info("\t\tLink name: " + property.linkName());
        log.info("\t\tBrunch: " + property.branch());
        log.info("\t\tBind version: " + majorVersion + "." + minorVersion);

        element = driver.findElement(By.linkText("Привязки ядер"));
        element.click();

        clickTableElemenet(property.linkName());

        Select select;
        try {
            select = new Select(driver.findElement(By.name("branchId")));
        } catch (Exception e) {
            select = new Select(driver.findElement(By.name("branchId")));
        }
        List<WebElement> options = select.getOptions();
        for (WebElement option : options) {
            if (option.getText().equals(property.branch())) {
                select.selectByValue(option.getAttribute("value"));
                break;
            }
        }

        select = new Select(driver.findElement(By.name("desiredMajorVersion")));
        select.selectByVisibleText(majorVersion);

        select = new Select(driver.findElement(By.name("desiredMinorVersion")));
        select.selectByVisibleText(minorVersion);

        element = driver.findElement(By.tagName("input"));
        element.submit();
    }

    private void close() {
        driver.quit();
        chrome.destroy();
        log.info("Chromium was closed. Process destroyed.");
    }

    private boolean clickTableElemenet(String name) {
        WebElement table;
        WebElement tbody;
        WebElement element;
        List<WebElement> trs;

        while (true) {
            table = driver.findElements(By.tagName("table")).get(1);
            try {
                tbody = table.findElement(By.tagName("tbody"));
            } catch (Exception e) {
                tbody = driver.findElements(By.tagName("table")).get(1).findElement(By.tagName("tbody"));
            }
            trs = tbody.findElements(By.tagName("tr"));
            for (WebElement tr : trs) {
                WebElement td = tr.findElements(By.tagName("td")).get(0);
                if (td.getText().equals(name)) {
                    td.findElement(By.tagName("a")).click();
                    return true;
                }
            }
            try {
                element = driver.findElement(By.linkText(">"));
                element.click();
            } catch (NoSuchElementException e) {
                return false;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            new BuildLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Press any key...");
        System.out.print("> ");
        System.in.read();
    }
}
