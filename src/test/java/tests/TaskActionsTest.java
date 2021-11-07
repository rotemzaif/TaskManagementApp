package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

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
        tl = new TaskList(driver);
        // extracting system date formats
        tl.goToSettingsPage();
        SettingsPage sp = new SettingsPage(driver);
        Assert.assertTrue(sp.isPageDisPlayed(sp.getSettingsLabel()), "Settings page is not displayed!! Cannot retrieve date formats");
        shortDateformat = sp.getShortDateFormat();
        shortDateCurrentYearFormat = sp.getShortDateCurrentYear();
        if (shortDateformat.isEmpty())
            Assert.fail("shortDate format variable is empty");
        if (shortDateCurrentYearFormat.isEmpty())
            Assert.fail("shortDateCurrentYear format variable is empty");
        sp.goBackToTasksPage();
        tl = new TaskList(driver);
        Assert.assertTrue(tl.isPageDisPlayed(tl.getTasksTotalEl()), "Tasks page is not displayed after returning from Settings page!!\n");
        tl.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "Advanced task page is not displayed!! Cannot enter a task!!\n");
        advancedTaskPriorityMap = atp.getPriorityOptionsMap();
        atp.goBack();
        tl = new TaskList(driver);
        Assert.assertTrue(tl.isPageDisPlayed(tl.getTasksTotalEl()), "Tasks page is not displayed after returning from Advanced Task page!!\n");
    }

    @Test(description = "check if a tab that its name contains 'rzf' exist, if not create one and add tasks to the list")
    public void tc02_search_for_testing_tab() {
        tl = new TaskList(driver);
        // extracting date formats from Settings page
        List<String> tabsContainingSameName = tl.getTabIdListForName("rzf", TaskList.SearchType.CONTAINS);
        // there at least one tab with name that contains 'rzf'
        if (tabsContainingSameName.size() > 0)
            tabId = tabsContainingSameName.get(0);
            // there are no tabs with name that contains 'rzf'
        else {
            // creating a new tab
            String tabName = "rzf - task actions test";
            tabId = tl.createNewTab(tabName, TaskList.AlertState.ACCEPT, TasksPage.SortOption.HAND, TaskList.OptionState.UNSELECT);
            if (tabId.isEmpty())
                Assert.fail("failed to create a tab for testing");
            Assert.assertTrue(tl.isTabExistInVisibleList(tabId), "tab id: " + tabId + " is created but not visible\n");
        }
        tl.goToTabById(tabId);
        // checking if there at least 3 tasks in the list
        if (tl.getTasksList().size() < 3)
            addTasksToList(tabId);
        // checking if tasks were added
        Assert.assertTrue(tl.getTasksList().size() >= 3, "tasks were not added!! cannot continue with test!!\n");
    }

    @Test(description = "select a simple task from the list and edit it by adding all tasks attributes")
    public void tc03_edit_a_simple_task() throws ParseException {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        int numOfTasksInListB4Edit = tl.getTasksList().size();
        // get first simple task index from the list
        taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        // if there is no simple task in the list then adding a new simple task
        if(taskIndex == -1){
            tl.addNewSimpleTask(new Task(null, null, null, "add simple task for edit test", null, null));
            taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        }
        // clicking on task action menu
        if(!tl.selectTaskAction(taskIndex, "Edit"))
            Assert.fail("task action menu is not displayed!!\n");
        // edit task
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getEditTaskLabel()), "Advanced task page is not displayed!! Cannot enter a task!!\n");
        Task modifiedTask = new Task("2", "01.11.21", null, "modified task name","testing task edit", "rzf");
        atp.submitTask(modifiedTask);
        tl = new TaskList(driver);
        Assert.assertEquals(tl.getTasksList().size(), numOfTasksInListB4Edit, "task was added instead of edited");
        Assert.assertEquals(tl.getTotalTasksDisplayVal(), numOfTasksInListB4Edit, "total tasks display value changed following task edit");
        // verifying task details modified are displayed correctly
        Task actualTask = tl.getTasksList().get(taskIndex);
        Assert.assertTrue(tl.compareTasks(actualTask, modifiedTask, TaskList.ExpectedTaskType.ENTERED,advancedTaskPriorityMap, shortDateformat, shortDateCurrentYearFormat), "one or more task details don't match!! View logs or console messages for more details");
    }

    @Test(description = "select a simple task from the list, select the 'edit note' from task actions menu, enter a note, cancel operation + verify note was not added")
    public void tc04_cancel_simple_task_note_edit() {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        // if there is no simple task(has no due date, note and tags) in the list then add one
        if(taskIndex == -1){
            tl.addNewSimpleTask(new Task(null, null, null, "add another simple task",null, null));
            taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        }
        // edit task note from Task page --> task list
        if(!tl.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        if(!tl.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tl.editTaskNote(taskIndex, noteToEnter, TaskList.TaskNoteConf.CANCEL);
        Assert.assertFalse(tl.taskHasNotes(taskIndex), "note was added although operation was cancelled");
        Assert.assertFalse(tl.isTaskToggleDisplayed(taskIndex), "Task toggle element is displayed although no note was entered");
    }

    @Test(description = "select a simple task from the list, select the 'edit note' from task actions menu, enter a note, save operation + verify note was added")
    public void tc05_save_simple_task_note_edit(){
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        // if there is no simple task(has no due date, note and tags) in the list then add one
        if(taskIndex == -1){
            tl.addNewSimpleTask(new Task(null, null, null, "add another simple task",null, null));
            taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        }
        // edit task note from Task page --> task list
        if(!tl.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        if(!tl.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tl.editTaskNote(taskIndex, noteToEnter, TaskList.TaskNoteConf.SAVE);
        Assert.assertTrue(tl.taskHasNotes(taskIndex), "note was not added to task\n");
        Assert.assertTrue(tl.isTaskToggleDisplayed(taskIndex), "Task toggle element is not displayed although note was entered and saved\n");
        // check if task note is displayed
        Assert.assertTrue(tl.isTaskNoteDisplayed(taskIndex), "Task note is not displayed although added\n");
        // check if task note matches the note entered
        Assert.assertEquals(tl.getTaskDisplayedNoteText(taskIndex), noteToEnter, "Displayed task note doesn't match the note entered!!\n");
    }

    @Test(description = "select an advanced task from the list, modify its existing note and verify that the new note is displayed")
    public void tc06_edit_an_existing_task_note() throws InterruptedException {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        taskIndex = tl.getTaskIndex(TaskList.TaskType.ADVANCED, TaskList.TaskAttribute.NOTE);
        // if there is no advanced task (has at least one of the following: due date, note and tags) in the list then add one
        if(taskIndex == -1){
            tl.goToAdvancedPage();
            atp = new AdvancedTaskPage(driver);
            Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "Advanced Task page is not displayed");
            atp.submitTask(new Task("2", "25.11.21", null, "create advanced task", "create a new advanced task for tasks note edit test", "rzf"));
            tl = new TaskList(driver);
            taskIndex = tl.getTaskIndex(TaskList.TaskType.ADVANCED, TaskList.TaskAttribute.NOTE);
        }
        // extract existing task note text
        tl.toggleTaskNoteDisplay(taskIndex, TaskList.TaskNoteToggleState.OPEN);
        String newTaskNoteText = "testing existing task note editing";
        if(!tl.selectTaskAction(taskIndex, "Edit Note"))
            Assert.fail("task action menu is not displayed!!\n");
        Thread.sleep(3000);
        if(!tl.isTaskNoteAreaDisplayed(taskIndex))
            Assert.fail("task note area is not displayed!! Cannot continue with test\n");
        tl.editTaskNote(taskIndex, newTaskNoteText, TaskList.TaskNoteConf.SAVE);
        tl.toggleTaskNoteDisplay(taskIndex, TaskList.TaskNoteToggleState.OPEN);
        String actualNoteText = tl.getTaskDisplayedNoteText(taskIndex);
        Assert.assertEquals(actualNoteText, newTaskNoteText, "Task note was not changed!! New note was not entered.\n");
    }

    @Test(description = "select a simple task from the list, change its priority and verify new priority is displayed")
    public void tc07_change_a_task_priority() {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        // if there is no simple task(has no due date, note and tags) in the list then add one
        if(taskIndex == -1){
            tl.addNewSimpleTask(new Task(null, null, null, "add simple task for priority edit test",null, null));
            taskIndex = tl.getTaskIndex(TaskList.TaskType.SIMPLE, null);
        }
        if(!tl.selectTaskAction(taskIndex, "Priority"))
            Assert.fail("task action menu is not displayed!!\n");
        String priorityB4Change = tl.getTasksList().get(taskIndex).getTaskPriority();
        String newPriority = "-1";
        String newPriorityVal = tl.getTaskPriorityValue(newPriority);
        tl.editTaskPriority(newPriority);
        String actualPriority = tl.getTasksList().get(taskIndex).getTaskPriority();
        if(actualPriority.equals(priorityB4Change))
            Assert.fail("task priority was not changed!!\n");
        Assert.assertEquals(actualPriority, newPriorityVal, "task priority was changed to the wrong priority value");
    }

    @Test(description = "select an advanced task from the list, move it to another tab and verify it was removed from current tab and added in target tab ")
    public void tc08_move_a_task_to_another_tab() throws ParseException {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        // extracting an advanced task (task object + task id)
        taskIndex = tl.getTaskIndex(TaskList.TaskType.ADVANCED, null);
        Task taskToMove = tl.getTasksList().get(taskIndex);
        String taskToMoveId = tl.getTaskId(taskIndex);
        String taskToMoveName = taskToMove.getTaskName();
        int numOfTasksB4 = tl.getTasksList().size();
        int totalTasksDisplayedValB4 = tl.getTotalTasksDisplayVal();
        // select 'move to' option from actions menu
        if(!tl.selectTaskAction(taskIndex, "Move to"))
            Assert.fail("task action menu is not displayed!!\n");
        // select a tab
        String targetTabId = "list_" + tl.moveToTab();
        String targetTabName = tl.getTabNameById(targetTabId);
        int actualNumOfTasks = tl.getTasksList().size();
        int actualTotalTasksDisplayed = tl.getTotalTasksDisplayVal();
        // verify num of tasks in current tab reduced by 1
        Assert.assertEquals(actualNumOfTasks, numOfTasksB4-1, "Task: '" + taskToMoveName + "' was not moved to tab: '" + targetTabName +"'");
        Assert.assertEquals(actualTotalTasksDisplayed, totalTasksDisplayedValB4-1, "Total tasks value display did not change although task was moved to another tab\n");
        // verify moved task is not in task list
        tl.searchText(taskToMoveName);
        Assert.assertFalse(tl.getTasksMap().containsKey(taskToMoveId), "target task: '" + taskToMoveName + "' is still in current tab!!\n");
        // move to target tab
        tl.goToTabFromList(targetTabId);
        tl.setTabSortDisplay(targetTabId, TasksPage.SortOption.HAND);
        tl.setTabCompletedTasksDisplay(targetTabId, TaskList.OptionState.UNSELECT);
        // verify 'moved task' was added in target tab
        tl.searchText(taskToMoveName);
        if(tl.getTasksList().size() == 0)
            Assert.fail("task was not added to target tab");
        Task addedTask = tl.getTasksList().get(tl.getTasksList().size()-1);
        Assert.assertTrue(tl.compareTasks(addedTask, taskToMove, TaskList.ExpectedTaskType.EXISTING, advancedTaskPriorityMap, shortDateformat, shortDateCurrentYearFormat));
    }

    @Test
    public void tc09_delete_a_task() {
        tl = new TaskList(driver);
        if(!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        int numOfTasksB4Delete = tl.getTasksList().size();
        int totalNumOfTasksDisplayValB4 = tl.getTotalTasksDisplayVal();
        // select last task from the list
        taskIndex = tl.getTasksList().size() - 1;
        String taskId = tl.getTaskId(taskIndex);
        String taskName = tl.getTasksList().get(taskIndex).getTaskName();
        // open actions menu and select 'Delete'
        if (!tl.selectTaskAction(taskIndex, "Delete"))
            Assert.fail("task action menu is not displayed!!\n");
        // cancel delete alarm
        tl.deleteTask(TaskList.AlertState.CANCEL);
        // verify task was not deleted
        Assert.assertEquals(tl.getTasksList().size(), numOfTasksB4Delete, "num of tasks changed following task delete cancel\n");
        Assert.assertTrue(tl.getTasksMap().containsKey(taskId), "task ' " + taskName + " was deleted although delete operation was canceled");
        // open actions menu and select 'Delete'
        if (!tl.selectTaskAction(taskIndex, "Delete"))
            Assert.fail("task action menu is not displayed!!\n");
        // confirm delete alarm
        tl.deleteTask(TaskList.AlertState.ACCEPT);
        // verify num of tasks reduced by 1
        Assert.assertEquals(tl.getTasksList().size(), numOfTasksB4Delete-1, "Task was not deleted. Num of tasks in list did not change\n");
        int actualTotolDisplay = tl.getTotalTasksDisplayVal();
        // verify task total num display reduced by 1
        Assert.assertEquals(actualTotolDisplay, totalNumOfTasksDisplayValB4-1, "Total tasks value display did not change after deleting a task");
        // verify task id doesn't exist
        Assert.assertFalse(tl.getTasksMap().containsKey(taskId), "task ' " + taskName + " was not deleted although confirming delete operation\n");
    }


    public void addTasksToList(String tabId) {
        tl = new TaskList(driver);
        AdvancedTaskPage atp;
        tl.goToTabById(tabId);
        for (Task task : getTasksToEnterList()) {
            tl.goToAdvancedPage();
            atp = new AdvancedTaskPage(driver);
            atp.submitTask(task);
            tl = new TaskList(driver);
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
