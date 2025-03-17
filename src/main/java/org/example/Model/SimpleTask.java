package org.example.Model;

import java.io.Serializable;

public class SimpleTask extends Task  implements Serializable {
    private int startHour;
    private int endHour;

    public SimpleTask(int idTask, int startHour, int endHour) {
        super(idTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {
        if(endHour >= startHour) {
            return endHour - startHour;
        } else {
            return 24 - startHour + endHour;
        }
    }
}
