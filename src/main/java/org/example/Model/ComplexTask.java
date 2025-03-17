package org.example.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComplexTask extends Task  implements Serializable {
    private List<Task> subTasks = new ArrayList<>();

    public ComplexTask(int idTask) {
        super(idTask);
    }

    public void addTask(Task task) {
        subTasks.add(task);
    }

    public void deleteTask(Task task) {
        subTasks.remove(task);
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    @Override
    public int estimateDuration() {
        int totalDuration = 0;
        for (Task task : subTasks) {
            totalDuration += task.estimateDuration();
        }

        return totalDuration;
    }
}
