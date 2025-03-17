package org.example.Model;

import java.io.Serializable;

public abstract class Task  implements Serializable {
    protected int idTask;
    protected boolean statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = false;
    }

    public abstract int estimateDuration();

    public boolean getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(boolean statusTask) {
        this.statusTask = statusTask;
    }

    public int getIdTask() {
        return idTask;
    }
}