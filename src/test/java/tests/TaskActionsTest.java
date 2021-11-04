package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.SettingsPage;
import pageObjects.Task;
import pageObjects.TasksPage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskActionsTest extends BaseTest {
    // test objects and variables
    AdvancedTaskPage atp;
    String shortDateformat = "";
    String shortDateCurrentYearFormat = "";
    int taskIndex = 0;
    Map<String, String> advancedTaskPriorityMap = new HashMap<>();
    String noteToEnter = "adding a note to simple task";

    @Test(description = "extracting system date formats")
    public void tc01_test_setup() {
        tp = new TasksPage(driver);
        // extracting system date formats
        tp.goToSettingsPage();
        SettingsPage sp = new SettingsPage(driver);
        Assert.assertTrue(sp.isPageDisPlayed(sp.getSettingsLabel()), "Settings page is not displayed!! Cannot retrieve date formats");
        shortDateformat = sp.getShortDateFormat();
        shortDateCurrentYearFormat = sp.getShortDateCurrentYear();
        if (shortDateformat.isEmpty())
            Assert.fail("shortDate format variable is empty");
        if (shortDateCurrentYearFormat.isEmpty())
            Assert.fail("shortDateCurrentYear format variable is empty");
        sp.goBackToTasksPage();
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isPageDisPlayed(tp.getTasksTotalEl()), "Tasks page is not displayed after returning from Settings page!!\n");
        tp.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "Advanced task page is not displayed!! Cannot enter a task!!\n");
        advancedTaskPriorityMap = atp.getPriorityOptionsMap();
        atp.goBack();
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isPageDisPlayed(tp.getTasksTotalEl()), "Tasks page is not displayed after returning from Advanced Task page!!\n");
    }

    @Test(description = "check if a tab that its name contains 'rzf' exist, if not create one and add tasks to the list")
    public void tc02_search_for_testing_tab() {
        tp = new TasksPage(driver);
        // extracting date formats from Settings page
        List<String> tabsContainingSameName = tp.getTabIdListForName("rzf", TasksPage.SearchType.CONTAINS);
        // there at least one tab with name that contains 'rzf'
        if (tabsContainingSameName.size() > 0)
            tabId = tabsContainingSameName.get(0);
            // there are no tabs with name that contains 'rzf'
        else {
            // creating a new tab
            String tabName = "rzf - task actions test";
            String sortOption = "Sort by hand";
            tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT, "Sort by hand", TasksPage.OptionState.UNSELECT);
            if (tabId.isEmpty())
                Assert.fail("failed to create a tab for testing");
            Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "tab id: " + tabId + " is created but not visible\n");
        }
        tp.goToTabById(tabId);
        // checking if there at least 3 tasks in the list
        if (tp.getTasksList().size() < 3)
            addTasksToList(tabId);
        // checking if tasks were added
        Assert.assertTrue(tp.getTasksList().size() >= 3, "tasks were not added!! cannot continue with test!!\n");
    }

    @Test(description = "select a simple task from the list and edit it by adding all tasks attributes")
    public void tc03_edit_a_simple_task() throws ParseException {
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        int numOfTasksInListB4Edit = tp.getTasksList().size();
        // get first simple task index from the list
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.SIMPLE, null);
        // clicking on task action menu
        if(!tp.selectTaskAction(taskIndex, "Edit"))
            Assert.fail("task action menu is not displayed!!\n");
        // edit task
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getEditTaskLabel()), "Advanced task page is not displayed!! Cannot enter a task!!\n");
        Task modifiedTask = new Task("2", "01.11.21", null, "modified task name","testing task edit", "rzf");
        atp.submitTask(modifiedTask);
        tp = new TasksPage(driver);
        Assert.assertEquals(tp.getTasksList().size(), numOfTasksInListB4Edit, "task was added instead of edited");
        Assert.assertEquals(tp.getTotalTasksDisplayVal(), numOfTasksInListB4Edit, "total tasks display value changed following task edit");
        // verifying task details modified are displayed correctly
        Task actualTask = tp.getTasksList().get(taskIndex);
        Assert.assertTrue(tp.compareTasks(actualTask, modifiedTask, TasksPage.ExpectedTaskType.ENTERED,advancedTaskPriorityMap, shortDateformat, shortDateCurrentYearFormat), "one or more task details don't match!! View logs or console messages for more details");
    }

    @Test(description = "select a simple task from the list, select the 'edit note' from task actions menu, enter a note, cancel operation + verify note was not added")
    public void tc04_cancel_simple_task_note_edit() {
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.SIMPLE, null);
        // edit task note from Task page --> task list
        if(!tp.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        if(!tp.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tp.editTaskNote(taskIndex, noteToEnter, TasksPage.TaskNoteConf.CANCEL);
        Assert.assertFalse(tp.taskHasNotes(taskIndex), "note was added although operation was cancelled");
        Assert.assertFalse(tp.isTaskToggleDisplayed(taskIndex), "Task toggle element is displayed although no note was entered");
    }

    @Test(description = "select a simple task from the list, select the 'edit note' from task actions menu, enter a note, save operation + verify note was added")
    public void tc05_save_simple_task_note_edit(){
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.SIMPLE, null);
        // edit task note from Task page --> task list
        if(!tp.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        if(!tp.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tp.editTaskNote(taskIndex, noteToEnter, TasksPage.TaskNoteConf.SAVE);
        Assert.assertTrue(tp.taskHasNotes(taskIndex), "note was not added to task\n");
        Assert.assertTrue(tp.isTaskToggleDisplayed(taskIndex), "Task toggle element is not displayed although note was entered and saved\n");
        // check if task note is displayed
        Assert.assertTrue(tp.isTaskNoteDisplayed(taskIndex), "Task note is not displayed although added\n");
        // check if task note matches the note entered
        Assert.assertEquals(tp.getTaskDisplayedNoteText(taskIndex), noteToEnter, "Displayed task note doesn't match the note entered!!\n");
    }

    @Test(description = "select an advanced task from the list, modify its existing note and verify that the new note is displayed")
    public void tc06_edit_an_existing_task_note() throws InterruptedException {
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.ADVANCED, TasksPage.TaskAttribute.NOTE);
        // extract existing task note text
        tp.toggleTaskNoteDisplay(taskIndex, TasksPage.TaskNoteToggleState.OPEN);
        String noteTextBeforeModify = tp.getTaskDisplayedNoteText(taskIndex);
        String newTaskNoteText = "testing existing task note editing";
        if(!tp.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        Thread.sleep(3000);
        if(!tp.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tp.editTaskNote(taskIndex, newTaskNoteText, TasksPage.TaskNoteConf.SAVE);
        tp.toggleTaskNoteDisplay(taskIndex, TasksPage.TaskNoteToggleState.OPEN);
        String actualNoteText = tp.getTaskDisplayedNoteText(taskIndex);
        Assert.assertEquals(actualNoteText, newTaskNoteText, "Task note was not changed!! New note was not entered.\n");
    }

    @Test(description = "select a simple task from the list, change its priority and verify new priority is displayed")
    public void tc07_change_a_task_priority() {
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.SIMPLE, null);
        if(!tp.selectTaskAction(taskIndex, "Priority"))
            Assert.fail("task action menu is not displayed!!\n");
        String priorityB4Change = tp.getTasksList().get(taskIndex).getTaskPriority();
        String newPriority = "-1";
        String newPriorityVal = tp.getTaskPriorityValue(newPriority);
        tp.editTaskPriority(newPriority);
        String actualPriority = tp.getTasksList().get(taskIndex).getTaskPriority();
        if(actualPriority.equals(priorityB4Change))
            Assert.fail("task priority was not changed!!\n");
        Assert.assertEquals(actualPriority, newPriorityVal, "task priority was changed to the wrong priority value");
    }

    @Test(description = "select an advanced task from the list, move it to another tab and verify it was removed from current tab and added in target tab ")
    public void tc08_move_a_task_to_another_tab() throws ParseException {
        tp = new TasksPage(driver);
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        // extracting an advanced task (task object + task id)
        taskIndex = tp.getTaskIndex(TasksPage.TaskType.ADVANCED, null);
        Task taskToMove = tp.getTasksList().get(taskIndex);
        String taskToMoveName = taskToMove.getTaskName();
        int numOfTasksB4 = tp.getTasksList().size();
        int totalTasksDisplayedValB4 = tp.getTotalTasksDisplayVal();
        // select 'move to' option from actions menu
        if(!tp.selectTaskAction(taskIndex, "Move to"))
            Assert.fail("task action menu is not displayed!!\n");
        // select a tab
        String targetTabId = "list_" + tp.moveToTab();
        String targetTabName = tp.getTabNameById(targetTabId);
        int actualNumOfTasks = tp.getTasksList().size();
        int actualTotalTasksDisplayed = tp.getTotalTasksDisplayVal();
        // verify num of tasks in current tab reduced by 1
        Assert.assertEquals(actualNumOfTasks, numOfTasksB4-1, "Task: '" + taskToMoveName + "' was not moved to tab: '" + targetTabName +"'");
        Assert.assertEquals(actualTotalTasksDisplayed, totalTasksDisplayedValB4-1, "Total tasks value display did not change although task was moved to another tab\n");
        // verify moved task is not in task list
        tp.searchText(taskToMoveName);
        Assert.assertTrue(tp.getTasksList().size() == 0, "target task: '" + taskToMoveName + "' is still in current tab!!\n");
        // move to target tab
        tp.goToTabFromList(targetTabId);
        tp.setTabSortDisplay(targetTabId, "Sort by hand");
        tp.setTabCompletedTasksDisplay(targetTabId, TasksPage.OptionState.UNSELECT);
        // verify 'moved task' was added in target tab
        tp.searchText(taskToMoveName);
        if(tp.getTasksList().size() == 0)
            Assert.fail("task was not added to target tab");
        Task addedTask = tp.getTasksList().get(tp.getTasksList().size()-1);
        Assert.assertTrue(tp.compareTasks(addedTask, taskToMove, TasksPage.ExpectedTaskType.EXISTING, advancedTaskPriorityMap, shortDateformat, shortDateCurrentYearFormat));
    }

    @Test
    public void tc09_delete_a_task() {
        tp = new TasksPage(driver);
        if (!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        int numOfTasksB4Delete = tp.getTasksList().size();
        int totalNumOfTasksDisplayValB4 = tp.getTotalTasksDisplayVal();
        // select last task from the list
        taskIndex = tp.getTasksList().size() - 1;
        String taskId = tp.getTaskId(taskIndex);
        String taskName = tp.getTasksList().get(taskIndex).getTaskName();
        // open actions menu and select 'Delete'
        if (!tp.selectTaskAction(taskIndex, "Delete"))
            Assert.fail("task action menu is not displayed!!\n");
        // cancel delete alarm
        tp.deleteTask(TasksPage.AlertState.CANCEL);
        // verify task was not deleted
        Assert.assertEquals(tp.getTasksList().size(), numOfTasksB4Delete, "num of tasks changed following task delete cancel\n");
        Assert.assertTrue(tp.getTasksMap().containsKey(taskId), "task ' " + taskName + " was deleted although delete operation was canceled");
        // open actions menu and select 'Delete'
        if (!tp.selectTaskAction(taskIndex, "Delete"))
            Assert.fail("task action menu is not displayed!!\n");
        // confirm delete alarm
        tp.deleteTask(TasksPage.AlertState.ACCEPT);
        // verify num of tasks reduced by 1
        Assert.assertEquals(tp.getTasksList().size(), numOfTasksB4Delete-1, "Task was not deleted. Num of tasks in list did not change\n");
        int actualTotolDisplay = tp.getTotalTasksDisplayVal();
        // verify task total num display reduced by 1
        Assert.assertEquals(actualTotolDisplay, totalNumOfTasksDisplayValB4-1, "Total tasks value display did not change after deleting a task");
        // verify task id doesn't exist
        Assert.assertFalse(tp.getTasksMap().containsKey(taskId), "task ' " + taskName + " was not deleted although confirming delete operation\n");
    }


    public void addTasksToList(String tabId) {
        tp = new TasksPage(driver);
        AdvancedTaskPage atp;
        tp.goToTabById(tabId);
        for (Task task : getTasksToEnterList()) {
            tp.goToAdvancedPage();
            atp = new AdvancedTaskPage(driver);
            atp.submitTask(task);
            tp = new TasksPage(driver);
        }
    }

    public List<Task> getTasksToEnterList() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("", "", "", "create automation basePage class", "", ""));
        taskList.add(new Task("", "", "", "simple task - create a new test method", "", ""));
        taskList.add(new Task("", "", "", "task - create another tab", "", ""));
        taskList.add(new Task("2", "12.10.21", null, "create a new tab", "create a new tab for testing purposes", "rzf"));
        return taskList;
    }
}
