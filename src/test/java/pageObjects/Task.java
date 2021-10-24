package pageObjects;

import java.util.*;

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
    private List<String> taskTagsList = new ArrayList<>();

    // constructor
    public Task(String taskPriority, String taskDueDateIn, String dueDateText, String taskName, String taskNotes, String taskTags) {
        if(taskPriority.isEmpty())
            this.taskPriority = "0";
        else this.taskPriority = taskPriority;
        this.taskDueDateIn = taskDueDateIn;
        this.dueDateText = dueDateText;
        this.taskName = taskName;
        this.taskNotes = taskNotes;
        this.taskTagsString = taskTags;
        if(taskTags.contains(",")){
            taskTags = taskTags.replaceAll(" ", "");
            this.taskTagsList = Arrays.asList(taskTags.split(","));
        }
        else
            this.taskTagsList.add(taskTags);

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
            tag = tag.trim();
            taskTagsMap.put(tag, tag);
        }
        return taskTagsMap;
    }
}
