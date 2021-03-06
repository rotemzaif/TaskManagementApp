package tests;

import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import pageObjects.TaskList;
import pageObjects.TasksPage;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    // objects
    WebDriver driver;
    TasksPage tp;
    TaskList tl;

    // variables
    String tabId = "";
    String tabName = "";

    @BeforeClass
    public void setup() throws InterruptedException {
        ChromeDriverManager.getInstance(DriverManagerType.CHROME).setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS) ;
        driver.get(Utils.readProperty("homePageURL"));
        tp = new TasksPage(driver);
        Thread.sleep(500);
        String expectedPageTitle = tp.getCurrentTabName() + " " + Utils.readProperty("pageTitle");
        String actualPageTitle = driver.getTitle();
        Assert.assertEquals(actualPageTitle, expectedPageTitle, "'" + expectedPageTitle + "' page is not displayed");
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

    @AfterMethod
    public void failedTest(ITestResult result) {
        //check if the test failed
        if (result.getStatus() == ITestResult.FAILURE ){
            TakesScreenshot ts = (TakesScreenshot)driver;
            File srcFile = ts.getScreenshotAs(OutputType.FILE);
            try {
                //result.getname() method will return current test case name.
                FileUtils.copyFile(srcFile, new File("./ScreenShots/" + result.getTestClass().getName() + "_" + result.getName() + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // common methods

    /**
     * this method checks if target/tested tab is selected;
     * if not selected, then moving to target/tested tab
     * @param tp - TasksPage object
     * @param tabId - target/tested tab id
     */
    public void moveToTargetTab(TasksPage tp, String tabId){
        // checking if target/tested tab is selected; if not, then moving/selecting target tab
        WebElement tab = (WebElement) tp.getTabsMap().get(tabId).get(0);
        if(!tab.getAttribute("class").contains("selected"))
            tp.goToTabById(tabId);
    }
}
