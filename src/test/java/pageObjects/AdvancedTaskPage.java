package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class represents the Advanced Task page and its elements and consists of getter, elements actions and validation methods
 */
public class AdvancedTaskPage extends BasePage {
    // page tasks elements
    @FindBy(css = "#page_taskedit > .mtt-inadd")
    private WebElement newTaskLabel;
    @FindBy(css = "#page_taskedit > .mtt-inedit")
    private WebElement editTaskLabel;
    @FindBy(css = "[name='prio']")
    private WebElement priorityEl;
    private Select prioritySel;
    @FindBy(css = "#duedate")
    private WebElement dueEditBox;
    @FindBy(css = ".form-row > [name='task']")
    private WebElement taskNameEditBox;
    @FindBy(css = "[name='note']")
    private WebElement taskNoteEditBox;
    @FindBy(css = "#edittags")
    private WebElement taskTagEditBox;
    @FindBy(css = "[value='Save']")
    private WebElement saveBtn;
    @FindBy(css = "[value='Cancel']")
    private WebElement cancelBtn;


    // page tags elements
    @FindBy(css = "#alltags_show")
    private WebElement tagsShowAll;
    @FindBy(css = "#alltags_hide")
    private WebElement tagsHideAll;
    @FindBy(css = "#alltags")
    private WebElement allTagsLabel;
    private List<WebElement> tagElList; // is initialized when calling showAllTags() method


    // page assistance variables
    Map<String, String> priorityOptions = new HashMap<>();

    Map<String, String> tagsMap = new HashMap<>();


    // page constructor
    public AdvancedTaskPage(WebDriver driver) {
        super(driver);
        prioritySel = new Select(priorityEl);
        priorityOptions.put("2", "+2");
        priorityOptions.put("1", "+1");
        priorityOptions.put("0", "±0");
        priorityOptions.put("-1", "−1");
    }

    // page getters
    public WebElement getNewTaskLabel() {
        return newTaskLabel;
    }

    public WebElement getEditTaskLabel() {
        return editTaskLabel;
    }

    public String getPriorityVal() {
        return getText(prioritySel.getFirstSelectedOption());
    }

    public Map<String, String> getPriorityOptionsMap() {
        return priorityOptions;
    }

    // page task action methods
    public void selectPriority(String value) {
        prioritySel.selectByValue(value);
    }

    public void enterDueDate(String date) {
        fillText(dueEditBox, date);
    }

    public void enterTaskName(String name) {
        fillText(taskNameEditBox, name);
    }

    public void enterTaskNote(String note) {
        fillText(taskNoteEditBox, note);
    }

    public void enterTaskTag(String tag) {
        fillText(taskTagEditBox, tag);
    }

    // tasks action methods //
    public void submitTask(Task task) {
        if(!task.getTaskPriority().isEmpty() || task.getTaskPriority() != null)
            selectPriority(task.getTaskPriority());
        if(!task.getTaskDueDateIn().isEmpty() || task.getTaskDueDateIn() != null)
            enterDueDate(task.getTaskDueDateIn());
        if(!task.getTaskName().isEmpty() || task.getTaskName() != null)
            enterTaskName(task.getTaskName());
        if(!task.getTaskNotes().isEmpty() || task.getTaskNotes() != null)
            enterTaskNote(task.getTaskNotes());
        if(!task.getTaskTagsString().isEmpty() || task.getTaskTagsString() != null)
            enterTaskTag(task.getTaskTagsString());
        click(saveBtn);
    }

    public void cancelSubmit() {
        click(cancelBtn);
    }


    // tags getter methods
    public Map<String, String> getTagsMap() {
        return tagsMap;
    }


    // tags display methods

    /**
     * @description this method clicks on the 'Show all' button, iterates on tag element list and insert the tag name into a tags map if it doesn't exist
     */
    public void showAllTags() {
        click(tagsShowAll);
        tagElList = driver.findElements(By.cssSelector(".tags-list > a"));
        String key;
        for (WebElement tag : tagElList) {
            key = getText(tag);
            if (!tagsMap.containsKey(key))
                tagsMap.put(key, null);
        }
    }

    public void hideAllTags() {
        click(tagsHideAll);
    }

    // tags display validation methods
    public boolean isAllTagsDisplayed() {
        return allTagsLabel.isDisplayed();
    }


    // page validation methods
}
