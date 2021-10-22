package pageObjects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task class is used for creating and testing new and existing tasks
 */
public class Task {
    // properties
    private String taskPriority;
    private String taskDueDateIn;
    private String dueDateText;
    private String taskName;
    private String taskNotes;
    private String taskTagsString;
    private List<String> taskTagsList;

    // constructor
    public Task(String taskPriority, String taskDueDateIn, String dueDateText, String taskName, String taskNotes, String taskTags) {
        this.taskPriority = taskPriority;
        this.taskDueDateIn = taskDueDateIn;
        this.dueDateText = dueDateText;
        this.taskName = taskName;
        this.taskNotes = taskNotes;
        this.taskTagsString = taskTags;
        taskTags = taskTags.replaceAll(" ", "");
        this.taskTagsList = Arrays.asList(taskTags.split(","));
    }

    // getters

    public String getTaskPriority() {
        return taskPriority;
    }

    public String getTaskDueDateIn() {
        return taskDueDateIn;
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

    public String getTaskTagsString() {
        return taskTagsString;
    }

    public List<String> getTaskTagsList(){
        return taskTagsList;
    }

    public Map<String,String> getTaskTagsMap(){
        Map<String,String> taskTagsMap = new HashMap<>();
        for (String tag : taskTagsList) {
            taskTagsMap.put(tag, tag);
        }
        return taskTagsMap;
    }
}
