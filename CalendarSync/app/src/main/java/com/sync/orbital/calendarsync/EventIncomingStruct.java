package com.sync.orbital.calendarsync;

import java.util.Calendar;

public class EventIncomingStruct {

    private String name, status, attendees;
    private Calendar startTime, endTime;

    public EventIncomingStruct(){

    }

    public EventIncomingStruct(String name, String status,
                               String attendees, Calendar startTime,
                               Calendar endTime) {
        this.name = name;
        this.status = status;
        this.attendees = attendees;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

}
