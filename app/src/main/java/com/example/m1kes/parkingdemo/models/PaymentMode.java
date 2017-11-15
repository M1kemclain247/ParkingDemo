package com.example.m1kes.parkingdemo.models;



public class PaymentMode {

    private int id;
    private String name;

    public PaymentMode() {
    }

    public PaymentMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PaymentMode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

