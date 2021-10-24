package pageObjects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BasePage class consists of objects and methods used by all page objects in the project.
 */
public class BasePage {
    // objects
    WebDriver driver;
    Actions actions ;
    WebDriverWait wait;
    JavascriptExecutor js;

    // elements
    @FindBy(css = "#loading")
    WebElement loading;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver,this);
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver,30);
    }

    // elements actions
    public void fillText(WebElement el, String text){
        elementHighLight(el, "yellow");
        el.clear();
        el.sendKeys(text);
    }

    public void click(WebElement el){
        elementHighLight(el, "yellow");
        el.click();
    }

    public String getText(WebElement el){
        return el.getText();
    }

    public void elementHighLight(WebElement element, String color){
        //keep the old style to change it back
        String originalStyle = element.getAttribute("style");
        String newStyle = "background-color:" + color + ";" + originalStyle;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Change the style
        js.executeScript("var tmpArguments = arguments;setTimeout(function () {tmpArguments[0].setAttribute('style', '" + newStyle + "');},0);",
                element);
        // Change the style back after few miliseconds
        js.executeScript("var tmpArguments = arguments;setTimeout(function () {tmpArguments[0].setAttribute('style', '"
                + originalStyle + "');},400);", element);
    }

    // for mouse roll over purposes
    public void moveTo(WebElement el){
        actions.moveToElement(el).build().perform();
    }

    // page verification
    public boolean isPageDisPlayed(WebElement el){
        return el.isDisplayed();
    }

    // alerts actions
    public void allertSendText(String text){
        driver.switchTo().alert().sendKeys(text);
    }

    public void allertAccept(){
        driver.switchTo().alert().accept();
    }

    public void allertcancel(){
        driver.switchTo().alert().dismiss();
    }


    // sleep
    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // loading
    public void loading(){
        wait.until(ExpectedConditions.visibilityOf(loading));
        wait.until(ExpectedConditions.invisibilityOf(loading));
    }

    public void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return js.executeScript("return document.readyState").equals("complete");
                    }
                };
        wait.until(pageLoadCondition);
    }
}
