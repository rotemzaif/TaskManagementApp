package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.TasksPage;

public class TabActionsTest extends BaseTest {
    // objects
    TasksPage tp;

    // variables
    String tabRaname = "rzf - tab rename test";
    int numOfTabBefore = 0;

    @Test(description = "cancel create new tab and verify num of visible tabs and num of tabs in TabSelectList has not changed")
    public void tc01_cancel_tab_creation(){
        tp = new TasksPage(driver);
        // getting num of visible tabs before the action
        numOfTabBefore = tp.getTabList().size();
        tabName = "rzf - new tab test";
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.CANCEL,"", null);
        if(tabId != null)
            Assert.fail("tab is created although canceling tab creation action\n");
        // getting num of visible tabs after the action
        int numOfTabsAfter = tp.getTabList().size();
        int actualNumOfTabsInSelectList = tp.getTabsSelectList().size()-2;
        Assert.assertEquals(numOfTabsAfter, numOfTabBefore, "check if tab was created although tab creation action was canceled\n");
        Assert.assertEquals(actualNumOfTabsInSelectList, numOfTabBefore, "check if tab was added in the Select list menu although tab creation action was canceled\n");
    }

    @Test(description = "create a new tab and verify it was created and added in the visible tab list and in the TabSelectList")
    public void tc02_create_new_tab(){
        tp = new TasksPage(driver);
        tabId = tp.createNewTab(tabName, TasksPage.AlertState.ACCEPT,"", null);
        tp = new TasksPage(driver);
        if(tabId.isEmpty())
            Assert.fail("failed to create a tab for testing");
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "tab id: '" + tabId + "' is created but not visible\n");
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "tab id: '" + tabId + "' was not added in the 'Select list' menu");
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc03_rename_tab_cancel(){
        tp = new TasksPage(driver);
        tp.renameTab(tabId, TasksPage.AlertState.CANCEL, tabRaname);
        String actualTabName = tp.getTabNameById(tabId);
        Assert.assertEquals(actualTabName, tabName);
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc04_rename_tab_accept(){
        tp = new TasksPage(driver);
        tp.renameTab(tabId, TasksPage.AlertState.ACCEPT, tabRaname);
        String actualTabName = tp.getTabNameById(tabId);
        Assert.assertEquals(actualTabName, tabRaname);
    }

    @Test(description = "select the new tab, open the action list, select 'Hide list' and verify that the tab is not visible\n")
    public void tc05_hide_tab(){
        tp = new TasksPage(driver);
        tp.hideTab(tabId);
        Assert.assertFalse(tp.isTabVisible(tabId), "tab id: " + tabId + "\tname: " + tabRaname + " is still displayed\n");
    }

    @Test(description = "open the 'Select list' menu, click on the requested tab and verify it is displayed in the visible tab list\n")
    public void tc06_show_tab(){
        tp = new TasksPage(driver);
        tp.goToTabFromList(tabId);
        Assert.assertTrue(tp.isTabVisible(tabId), "tab id: " + tabId + "\tname: " + tabRaname + " is not displayed in the visible tab list\n");
    }

    @Test
    public void tc07_tab_display_sort_by_priority(){
        tp = new TasksPage(driver);
        String sortOption = "Sort by priority";
        tp.setTabSortDisplay(tabId, sortOption);
        String actualSortOption = tp.getTabSortOption(tabId);
        Assert.assertEquals(actualSortOption, sortOption, "'" + sortOption + "' option is not selected\n");
    }

    @Test
    public void tc08_tab_display_unselect_Show_completed_tasks(){
        tp = new TasksPage(driver);
        tp.setTabCompletedTasksDisplay(tabId, TasksPage.OptionState.UNSELECT);
        if(tp.isTabCompletedTasksChecked(tabId)) // if 'Show completed tasks' is checked --> test failed
            Assert.fail("'Show completed tasks' option is still selected/checked");
    }

    @Test(description = "cancel tab delete action and verify tab was not deleted from visible list and tab select list")
    public void tc09_cancel_delete_tab(){
        tp = new TasksPage(driver);
        tp.deleteTabById(tabId, TasksPage.AlertState.CANCEL);
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "tab was deleted from visible tab list although delete action was canceled");
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "tab was deleted from the 'Select list' menu although delete action was canceled");
    }

    @Test(description = "accept delete tab action and verify tab doesn't exist in visible tab list and in the tab Select list")
    public void tc10_accept_delete_tab(){
        tp = new TasksPage(driver);
        tp.deleteTabById(tabId, TasksPage.AlertState.ACCEPT);
        Assert.assertFalse(tp.isTabExistInVisibleList(tabId), "tab was not deleted from visible list although accepted delete action");
        Assert.assertFalse(tp.isTabExistInSelectList(tabId), "tab was not deleted from 'Select list' menu although accepted delete action");
    }
}
