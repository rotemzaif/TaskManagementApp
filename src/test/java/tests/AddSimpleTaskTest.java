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

    @Test(description = "creating a new tab for testing simple tasks addition")
    public void tc01_create_new_tab_for_testing(){
        tp = new TasksPage(driver);
        tabName = "rzf - tasks";
        tabId = tp.createNewTab(tabName, "accept");
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "the tab was not created or added to the visible tab list");
    }

    @Test(description = "set tab sort display to 'sort by hand' and verify it is selected")
    public void tc02_set_tab_display_select_sort_by_hand(){
        tp = new TasksPage(driver);
        String sortOption = "Sort by hand";
        if(tp.setTabSortDisplay(tabId, sortOption)){
            String actualSelectedOption = tp.getTabSortOption(tabId);
            Assert.assertEquals(actualSelectedOption, sortOption, sortOption + " is not selected\n");
        }
        else {
            Assert.fail("'" + sortOption + "'" + " was not found in the tab action list");
        }
    }

    @Test(description = "un-select 'Show completed tasks' option and verify it is unselected")
    public void tc03_set_tab_display_unselect_completed_tasks(){
        tp = new TasksPage(driver);
        totalTaskDisplay = tp.getTotalTasksDisplay();
        if(tp.setTabcompletedTasksDisplay(tabId, "un-select"))
            Assert.assertFalse(tp.isTabCompletedTasksChecked(tabId), "'Show completed tasks' option is un-selected");
        else
            Assert.fail("'Show completed tasks'" + " options was not found in the tab action list");
    }

    @Test(dataProvider = "getSimpleTasksData", description = "create multiple simple tasks and verify each new task is added in the list and its name")
    public void tc04_create_multiple_simple_tasks(String taskName){
        tp = new TasksPage(driver);
        totalTasksInList = tp.getTasksList().size();
        task = new Task(null, null, null, null, taskName, null, null);
        tp.addNewSimpleTask(task);
        tasksCounter++;
        int actualTasksInList = tp.getTasksList().size();
        Assert.assertEquals(actualTasksInList, totalTasksInList+1, "task was not added to list\n");
        String actualTaskName = tp.getTaskName(tp.getTasksList().size()-1);
        Assert.assertEquals(actualTaskName, taskName, "task was added to list but doesn't have the correct name\n");
    }

    @Test(description = "verify total num of tasks display")
    public void tc05_verify_total_num_of_tasks_display(){
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
