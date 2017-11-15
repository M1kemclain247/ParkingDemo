package com.example.m1kes.parkingdemo.models;


import java.util.Date;

public class Site {

    private int id;
    private String siteCode;
    private String name;
    private String descripiton;
    private String address;
    private String city;
    private String tel;
    private String cell;
    private String fax;
    private String email;
    private String motto;
    private String comment;

    private String audp;
    private String lu_audp;



    public Site() {
    }


    public Site(int id, String siteCode, String name, String descripiton, String address, String city, String tel, String cell, String fax, String email, String motto, String comment, String audp, String lu_audp) {
        this.id = id;
        this.siteCode = siteCode;
        this.name = name;
        this.descripiton = descripiton;
        this.address = address;
        this.city = city;
        this.tel = tel;
        this.cell = cell;
        this.fax = fax;
        this.email = email;
        this.motto = motto;
        this.comment = comment;
        this.audp = audp;
        this.lu_audp = lu_audp;
    }

    public Site(int id, String siteCode, String name, String descripiton, String address, String city, String tel, String cell, String fax, String email, String motto, String comment) {
        this.id = id;
        this.siteCode = siteCode;
        this.name = name;
        this.descripiton = descripiton;
        this.address = address;
        this.city = city;
        this.tel = tel;
        this.cell = cell;
        this.fax = fax;
        this.email = email;
        this.motto = motto;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

        Site site = (Site) o;

        if (id != site.id) return false;
        if (!siteCode.equals(site.siteCode)) return false;
        if (!name.equals(site.name)) return false;
        if (!descripiton.equals(site.descripiton)) return false;
        if (!address.equals(site.address)) return false;
        if (!city.equals(site.city)) return false;
        if (!tel.equals(site.tel)) return false;
        if (!cell.equals(site.cell)) return false;
        if (!fax.equals(site.fax)) return false;
        if (!email.equals(site.email)) return false;
        if (!motto.equals(site.motto)) return false;
        return comment.equals(site.comment);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + siteCode.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + descripiton.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + tel.hashCode();
        result = 31 * result + cell.hashCode();
        result = 31 * result + fax.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + motto.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", siteCode='" + siteCode + '\'' +
                ", name='" + name + '\'' +
                ", descripiton='" + descripiton + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", tel='" + tel + '\'' +
                ", cell='" + cell + '\'' +
                ", fax='" + fax + '\'' +
                ", email='" + email + '\'' +
                ", motto='" + motto + '\'' +
                ", comment='" + comment + '\'' +
                ", audp='" + audp + '\'' +
                ", lu_audp='" + lu_audp + '\'' +
                '}';
    }
}


