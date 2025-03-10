package org.example.Model;

import java.util.ArrayList;
import java.util.List;

class ComplexTask extends Task {
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

    @Override
    public int estimateDuration() {
        return 0;
    }
}
