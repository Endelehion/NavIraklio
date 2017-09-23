package com.example.varda.naviraklio.model;

public class Cinema {

    private String cWorkingHours;
    private String cAddress;
    private String dailyProgram;

    public String getcWorkingHours() {
        return cWorkingHours;
    }

    public void setcWorkingHours(String cWorkingHours) {
        this.cWorkingHours = cWorkingHours;
    }

    public String getcAddress() {
        return cAddress;
    }

    public void setcAddress(String cAddress) {
        this.cAddress = cAddress;
    }

    public String getDailyProgram() {
        return dailyProgram;
    }

    public void setDailyProgram(String dailyProgram) {
        this.dailyProgram = dailyProgram;
    }
}
