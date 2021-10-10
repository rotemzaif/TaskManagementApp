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

    @Test(description = "cancel create new tab and verify num of tabs in the visible tab list stays the same")
    public void tc01_cancel_tab_creation(){
        tp = new TasksPage(driver);
        // getting num of tabs before the action
        numOfTabBefore = tp.getTabList().size();
        tabName = "rzf - new tab test";
        tp.createNewTab(tabName,"cancel");
        // getting num of tabs after the action
        int numOfTabsAfter = tp.getTabList().size();
        Assert.assertEquals(numOfTabsAfter, numOfTabBefore, "check if tabs was created although canceled");
    }

    @Test(description = "verify that num of tabs in the 'Select list' menu has not changed")
    public void tc02_cancel_tab_creation_verify_num_of_tabs_in_Select_menu(){
        tp = new TasksPage(driver);
        int actualNumOfTabsInSelectList = tp.getTabsSelectList().size()-2;
        Assert.assertEquals(actualNumOfTabsInSelectList, numOfTabBefore, "check if tab was added in the Select list menu");
    }

    @Test(description = "create a new tab and verify it was created and added in the visible tab list")
    public void tc03_create_new_tab(){
        tp = new TasksPage(driver);
        tabId = tp.createNewTab(tabName, "accept");
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "the tab was not created or added to the visible tab list");
    }

    @Test(description = "verify that the new tab was added to the'Select list' menu")
    public void tc04_verify_new_tab_in_Select_list_menu(){
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "the new tab was not added in the 'Select list' menu");
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc05_rename_tab_cancel(){
        tp = new TasksPage(driver);
        tp.renameTab(tabId, "cancel", tabRaname);
        String actualTabName = tp.getTabName(tabId);
        Assert.assertEquals(actualTabName, tabName);
    }

    @Test(description = "cancel tab rename action and verify tab name didn't change")
    public void tc06_rename_tab_accept(){
        tp = new TasksPage(driver);
        tp.renameTab(tabId, "accept", tabRaname);
        String actualTabName = tp.getTabName(tabId);
        Assert.assertEquals(actualTabName, tabRaname);
    }

    @Test(description = "select the new tab, open the action list, select 'Hide list' and verify that the tab is not visible")
    public void tc07_hide_tab(){
        tp = new TasksPage(driver);
        tp.hideTab(tabId);
        Assert.assertFalse(tp.isTabVisible(tabId), "tab id: " + tabId + "\tname: " + tabRaname + " is still displayed");
    }

    @Test(description = "open the 'Select list' menu, click on the requested tab and verify it is displayed in the visible tab list")
    public void tc08_re_show_tab(){
        tp = new TasksPage(driver);
        tp.goToTabFromList(tabId);
        Assert.assertTrue(tp.isTabVisible(tabId), "tab id: " + tabId + "\tname: " + tabRaname + " is not displayed in the visible tab list");
    }

    @Test(description = "cancel tab delete action")
    public void tc09_cancel_delete_tab(){
        tp = new TasksPage(driver);
        tp.deleteTabById(tabId, "cancel");
        Assert.assertTrue(tp.isTabExistInVisibleList(tabId), "tab was deleted although delete action was canceled");
    }

    @Test(description = "verify tab was not deleted from the 'Select list'")
    public void tc10_cancel_delete_tab_verify_tab_in_Select_list(){
        tp = new TasksPage(driver);
        Assert.assertTrue(tp.isTabExistInSelectList(tabId), "tab was deleted from the 'Select list' menu");
    }


    @Test(description = "accept delete tab action and verify tab doesn't exist in visible tab list")
    public void tc11_accept_delete_tab(){
        tp = new TasksPage(driver);
        tp.deleteTabById(tabId, "accept");
        Assert.assertFalse(tp.isTabExistInVisibleList(tabId), "tab was not deleted although accepted delete action");
    }

    @Test(description = "verify that tab doesn't exist in 'Select list' menu")
    public void tc12_accept_delete_tab(){
        tp = new TasksPage(driver);
        Assert.assertFalse(tp.isTabExistInSelectList(tabId), "tab was not deleted from 'Select list' menu although accepted delete action");
    }
}
