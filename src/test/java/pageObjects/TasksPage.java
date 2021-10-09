package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TasksPage Class consists of tabs, tasks and page elements, elements actions and validations.
 * It has separated methods for actions and validations for each page section (i.e. tabs, tasks)
 */
public class TasksPage extends BasePage{
    // properties



    // page elements
    @FindBy(css = ".topblock-title>h2")
    private WebElement pageLabel;

    // tab elements
    @FindBy(css = ".mtt-tabs-add-button")
    private WebElement newTabBtn;
    @FindBy(css = ".mtt-tabs.ui-sortable > li")
    private List<WebElement> tabList;
    private WebElement tabActionListBtn; // will be initialized when tab is selected
    @FindBy(css = "#listmenucontainer > ul > li")
    private List<WebElement> tabActionList;
    @FindBy(css = "#tabs_buttons")
    private WebElement tabsSelectListBtn;
    @FindBy(css = "#slmenucontainer > ul > li")
    private List<WebElement> tabsSelectList;

    // constructor //
    public TasksPage(WebDriver driver) {
        super(driver);
    }

    // page getters //

    public List<WebElement> getTabList(){
        return tabList;
    }

    public List<WebElement> getTabsSelectList(){
        return tabsSelectList;
    }

    public Map<String, WebElement> getTabsMap(){
        Map<String, WebElement> tabsMap = new HashMap<>();
        String key;
        for (WebElement tab : tabList) {
            key = tab.getAttribute("id");
            tabsMap.put(key, tab);
        }
        return tabsMap;
    }

    public Map<String, WebElement> getTabsFromListMap(){
        Map<String, WebElement> tabsSelectListMap = new HashMap<>();
        String key;
        for (int i = 2; i < tabsSelectList.size(); i++) {
            key = tabsSelectList.get(i).getAttribute("id");
            tabsSelectListMap.put(key, tabsSelectList.get(i));
        }
        return tabsSelectListMap;
    }

    /**
     * @param tabId - string - the tab id we want to get its name
     * @return - string - the tab name
     */
    public String getTabName(String tabId){
        return getText(getTabsMap().get(tabId).findElement(By.cssSelector("a>span")));
    }

    /**
     * this method runs over the tab list and finds the first selected tab; for page title validation test
     * @return - string - tab name
     */
    public String getFirstTabName(){
        String tabName = "";
        for (WebElement tab : tabList) {
            if(tab.getAttribute("class").contains("selected")){
                tabName = getText(tab.findElement(By.cssSelector("a>span")));
            }
        }
        return tabName;
    }

    // tab action methods //
    /**
     * function that creates a new tab
     * @param tabName - string - tab name to enter
     * @param state - string - indicates whether to accept or cancel the allert
     * @return - string - tab id - in case of 'accept' and null in case of 'cancel'
     */
    public String createNewTab(String tabName, String state){
        String tabId = "";
        String selectListTabId = "";
        click(newTabBtn);
        allertSendText(tabName);
        if(state.equals("accept")){
            allertAccept();
            loading();
            tabId = tabList.get(tabList.size()-1).getAttribute("id");
        }
        else if(state.equals("cancel")){
            allertcancel();
            tabId = null;
        }
        return tabId;
    }

    /**
     * this method clicks on requested tab given its id
     * @param tabId
     */
    public void goToTabById(String tabId){
        WebElement tab = getTabsMap().get(tabId);
        click(tab);
        tabActionListBtn = tab.findElement(By.cssSelector(".list-action"));
    }

    /**
     * @description this method opens the 'Select list' menu and clicks on the requested tab name
     * @param tabId - string
     */
    public void goToTabFromList(String tabId){
        String tabIdNum = tabId.split("_")[1];
        String tabIdInList = "slmenu_list:" + tabIdNum;
        click(tabsSelectListBtn);
        sleep(200);
        click(getTabsFromListMap().get(tabIdInList));
        WebElement tab = getTabsMap().get(tabId);
        tabActionListBtn = tab.findElement(By.cssSelector(".list-action"));
    }

    public void printTabActionList(String tabid){
        goToTabById(tabid);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            System.out.println(getText(op));
        }
    }

    /**
     * this method clicks on the requested tab, opens its action list menu and select the 'Delete list', and accepts or cancels it according to param
     * @param tabId - string - for clicking the requested tab
     * @param state - accept/cancel indication for delete action
     */
    public void deleteTabById(String tabId, String state){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Delete list")){
                click(op);
                break;
            }
        }
        if(state.equals("accept")){
            allertAccept();
            loading();
        }
        else if(state.equals("cancel"))
            allertcancel();

    }

    /**
     * this method opens the tab action list menu and selects the 'Rename list' option, enters the new tab name, and accepts/cancels
     * @param tabId - string - the tab id in which we want to rename
     * @param state - string - indicates if we want accept or cancel the rename action
     * @param tabName - string - the new tab name we want to enter
     */
    public void renameTab(String tabId, String state, String tabName){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Rename list")){
                click(op);
                break;
            }
        }
        allertSendText(tabName);
        if(state.equals("accept")){
            allertAccept();
            loading();
        }
        else if(state.equals("cancel"))
            allertcancel();
    }

    /**
     * @description this method selects the given tab, opens its action list menu and selects 'Hide list'
     * @param tabId - string
     */
    public void hideTab(String tabId){
        goToTabById(tabId);
        click(tabActionListBtn);
        for (WebElement op : tabActionList) {
            if(getText(op).equals("Hide list")){
                click(op);
                break;
            }
        }
        loading();
    }

    // tab validation methods //
    /**
     * @description validation method which indicates if a tab exist or not in the visible tab list given tabid
     * @param tabId - string
     * @return true/false if tab exist
     */
    public boolean  isTabExistInVisibleList(String tabId){
        return getTabsMap().containsKey(tabId);
    }

    /**
     * @description validation method which indicates if a tab exist or not in the tabs 'Select List' menu given tabid and tab name params
     * @param tabId - string
     * @return true/false if tab exist
     */
    public boolean  isTabExistInSelectList(String tabId){
        String idInList = "slmenu_list:" + tabId.split("_")[1];
        click(tabsSelectListBtn);
        sleep(200);
        return getTabsFromListMap().containsKey(idInList);
    }

    /**
     * @description this method checks if a given tab is visible or not
     * @param tabId
     * @return true/false if tab is visible
     */
    public boolean isTabVisible(String tabId){
        return getTabsMap().get(tabId).isDisplayed();
    }

//    public boolean isTabExistByName(String tabName){
//        boolean result = false;
//        for (WebElement tab : tabList) {
//            if(getText(tab.findElement(By.cssSelector("a>span"))).equals(tabName)){
//                result = true;
//                break;
//            }
//        }
//        return result;
//    }
}
