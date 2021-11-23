package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;

/**
 * this class derives from BasePage class and focuses on the tab elements and general page elements
 * which will be passed to class TaskList which focuses on the tasks part
 */
public class TasksPage extends BasePage {

    // page general elements
    @FindBy(css = "#settings")
    private WebElement settingsLink;
    @FindBy(css = "#tagcloudbtn")
    private WebElement tagsBtn;
    @FindBy(css = "#tagcloudcancel")
    private WebElement closeTagsMenuBtn;
    @FindBy(css = "#tagcloudcontent > a")
    private List<WebElement> tagElList;

    // tab elements
    @FindBy(css = ".mtt-tabs-add-button")
    private WebElement newTabBtn;
    @FindBy(css = ".mtt-tabs.ui-sortable > li")
    private List<WebElement> tabElList;
    @FindBy(css = ".mtt-tabs > li > a > span")
    private List<WebElement> tabNameListEl;
    @FindBy(css = ".mtt-tabs.ui-sortable>li>a>.list-action")
    private List<WebElement> tabActionsBtnListEl; // will be initialized when tab is selected
    @FindBy(css = "#listmenucontainer > ul > li")
    private List<WebElement> tabActionList;
    // tab Select list
    @FindBy(css = "#tabs_buttons")
    private WebElement tabsSelectListBtn;
    @FindBy(css = "#slmenucontainer > ul > li")
    private List<WebElement> tabElSelectList;
    @FindBy(css = "#slmenucontainer > ul >li>a")
    private List<WebElement> tabsSelectNameList;


    // tab properties
    Map<String, List<Object>> tabsMap = new HashMap<>();
    Map<String, List<Object>> tabsSelectListMap = new HashMap<>();

    // constructor
    public TasksPage(WebDriver driver) {
        super(driver);
        // initializing tabs maps (visible list and select list)
        initTabsMap();
        initTabsSelectMap();
    }

    // general getters methods

    /**
     * @return List<String> tag names
     * @description this extracts all tag elements, extracts their name and add it to a list
     */
    public List<String> getTagsNameList() {
        click(tagsBtn);
        wait.until(ExpectedConditions.visibilityOf(closeTagsMenuBtn));
        List<String> tagList = new ArrayList<>();
        // checking if there are tags
        if (tagElList.size() != 0) {
            String tagName = "";
            for (WebElement el : tagElList) {
                tagName = getText(el);
                tagList.add(tagName);
            }
            click(closeTagsMenuBtn);
        }
        return tagList;
    }

    // general action method
    public void goToSettingsPage() {
        click(settingsLink);
    }

    // general validation
    public boolean tagsExistance(){
        boolean result = false;
        click(tagsBtn);
        wait.until(ExpectedConditions.visibilityOf(closeTagsMenuBtn));
        if(tagElList.size() != 0)
            result = true;
        click(closeTagsMenuBtn);
        return result;
    }

    // tab related getters
    public List<WebElement> getTabElList() {
        return tabElList;
    }

    /**
     * @param tabName    - String
     * @param searchType - enum EQUALS/CONTAINS
     * @return List<Integer> of tab id
     * @description this method iterates through the tab element list, checks if the tab name equals/contains the tab name arg;
     * if it matches, it gets its ID and adds it to the list
     */
    public List<String> getTabIdListForName(String tabName, SearchType searchType) {
        List<String> tabIdList = new ArrayList<>();
        if (tabElList.size() == tabNameListEl.size()) {
            String name;
            for (int i = 0; i < tabElList.size(); i++) {
                name = getText(tabNameListEl.get(i));
                if (searchType == SearchType.EQUAL) {
                    if (name.equals(tabName))
                        tabIdList.add(tabElList.get(i).getAttribute("id"));
                } else if (searchType == SearchType.CONTAINS) {
                    if (name.contains(tabName))
                        tabIdList.add(tabElList.get(i).getAttribute("id"));
                }
            }
        } else System.out.println("num of tabs names doesn't match num of visible tabs");
        return tabIdList;
    }

    public List<WebElement> getTabElSelectList() {
        return tabElSelectList;
    }

    /**
     * @return - map of tab elements key = tabid, value = list of the following objects: tab element, tab name, tab actions button
     */
    public Map<String, List<Object>> getTabsMap() {
        return tabsMap;
    }

    /**
     * @return - map of tab elements key = tabid, value = list of the following objects: list element, tab name
     */
    public Map<String, List<Object>> getTabsFromListMap() {
//        Map<String, WebElement> tabsSelectListMap = new HashMap<>();
//        String key;
//        for (int i = 2; i < tabElSelectList.size(); i++) {
//            key = tabElSelectList.get(i).getAttribute("id");
//            tabsSelectListMap.put(key, tabElSelectList.get(i));
//        }
        return tabsSelectListMap;
    }

    /**
     * @param tabId - string - the tab id we want to get its name
     * @return - string - the tab name
     */
    public String getTabNameById(String tabId) {
        return (String) getTabsMap().get(tabId).get(1);
    }

    /**
     * @return - tab name - String
     * @description this method iterates on all tabs and checks which tab is currently selected and returns its name
     */
    public String getCurrentTabName() {
        String tabName = "";
        for (WebElement tab : tabElList) {
            if (tab.getAttribute("class").contains("selected")) {
                tabName = getText(tab.findElement(By.cssSelector("a > span")));
                break;
            }
        }
        return tabName;
    }

    /**
     * this method checks which is tab is currently selected and returns its ID
     *
     * @return - tabId - String
     */
    public String getCurrentTabId() {
        String tabId = "";
        for (WebElement tab : tabElList) {
            if (tab.getAttribute("class").contains("selected")) {
                tabId = tab.getAttribute("id");
                break;
            }
        }
        return tabId;
    }

    /**
     * this checks which sort option is selected and returns its name/description
     *
     * @param tabId - string
     * @return selectedOption - string
     */
    public String getTabSortOption(String tabId) {
        String selectedOption = "";
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
//        click(tabActionsBtn);
        for (WebElement op : tabActionList) {
            if (op.getAttribute("class").contains("sort") &&
                    op.getAttribute("class").contains("checked")) {
                selectedOption = getText(op);
                if (!selectedOption.equals("Sort by hand"))
                    selectedOption = selectedOption.substring(0, selectedOption.length() - 1).trim();
                break;
            }
        }
        click(tabActionsBtn);
        return selectedOption;
    }

    // tab related actions

    /**
     * @param tabName     - String
     * @param allertState - enum ACCPET/CANCEL
     * @param sortOption  - String
     * @param sctSate     - enum SELECT/UNSELECT, sct - show completed tasks
     * @return tabId - new tab Id - String
     * @description method that creates a new tab (or cancels), sets tab sort option according to arg entered, and select/un-select
     * 'Show completed tasks' according to state arg entered
     */
    public String createNewTab(String tabName, AlertState allertState, SortOption sortOption, OptionState sctSate) {
        String tabId = "";
        click(newTabBtn);
        allertSendText(tabName);
        if (allertState == AlertState.ACCEPT) {
            allertAccept();
            loading();
            tabId = tabElList.get(tabElList.size() - 1).getAttribute("id");
            // set tab sort display
            if(sortOption != null){
                if (!setTabSortDisplay(tabId, sortOption))
                    System.out.println("'" + sortOption + "' was not found in the tab action menu list!\n");
            }
            // set tab 'show completed tasks' state - select/un-select
            if (sctSate != null) {
                if (!setTabCompletedTasksDisplay(tabId, sctSate))
                    System.out.println("'Show completed tasks' option was not found in the tab action menu list!\n");
            }
        } else if (allertState == AlertState.CANCEL)
            allertcancel();
        return tabId;
    }

    /**
     * this method clicks on requested tab given its id
     *
     * @param tabId
     */
    public void goToTabById(String tabId) {
        WebElement tab = (WebElement) getTabsMap().get(tabId).get(0);
        click(tab);
        loading();
    }

    /**
     * @param tabId - string
     * @description this method opens the 'Select list' menu and clicks on the requested tab name
     */
    public void goToTabFromList(String tabId) {
        String tabIdInList = "";
        if (!tabId.contains("slmenu_list")) {
            String tabIdNum = tabId.split("_")[1];
            tabIdInList = "slmenu_list:" + tabIdNum;
        } else tabIdInList = tabId;
        click(tabsSelectListBtn); // opening the tabs Select list
        wait.until(ExpectedConditions.elementToBeClickable((WebElement) getTabsFromListMap().get(tabIdInList).get(0)));
        click((WebElement) getTabsFromListMap().get(tabIdInList).get(0));
    }

    public void openTabActionsMenu(String tabId){
        // getting target/tested tab actions menu button element
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        // opening the tab action menu
        click(tabActionsBtn);
        wait.until(ExpectedConditions.visibilityOfAllElements(tabActionList));
    }

    /**
     * this method selects the 'Delete list' option from the tab actions menu, accepts or cancels it according to param
     * @param state - accept/cancel indication for delete action
     */
    public void deleteTab(AlertState state) {
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Delete list")) {
                click(op);
                break;
            }
        }
        if (state == AlertState.ACCEPT) {
            allertAccept();
            loading();
        } else if (state == AlertState.CANCEL)
            allertcancel();
    }

    /**
     * this method modifies a tab name according to alert state (accept/cancel) and new name received
     * @param state         - string - indicates if we want accept or cancel the rename action
     * @param tabNewName    - string - the new tab name we want to enter
     */
    public void renameTab(AlertState state, String tabNewName){
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Rename list")) {
                click(op);
                break;
            }
        }
        allertSendText(tabNewName);
        if (state == AlertState.ACCEPT) {
            allertAccept();
            loading();
        } else if (state == AlertState.CANCEL)
            allertcancel();
    }

    public void hideTab(){
        for (WebElement op : tabActionList) {
            if (getText(op).equals("Hide list")) {
                click(op);
                break;
            }
        }
        loading();
    }

    /**
     * @param tabId      - String
     * @param sortOption - String
     * @description this method moves to the desired tab and sets its sort display according to param
     */
    public boolean setTabSortDisplay(String tabId, SortOption sortOption) {
        String sortType = sortOption.getSort();
        boolean optionFound = false;
        // getting current tab action menu button element
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(sortType)) {
                optionFound = true;
                if (!op.getAttribute("class").contains("checked"))
                    click(op);
                else click(tabActionsBtn);
                break;
            }
        }
        return optionFound;
    }

    /**
     * @param tabId - string
     * @param state - string - indicates select/un-select option
     * @description this method sets the tab 'Show completed tasks' state according to param
     */
    public boolean setTabCompletedTasksDisplay(String tabId, OptionState state) {
        boolean optionFound = false;
        String option = "Show completed tasks";
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(option)) {
                optionFound = true;
                if (state == OptionState.SELECT) {
                    if (!op.getAttribute("class").contains("checked")) {
                        click(op);
                        loading();
                        break;
                    } else click(tabActionsBtn); // for closing the actions menu
                } else if (state == OptionState.UNSELECT) {
                    if (op.getAttribute("class").contains("checked")) {
                        click(op);
                        loading();
                        break;
                    } else click(tabActionsBtn); // for closing the actions menu
                }
            }
        }
        return optionFound;
    }

    // tab related validations

    /**
     * @param tabId - string
     * @return true/false if tab exist
     * @description validation method which indicates if a tab exist or not in the visible tab list given tabid
     */
    public boolean isTabExistInVisibleList(String tabId) {
        return getTabsMap().containsKey(tabId);
    }

    /**
     * @param tabId - string
     * @return true/false if tab exist
     * @description validation method which indicates if a tab exist or not in the tabs 'Select List' menu given tabid and tab name params
     */
    public boolean isTabExistInSelectList(String tabId) {
        String idInList = "slmenu_list:" + tabId.split("_")[1];
        click(tabsSelectListBtn);
        wait.until(ExpectedConditions.visibilityOfAllElements(tabElSelectList));
        return getTabsFromListMap().containsKey(idInList);
    }

    /**
     * @param tabId
     * @return true/false if tab is visible
     * @description this method checks if a given tab is visible or not
     */
    public boolean isTabVisible(String tabId) {
        WebElement tab = (WebElement) getTabsMap().get(tabId).get(0);
        return tab.isDisplayed();
    }

    /**
     * this method checks if a tab 'Show completed tasks' option is checked or not
     *
     * @param tabId - string
     * @return boolean true/false if option is checked or not
     */
    public boolean isTabCompletedTasksChecked(String tabId) {
        String option = "Show completed tasks";
        boolean result = false;
        WebElement tabActionsBtn = (WebElement) getTabsMap().get(tabId).get(2);
        for (WebElement op : tabActionList) {
            if (getText(op).equals(option)) {
                if (op.getAttribute("class").contains("checked"))
                    result = true;
                break;
            }
        }
        click(tabActionsBtn); // closing the tab actions menu
        return result;
    }

    // enums
    public enum AlertState {
        ACCEPT, CANCEL;
    }

    public enum SearchType {
        CONTAINS, EQUAL,
        ;
    }

    public enum OptionState {
        SELECT, UNSELECT;
    }

    public enum SortOption {
        HAND("Sort by hand"),
        DATECREATED("Sort by date created"),
        PRIORITY("Sort by priority"),
        DUEDATE("Sort by due date"),
        DATEMODIFIED("Sort by date modified");

        private String sort;

        public String getSort(){
            return this.sort;
        }

        SortOption(String sort){
            this.sort = sort;
        }
    }

    // assistance methods
    public void initTabsMap(){
        String tabId, tabName;
        WebElement tabEl, tabActionsBtn;
        if(tabElList.size() == tabNameListEl.size() && tabElList.size() == tabActionsBtnListEl.size()){
            for (int i = 0; i < tabElList.size(); i++) {
                tabId = tabElList.get(i).getAttribute("id");
                tabEl = tabElList.get(i);
                tabName = getText(tabNameListEl.get(i));
                tabActionsBtn = tabActionsBtnListEl.get(i);
                tabsMap.put(tabId, Arrays.asList(tabEl, tabName, tabActionsBtn));
            }
        }
        else {
            System.out.println("tabs name list (tabNameListEl) size or tabs action menu button list (tabActionsBtnListEl) size doesn't match tabs element list:");
            System.out.printf("tabs element list size: %d\ntabs name list size: %d\ntabs action menu button list size: %d\n", tabElList.size(), tabNameListEl.size(),
                    tabActionsBtnListEl.size());
        }
    }

    public void initTabsSelectMap(){
        String tabId, tabName;
        WebElement tabListEl;
        for (int i = 2; i < tabElSelectList.size(); i++) {
            tabId = tabElSelectList.get(i).getAttribute("id");
            tabListEl = tabElSelectList.get(i);
//            tabName = getText(tabsSelectNameList.get(i-1));
            tabName = tabsSelectNameList.get(i-1).getAttribute("innerHTML");
            tabsSelectListMap.put(tabId, Arrays.asList(tabListEl, tabName));
        }
    }
}
