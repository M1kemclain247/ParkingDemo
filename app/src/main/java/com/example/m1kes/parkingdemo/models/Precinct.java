package com.example.m1kes.parkingdemo.models;



public class Precinct {

    private int id;
    private String name;
    private String zoneName;
    private double target;
    private String lunch;
    private double amount;

    public Precinct() {

    }

    public Precinct(int id, String name, String zoneName, double target, String lunch, double amount) {
        this.id = id;
        this.name = name;
        this.zoneName = zoneName;
        this.target = target;
        this.lunch = lunch;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Precinct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", target=" + target +
                ", lunch='" + lunch + '\'' +
                ", amount=" + amount +
                '}';
    }
}
