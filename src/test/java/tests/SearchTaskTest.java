package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.Task;
import pageObjects.TaskList;
import pageObjects.TasksPage;

import java.util.ArrayList;
import java.util.List;

public class SearchTaskTest extends BaseTest {
    // variables
    String tabName = "rzf - search task test";
    String textToSearch = "task";
    List<Task> expectedTaskList = new ArrayList<>();

    @Test(description = "creating a new tab for testing text search in the task list")
    public void tc01_create_new_tab_for_testing() {
        tl = new TaskList(driver);
        tabId = tl.createNewTab(tabName, TaskList.AlertState.ACCEPT);
        // verifying tab is created
        if(tabId.isEmpty())
            Assert.fail("failed to create a tab for testing");
        tl = new TaskList(driver);
        // verifying new tab is displayed
        Assert.assertTrue(tl.isTabExistInVisibleList(tabId), "tab id: \" + tabId + \" is created but not visible\\n");
        // setting and verifying tab sort display - Sort by Hand
        tl.goToTabById(tabId);
        tl.openTabActionsMenu(tabId);
        if (!tl.setTabSortDisplay(tabId, TasksPage.SortOption.HAND))
            System.out.println("failed to set tab sort option 'Sort by hand'!! This might affect further tests results");
        // setting testing tab 'Show completed tasks' - un-select (in case selected) for further testing purpose
        tl.openTabActionsMenu(tabId);
        if (!tl.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT))
            System.out.println("failed to un-select 'Show completed tasks' option!! This might affect further tests results");
    }

    @Test(description = "adding simple and advanced tasks to the list which contain search keyword")
    public void tc02_add_tasks_to_tab(){
        tl = new TaskList(driver);
        moveToTargetTab(tl, tabId);
        AdvancedTaskPage atp;
        for (Task task : getTasksToEnterList()) {
            if(task.getTaskName().contains("task") || task.getTaskNotes().contains("task"))
                expectedTaskList.add(task);
            tl.goToAdvancedPage();
            atp = new AdvancedTaskPage(driver);
            atp.submitTask(task);
            tl = new TaskList(driver);
        }
        int actualTasksNum = tl.getTaskElementList().size();
        Assert.assertEquals(actualTasksNum, getTasksToEnterList().size());
    }

    @Test(description = "entering keyword for search and verifying the result list and total num of tasks display")
    public void tc03_search_for_keyword(){
        tl = new TaskList(driver);
        moveToTargetTab(tl, tabId);
        tl.searchText(textToSearch);
        tl = new TaskList(driver);
        List<Task> actualListAfterSearch = tl.getTasksList();
        if(actualListAfterSearch.isEmpty())
            Assert.fail("Failed to detect tasks with keyword '" + textToSearch + "'\n");
        Assert.assertEquals(actualListAfterSearch.size(), expectedTaskList.size(), "not all tasks with keyword '" + textToSearch + "' are displayed\n");
        int actualTotalTasksDisplay = tl.getTotalTasksDisplayVal();
        Assert.assertEquals(actualTotalTasksDisplay, expectedTaskList.size(), "incorrect total num of tasks display");
        for (Task task : actualListAfterSearch) {
            if(!task.getTaskName().contains(textToSearch) && !task.getTaskNotes().contains(textToSearch))
                    Assert.fail(task.toString() + " is a search result but doesn't contain the keyword '" + textToSearch +"'\n");
        }
    }

    @Test(description = "removing the search keyword and verifying the original task list")
    public void tc04_remove_search_text(){
        tl = new TaskList(driver);
        moveToTargetTab(tl, tabId);
        tl.closeSearch();
        tl = new TaskList(driver);
        Assert.assertEquals(tl.getTaskElementList().size(), getTasksToEnterList().size(), "closing the text search doesn't display the original task list\n");
        Assert.assertEquals(tl.getTotalTasksDisplayVal(), getTasksToEnterList().size(), "incorrect total tasks display after closing the text search\n");
    }

    public List<Task> getTasksToEnterList(){
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("", "", "", "create automation basePage class", "", ""));
        taskList.add(new Task("", "", "", "simple task - create a new test method", "", ""));
        taskList.add(new Task("2", "12.10.21", null, "create a new tab", "create a new tab for testing purposes", "rzf"));
        taskList.add(new Task("1", "20.10.2021", null, "create a new simple task", "create a new advanced task", "automation, rzf"));
        taskList.add(new Task("-1", "24.10.21", null, "create a new advanced task", "", "automation, rzf"));
        taskList.add(new Task("", "29.10.2021", null, "add project logs", "important task", ""));
        taskList.add(new Task("2", "15.12.21", null, "create reporting system", "use allure", "reporting"));
        taskList.add(new Task("1", "05.01.2022", null, "create negative scenarios", "", "automation"));
        return taskList;
    }
}
