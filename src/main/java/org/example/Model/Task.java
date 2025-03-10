package org.example.Model;

public abstract class Task {
    protected int idTask;
    protected boolean statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = false;
    }

    public abstract int estimateDuration();

    public void setStatusTask(boolean statusTask) {
        this.statusTask = statusTask;
    }

    public int getIdTask() {
        return idTask;
    }
}