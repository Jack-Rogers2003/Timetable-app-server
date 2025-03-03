package org.example.metablegenerator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


@RestController
public class controller {

    @ResponseBody
    public ArrayList<String> getModules(String[] moduleNames) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);


        /*
        String chromeBinaryPath = "/usr/bin/google-chrome-stable";  // Path to the Chrome binary

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");

        // Create ChromeOptions and set the binary path
        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromeBinaryPath);
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");  // Necessary for AWS
        options.addArguments("--disable-dev-shm-usage"); // Optional: run in headless mode (no UI)

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver(options);

         */
        try {
            LocalDate  currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            int currentYear = currentDate.getYear();

            // Navigate to Google
            driver.get("https://mytimetable.swan.ac.uk/timetables?view=agenda&timetableTypeSelected=525fe79b-73c3-4b5c-8186-83c652b3adcc&datePeriod=semester%201%20%26%202&date="+currentYear+"-"+currentMonth+"-01");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            for (String moduleName : moduleNames) {
                WebElement module = driver.findElement(By.id("mat-input-0"));
                module.sendKeys(moduleName);
                Thread.sleep(2500);
                WebElement selectElement = driver.findElement(By.cssSelector("mat-selection-list"));
                selectElement.click();
                module.clear();
                Thread.sleep(2500);
            }

            return getModules(driver);
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<String> getModules(WebDriver driver) {
        try {
        ArrayList<String> event = new ArrayList<>();
        WebElement body = driver.findElement(By.cssSelector("table.e-schedule-table.e-content-table"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
            for (WebElement row : rows) {
            WebElement day = row.findElements(By.tagName("td")).get(1);
            WebElement slots = day.findElement(By.tagName("ul"));
            for (WebElement slot : slots.findElements(By.tagName("li"))) {
                WebElement div = slot.findElement(By.tagName("div"));
                event.add(div.getAttribute("aria-label"));
            }
        }
            return event;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            driver.quit();
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint() {
        return "this is working";
    }

    @GetMapping("/getModules/{moduleNames}")
    @ResponseBody
    public ArrayList<String> getModules(@PathVariable String moduleNames) {
        String[] modules = moduleNames.split(",");

        return getModules(modules);
    }

    @GetMapping("/presentation")
    @ResponseBody
    public String presentation() {
        return "<h1><b>Hope you're enjoying the Presentation :)</b></h1>";
    }




}
