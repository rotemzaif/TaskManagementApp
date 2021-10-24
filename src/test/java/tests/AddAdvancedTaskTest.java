package tests;

import com.sun.xml.internal.bind.v2.TODO;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.SettingsPage;
import pageObjects.Task;
import pageObjects.TasksPage;
import utils.DateAnalysis;
import utils.Utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAdvancedTaskTest extends BaseTest {

    // test global variables and objects
    AdvancedTaskPage atp;
    String tabName = "rzf - tasks";
    int numOfTasksInListBefore = 0;
    int totTasksDisplayBefore = 0;
    int counter = 0;
    Map<String, String> advancedTaskPriorityMap = new HashMap<>();
    Task task;
    String shortDateformat = "";
    String shortDateCurrentYearFormat = "";

    @Test(description = "check if 'rzf - tasks' tab exists, if there are multiple tabs with that name, delete all except the first;" +
            "if there are no tabs with that name, create a new tab with that name and set its sort option and un-select 'Show completed tasks")
    public void tc01_check_for_existing_testing_tab() {
        tp = new TasksPage(driver);
        List<String> tabsWithSameName = tp.getTabIdListForName(tabName);
        // if there is one tab with tabName, get its id
        if(tabsWithSameName.size() == 1)
            tabId = tp.getTabIdListForName(tabName).get(0);
        // if there multiple tabs with the sane tabName, delete all tabs except the first
        else if(tabsWithSameName.size() > 1){
            String id;
            for (int i = tabsWithSameName.size()-1; i > 0 ; i--) {
                id = tabsWithSameName.get(i);
                tp.deleteTabById(id, "accept");
            }
            tabsWithSameName = tp.getTabIdListForName(tabName);
            if(tabsWithSameName.size() == 1)
                tabId = tp.getTabIdListForName(tabName).get(0);
            else
                Assert.fail("failed to delete tabs with same name");
        }
        // if there is no tab with tabName, create a new tab and set its display settings
        else if(tabsWithSameName.isEmpty())
            tabId = tp.createNewTab(tabName, "accept");
        tp.setTabSortDisplay(tabId,"Sort by hand");
        tp.setTabCompletedTasksDisplay(tabId, "un-select");
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "failed to retrieve a tab for testing!!\n");
    }

    @Test
    public void tc02_extract_system_date_formats(){
        tp = new TasksPage(driver);
        tp.goToSettingsPage();
        SettingsPage sp = new SettingsPage(driver);
        Assert.assertTrue(sp.isPageDisPlayed(sp.getSettingsLabel()), "Settings page is not displayed!! Cannot retrieve date formats");
        shortDateformat = sp.getShortDateFormat();
        shortDateCurrentYearFormat = sp.getShortDateCurrentYear();
        if(shortDateformat.isEmpty())
            Assert.fail("shortDate format variable is empty");
        if(shortDateCurrentYearFormat.isEmpty())
            Assert.fail("shortDateCurrentYear format variable is empty");
        sp.goBackToTasksPage();
        tp = new TasksPage(driver);
        String expectedPageTitle = tp.getCurrentTabName() + " " + Utils.readProperty("pageTitle");
        String actualPageTitle = driver.getTitle();
        Assert.assertEquals(actualPageTitle, expectedPageTitle, "'" + expectedPageTitle + "' page is not displayed");
    }

    @Test
    public void tc03_go_to_advanced_task_page(){
        tp = new TasksPage(driver);
        numOfTasksInListBefore = tp.getTasksList().size();
        totTasksDisplayBefore = tp.getTotalTasksDisplay();
        tp.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        if(atp.isPageDisPlayed(atp.getNewTaskLabel()))
            advancedTaskPriorityMap = atp.getPriorityOptionsMap(); // for testing tasks details test method
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "'Advanced Task' page is not displayed");
    }

    @Test(dataProvider = "getTaskDetails")
    public void tc04_create_multiple_advanced_tasks(String priorityIn, String dueDateIn, String nameIn, String noteIn, String tagsIn) throws InterruptedException, ParseException {
        // check if we are at Tasks page - if so, move to Advanced Task page
        if(tp.isAdvancedBtnDisplayed()){
            tp.goToTabById(tabId);
            tp.goToAdvancedPage();
        }
        atp = new AdvancedTaskPage(driver);
        // adding the new advanced task
        task = new Task(priorityIn, dueDateIn,null,nameIn, noteIn, tagsIn);
        atp.submitTask(task);
        counter++;
        tp = new TasksPage(driver);
        // checking if task was added to list - by checking the actual number of tasks in the list
        int actualNumOfTasksInList = tp.getTasksList().size();
        Assert.assertEquals(actualNumOfTasksInList, numOfTasksInListBefore+counter, "number of tasks in list didn't change after submitting " +
                "task: " + task.getTaskName() + "\n");
        // verifying added task details match the details entered
        Task addedTask = tp.getTasksList().get(tp.getTasksList().size()-1);
        // checking task details - priorityIn
        String expectedPriorityValDisplay = "";
        if(priorityIn.isEmpty())
            expectedPriorityValDisplay = advancedTaskPriorityMap.get("0");
        else
            expectedPriorityValDisplay = advancedTaskPriorityMap.get(priorityIn);
        String actualPriorityValDisplay = addedTask.getTaskPriority();
        Assert.assertEquals(actualPriorityValDisplay, expectedPriorityValDisplay, "Actual task priority doesn't match the priority entered\n");
        // checking task details - due date
        String expectedDueDateValDisplay = DateAnalysis.getExpectedDateDisplay(dueDateIn, shortDateformat, shortDateCurrentYearFormat);
        String actaulDueDateValDisplay = addedTask.getDueDateText();
        Assert.assertEquals(actaulDueDateValDisplay, expectedDueDateValDisplay, "Actual task due date value doesn't match the due date entered");
        // checking task details - name
        String actualTaskName = addedTask.getTaskName();
        Assert.assertEquals(actualTaskName, nameIn, "Actual task name doesn't match the name entered");
        // checking task details - notes
        String actualTaskNote = addedTask.getTaskNotes();
        Assert.assertEquals(actualTaskNote, noteIn, "Actual task note doesn't match the note entered");
        // checking task details - tags
        tagsIn = tagsIn.replaceAll(" ","");
        List<String> enteredTagsList = Arrays.asList(tagsIn.split(","));
        Map<String,String> actualTaskTagsMap = addedTask.getTaskTagsMap();
        // checking num of actual tags vs. num of entered tags
        Assert.assertEquals(actualTaskTagsMap.size(), enteredTagsList.size(),"Actual task number of tags doesn't match the number of tags entered!!");
        // checking if tags are identical
        for (String tag : enteredTagsList) {
            if(!actualTaskTagsMap.containsKey(tag)){
                Assert.fail("actual task doesn't include the entered tag: " + tag);
                break;
            }
        }
    }

    @Test
    public void tc05_check_total_num_tasks_display(){
        tp = new TasksPage(driver);
        // check if we are the testing tab, if not, move to the testing tab
        if(!tp.getCurrentTabId().equals(tabId))
            tp.goToTabById(tabId);
        // get actual num of tasks display
        int actualTotNumOfTaskDisplay = tp.getTotalTasksDisplay();
        Assert.assertEquals(actualTotNumOfTaskDisplay, totTasksDisplayBefore + counter, "Total number of tasks displayed doesn't match " +
                "the number of tasks in the list");
    }

    @DataProvider
    public Object[][] getTaskDetails(){
        String filePath = Utils.readProperty("excelFilePath");
        String sheetName = Utils.readProperty("advancedTasksSheetName");
        return Utils.getDataFromExcel(filePath, sheetName);
    }
}
