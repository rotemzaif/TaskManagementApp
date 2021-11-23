package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.TasksPage;


public class TabActionsTest extends BaseTest {

    // variables
    String tabRaname = "rzf - tab rename test";
    int numOfTabBefore = 0;
    int numOfTabsInSelectListBefore = 0;



    @Test(description = "cancel create new tab and verify num of tabs has not changed in visible tab list and select list")
    public void tc01_cancel_tab_creation(){
        tp = new TasksPage(driver);
        // getting num of tabs in visible list before the action
        numOfTabBefore = tp.getTabElList().size();
        // getting num of tabs in Select list before the action
        numOfTabsInSelectListBefore = tp.getTabElSelectList().size()-2;
        tabName = "rzf - new tab test";
        // tab creation action + cancel creation
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.CANCEL,null, null);
        if(!tabId.isEmpty())
            Assert.fail("tab is created although canceling tab creation action\n");
        // getting num of tabs in visible list after the cancelling tab creation
        int actualNumOfTabs = tp.getTabElList().size();
        // getting num of tabs in Select list after canceling the action
        int actualNumOfTabsInSelectList = tp.getTabElSelectList().size()-2;
        Assert.assertEquals(actualNumOfTabs, numOfTabBefore, "\ncheck if tab was created although tab creation action was canceled\n");
        Assert.assertEquals(actualNumOfTabsInSelectList, numOfTabBefore, "\ncheck if tab was added in the Select list menu although tab creation action was canceled\n");
    }

    @Test(description = "create a new tab and verify it was created and added in both visible and Select list")
    public void tc02_create_new_tab(){
        tp = new TasksPage(driver);
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT,null, null);
        tp = new TasksPage(driver);
        if(tabId.isEmpty())
            Assert.fail("failed to create a tab for testing");
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "\ntab id: '" + tabId + "' is created but not visible\n");
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "\ntab id: '" + tabId + "' was not added in the 'Select list' menu");
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc03_rename_tab_cancel(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.renameTab(TasksPage.AlertState.CANCEL, tabRaname);
        String actualTabName = tp.getTabNameById(tabId);
        Assert.assertEquals(actualTabName, tabName, "\ntab name was modified although tab rename action was canceled\n");
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc04_rename_tab_accept(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.renameTab(TasksPage.AlertState.ACCEPT, tabRaname);
        tp = new TasksPage(driver);
        String actualTabName = tp.getTabNameById(tabId);
        Assert.assertEquals(actualTabName, tabRaname, "\ntab name was not modified\n");
    }

    @Test(description = "select the new tab, open the action list, select 'Hide list' and verify that the tab is not visible\n")
    public void tc05_hide_tab(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.hideTab();
        Assert.assertFalse(tp.isTabVisible(tabId), "\ntab id: " + tabId + "\tname: " + tabRaname + " is still displayed\n");
    }

    @Test(description = "open the 'Select list' menu, click on the requested tab and verify it is displayed in the visible tab list\n")
    public void tc06_show_tab(){
        tp = new TasksPage(driver);
        tp.goToTabFromList(tabId);
        Assert.assertTrue(tp.isTabVisible(tabId), "\ntab id: " + tabId + "\tname: " + tabRaname + " is not displayed in the visible tab list\n");
    }

    @Test(description = "selecting tab sort display and verifying it is selected")
    public void tc07_tab_display_sort_by_priority(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.setTabSortDisplay(tabId, TasksPage.SortOption.PRIORITY);
        String expectedSortOption = TasksPage.SortOption.PRIORITY.getSort();
        tp.openTabActionsMenu(tabId);
        String actualSortOption = tp.getTabSortOption(tabId);
        Assert.assertEquals(actualSortOption, expectedSortOption, "\n'" + expectedSortOption + "' option is not selected\n");
    }

    @Test(description = "un-selecting 'Show completed tasks' option and verifying it is not selected")
    public void tc08_tab_display_unselect_Show_completed_tasks(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT);
        tp.openTabActionsMenu(tabId);
        if(tp.isTabCompletedTasksChecked(tabId)) // if 'Show completed tasks' is checked --> test failed
            Assert.fail("'Show completed tasks' option is still selected/checked");
    }

    @Test(description = "cancel tab delete action and verify tab was not deleted from visible list and tab select list")
    public void tc09_cancel_delete_tab(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.deleteTab(TasksPage.AlertState.CANCEL);
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "\ntab was deleted from visible tab list although delete action was canceled");
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "\ntab was deleted from the 'Select list' menu although delete action was canceled");
    }

    @Test(description = "accept delete tab action and verify tab doesn't exist in visible tab list and in the tab Select list")
    public void tc10_accept_delete_tab(){
        tp = new TasksPage(driver);
        moveToTargetTab(tp, tabId);
        tp.openTabActionsMenu(tabId);
        tp.deleteTab(TasksPage.AlertState.ACCEPT);
        tp = new TasksPage(driver);
        Assert.assertFalse(tp.isTabExistInVisibleList(tabId), "\ntab was not deleted from visible list although accepted delete action");
        Assert.assertFalse(tp.isTabExistInSelectList(tabId), "\ntab was not deleted from 'Select list' menu although accepted delete action");
    }
}
