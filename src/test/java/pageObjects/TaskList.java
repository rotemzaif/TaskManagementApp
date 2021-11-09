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
 * this class derives from class TaskPage and relates to all tasks aspects:
 * - creating new simple/advanced tasks
 * - task list with existing tasks
 * - search text
 * - task actions (edit, edit note, change priority, move to another tab, delete)
 * the class contains all task related elements, support and validation methods
 */
public class TaskList extends TasksPage {

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
    private WebElement tasksTotalEl;

    // task actions elements
    @FindBy(css = ".taskactionbtn")
    private List<WebElement> taskActionBtnList;
    @FindBy(css = "#taskcontextcontainer > ul > li")
    private List<WebElement> taskActionListEl;
    @FindBy(css = "#taskcontextcontainer")
    private WebElement taskActionsMenu;
    @FindBy(css = ".task-note-area")
    private List<WebElement> taskNoteAreaListel;
    @FindBy(css = "textarea")
    private List<WebElement> taskNoteTextAreaListEl;
    @FindBy(css = ".mtt-action-note-save")
    private List<WebElement> taskNoteSaveBtnListEl;
    @FindBy(css = ".mtt-action-note-cancel")
    private List<WebElement> taskNoteCancelBtnListEl;
    @FindBy(css = ".task-toggle")
    private List<WebElement> taskToggleListel;
    @FindBy(css = ".task-note")
    private List<WebElement> taskNoteDisplayedListEl;
    @FindBy(css = "#cmenupriocontainer>ul>li")
    private List<WebElement> taskActionsPriorityListel;
    @FindBy(css = "#cmenulistscontainer>ul>li")
    private List<WebElement> taskActionsTabListel;

    // search text related elements
    @FindBy(css = "#taskcontainer")
    private WebElement taskListContainer;
    @FindBy(css = "#search")
    private WebElement searchBox;
    @FindBy(css = "#search_close")
    private WebElement searchClose;

    // class properties
    Map<String, WebElement> taskPriorityMap = new HashMap<>();

    // constructor
    public TaskList(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.visibilityOf(taskListContainer));
        wait.until(ExpectedConditions.visibilityOf(tasksTotalEl));
        String key;
        for (WebElement prio : taskActionsPriorityListel) {
            key = prio.getAttribute("id").split(":")[1];
            taskPriorityMap.put(key, prio);
        }
    }

    // task getter methods

    /**
     * @return a list of Task objects based on the tasks displayed in the list
     */
    public List<Task> getTasksList() {
        List<Task> taskList = new ArrayList<>();
        String priority, dueDateText, taskName, note, taskTags;
        WebElement taskPriorityEl, taskNameEl;
        List<WebElement> taskTagListEl = new ArrayList<>();
        String taskRowClassVal;
        for (WebElement taskrow : taskElementList) {
            taskRowClassVal = taskrow.getAttribute("class");
            // task elements
            taskPriorityEl = taskrow.findElement(By.cssSelector(".task-prio"));
            taskNameEl = taskrow.findElement(By.cssSelector(".task-title"));
            // local variables for creating a task
            priority = "";
            dueDateText = "";
            note = "";
            taskTags = "";
            // checking if task is simple; i.e. simple task class name length is 8 characters and priority is 0
            if (taskRowClassVal.length() < 10 && getText(taskPriorityEl).equals("0"))
                taskName = getText(taskNameEl);
                // task class name length >= 10 --> detailed task
            else {
                // task priority init
                priority = taskPriorityEl.getAttribute("innerHTML");
                // due dates fields init
                if (taskRowClassVal.contains("past") || taskRowClassVal.contains("today") || taskRowClassVal.contains("future") ||
                        taskRowClassVal.contains("soon"))
                    dueDateText = getText(taskrow.findElement(By.cssSelector(".duedate")));
                else
                    dueDateText = "";
                // task name init
                taskName = getText(taskNameEl);
                // task note init
                if (taskRowClassVal.contains("has-note")) // checking if task has a note
                    note = taskrow.findElement(By.cssSelector(".task-note > span")).getAttribute("innerHTML");
                else
                    note = "";
                // task tag init
                // checking if task has a tag
                if (taskRowClassVal.contains("tag")) {
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

    /**
     * @return - map of task elements; key = taskId, value = task element; based on task row element list
     */
    public Map<String, WebElement> getTasksMap(){
        Map<String, WebElement> tasksMap = new HashMap<>();
        String taskId;
        for (WebElement task : taskElementList) {
            taskId = task.getAttribute("id");
            tasksMap.put(taskId, task);
        }
        return tasksMap;
    }

    /**
     * @param taskType - SIMPLE/ADVANCED - enum
     * @return taskId - String
     * @description method that returns the first simple/advanced task index from the tasklist
     */
    public int getTaskIndex(TaskType taskType, TaskAttribute attribute) {
        int taskIndex = -1;
        String taskClass;
        for (WebElement taskrow : taskElementList) {
            taskClass = taskrow.getAttribute("class");
            if (taskType == TaskType.SIMPLE) {
                if (taskClass.length() < 10) {
                    taskIndex = taskElementList.indexOf(taskrow);
                    break;
                }
            } else if (taskType == TaskType.ADVANCED) {
                if (taskClass.length() >= 10) {
                    if (attribute != null) {
                        switch (attribute) {
                            case DUEDATE:
                                if (taskClass.contains("past") || taskClass.contains("future") || taskClass.contains("today") || taskClass.contains("soon"))
                                    taskIndex = taskElementList.indexOf(taskrow);
                                break;
                            case NOTE:
                                if (taskClass.contains("task-has-note"))
                                    taskIndex = taskElementList.indexOf(taskrow);
                                break;
                            case TAGS:
                                if (taskClass.contains("tag"))
                                    taskIndex = taskElementList.indexOf(taskrow);
                                break;
                        }
                    } else taskIndex = taskElementList.indexOf(taskrow);
                    break;
                }
            }
        }
        return taskIndex;
    }

    /**
     * @param index - String - index of the task element in the task element list
     * @return - taskId - String
     */
    public String getTaskId(int index){
        return taskElementList.get(index).getAttribute("id");
    }

    /**
     * @return - tasks total element
     */
    public WebElement getTasksTotalEl() {
        return tasksTotalEl;
    }

    public int getTotalTasksDisplayVal() {
        String total = getText(tasksTotalEl);
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
     * @return map of tasks actions where key = action name (string) and value = action (element)
     */
    public Map<String, WebElement> getTaskActionMap() {
        Map<String, WebElement> actions = new HashMap<>();
        String actionName;
        for (WebElement action : taskActionListEl) {
            actionName = getText(action);
            actions.put(actionName, action);
        }
        return actions;
    }

    public String getTaskDisplayedNoteText(int index) {
        return getText(taskNoteDisplayedListEl.get(index));
    }

    public String getTaskPriorityValue(String key) {
        WebElement priority = taskPriorityMap.get(key);
        return priority.getAttribute("textContent");
    }

    // task creation related methods
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

    // task text search related methods
    public void searchText(String keyword) {
        fillText(searchBox, keyword);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskListContainer));
    }

    public void closeSearch() {
        click(searchClose);
        loading();
        wait.until(ExpectedConditions.visibilityOfAllElements(taskElementList));
    }

    // task actions (edit, note edit, priority edit, delete, move to to tab) related methods

    public void openTaskActionsMenu(int index){
        WebElement taskActionBtn = taskActionBtnList.get(index);
        moveTo(taskActionBtn);
        wait.until(ExpectedConditions.elementToBeClickable(taskActionBtn));
        click(taskActionBtn);
    }

    /**
     * @description - this method open the task actions menu (per task index) and clicks on the action per input
     * @param index - int - task index - for selecting the right action menu from a list
     * @param action - string - the action required
     * @return - boolean - true/false if the action menu is opened and displayed
     */
    public boolean selectTaskAction(int index, String action) {
        boolean menuOpened = true;
        WebElement taskActionBtn = taskActionBtnList.get(index);
        moveTo(taskActionBtn);
        wait.until(ExpectedConditions.elementToBeClickable(taskActionBtn));
        click(taskActionBtn);
        if (!isTaskActionsMenuDisplayed())
            menuOpened = false;
        else
            click(getTaskActionMap().get(action));
        return menuOpened;
    }

    /**
     * this method is part of the task action 'edit nonte' flow which edits a new or existing task note
     * @param index - int - task index in the list
     * @param note - string - note text to enter
     * @param action - enum - save or cancel
     */
    public void editTaskNote(int index, String note, TaskNoteConf action) {
        WebElement noteTextArea = taskNoteTextAreaListEl.get(index);
        WebElement saveBtn = taskNoteSaveBtnListEl.get(index);
        WebElement cancelBtn = taskNoteCancelBtnListEl.get(index);
        fillText(noteTextArea, note);
        if (action == TaskNoteConf.SAVE) {
            click(saveBtn);
            loading();
        } else if (action == TaskNoteConf.CANCEL)
            click(cancelBtn);
    }

    /**
     * this method refers to the toggle elements of the left side of the task row, which displays the task note text
     * @param index - int - task index - for clicking on the right toggle element
     * @param state - enum - open/close
     */
    public void toggleTaskNoteDisplay(int index, TaskNoteToggleState state) {
        if (state == TaskNoteToggleState.OPEN) {
            if (!isTaskNoteDisplayed(index)) // toggle state = closed --> then open
                click(taskToggleListel.get(index));
        } else if (state == TaskNoteToggleState.CLOSE) {
            if (isTaskNoteDisplayed(index)) // toggle state = opened --> then close
                click(taskToggleListel.get(index));
        }

    }

    public void editTaskPriority(String priority) {
        click(taskPriorityMap.get(priority));
    }

    /**
     * this method is a part of the task action 'move to' tab which moves to the first enabled (not hidden) tab and returns its id
     * @return - tabId - string
     */
    public String moveToTab() {
        String tabId = "";
        boolean isTabElEnabled;
        for (WebElement el : taskActionsTabListel) {
            isTabElEnabled = !el.getAttribute("class").contains("disabled");
            if (isTabElEnabled) {
                tabId = el.getAttribute("id").split(":")[1];
                wait.until(ExpectedConditions.elementToBeClickable(el));
                click(el);
                loading();
                break;
            }
        }
        return tabId;
    }

    /**
     * this method is a part of the task action 'Delete' flow and deletes a task or not based on input arg (enum: accept/cancel)
     * @param state - enum - ACCEPT/CANCEL
     */
    public void deleteTask(AlertState state){
        if(state == AlertState.ACCEPT){
            allertAccept();
            loading();
        }
        else if(state == AlertState.CANCEL)
            allertcancel();
    }

    // task validation methods
    public boolean isAdvancedBtnDisplayed() {
        return advancedBtn.isDisplayed();
    }

    public boolean compareTasks(Task actual, Task expected, ExpectedTaskType expectedTaskType, Map<String, String> advancedTaskPriorityMap, String shortDateformat, String shortDateCurrentYearFormat) throws ParseException {
        boolean result = true;
        // task priority compare
        String expectedPrio = "";
        String expectedDueDateValDisplay = "";
        List<String> expectedTagList = new ArrayList<>();
        if(expectedTaskType == ExpectedTaskType.ENTERED){
            expectedPrio = advancedTaskPriorityMap.get(expected.getTaskPriority());
            expectedDueDateValDisplay = DateAnalysis.getExpectedDateDisplay(expected.getTaskDueDateIn(), shortDateformat, shortDateCurrentYearFormat);
            String expectedTaskTagString = expected.getTaskTagsString().replaceAll(" ", "");
            expectedTagList = Arrays.asList(expectedTaskTagString.split(","));
        }
        else if(expectedTaskType == ExpectedTaskType.EXISTING){
            expectedPrio = expected.getTaskPriority();
            expectedDueDateValDisplay = expected.getDueDateText();
            expectedTagList = expected.getTaskTagsList();
        }
        if (!actual.getTaskPriority().equals(expectedPrio)) {
            System.out.println("actual task priority doesn't match expected task priority!!");
            System.out.println("Expected: " + expectedPrio + "\nActual: " + actual.getTaskPriority());
            result = false;
        }
        // due date text compare
        if (!actual.getDueDateText().equals(expectedDueDateValDisplay)) {
            System.out.println("actual task due date text doesn't match expected task due date!!");
            System.out.println("Expected task due date inserted: " + expected.getTaskDueDateIn() + "\nExpected task due date text display: " + expectedDueDateValDisplay
                    + "\nActual task due date text: " + actual.getTaskPriority());
            result = false;
        }
        // task name compare
        if (!actual.getTaskName().equals(expected.getTaskName())) {
            System.out.println("actual task name doesn't match expected task name!!");
            System.out.println("actual task name: " + actual.getTaskName() + "\nExpected task name: " + expected.getTaskName());
            result = false;
        }
        // task notes compare
        if (!actual.getTaskNotes().equals(expected.getTaskNotes())) {
            System.out.println("actual task note doesn't match expected task note!!");
            System.out.println("actual task name: " + actual.getTaskNotes() + "\nExpected task name: " + expected.getTaskNotes());
            result = false;
        }
        // task tags compare
        Map<String, String> actualTaskTagsMap = actual.getTaskTagsMap();
        if (actualTaskTagsMap.size() == expectedTagList.size()) {
            for (String tag : expected.getTaskTagsList()) {
                if (!actualTaskTagsMap.containsKey(tag)) {
                    System.out.println("actual task doesn't include the entered tag: " + tag);
                    result = false;
                    break;
                }
            }
        } else {
            System.out.println("Actual task number of tags doesn't match the number of tags entered!!");
            System.out.println("actual task tags: " + actual.getTaskTagsList().toString());
            System.out.println("expected task tags: " + expected.getTaskTagsList().toString());
            result = false;
        }
        return result;
    }

    public boolean isTaskActionsMenuDisplayed() {
        return taskActionsMenu.isDisplayed();
    }

    public boolean isTaskNoteAreaDisplayed(int index) {
        return taskNoteAreaListel.get(index).isDisplayed();
    }

    public boolean taskHasNotes(int index) {
        return taskElementList.get(index).getAttribute("class").contains("note");
    }

    public boolean isTaskToggleDisplayed(int index) {
        return taskToggleListel.get(index).isDisplayed();
    }

    public boolean isTaskNoteDisplayed(int index) {
        return taskNoteDisplayedListEl.get(index).isDisplayed();
    }

    // enums

    public enum TaskType {
        SIMPLE, ADVANCED;
    }

    public enum TaskNoteConf {
        SAVE, CANCEL;
    }

    public enum TaskAttribute {
        DUEDATE, NOTE, TAGS;
    }

    public enum TaskNoteToggleState {
        OPEN, CLOSE;
    }

    public enum ExpectedTaskType {
        ENTERED, EXISTING;
    }



}
