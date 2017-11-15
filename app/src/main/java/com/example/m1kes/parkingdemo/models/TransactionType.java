package com.example.m1kes.parkingdemo.models;


import java.util.Date;

public class TransactionType {

    public static final String TYPE_PAYMENT = "MakePayment";
    public static final String TYPE_FINE = "MakeFine";
    public static final String TYPE_PAY_ARREARS = "PayArrears";
    public static final String TYPE_PREPAYMENT = "Prepayment";


    private int id;
    private String name;


    public TransactionType() {
    }

    public TransactionType(int id, String name) {
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
        return "TransactionType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
