package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageObjects.Task;
import pageObjects.TasksPage;
import utils.Utils;

public class AddSimpleTaskTest extends BaseTest {
    // test global objects
    Task task;
    // test global variables
    int tasksCounter = 0;
    int totalTaskDisplay = 0;
    int totalTasksInList = 0;

    @Test(description = "creating a new tab for testing simple tasks and setting tab sort display to 'Sort by hand' and un-selecting 'Show completed tasks'")
    public void tc01_create_new_tab_for_testing(){
        tp = new TasksPage(driver);
        tabName = "rzf - tasks";
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT);
        tp = new TasksPage(driver);
        // verifying tab is created
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "the tab was not created or added to the visible tab list\n");
        // setting tab sort display to 'Sort by hand' and verifying it is selected/checked
        String sortOption = "Sort by hand";
        if(!tp.setTabSortDisplay(tabId, sortOption))
            Assert.fail("'" + sortOption + "' was not found in the tab action menu list!\n");
        // setting tab 'Show completed  tasks' to un-select and verifying it is not selected
        if(!tp.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT))
            Assert.fail("'Show completed tasks' option was not found in the tab action menu list!\n");
    }

    @Test(dataProvider = "getSimpleTasksData", description = "create multiple simple tasks and verify each new task is added in the list and its name")
    public void tc03_create_multiple_simple_tasks(String taskName){
        tp = new TasksPage(driver);
        totalTasksInList = tp.getTasksList().size();
        task = new Task(null, null, null, taskName, null, null);
        tp.addNewSimpleTask(task);
        tasksCounter++;
        int actualTasksInList = tp.getTasksList().size();
        Assert.assertEquals(actualTasksInList, totalTasksInList+1, "task was not added to list\n");
        String actualTaskName = tp.getTaskName(tp.getTasksList().size()-1);
        Assert.assertEquals(actualTaskName, taskName, "task was added to list but doesn't have the correct name\n");
    }

    @Test(description = "verify total num of tasks display")
    public void tc04_verify_total_num_of_tasks_display(){
        tp = new TasksPage(driver);
        int actualNumOfTaskDisplay = tp.getTotalTasksDisplay();
        Assert.assertEquals(actualNumOfTaskDisplay, totalTaskDisplay + tasksCounter, "total num of tasks displayed doesn't match actual num of tasks in the list");
    }

    @DataProvider
    public Object[][] getSimpleTasksData(){
        String filePath = Utils.readProperty("excelFilePath");
        String sheetName = Utils.readProperty("simpleTasksSheetName");
        return Utils.getDataFromExcel(filePath, sheetName);
    }
}
