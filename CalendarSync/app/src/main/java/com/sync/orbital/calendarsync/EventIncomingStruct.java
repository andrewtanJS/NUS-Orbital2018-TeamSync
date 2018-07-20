package com.sync.orbital.calendarsync;

import java.util.Calendar;

public class EventIncomingStruct {

    private String name, status, attendees, startDate, startTime, endDate, endTime;
    // Date is formatted as "**/**/****"
    // Time is formatted as "**:**" in 24hr time format


    public EventIncomingStruct(){

    }

    public EventIncomingStruct(String name, String status,
                               String attendees, String startDate, String startTime,
                               String endDate, String endTime) {
        this.name = name;
        this.status = status;
        this.attendees = attendees;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
