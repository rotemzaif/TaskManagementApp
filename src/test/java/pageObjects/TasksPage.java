package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.DateAnalysis;

import java.text.ParseException;
import java.util.*;

/**
 * TasksPage Class consists of tabs, tasks and page elements, elements actions and validations.
 * It has separated methods for actions and validations for each page section (i.e. tabs, tasks)
 */
public class TasksPage extends BasePage {
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
    @FindBy(css = ".list-action")
    private List<WebElement> tabActionsBtnListEl; // will be initialized when tab is selected
    @FindBy(css = "#listmenucontainer > ul > li")
    private List<WebElement> tabActionList;
    @FindBy(css = "#tabs_buttons")
    private WebElement tabsSelectListBtn;
    @FindBy(css = "#slmenucontainer > ul > li")
    private List<WebElement> tabsSelectList;
    @FindBy(css = ".mtt-tabs > li > a > span")
    private List<WebElement> tabNameListEl;


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
    @FindBy(css = ".taskactionbtn")
    private List<WebElement> taskActionBtnList;
    @FindBy(css = "#taskcontextcontainer > ul > li")
    private List<WebElement> taskActionListEl;
    @FindBy(css = "#taskcontextcontainer")
    private WebElement taskActionsMenu;

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

    public List<WebElement> getTabList() {
        return tabList;
    }

    /**
     * @description this method iterates through the tab element list, checks if the tab name equals/contains the tab name arg;
     * if it matches, it gets its ID and adds it to the list
     * @param tabName - String
     * @param searchType - enum EQUALS/CONTAINS
     * @return List<Integer> of tab id
     */
    public List<String> getTabIdListForName(String tabName, SearchType searchType){
        List<String> tabIdList = new ArrayList<>();
        if(tabList.size() == tabNameListEl.size()){
            String name;
            for (int i = 0; i < tabList.size(); i++) {
                name = getText(tabNameListEl.get(i));
                if(searchType == SearchType.EQUAL){
                    if(name.equals(tabName))
                        tabIdList.add(tabList.get(i).getAttribute("id"));
                }
                else if(searchType == SearchType.CONTAINS){
                    if(name.contains(tabName))
                        tabIdList.add(tabList.get(i).getAttribute("id"));
                }
            }
        }
        else System.out.println("num of tabs names doesn't match num of visible tabs");
        return tabIdList;
    }

    public List<WebElement> getTabsSelectList() {
        return tabsSelectList;
    }

    public Map<String, List<Object>> getTabsMap() {
        Map<String, List<Object>> tabsMap = new HashMap<>();
        String tabId, tabName;
        WebElement tabEl, tabActionsBtn;
        if (tabList.size() == tabNameListEl.size() && tabList.size() == tabActionsBtnListEl.size()-1) {
            for (int i = 0; i < tabList.size(); i++) {
                tabId = tabList.get(i).getAttribute("id");
                tabEl = tabList.get(i);
                tabName = getText(tabNameListEl.get(i));
                tabActionsBtn = tabActionsBtnListEl.get(i);
                tabsMap.put(tabId, Arrays.asList(tabEl, tabName, tabActionsBtn));
            }
        } else System.out.println("num of actual tabs doesn't match num of tab names or num of tab actions btn");
        return tabsMap;
    }

    public Map<String, WebElement> getTabsFromListMap() {
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
    public String getTabNameById(String tabId) {
        return (String) getTabsMap().get(tabId).get(1);
//        return getText(getTabsMap().get(tabId).findElement(By.cssSelector("a>span")));
    }

    /**
     * @return - tab name - String
     * @description this method iterates on all tabs and checks which tab is currently selected and returns its name
     */
    public String getCurrentTabName() {
        String tabName = "";
        for (WebElement tab : tabList) {
            if (tab.getAttribute("class").contains("selected")) {
                tabName = getText(tab.findElement(By.cssSelector("a > span")));
                break;
            }
        }
        return tabName;
    }

    /**
     * this method checks which is tab is currently selected and returns its ID
     *
     * @return - tabId - String
     */
    public String getCurrentTabId() {
        String tabId = "";
        for (WebElement tab : tabList) {
            if (tab.getAttribute("class").contains("selected")) {
                tabId = tab.getAttribute("id");
                break;
            }
        }
        return tabId;
    }

    /**
     * this checks which sort option is selected and returns its name/description
     *
     * @param tabId - string
     * @return selectedOption - string
     */
    public String getTabSortOption(String tabId) {
        String selectedOption = "";
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
        for (WebElement op : tabActionList) {
            if (op.getAttribute("class").contains("sort") &&
                    op.getAttribute("class").contains("checked")) {
                selectedOption = getText(op);
                if (!selectedOption.equals("Sort by hand"))
                    selectedOption = selectedOption.substring(0, selectedOption.length() - 1).trim();
                break;
            }
        }
        return selectedOption;
    }

    // tab action methods //

    /**
     * @description method that creates a new tab (or cancels), sets tab sort option according to arg entered, and select/un-select
     * 'Show completed tasks' according to state arg entered
     * @param tabName - String
     * @param allertState - enum ACCPET/CANCEL
     * @param sortOption - String
     * @param sctSate - enum SELECT/UNSELECT, sct - show completed tasks
     * @return tabId - new tab Id - String
     */
    public String createNewTab(String tabName, AlertState allertState, String sortOption, OptionState sctSate){
        String tabId = "";
        click(newTabBtn);
        allertSendText(tabName);
        if (allertState == AlertState.ACCEPT) {
            allertAccept();
            loading();
            tabId = tabList.get(tabList.size() - 1).getAttribute("id");
            // set tab sort display
            if(!sortOption.isEmpty()){
                if(!setTabSortDisplay(tabId, sortOption))
                    System.out.println("'" + sortOption + "' was not found in the tab action menu list!\n");
            }
            // set tab 'show completed tasks' state - select/un-select
            if(sctSate != null){
                if(!setTabCompletedTasksDisplay(tabId, sctSate))
                    System.out.println("'Show completed tasks' option was not found in the tab action menu list!\n");
            }
        } else if (allertState == AlertState.CANCEL) {
            allertcancel();
            tabId = null;
        }
        return tabId;
    }

    /**
     * this method clicks on requested tab given its id
     *
     * @param tabId
     */
    public void goToTabById(String tabId) {
        WebElement tab = (WebElement) getTabsMap().get(tabId).get(0);
        click(tab);
        loading();
    }

    /**
     * @param tabId - string
     * @description this method opens the 'Select list' menu and clicks on the requested tab name
     */
    public void goToTabFromList(String tabId) {
        String tabIdNum = tabId.split("_")[1];
        String tabIdInList = "slmenu_list:" + tabIdNum;
        click(tabsSelectListBtn);
        sleep(200);
        click(getTabsFromListMap().get(tabIdInList));
        WebElement tab = (WebElement) getTabsMap().get(tabId).get(0);
//        WebElement tab = getTabsMap().get(tabId);
//        tabActionListBtn = tab.findElement(By.cssSelector(".list-action"));
    }

    /**
     * this method clicks on the requested tab, opens its action list menu and select the 'Delete list', and accepts or cancels it according to param
     *
     * @param tabId - string - for clicking the requested tab
     * @param state - accept/cancel indication for delete action
     */
    public void deleteTabById(String tabId, AlertState state) {
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
//        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Delete list")) {
                click(op);
                break;
            }
        }
        if (state == AlertState.ACCEPT) {
            allertAccept();
            loading();
        } else if (state == AlertState.CANCEL)
            allertcancel();

    }

    /**
     * this method opens the tab action list menu and selects the 'Rename list' option, enters the new tab name, and accepts/cancels
     *
     * @param tabId   - string - the tab id in which we want to rename
     * @param state   - string - indicates if we want accept or cancel the rename action
     * @param tabNewName - string - the new tab name we want to enter
     */
    public void renameTab(String tabId, AlertState state, String tabNewName) {
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
//        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Rename list")) {
                click(op);
                break;
            }
        }
        allertSendText(tabNewName);
        if (state == AlertState.ACCEPT) {
            allertAccept();
            loading();
        } else if (state == AlertState.CANCEL)
            allertcancel();
    }

    /**
     * @param tabId - string
     * @description this method selects the given tab, opens its action list menu and selects 'Hide list'
     */
    public void hideTab(String tabId) {
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
//        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Hide list")) {
                click(op);
                break;
            }
        }
        loading();
    }

    /**
     * @description this method moves to the desired tab and sets its sort display according to param
     * @param tabId - String
     * @param sortOption - String
     */
    public boolean setTabSortDisplay(String tabId, String sortOption) {
        boolean optionFound = false;
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(sortOption)) {
                optionFound = true;
                if (!op.getAttribute("class").contains("checked"))
                    click(op);
                else click(tabActionsBtn);
                break;
            }
        }
        return optionFound;
    }

    /**
     * @description this method sets the tab 'Show completed tasks' state according to param
     * @param tabId - string
     * @param state - string - indicates select/un-select option
     */
    public boolean setTabCompletedTasksDisplay(String tabId, OptionState state) {
        boolean optionFound = false;
        String option = "Show completed tasks";
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(option)) {
                optionFound = true;
                if (state == OptionState.SELECT) {
                    if (!op.getAttribute("class").contains("checked")) {
                        click(op);
                        loading();
                        break;
                    } else click(tabActionsBtn);
                } else if (state == OptionState.UNSELECT) {
                    if (op.getAttribute("class").contains("checked")) {
                        click(op);
                        loading();
                        break;
                    } else click(tabActionsBtn);
                }
            }
        }
        return optionFound;
    }

    // tab validation methods //

    /**
     * @param tabId - string
     * @return true/false if tab exist
     * @description validation method which indicates if a tab exist or not in the visible tab list given tabid
     */
    public boolean isTabExistInVisibleList(String tabId) {
        return getTabsMap().containsKey(tabId);
    }

    /**
     * @param tabId - string
     * @return true/false if tab exist
     * @description validation method which indicates if a tab exist or not in the tabs 'Select List' menu given tabid and tab name params
     */
    public boolean isTabExistInSelectList(String tabId) {
        String idInList = "slmenu_list:" + tabId.split("_")[1];
        click(tabsSelectListBtn);
        sleep(200);
        return getTabsFromListMap().containsKey(idInList);
    }

    /**
     * @param tabId
     * @return true/false if tab is visible
     * @description this method checks if a given tab is visible or not
     */
    public boolean isTabVisible(String tabId) {
        WebElement tab = (WebElement) getTabsMap().get(tabId).get(0);
        return tab.isDisplayed();
    }

    /**
     * this method checks if a tab 'Show completed tasks' option is checked or not
     *
     * @param tabId - string
     * @return boolean true/false if option is checked or not
     */
    public boolean isTabCompletedTasksChecked(String tabId) {
        String option = "Show completed tasks";
        boolean result = false;
        goToTabById(tabId);
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        click(tabActionsBtn);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(option)) {
                if (op.getAttribute("class").contains("checked"))
                    result = true;
                break;
            }
        }
        click(tabActionsBtn);
        return result;
    }

    // tasks getters

    public WebElement getTasksTotal() {
        return tasksTotal;
    }

    public List<Task> getTasksList() {
        List<Task> taskList = new ArrayList<>();
        String priority, dueDateText, taskName, note, taskTags;
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
            if (taskrow.getAttribute("class").length() < 10 && getText(taskPriorityEl).equals("0"))
                taskName = getText(taskNameEl);
                // task class name length >= 10 --> detailed task
            else {
                // task priority init
                priority = taskPriorityEl.getAttribute("innerHTML");
                // due dates fields init
                if (taskrow.getAttribute("class").contains("past") || taskrow.getAttribute("class").contains("today") ||
                        taskrow.getAttribute("class").contains("future") ||
                        taskrow.getAttribute("class").contains("soon"))
                    dueDateText = getText(taskrow.findElement(By.cssSelector(".duedate")));
                else
                    dueDateText = "";
                // task name init
                taskName = getText(taskNameEl);
                // task note init
                if (taskrow.getAttribute("class").contains("has-note")) // checking if task has a note
                    note = taskrow.findElement(By.cssSelector(".task-note > span")).getAttribute("innerHTML");
                else
                    note = "";
                // task tag init
                // checking if task has a tag
                if (taskrow.getAttribute("class").contains("tag")) {
                    taskTagListEl = taskrow.findElements(By.cssSelector(".task-tags > a"));
                    for (int i = 0; i < taskTagListEl.size(); i++) {
                        if (i != taskTagListEl.size() - 1)
                            taskTags = taskTags + getText(taskTagListEl.get(i)) + ", ";
                        else
                            taskTags = taskTags + getText(taskTagListEl.get(i));
                    }
                } else
                    taskTags = "";
            }
            taskList.add(new Task(priority, null, dueDateText, taskName, note, taskTags));
        }
        return taskList;
    }

    public int getTotalTasksDisplayVal() {
        String total = getText(tasksTotal);
        return Integer.parseInt(total);
    }

    /**
     * @param taskIndex - String
     * @return taskName - String
     * @description this method gets a taskList index and returns the task's name in that index
     */
    public String getTaskName(int taskIndex) {
        return getTasksList().get(taskIndex).getTaskName();
    }

    /**
     * @return List<String> tag names
     * @description this extracts all tag elements, extracts their name and add it to a list
     */
    public List<String> getTags() {
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

    /**
     * @description method that returns the first simple/advanced task index from the tasklist
     * @param taskType - SIMPLE/ADVANCED - enum
     * @return taskId - String
     */
    public int getTaskIndex(TaskType taskType){
        int taskIndex = 0;
        for (WebElement taskrow : taskElementList) {
            if(taskType == TaskType.SIMPLE){
                if(taskrow.getAttribute("class").length() < 10){
                    taskIndex = taskElementList.indexOf(taskrow);
                    break;
                }
            }
            else if(taskType == TaskType.ADVANCED){
                if(taskrow.getAttribute("class").length() >= 10){
                    taskIndex = taskElementList.indexOf(taskrow);
                    break;
                }
            }
        }
        return taskIndex;
    }

    public Map<String, WebElement> getTaskActionMap(){
        Map<String, WebElement> actions = new HashMap<>();
        String actionName;
        for (WebElement action : taskActionListEl) {
            actionName = getText(action);
            actions.put(actionName, action);
        }
        return actions;
    }

    // tasks action methods

    /**
     * @param task
     * @descrition this method gets a Task object as a param, enters the task name in the simple task edit
     * box and clicks on the 'add task' button
     */
    public void addNewSimpleTask(Task task) {
        fillText(simpleTaskEditBox, task.getTaskName());
        click(simpleTaskAddBtn);
        loading();
    }

    public void goToAdvancedPage() {
        click(advancedBtn);
    }

    public void searchText(String keyword) {
        fillText(searchBox, keyword);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    public void closeSearch() {
        click(searchClose);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    public void openActionsMenu(int taskIndex){
        WebElement taskActionBtn = taskActionBtnList.get(taskIndex);
        moveTo(taskActionBtn);
        sleep(200);
        click(taskActionBtn);
    }

    public void taskEdit(){
        click(getTaskActionMap().get("Edit"));
    }

    // other actions
    public void goToSettingsPage() {
        click(settingsLink);
    }

    // tasks validation methods
    public boolean isAdvancedBtnDisplayed() {
        return advancedBtn.isDisplayed();
    }

    public boolean isTaskActionsMenuDisplayed(){
        return taskActionsMenu.isDisplayed();
    }

    public boolean compareTasks(Task actual, Task expected, Map<String, String> advancedTaskPriorityMap, String shortDateformat, String shortDateCurrentYearFormat) throws ParseException {
        boolean result = true;
        // task priority compare
        String expectedPrio = advancedTaskPriorityMap.get(expected.getTaskPriority());
        if(!actual.getTaskPriority().equals(expectedPrio)){
            System.out.println("actual task priority doesn't match expected task priority!!");
            System.out.println("Expected: " + expectedPrio + "\nActual: " + actual.getTaskPriority());
            result = false;
        }
        // due date text compare
        String expectedDueDateValDisplay = DateAnalysis.getExpectedDateDisplay(expected.getTaskDueDateIn(), shortDateformat, shortDateCurrentYearFormat);
        if(!actual.getDueDateText().equals(expectedDueDateValDisplay)){
            System.out.println("actual task due date text doesn't match expected task due date!!");
            System.out.println("Expected task due date inserted: " + expected.getTaskDueDateIn() + "\nExpected task due date text display: " + expectedDueDateValDisplay
                    + "\nActual task due date text: " + actual.getTaskPriority());
            result = false;
        }
        // task name compare
        if(!actual.getTaskName().equals(expected.getTaskName())){
            System.out.println("actual task name doesn't match expected task name!!");
            System.out.println("actual task name: " + actual.getTaskName() + "\nExpected task name: " + expected.getTaskName());
            result = false;
        }
        // task notes compare
        if(!actual.getTaskNotes().equals(expected.getTaskNotes())){
            System.out.println("actual task note doesn't match expected task note!!");
            System.out.println("actual task name: " + actual.getTaskNotes() + "\nExpected task name: " + expected.getTaskNotes());
            result = false;
        }
        // task tags compare
        String expectedTaskTagString = expected.getTaskTagsString().replaceAll(" ", "");
        List<String> expectedTagList = Arrays.asList(expectedTaskTagString.split(","));
        Map<String,String> actualTaskTagsMap = actual.getTaskTagsMap();
        if(actualTaskTagsMap.size() == expectedTagList.size()){
            for (String tag : expected.getTaskTagsList()) {
                if(!actualTaskTagsMap.containsKey(tag)){
                    System.out.println("actual task doesn't include the entered tag: " + tag);
                    result = false;
                    break;
                }
            }
        }
        else{
            System.out.println("Actual task number of tags doesn't match the number of tags entered!!");
            System.out.println("actual task tags: " + actual.getTaskTagsList().toString());
            System.out.println("expected task tags: " + expected.getTaskTagsList().toString());
            result = false;
        }
        return result;
    }

    // enums
    public enum AlertState {
        ACCEPT, CANCEL;
    }

    public enum SearchType {
        CONTAINS, EQUAL, ;
    }

    public enum OptionState {
        SELECT, UNSELECT;
    }

    public enum TaskType {
        SIMPLE, ADVANCED;
    }
}
