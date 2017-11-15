package com.example.m1kes.parkingdemo.models;


import java.util.Date;

public class Shift {


    private int id;
    private String shiftId;
    private String username;
    private int precinctID;
    private String precinctZoneName;
    private String precinctName;
    private int terminalID;
    private long start_time;
    private long end_time;
    private boolean isActive;
    private boolean isStartShiftSynced;
    private boolean isCloseShiftSynced;

    private double total_declaredAmount;
    private double cash_collected;
    private double total_collected;
    private double total_cashed;
    private double ePayments;
    private double total_arrears;
    private double total_prepaid;
    private double ticketissued_amount;


    public Shift() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPrecinctID() {
        return precinctID;
    }

    public void setPrecinctID(int precinctID) {
        this.precinctID = precinctID;
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public boolean isStartShiftSynced() {
        return isStartShiftSynced;
    }

    public void setStartShiftSynced(boolean startShiftSynced) {
        isStartShiftSynced = startShiftSynced;
    }

    public boolean isCloseShiftSynced() {
        return isCloseShiftSynced;
    }

    public void setCloseShiftSynced(boolean closeShiftSynced) {
        isCloseShiftSynced = closeShiftSynced;
    }

    public String getPrecinctZoneName() {
        return precinctZoneName;
    }

    public void setPrecinctZoneName(String precinctZoneName) {
        this.precinctZoneName = precinctZoneName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getTotal_declaredAmount() {
        return total_declaredAmount;
    }

    public void setTotal_declaredAmount(double total_declaredAmount) {
        this.total_declaredAmount = total_declaredAmount;
    }

    public double getCash_collected() {
        return cash_collected;
    }

    public void setCash_collected(double cash_collected) {
        this.cash_collected = cash_collected;
    }

    public double getTotal_collected() {
        return total_collected;
    }

    public void setTotal_collected(double total_collected) {
        this.total_collected = total_collected;
    }

    public double getTotal_cashed() {
        return total_cashed;
    }

    public void setTotal_cashed(double total_cashed) {
        this.total_cashed = total_cashed;
    }

    public double getePayments() {
        return ePayments;
    }

    public void setePayments(double ePayments) {
        this.ePayments = ePayments;
    }

    public double getTotal_arrears() {
        return total_arrears;
    }

    public void setTotal_arrears(double total_arrears) {
        this.total_arrears = total_arrears;
    }

    public double getTotal_prepaid() {
        return total_prepaid;
    }

    public void setTotal_prepaid(double total_prepaid) {
        this.total_prepaid = total_prepaid;
    }

    public double getTicketissued_amount() {
        return ticketissued_amount;
    }

    public void setTicketissued_amount(double ticketissued_amount) {
        this.ticketissued_amount = ticketissued_amount;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", shiftId='" + shiftId + '\'' +
                ", username='" + username + '\'' +
                ", precinctID=" + precinctID +
                ", precinctZoneName='" + precinctZoneName + '\'' +
                ", precinctName='" + precinctName + '\'' +
                ", terminalID=" + terminalID +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", isActive=" + isActive +
                ", isStartShiftSynced=" + isStartShiftSynced +
                ", isCloseShiftSynced=" + isCloseShiftSynced +
                ", total_declaredAmount=" + total_declaredAmount +
                ", cash_collected=" + cash_collected +
                ", total_collected=" + total_collected +
                ", total_cashed=" + total_cashed +
                ", ePayments=" + ePayments +
                ", total_arrears=" + total_arrears +
                ", total_prepaid=" + total_prepaid +
                ", ticketissued_amount=" + ticketissued_amount +
                '}';
    }
}
