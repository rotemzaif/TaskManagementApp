package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * This class represents the Settings page and used to extract the 'short date' format and short date current year' format
 * used in the application for displaying advanced tasks due date (in case entered);
 * inherits from BasePage;
 */
public class SettingsPage extends BasePage {
    // page elements
    @FindBy(css = "#page_ajax>h3")
    private WebElement settingsLabel;
    @FindBy(css = "#page_ajax > div > .mtt-back-button")
    private WebElement backLink;
    @FindBy(css = "[name='dateformat2']")
    private WebElement shortDateFormat;
    @FindBy(css = "[name='dateformatshort']")
    private WebElement shortDateCurrentYear;

    public SettingsPage(WebDriver driver) {
        super(driver);
    }

    // getters

    public WebElement getSettingsLabel() {
        return settingsLabel;
    }

    public String getShortDateFormat() {
        return shortDateFormat.getAttribute("value");
    }

    public String getShortDateCurrentYear() {
        return shortDateCurrentYear.getAttribute("value");
    }

    // actions
    public void goBackToTasksPage(){
        click(backLink);
    }


}
