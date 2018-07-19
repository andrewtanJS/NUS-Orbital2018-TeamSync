package com.sync.orbital.calendarsync;

public class EventIncomingStruct {

    private String name, status, attendees, startTime, endTime, date;

    public EventIncomingStruct(){

    }

    public EventIncomingStruct(String name, String status,
                               String attendees, String startTime,
                               String endTime, String date) {
        this.name = name;
        this.status = status;
        this.attendees = attendees;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
