package com.example.m1kes.parkingdemo.models;




public class Schedule {

    private String lunchTime;
    private String zone;
    private String precinct;
    private double target;

    public Schedule() {
    }

    public Schedule(String lunchTime, String zone, String precinct, double target) {
        this.lunchTime = lunchTime;
        this.zone = zone;
        this.precinct = precinct;
        this.target = target;
    }

    public String getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPrecinct() {
        return precinct;
    }

    public void setPrecinct(String precinct) {
        this.precinct = precinct;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "lunchTime='" + lunchTime + '\'' +
                ", zone='" + zone + '\'' +
                ", precinct='" + precinct + '\'' +
                ", target=" + target +
                '}';
    }
}
