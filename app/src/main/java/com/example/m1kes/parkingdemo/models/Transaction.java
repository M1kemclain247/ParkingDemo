package com.example.m1kes.parkingdemo.models;


import java.util.Date;

public class Transaction {

    //locally incrementing transaction ID
    private int id;

    private String shiftId;
    private String username;
    private int terminalId;
    private String recieptNumber;
    private String precinctID;
    private int bayNumber;
    private long requestDateTime;
    private String vehicleregNumber;
    private int duration;
    private long in_datetime;
    private long transactionDateTime;

    //For us to keep track of what has synced or not
    private boolean is_synced;
    //use this to see still logged in vehicles
    private long expiry_datetime;
    //get from precincts as a tarrif to charge when offline
    private double amountDue;
    private double amountPaid;
    private double userBalance;

    private double tenderAmount;
    private double change;


    //Transaction Type so that we can keep track of the types of transactions
    private String transactionType;
    //Payment method ID which is local but hardcoded for now it wont be used
    private int payment_mode_id;
    private String remaining_time;
    private boolean is_Expired;
    private boolean is_Notified;
    private boolean is_Prepayment;

    private boolean is_Paid;
    private String fineRefNo;
    private String paymentRefNo;




    public Transaction() {
    }

    public Transaction(int id, String shiftId, String username, int terminalId, String recieptNumber, String precinctID, int bayNumber, long requestDateTime, String vehicleregNumber, int duration, long in_datetime, long transactionDateTime, boolean is_synced, long expiry_datetime, double amountDue, double amountPaid, double tenderAmount, double change, String transactionType, int payment_mode_id) {
        this.id = id;
        this.shiftId = shiftId;
        this.username = username;
        this.terminalId = terminalId;
        this.recieptNumber = recieptNumber;
        this.precinctID = precinctID;
        this.bayNumber = bayNumber;
        this.requestDateTime = requestDateTime;
        this.vehicleregNumber = vehicleregNumber;
        this.duration = duration;
        this.in_datetime = in_datetime;
        this.transactionDateTime = transactionDateTime;
        this.is_synced = is_synced;
        this.expiry_datetime = expiry_datetime;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.tenderAmount = tenderAmount;
        this.change = change;
        this.transactionType = transactionType;
        this.payment_mode_id = payment_mode_id;
    }

    public Transaction(String vehicleregNumber, long expiry_datetime) {
        this.vehicleregNumber = vehicleregNumber;
        this.expiry_datetime = expiry_datetime;
    }

    public Transaction(String vehicleregNumber, long expiry_datetime,boolean is_Expired) {
        this.vehicleregNumber = vehicleregNumber;
        this.expiry_datetime = expiry_datetime;
        this.is_Expired = is_Expired;
    }

    public Transaction(String vehicleregNumber, long expiry_datetime,boolean is_Expired,boolean is_Notified) {
        this.vehicleregNumber = vehicleregNumber;
        this.expiry_datetime = expiry_datetime;
        this.is_Expired = is_Expired;
        this.is_Notified = is_Notified;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public String getRecieptNumber() {
        return recieptNumber;
    }

    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    public String getPrecinctID() {
        return precinctID;
    }

    public void setPrecinctID(String precinctID) {
        this.precinctID = precinctID;
    }

    public String getRemaining_time() {

        return remaining_time;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }

    public boolean is_Notified() {
        return is_Notified;
    }

    public void setIs_Notified(boolean is_Notified) {
        this.is_Notified = is_Notified;
    }

    public boolean is_Expired() {
        return is_Expired;
    }

    public void setIs_Expired(boolean is_Expired) {
        this.is_Expired = is_Expired;
    }

    public void setRemaining_time(String remaining_time) {
        this.remaining_time = remaining_time;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public void setBayNumber(int bayNumber) {
        this.bayNumber = bayNumber;
    }

    public long getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(long requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public String getVehicleregNumber() {
        return vehicleregNumber;
    }

    public void setVehicleregNumber(String vehicleregNumber) {
        this.vehicleregNumber = vehicleregNumber;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public boolean is_synced() {
        return is_synced;
    }

    public void setIs_synced(boolean is_synced) {
        this.is_synced = is_synced;
    }

    public long getIn_datetime() {
        return in_datetime;
    }

    public void setIn_datetime(long in_datetime) {
        this.in_datetime = in_datetime;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public int getPayment_mode_id() {
        return payment_mode_id;
    }

    public void setPayment_mode_id(int payment_mode_id) {
        this.payment_mode_id = payment_mode_id;
    }


    public long getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(long transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public long getExpiry_datetime() {
        return expiry_datetime;
    }

    public void setExpiry_datetime(long expiry_datetime) {
        this.expiry_datetime = expiry_datetime;
    }


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    public double getTenderAmount() {
        return tenderAmount;
    }

    public void setTenderAmount(double tenderAmount) {
        this.tenderAmount = tenderAmount;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public boolean is_Prepayment() {
        return is_Prepayment;
    }

    public void setIs_Prepayment(boolean is_Prepayment) {
        this.is_Prepayment = is_Prepayment;
    }


    public boolean is_Paid() {
        return is_Paid;
    }

    public void setIs_Paid(boolean is_Paid) {
        this.is_Paid = is_Paid;
    }

    public String getFineRefNo() {
        return fineRefNo;
    }

    public void setFineRefNo(String fineRefNo) {
        this.fineRefNo = fineRefNo;
    }

    public String getPaymentRefNo() {
        return paymentRefNo;
    }

    public void setPaymentRefNo(String paymentRefNo) {
        this.paymentRefNo = paymentRefNo;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", shiftId='" + shiftId + '\'' +
                ", username='" + username + '\'' +
                ", terminalId=" + terminalId +
                ", recieptNumber='" + recieptNumber + '\'' +
                ", precinctID='" + precinctID + '\'' +
                ", bayNumber=" + bayNumber +
                ", requestDateTime=" + requestDateTime +
                ", vehicleregNumber='" + vehicleregNumber + '\'' +
                ", duration=" + duration +
                ", in_datetime=" + in_datetime +
                ", transactionDateTime=" + transactionDateTime +
                ", is_synced=" + is_synced +
                ", expiry_datetime=" + expiry_datetime +
                ", amountDue=" + amountDue +
                ", amountPaid=" + amountPaid +
                ", tenderAmount=" + tenderAmount +
                ", change=" + change +
                ", transactionType='" + transactionType + '\'' +
                ", payment_mode_id=" + payment_mode_id +
                ", remaining_time='" + remaining_time + '\'' +
                ", is_Expired=" + is_Expired +
                ", is_Notified=" + is_Notified +
                ", is_Prepayment=" + is_Prepayment +
                '}';
    }
}
