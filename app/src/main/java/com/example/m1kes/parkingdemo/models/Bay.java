package com.example.m1kes.parkingdemo.models;


import java.util.Date;

public class Bay {

    private int id;
    private int precinctId;
    private String bayNumber;

    private String audp;
    private String lu_audp;



    public Bay() {
    }

    public Bay(int id, int precinctId, String bayNumber, String audp, String lu_audp) {
        this.id = id;
        this.precinctId = precinctId;
        this.bayNumber = bayNumber;
        this.audp = audp;
        this.lu_audp = lu_audp;
    }

    public Bay(int id, int precinctId, String bayNumber) {
        this.id = id;
        this.precinctId = precinctId;
        this.bayNumber = bayNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getBayNumber() {
        return bayNumber;
    }

    public void setBayNumber(String bayNumber) {
        this.bayNumber = bayNumber;
    }

    public int getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(int precinctId) {
        this.precinctId = precinctId;
    }

    public String getAudp() {
        return audp;
    }

    public void setAudp(String audp) {
        this.audp = audp;
    }


    public String getLu_audp() {
        return lu_audp;
    }

    public void setLu_audp(String lu_audp) {
        this.lu_audp = lu_audp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bay bay = (Bay) o;

        if (id != bay.id) return false;
        if (precinctId != bay.precinctId) return false;
        return bayNumber.equals(bay.bayNumber);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + precinctId;
        result = 31 * result + bayNumber.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Bay{" +
                "id=" + id +
                ", precinctId=" + precinctId +
                ", bayNumber='" + bayNumber + '\'' +
                ", audp='" + audp + '\'' +
                ", lu_audp='" + lu_audp + '\'' +
                '}';
    }
}

