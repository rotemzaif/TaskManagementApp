package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.TasksPage;
import utils.Utils;

import java.util.List;
import java.util.Map;

public class AdvancedTaskPageTest extends BaseTest {
    // test variables
    List<String> tagList;
    int numOfTasksInListBefore = 0;
    int numOfTasksDisplayedBefore = 0;
    String curTabName = "";

    // test objects
    AdvancedTaskPage atp;

    @Test(description = "click on the 'advanced' button and verify that 'Advanced task' page is displayed")
    public void tc01_go_to_advanced_task_page() throws InterruptedException {
        tp = new TasksPage(driver);
        tagList = tp.getTags(); // will be used in following tests
        numOfTasksInListBefore = tp.getTasksList().size(); // will be used in following tests
        numOfTasksDisplayedBefore = tp.getTotalTasksDisplay(); // will be used in following tests
        curTabName = tp.getCurrentTabName(); // getting tab current tab name before moving to advanced task page; will used in further tests
        tp.goToAdvancedPage();
        Thread.sleep(1000);
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "'Advanced task' page is not displayed\n");
    }

    @Test(description = "select one of the priority options and verify it is displayed as selected option")
    public void tc02_verify_priority_selection(){
        atp = new AdvancedTaskPage(driver);
        String valueToEnter = "2";
        atp.selectPriority(valueToEnter);
        String expectedValue = atp.getPriorityOptionsMap().get(valueToEnter);
        String actualValue = atp.getPriorityVal();
        Assert.assertEquals(actualValue, expectedValue);
    }

    @Test(description = "click on 'Show all' and verify that all tags are displayed and match the tag list in the Tasks page")
    public void tc03_show_all_tags(){
        atp = new AdvancedTaskPage(driver);
        atp.showAllTags();
        Assert.assertTrue(atp.isAllTagsDisplayed(), "Tags are not displayed");
        Map<String, String> actualTagsMap = atp.getTagsMap();
        // comparing between tag list in Tasks page and tag list in Advanced Task page
        if(tagList.size() != actualTagsMap.size()){ // comparing list size
            Assert.fail("tag list size in Advanced Task page is not equal to task list size in Tasks page!!\ntag list in Tasks page: " +
                            tagList.toString() + "\ntag list in Advanced Task page: " + actualTagsMap.keySet());
        }
        for (String tagName : tagList) { // comparing tags name
            if(!actualTagsMap.containsKey(tagName)){
                Assert.fail("tag '" + tagName + "' is not displayed in tag list in Advanced Task page!!\ntag list in Tasks page: " + tagList.toString()
                        + "\ntag list in Advanced Task page: " + actualTagsMap.keySet());
                break;
            }
        }
    }

    @Test(description = "click on 'Hide all' and verify that 'All tags: ...' is not displayed")
    public void tc04_hide_tags(){
        atp = new AdvancedTaskPage(driver);
        atp.hideAllTags();
        Assert.assertFalse(atp.isAllTagsDisplayed(), "All tags is still displayed when clicking on 'Hide all'\n");
    }

    @Test(description = "click on 'cancel' and verify that Tasks page is displayed")
    public void tc05_cancel_advanced_task() throws InterruptedException {
        atp = new AdvancedTaskPage(driver);
        atp.enterTaskName("rzf - advanced task");
        atp.cancelSubmit();
        Thread.sleep(1000);
        tp = new TasksPage(driver);
        // verifying that we are getting back to the tab we came from
        String expectedPageTitle = curTabName + " " + Utils.readProperty("pageTitle");
        String actualPageTitle = driver.getTitle();
        Assert.assertEquals(actualPageTitle, expectedPageTitle, "'" + expectedPageTitle + "' page is not displayed\n");
    }

    @Test(description = "verifying that num of tasks in task list has not changed after canceling task submit in Advanced Task page")
    public void tc06_verify_num_of_tasks_in_list_after_cancel(){
        tp = new TasksPage(driver);
        int actualNumOfTaskInList = tp.getTasksList().size();
        Assert.assertEquals(actualNumOfTaskInList, numOfTasksInListBefore, "task has been added or deleted after canceling advanced task submition\n");
    }

    @Test(description = "verifying that total num of tasks displayed has not changed after canceling task submit in Advanced Task page")
    public void tc06_verify_total_num_of_tasks_displayed_after_cancel(){
        tp = new TasksPage(driver);
        int actualTotalNumOfTaskDisplayed = tp.getTotalTasksDisplay();
        Assert.assertEquals(actualTotalNumOfTaskDisplayed, numOfTasksDisplayedBefore, "num of tasks displayed has changed although " +
                "canceling advanced task submition\n");
    }
}
