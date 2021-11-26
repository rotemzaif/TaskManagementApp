#### Project Description

This project includes testing framework and automated tests for a task management web application.

The web application details can found in the following address: https://www.mytinytodo.net/

The tests were designed and executed on the following web application address: https://www.mytinytodo.net/demo/

In General, the application consists of a main page, which consists of N tasks lists.

Each task list can be set to different display settings (i.e. sort options, show/hide completed tasks).

The user can choose to add a simple task (name only and priority by default is 0) or to add an advanced task (priority, due date, notes and tags). 

In order to create an advanced task, it is necessary to move to a different page.

The application has many more features. In order to see this features, please go the application links specified at the top.

Testing details (test plan & tests execution) are specified in the "Testing Details" section below.

#### Project Environment Details
Develop & Execution OS:    Win 10 64 bit

IDE:    IntelliJ

Web Browser:    Chrome

#### Project Technical Details
Programming Language:   JAVA (SDK 1.8)

Project Type:   Maven

Frameworks:
* Selenium
* TestNG

Design Patterns used:
* POM (Page Object Module)
* PageFactory

Project Structure:
* \src\test\java - framework & tests code (3 packages)

    - pageObjects package - consists of BasePage, main app page class, linked app pages classes and other assistance classes
    
    - tests package - consists of BaseTest class and all tested features classes
    
    - utils package - consists of 2 assistance classes for reading and analysing data
    
* \src\test\resources - data package

    - consists of 2 files: configuration properties and tasks details for testing purposes

#### Testing Details

Test plan

* a detailed application test plan can be found under the project folder: MyTinyTodo_test_plan.xlsx

* there are 7 test classes:

    - tab actions: creating a new tab, renaming a tab, deleting a tab, sort display options
    
    - adding a simple task: creating a dedicated tab for testing, adding multiple simple (name only) tasks (using @dataprovider & tasks data from excel file), verifying
    that num of tasks in the list has increased
    
    - advanced task page: testing all page elements (edit boxes, Select element options, save/cancel buttons, labels etc.)
    
    - add an advanced task: creating a dedicated testing tab if doesn't exists, adding multiple advanced tasks (using @dataprovider & tasks data from excel file), verifying that task list has increased, verifying added task Vs. entered task details
    
    - search text: searching a text in a tab, expecting to get a list of task that have that text in their name or notes
    
    - task actions: edit (all task attributes), task priority (directly from the list), task note (directly from the list), move task to another tab, and delete tab
    
    - defects: this test class includes 5 test methods which should fail intentionally    

* NOTE: not all application features have been tested

Setup & Prerequisites

* no specific setup or prerequisites are required

* make sure you have an internet connection

Test Execution - there are 3 ways to execute the various tests
1. executing each test separately

2. executing the testing.xml (under project folder) file which will run all test classes

3. via command line with maven command:
    * open command line window (cmd) and move to the project folder
    * enter the following command: mvn clean test 

Logs

    * there is no log file
    * all tests assertions have messages printed to console

Reporting

    * Allure reporting system
    * in order to get the test run report: execute the following command after running the test suit via maven: mvn allure:serve

Notes

* for test methods which read data from files (tasks data) - I used @dataprovider

* the framework captures failed tests images which are saved to folder 'Screenshots' under the project folder: 

* in the framework code, I used Maps (tabs, tasks, tasks tags, task priorities) whenever I could for search performance 

#### Challenges
* Slow internet connection

  Problem: sometimes I faced slow internet connection which failed tests (elements were not recognized)

  Solution: added fluent wait for some elements
  
* Task 'Due date' presentation in the task list

  Problem:
  
    - when entering an advanced task with a due date, the date format I used is: 'dd/mm/yy' or 'd/m/yy' or 'dd/mm/yyyy'
    
    - the application does not use any of the date formats I used to enter a task due date.
    
      It uses several logic's in order to present/display the task due date:
      
        * it checks if the due date is in current year or not
        
        * it displays the due date based on date formats in the Settings page
        
  Solution:
  
    - I created a SettingsPage class and developed methods that extract the date formats used in the application
    
    - created an assistance class which has several methods which get the application date formats and calculates the expected due date text     




