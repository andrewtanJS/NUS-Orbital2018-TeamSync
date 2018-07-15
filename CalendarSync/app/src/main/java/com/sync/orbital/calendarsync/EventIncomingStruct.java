package com.sync.orbital.calendarsync;

public class EventIncomingStruct {

    private String name, status, attendees, time, date;

    public EventIncomingStruct(){

    }

    public EventIncomingStruct(String name, String status, String attendees, String time, String date) {
        this.name = name;
        this.status = status;
        this.attendees = attendees;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
