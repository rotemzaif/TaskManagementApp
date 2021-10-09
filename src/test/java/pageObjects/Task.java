package pageObjects;

/**
 * Task class is used for creating and testing new and existing tasks
 */
public class Task {
    // properties
    private String taskPriority;
    private String taskDueDate;
    private String taskName;
    private String taskNotes;
    private String taskTags;

    // constructor
    public Task(String taskPriority, String taskDueDate, String taskName, String taskNotes, String taskTags) {
        this.taskPriority = taskPriority;
        this.taskDueDate = taskDueDate;
        this.taskName = taskName;
        this.taskNotes = taskNotes;
        this.taskTags = taskTags;
    }

    // getters
    public String getTaskPriority() {
        return taskPriority;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskNotes() {
        return taskNotes;
    }

    public String getTaskTags() {
        return taskTags;
    }

}
