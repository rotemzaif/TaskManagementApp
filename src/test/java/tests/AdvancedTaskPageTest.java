package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AdvancedTaskPage;
import pageObjects.Task;
import pageObjects.TaskList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedTaskPageTest extends BaseTest {
    // test variables
    private List<String> tagList = new ArrayList<>();
    private int numOfTasksInListBefore = 0;
    private int numOfTasksDisplayedBefore = 0;
    private String testingTabId = "";

    // test objects
    AdvancedTaskPage atp;

    @Test(description = "creating and extracting variables data which will be used in further test methods: current tab id, current num of tasks, " +
            "checking if there are tags, if not then create an advanced task and add tags")
    public void tc01_test_setup_creating_and_extracting_variables_data() {
        tl = new TaskList(driver);
        testingTabId = tl.getCurrentTabId(); // will be used for verifying that we returned to the correct tab from advanced page
        // checking if there are tasks in the list OR if there are tasks then check if there are tags in the task list;
        // in there no tasks or tags, then create and advanced task with tags
        if(!tl.tagsExistance()){
            tl.goToAdvancedPage();
            atp = new AdvancedTaskPage(driver);
            Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "Advanced Task page is not displayed!! following tests may fail!!\n");
            atp.submitTask(new Task("2", "12.10.21", null, "create a new advanced task", "create a new advanced tab for testing purposes", "rzf, automation"));
            tl = new TaskList(driver);
        }
        tagList = tl.getTagsNameList(); // will be used to check tags in advanced page
        numOfTasksInListBefore = tl.getTasksList().size(); // will be used for verifying num of task when returning from advanced page
        numOfTasksDisplayedBefore = tl.getTotalTasksDisplayVal(); // will be used for verifying num of task when returning from advanced page
        Assert.assertTrue(numOfTasksInListBefore != 0, "There are no tasks in the list; some tests will fail!!\n");
        Assert.assertTrue(tagList.size() != 0, "There are no tags in the list, some tests may fail!!\n");
    }

    @Test(description = "select one of the priority options and verify it is displayed as selected option")
    public void tc02_verify_priority_selection(){
        tl = new TaskList(driver);
        tl.goToAdvancedPage();
        atp = new AdvancedTaskPage(driver);
        Assert.assertTrue(atp.isPageDisPlayed(atp.getNewTaskLabel()), "Advanced Task page is not displayed!! following tests may fail!!\n");
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
    public void tc05_cancel_advanced_task() {
        atp = new AdvancedTaskPage(driver);
        atp.enterTaskName("rzf - advanced task");
        atp.cancelSubmit();
        tl = new TaskList(driver);
        // verifying that we are getting back to the tab we came from
        String actualTabId = tl.getCurrentTabId();
        Assert.assertEquals(actualTabId, testingTabId, "Did not return to the tab we came from when canceling advanced task submit!!\n");
    }

    @Test(description = "verifying that num of tasks in task list has not changed after canceling task submit in Advanced Task page")
    public void tc06_verify_num_of_tasks_in_list_after_cancel(){
        tl = new TaskList(driver);
        int actualNumOfTaskInList = tl.getTasksList().size();
        Assert.assertEquals(actualNumOfTaskInList, numOfTasksInListBefore, "task has been added or deleted after canceling advanced task submition\n");
    }

    @Test(description = "verifying that total num of tasks displayed has not changed after canceling task submit in Advanced Task page")
    public void tc07_verify_total_num_of_tasks_displayed_after_cancel(){
        tl = new TaskList(driver);
        int actualTotalNumOfTaskDisplayed = tl.getTotalTasksDisplayVal();
        Assert.assertEquals(actualTotalNumOfTaskDisplayed, numOfTasksDisplayedBefore, "num of tasks displayed has changed although " +
                "canceling advanced task submition\n");
    }
}
