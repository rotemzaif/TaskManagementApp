package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageObjects.Task;
import pageObjects.TasksPage;
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
        tp = new TasksPage(driver);
        tabName = "rzf - tasks";
        List<String> tabsIdWithSameName = tp.getTabIdListForName(tabName, TasksPage.SearchType.EQUAL);
        // checking if there are tabs with the same name; if there are, selecting the 1st one and setting its sort display
        if(tabsIdWithSameName.size() > 0){
            tabId = tabsIdWithSameName.get(0);
            tp.setTabSortDisplay(tabId, "Sort by hand");
            tp.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT);
        }
        // there are no tabs with name 'rzf - tasks' --> creating a new tab + setting view settings
        else
            tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT, "Sort by hand", TasksPage.OptionState.UNSELECT);
        tp = new TasksPage(driver);
        // verifying tab is created
        if(tabId.isEmpty())
            Assert.fail("failed to create a tab for testing");
        totalTaskDisplay = tp.getTotalTasksDisplayVal();
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "tab id: " + tabId + " is created but not visible\\n");
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
        int actualNumOfTaskDisplay = tp.getTotalTasksDisplayVal();
        Assert.assertEquals(actualNumOfTaskDisplay, totalTaskDisplay + tasksCounter, "total num of tasks displayed doesn't match actual num of tasks in the list");
    }

    @DataProvider
    public Object[][] getSimpleTasksData(){
        String filePath = Utils.readProperty("excelFilePath");
        String sheetName = Utils.readProperty("simpleTasksSheetName");
        return Utils.getDataFromExcel(filePath, sheetName);
    }
}
