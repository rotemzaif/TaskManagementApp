package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageObjects.*;
import utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddAdvancedTaskTest extends BaseTest {

    // test global variables and objects
    AdvancedTaskPage atp;
    String tabName = "rzf - tasks";
    int numOfTasksInListBefore = 0;
    int totTasksDisplayBefore = 0;
    int counter = 0;
    Map<String, String> advancedTaskPriorityMap = new HashMap<>();
    Task enteredTask;
    String shortDateformat = "";
    String shortDateCurrentYearFormat = "";

    @Test(description = "check if 'rzf - tasks' tab exists, if there are multiple tabs with that name, delete all except the first;" +
            "if there are no tabs with that name, create a new tab with that name and set its sort option and un-select 'Show completed tasks")
    public void tc01_check_for_existing_testing_tab() {
        tl = new TaskList(driver);
        List<String> tabsWithSameName = tl.getTabIdListForName(tabName, TaskList.SearchType.EQUAL);
        // if there is one tab with tabName, get its id
        if (tabsWithSameName.size() == 1)
            tabId = tabsWithSameName.get(0);
            // if there multiple tabs with the same tabName, delete all tabs except the first
        else if (tabsWithSameName.size() > 1) {
            String id;
            for (int i = tabsWithSameName.size() - 1; i > 0; i--) {
                id = tabsWithSameName.get(i);
                tl.goToTabById(id);
                tl.openTabActionsMenu(id);
                tl.deleteTab(TaskList.AlertState.ACCEPT);
                tl = new TaskList(driver);
            }
            tabsWithSameName = tl.getTabIdListForName(tabName, TaskList.SearchType.EQUAL);
            if (tabsWithSameName.size() == 1)
                tabId = tabsWithSameName.get(0);
            else
                Assert.fail("failed to delete tabs with same name");
        }
        // if there is no tab with tabName, create a new tab and set its display settings
        else{
            tabId = tl.createNewTab(tabName, TaskList.AlertState.ACCEPT);
            tl = new TaskList(driver);
        }
        // verifying testing tab is visible
        if (!tl.isTabExistInVisibleList(tabId))
            Assert.fail("failed to retrieve a tab for testing!!\n");
        // setting testing tab sort display for further testing purpose
        tl.goToTabById(tabId);
        tl.openTabActionsMenu(tabId);
        if (!tl.setTabSortDisplay(tabId, TasksPage.SortOption.HAND))
            System.out.println("failed to set tab sort option 'Sort by hand'!! This might affect further tests results");
        // setting testing tab 'Show completed tasks' - un-select (in case selected) for further testing purpose
        tl.openTabActionsMenu(tabId);
        if (!tl.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT))
            System.out.println("failed to un-select 'Show completed tasks' option!! This might affect further tests results");
    }

    @Test(description = "moving to Settings page in order to extract system date formats for following testing methods validations")
    public void tc02_extract_system_date_formats() {
        tl = new TaskList(driver);
        tl.goToTabById(tabId);
        numOfTasksInListBefore = tl.getTaskElementList().size();
        totTasksDisplayBefore = tl.getTotalTasksDisplayVal();
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
        String expectedPageTitle = tl.getCurrentTabName() + " " + Utils.readProperty("pageTitle");
        String actualPageTitle = driver.getTitle();
        Assert.assertEquals(actualPageTitle, expectedPageTitle, "'" + expectedPageTitle + "' page is not displayed");
    }

    @Test(dataProvider = "getTaskDetails", description = "for each iteration, moving to Advanced Task page, submitting a task, verifying task was added " +
            "to list and matches the details entered")
    public void tc03_create_multiple_advanced_tasks(String priorityIn, String nameIn, String noteIn, String tagsIn) throws ParseException {
        tl = new TaskList(driver);
        if (!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        tl.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "'Advanced Task' page is not displayed");
        advancedTaskPriorityMap = atp.getPriorityOptionsMap(); // for testing tasks details test method
        // adding the new advanced task
        counter++;
        String dueDateIn = getDueDateIn();
        enteredTask = new Task(priorityIn, dueDateIn, null, nameIn, noteIn, tagsIn);
        atp.submitTask(enteredTask);
        tl = new TaskList(driver);
        // checking if task was added to list - by checking the actual number of tasks in the list
        int actualNumOfTasksInList = tl.getTaskElementList().size();
        Assert.assertEquals(actualNumOfTasksInList, numOfTasksInListBefore + counter, "number of tasks in list didn't change after submitting " +
                "task: " + enteredTask.getTaskName() + "\n");
        // verifying that added task details match the details entered
        Task addedTask = tl.getTasksList().get(tl.getTasksList().size() - 1);
        Assert.assertTrue(tl.compareTasks(addedTask, enteredTask, TaskList.ExpectedTaskType.ENTERED, advancedTaskPriorityMap, shortDateformat, shortDateCurrentYearFormat), "one or more task details don't match!! View logs or console messages for more details");
    }

    @Test(description = "verifying tasks total num display after entering multiple advanced tasks")
    public void tc04_check_total_num_tasks_display() {
        tl = new TaskList(driver);
        // check if we are the testing tab, if not, move to the testing tab
        if (!tl.getCurrentTabId().equals(tabId))
            tl.goToTabById(tabId);
        // get actual num of tasks display
        int actualTotNumOfTaskDisplay = tl.getTotalTasksDisplayVal();
        Assert.assertEquals(actualTotNumOfTaskDisplay, totTasksDisplayBefore + counter, "Total number of tasks displayed doesn't match " +
                "the number of tasks in the list");
    }

    @DataProvider
    public Object[][] getTaskDetails() {
        String filePath = Utils.readProperty("excelFilePath");
        String sheetName = Utils.readProperty("advancedTasksSheetName");
        return Utils.getDataFromExcel(filePath, sheetName);
    }

    public String getDueDateIn() {
        String dueDateIn = "";
        Date date;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        int maxPos = 7;
        int minPos = 2;
        int random = new Random().nextInt(maxPos - minPos + 1) + minPos;
        Calendar c = Calendar.getInstance(); // starts with today's date and time
        switch (counter) {
            case 1: // today
                break;
            case 2: // in 2 - 7 days (from current date)
                c.add(Calendar.DAY_OF_YEAR, random);
                break;
            case 3: // 2 - 7 days ago (from current date)
                c.add(Calendar.DAY_OF_YEAR, -random);
                break;
            case 4: // in 10 days (from current date)
                c.add(Calendar.DAY_OF_YEAR, 10);
                break;
            case 5: // 10 days ago (from current date)
                c.add(Calendar.DAY_OF_YEAR, -10);
                break;
            case 6: // in one year from (from current date)
                c.add(Calendar.YEAR, 1);
                break;
            default: // in one month (from current date)
                c.add(Calendar.MONTH, 1);
                break;
        }
        date = c.getTime();
        dueDateIn = formatter.format(date);
        return dueDateIn;
    }
}
