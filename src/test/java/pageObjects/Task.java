package pageObjects;

/**
 * Task class is used for creating and testing new and existing tasks
 */
public class Task {
    // properties
    private String taskPriority;
    private String taskDueDateIn;
    private String dueDateTitle;
    private String dueDateText;
    private String taskName;
    private String taskNotes;
    private String taskTags;

    // constructor
    public Task(String taskPriority, String taskDueDateIn, String dueDateTitle, String dueDateText, String taskName, String taskNotes, String taskTags) {
        this.taskPriority = taskPriority;
        this.taskDueDateIn = taskDueDateIn;
        this.dueDateTitle = dueDateTitle;
        this.dueDateText = dueDateText;
        this.taskName = taskName;
        this.taskNotes = taskNotes;
        this.taskTags = taskTags;
    }

    // getters

    public String getTaskPriority() {
        return taskPriority;
    }

    public String getTaskDueDateIn() {
        return taskDueDateIn;
    }

    public String getDueDateTitle() {
        return dueDateTitle;
    }

    public String getDueDateText() {
        return dueDateText;
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
