package com.example.doctorappointment.doctorModel;

import java.io.Serializable;

public class VisitingScheduleModel implements Serializable {

    String schedule;

    public VisitingScheduleModel() {
    }

    public VisitingScheduleModel(String schedule) {
        this.schedule = schedule;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
