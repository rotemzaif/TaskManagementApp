package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageObjects.Task;
import pageObjects.TaskList;
import utils.Utils;

import java.util.List;

public class AddSimpleTaskTest extends BaseTest {
    // test global objects
    Task task;
    // test global variables
    int tasksCounter = 0;
    int totalTaskDisplay = 0;
    int totalTasksInList = 0;

    @Test(description = "creating a new tab for testing simple tasks and setting tab sort display to 'Sort by hand' and un-selecting 'Show completed tasks'")
    public void tc01_create_new_tab_for_testing(){
        tl = new TaskList(driver);
        tabName = "rzf - tasks";
        // checking if there are tabs with the same name; if there are, selecting the 1st one and setting its sort display
        List<String> tabsIdWithSameName = tl.getTabIdListForName(tabName, TaskList.SearchType.EQUAL);
        // if the list is not empty then get the first tab
        if(tabsIdWithSameName.size() > 0)
            tabId = tabsIdWithSameName.get(0);
        // there are no tabs with name 'rzf - tasks' --> creating a new tab + setting view settings
        else
            tabId = tl.createNewTab(tabName, TaskList.AlertState.ACCEPT);
        tl = new TaskList(driver);
        // verifying tab is created
        if(tabId.isEmpty())
            Assert.fail("failed to create a tab for testing");
        // set tab sort display and un-select 'Show completed task' option (in case it is selected) for following tests
        tl.goToTabById(tabId);
        tl.openTabActionsMenu(tabId);
        tl.setTabSortDisplay(tabId, TaskList.SortOption.HAND);
        tl.openTabActionsMenu(tabId);
        tl.setTabCompletedTasksDisplay(tabId, TaskList.OptionState.UNSELECT);
        totalTaskDisplay = tl.getTotalTasksDisplayVal(); // getting total tasks display value prior to tasks addition test
        Assert.assertTrue(tl.isTabExistInVisibleList(tabId), "tab id: " + tabId + " is created but not visible\\n");
    }

    @Test(dataProvider = "getSimpleTasksData", description = "create multiple simple tasks and verify each new task is added in the list and its name")
    public void tc02_create_multiple_simple_tasks(String taskName){
        tl = new TaskList(driver);
        totalTasksInList = tl.getTaskElementList().size();
        task = new Task(null, null, null, taskName, null, null);
        tl.addNewSimpleTask(task);
        tl = new TaskList(driver);
        tasksCounter++;
        // checking # of tasks in list after adding a task
        int actualTasksInList = tl.getTaskElementList().size();
        Assert.assertEquals(actualTasksInList, totalTasksInList+1, "task was not added to list\n");
        // verifying new task name in the list
        String actualTaskName = tl.getTaskName(tl.getTasksList().size()-1);
        Assert.assertEquals(actualTaskName, taskName, "task was added to list but doesn't have the correct name\n");
    }

    @Test(description = "verify total num of tasks display")
    public void tc03_verify_total_num_of_tasks_display(){
        tl = new TaskList(driver);
        int actualNumOfTaskDisplay = tl.getTotalTasksDisplayVal();
        Assert.assertEquals(actualNumOfTaskDisplay, totalTaskDisplay + tasksCounter, "total num of tasks displayed doesn't match actual num of tasks in the list");
    }

    @DataProvider
    public Object[][] getSimpleTasksData(){
        String filePath = Utils.readProperty("excelFilePath");
        String sheetName = Utils.readProperty("simpleTasksSheetName");
        return Utils.getDataFromExcel(filePath, sheetName);
    }
}
