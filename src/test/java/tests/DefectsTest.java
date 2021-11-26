package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.Task;
import pageObjects.TaskList;
import pageObjects.TasksPage;

/**
 * This class includes 5 test methods which create bugs.
 * The purpose is to verify that test fails and screen shots are captured
 */

public class DefectsTest extends BaseTest {

    AdvancedTaskPage atp;

    @Test(description = "create a new tab, move to the new tab and check tab name with a faulty name")
    public void tc01_incorrect_tab_name(){
        tp = new TasksPage(driver);
        tabName = "rzf - defects testing";
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT);
        if(tabId.isEmpty())
            Assert.fail("tab '" + tabName + "' was not created!!\n");
        tp.goToTabById(tabId);
        String expectedTabName = "rzf - defects";
        String actualTabName = tp.getCurrentTabName();
        Assert.assertEquals(actualTabName, expectedTabName, "actual tab name doesn't match expected tab name!!\n");
    }

    @Test(description = "create a new task and check new task name with a faulty name")
    public void tc02_incorrect_task_name(){
        tl = new TaskList(driver);
        moveToTargetTab(tl, tabId);
        String faultyTaskName = "create a new scenario";
        tl.addNewSimpleTask(new Task(null, null, null, "create a faulty scenario", null, null));
        String actualTaskName = tl.getTasksList().get(tl.getTasksList().size()-1).getTaskName();
        Assert.assertEquals(actualTaskName, faultyTaskName, "Actual task name doesn't match expected task name!!\n");
    }

    @Test(description = "add a couple of tasks to the task list and compare total tasks display with num of tasks before adding the new tasks")
    public void tc03_incorrect_total_task_display(){
        tl = new TaskList(driver);
        moveToTargetTab(tl, tabId);
        int totalTasksDisplayedBefore = tl.getTotalTasksDisplayVal();
        tl.addNewSimpleTask(new Task(null, null, null, "add logs to project", null, null));
        tl.addNewSimpleTask(new Task(null, null, null, "connect project to jenkins", null, null));
        int actualTotalTasksDisplayed = tl.getTotalTasksDisplayVal();
        Assert.assertEquals(actualTotalTasksDisplayed, totalTasksDisplayedBefore, "Total tasks displayed doesn't match expected num of tasks in the list!!\n");
    }

    @Test(description = "move to Advanced Task page and compare 'New Task' label text with a different string")
    public void tc04_advanced_page_label_is_not_displayed(){
        tl = new TaskList(driver);
        tl.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        Assert.assertFalse(atp.isPageDisPlayed(atp.getNewTaskLabel()), "'New Task' label is displayed!!\n");
    }

    @Test(description = "select a task from the list and click on the actions menu, verify that it is not displayed")
    public void tc05_task_actions_menu_is_not_displayed(){
        atp = new AdvancedTaskPage(driver);
        atp.goBack();
        tl = new TaskList(driver);
        Assert.assertTrue(tl.isPageDisPlayed(tl.getTasksTotalEl()), "tasks page is not displayed");
        int taskIndex = 0;
        tl.openTaskActionsMenu(taskIndex);
        Assert.assertFalse(tl.isTaskActionsMenuDisplayed(), "Task actions menu is displayed!!\n");
    }
}
