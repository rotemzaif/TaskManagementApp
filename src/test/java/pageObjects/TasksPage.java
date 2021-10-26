package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TasksPage Class consists of tabs, tasks and page elements, elements actions and validations.
 * It has separated methods for actions and validations for each page section (i.e. tabs, tasks)
 */
public class TasksPage extends BasePage{
    // page elements
    @FindBy(css = ".topblock-title>h2")
    private WebElement pageLabel;
    @FindBy(css = "#settings")
    private WebElement settingsLink;

    // tab elements
    @FindBy(css = ".mtt-tabs-add-button")
    private WebElement newTabBtn;
    @FindBy(css = ".mtt-tabs.ui-sortable > li")
    private List<WebElement> tabList;
    private WebElement tabActionListBtn; // will be initialized when tab is selected
    @FindBy(css = "#listmenucontainer > ul > li")
    private List<WebElement> tabActionList;
    @FindBy(css = "#tabs_buttons")
    private WebElement tabsSelectListBtn;
    @FindBy(css = "#slmenucontainer > ul > li")
    private List<WebElement> tabsSelectList;

    // create task elements
    @FindBy(css = "#task")
    private WebElement simpleTaskEditBox;
    @FindBy(css = "#newtask_submit")
    private WebElement simpleTaskAddBtn;
    @FindBy(css = "#newtask_adv")
    private WebElement advancedBtn;

    // task list elements
    @FindBy(css = "#tasklist > li")
    private List<WebElement> taskElementList;
    @FindBy(css = "#total")
    private WebElement tasksTotal;

    // other elements
    @FindBy(css = "#tagcloudbtn")
    WebElement tagsBtn;
    @FindBy(css = "#search")
    WebElement searchBox;
    @FindBy(css = "#search_close")
    WebElement searchClose;


    // constructor //
    public TasksPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.visibilityOfAllElements(tasksTotal));
//        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    // tab getters //

    public List<WebElement> getTabList(){
        return tabList;
    }

    /**
     * @description this method iterates on each tab, checks if it matches the tab name parameter;
     * if it matches, it gets its ID and adds it to the list
     * @param tabName
     * @return List<Integer> of tab index in the tab list
     */
    public List<String> getTabIdListForName(String tabName){
        String name,tabId;
        List<String> tabIdList = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            name = getText(tabList.get(i).findElement(By.cssSelector("a>span")));
            if(name.equals(tabName)){
                tabId = tabList.get(i).getAttribute("id");
                tabIdList.add(tabId);
            }
        }
        return tabIdList;
    }

    public List<WebElement> getTabsSelectList(){
        return tabsSelectList;
    }

    public Map<String, WebElement> getTabsMap(){
        Map<String, WebElement> tabsMap = new HashMap<>();
        String key;
        for (WebElement tab : tabList) {
            key = tab.getAttribute("id");
            tabsMap.put(key, tab);
        }
        return tabsMap;
    }

    public Map<String, WebElement> getTabsFromListMap(){
        Map<String, WebElement> tabsSelectListMap = new HashMap<>();
        String key;
        for (int i = 2; i < tabsSelectList.size(); i++) {
            key = tabsSelectList.get(i).getAttribute("id");
            tabsSelectListMap.put(key, tabsSelectList.get(i));
        }
        return tabsSelectListMap;
    }

    /**
     * @param tabId - string - the tab id we want to get its name
     * @return - string - the tab name
     */
    public String getTabNameById(String tabId){
        return getText(getTabsMap().get(tabId).findElement(By.cssSelector("a>span")));
    }

    /**
     * @description this method iterates on all tabs and checks which tab is currently selected and returns its name
     * @return - tab name - String
     */
    public String getCurrentTabName(){
        String tabName = "";
        for (WebElement tab : tabList) {
            if(tab.getAttribute("class").contains("selected")){
                tabName = getText(tab.findElement(By.cssSelector("a > span")));
                break;
            }
        }
        return tabName;
    }

    /**
     * this method checks which is tab is currently selected and returns its ID
     * @return - tabId - String
     */
    public String getCurrentTabId(){
        String tabId = "";
        for (WebElement tab : tabList) {
            if(tab.getAttribute("class").contains("selected")){
                tabId = tab.getAttribute("id");
                break;
            }
        }
        return tabId;
    }

    /**
     * this checks which sort option is selected and returns its name/description
     * @param tabId - string
     * @return selectedOption - string
     */
    public String getTabSortOption(String tabId){
        String selectedOption = "";
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(op.getAttribute("class").contains("sort") &&
                    op.getAttribute("class").contains("checked")){
                selectedOption = getText(op);
                if(!selectedOption.equals("Sort by hand"))
                    selectedOption = selectedOption.substring(0, selectedOption.length()-1).trim();
                break;
            }
        }
        return selectedOption;
    }

    // tab action methods //
    /**
     * function that creates a new tab
     * @param tabName - string - tab name to enter
     * @param state - string - indicates whether to accept or cancel the allert
     * @return - string - tab id - in case of 'accept' and null in case of 'cancel'
     */
    public String createNewTab(String tabName, String state){
        String tabId = "";
        click(newTabBtn);
        allertSendText(tabName);
        if(state.equals("accept")){
            allertAccept();
            loading();
            tabId = tabList.get(tabList.size()-1).getAttribute("id");
        }
        else if(state.equals("cancel")){
            allertcancel();
            tabId = null;
        }
        return tabId;
    }

    /**
     * this method clicks on requested tab given its id
     * @param tabId
     */
    public void goToTabById(String tabId){
        WebElement tab = getTabsMap().get(tabId);
        click(tab);
        loading();
        tabActionListBtn = tab.findElement(By.cssSelector(".list-action"));
    }

    /**
     * @description this method opens the 'Select list' menu and clicks on the requested tab name
     * @param tabId - string
     */
    public void goToTabFromList(String tabId){
        String tabIdNum = tabId.split("_")[1];
        String tabIdInList = "slmenu_list:" + tabIdNum;
        click(tabsSelectListBtn);
        sleep(200);
        click(getTabsFromListMap().get(tabIdInList));
        WebElement tab = getTabsMap().get(tabId);
        tabActionListBtn = tab.findElement(By.cssSelector(".list-action"));
    }

    /**
     * this method clicks on the requested tab, opens its action list menu and select the 'Delete list', and accepts or cancels it according to param
     * @param tabId - string - for clicking the requested tab
     * @param state - accept/cancel indication for delete action
     */
    public void deleteTabById(String tabId, String state){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Delete list")){
                click(op);
                break;
            }
        }
        if(state.equals("accept")){
            allertAccept();
            loading();
        }
        else if(state.equals("cancel"))
            allertcancel();

    }

    /**
     * this method opens the tab action list menu and selects the 'Rename list' option, enters the new tab name, and accepts/cancels
     * @param tabId - string - the tab id in which we want to rename
     * @param state - string - indicates if we want accept or cancel the rename action
     * @param tabName - string - the new tab name we want to enter
     */
    public void renameTab(String tabId, String state, String tabName){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Rename list")){
                click(op);
                break;
            }
        }
        allertSendText(tabName);
        if(state.equals("accept")){
            allertAccept();
            loading();
        }
        else if(state.equals("cancel"))
            allertcancel();
    }

    /**
     * @description this method selects the given tab, opens its action list menu and selects 'Hide list'
     * @param tabId - string
     */
    public void hideTab(String tabId){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Hide list")){
                click(op);
                break;
            }
        }
        loading();
    }

    /**
     * this method moves to the desired tab and sets its sort display according to param
     * @param tabId
     * @param sortOption
     */
    public boolean setTabSortDisplay(String tabId, String sortOption) {
        boolean optionFound = false;
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals(sortOption)){
                optionFound = true;
                if(!op.getAttribute("class").contains("checked"))
                    click(op);
                else click(tabActionListBtn);
                break;
            }
        }
        return optionFound;
    }

    /**
     * this method sets the tab 'Show completed tasks' state according to param
     * @param tabId - string
     * @param state - string - indicates select/un-select option
     */
    public boolean setTabCompletedTasksDisplay(String tabId, String state){
        boolean optionFound = false;
        String option = "Show completed tasks";
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals(option)){
                optionFound = true;
                if(state.equals("select")){
                    if(!op.getAttribute("class").contains("checked")){
                        click(op);
                        loading();
                        break;
                    }
                    else click(tabActionListBtn);
                }
                else if(state.equals("un-select")){
                    if(op.getAttribute("class").contains("checked")){
                        click(op);
                        loading();
                        break;
                    }
                    else click(tabActionListBtn);
                }
            }
        }
        return optionFound;
    }

    // tab validation methods //
    /**
     * @description validation method which indicates if a tab exist or not in the visible tab list given tabid
     * @param tabId - string
     * @return true/false if tab exist
     */
    public boolean  isTabExistInVisibleList(String tabId){
        return getTabsMap().containsKey(tabId);
    }

    /**
     * @description validation method which indicates if a tab exist or not in the tabs 'Select List' menu given tabid and tab name params
     * @param tabId - string
     * @return true/false if tab exist
     */
    public boolean  isTabExistInSelectList(String tabId){
        String idInList = "slmenu_list:" + tabId.split("_")[1];
        click(tabsSelectListBtn);
        sleep(200);
        return getTabsFromListMap().containsKey(idInList);
    }

    /**
     * @description this method checks if a given tab is visible or not
     * @param tabId
     * @return true/false if tab is visible
     */
    public boolean isTabVisible(String tabId){
        return getTabsMap().get(tabId).isDisplayed();
    }

    /**
     * this method checks if a tab 'Show completed tasks' option is checked or not
     * @param tabId - string
     * @return boolean true/false if option is checked or not
     */
    public boolean isTabCompletedTasksChecked(String tabId){
        String option = "Show completed tasks";
        boolean result = false;
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals(op)){
                if(op.getAttribute("class").contains("checked"))
                    result = true;
                break;
            }
        }
        click(tabActionListBtn);
        return result;
    }

    // tasks getters
    public List<Task> getTasksList(){
        List<Task> taskList = new ArrayList<>();
        String priority, dueDateText,taskName, note, taskTags;
        WebElement taskPriorityEl, taskNameEl;
        List<WebElement> taskTagListEl = new ArrayList<>();
        for (WebElement taskrow : taskElementList) {
            // task elements
            taskPriorityEl = taskrow.findElement(By.cssSelector(".task-prio"));
            taskNameEl = taskrow.findElement(By.cssSelector(".task-title"));
            // local variables for creating a task
            priority = "";
            dueDateText = "";
            note = "";
            taskTags = "";
            // checking if task is simple; i.e. simple task class name length is 8 characters and priority is 0
            if(taskrow.getAttribute("class").length() < 10 && getText(taskPriorityEl).equals("0"))
                taskName = getText(taskNameEl);
            // task class name length >= 10 --> detailed task
            else {
                // task priority init
                priority = taskPriorityEl.getAttribute("innerHTML");
                // due dates fields init
                if(taskrow.getAttribute("class").contains("past") || taskrow.getAttribute("class").contains("today") ||
                        taskrow.getAttribute("class").contains("future") ||
                        taskrow.getAttribute("class").contains("soon"))
                    dueDateText = getText(taskrow.findElement(By.cssSelector(".duedate")));
                else
                    dueDateText = "";
                // task name init
                taskName = getText(taskNameEl);
                // task note init
                if(taskrow.getAttribute("class").contains("has-note")) // checking if task has a note
                    note = taskrow.findElement(By.cssSelector(".task-note > span")).getAttribute("innerHTML");
                else
                    note = "";
                // task tag init
                // checking if task has a tag
                if(taskrow.getAttribute("class").contains("tag")){
                    taskTagListEl = taskrow.findElements(By.cssSelector(".task-tags > a"));
                    for (int i = 0; i < taskTagListEl.size(); i++) {
                        if(i != taskTagListEl.size()-1)
                            taskTags = taskTags + getText(taskTagListEl.get(i)) + ", ";
                        else
                            taskTags = taskTags + getText(taskTagListEl.get(i));
                    }
                }
                else
                    taskTags = "";
            }
            taskList.add(new Task(priority, null, dueDateText, taskName, note, taskTags));
        }
        return taskList;
    }

    public int getTotalTasksDisplay(){
        String total = getText(tasksTotal);
        return Integer.parseInt(total);
    }

    /**
     * @description this method gets a taskList index and returns the task's name in that index
     * @param taskIndex - String
     * @return taskName - String
     */
    public String getTaskName(int taskIndex){
        return getTasksList().get(taskIndex).getTaskName();
    }

    /**
     * @description this extracts all tag elements, extracts their name and add it to a list
     * @return List<String> tag names
     */
    public List<String> getTags(){
        click(tagsBtn);
        sleep(200);
        List<WebElement> tagElList = driver.findElements(By.cssSelector("#tagcloudcontent > a"));
        List<String> tagList = new ArrayList<>();
        String tagName = "";
        for (WebElement el : tagElList) {
            tagName = getText(el);
            tagList.add(tagName);
        }
        return tagList;
    }

    // tasks action methods

    /**
     * @descrition this method gets a Task object as a param, enters the task name in the simple task edit
     * box and clicks on the 'add task' button
     * @param task
     */
    public void addNewSimpleTask(Task task) {
        fillText(simpleTaskEditBox, task.getTaskName());
        click(simpleTaskAddBtn);
        loading();
    }

    public void goToAdvancedPage(){
        click(advancedBtn);
    }

    public void searchText(String keyword){
        fillText(searchBox, keyword);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    public void closeSearch(){
        click(searchClose);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    // other actions
    public void goToSettingsPage(){
        click(settingsLink);
    }

    // tasks validation methods
    public boolean isAdvancedBtnDisplayed(){
        return advancedBtn.isDisplayed();
    }
}
