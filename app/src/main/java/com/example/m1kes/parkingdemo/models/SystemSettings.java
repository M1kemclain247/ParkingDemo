package com.example.m1kes.parkingdemo.models;

import java.util.Date;



public class SystemSettings {

    private int id;
    private String version;
    private Date versionDate;
    private String serverName;
    private String serverIp;
    private String localAddress;
    private String deviceServiceAddress;

    public SystemSettings() {
    }

    public SystemSettings(int id, String version, Date versionDate, String serverName, String serverIp, String localAddress, String deviceServiceAddress) {
        this.id = id;
        this.version = version;
        this.versionDate = versionDate;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.localAddress = localAddress;
        this.deviceServiceAddress = deviceServiceAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getDeviceServiceAddress() {
        return deviceServiceAddress;
    }

    public void setDeviceServiceAddress(String deviceServiceAddress) {
        this.deviceServiceAddress = deviceServiceAddress;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemSettings that = (SystemSettings) o;

        if (id != that.id) return false;
        if (!version.equals(that.version)) return false;
        if (!versionDate.equals(that.versionDate)) return false;
        if (!serverName.equals(that.serverName)) return false;
        if (!serverIp.equals(that.serverIp)) return false;
        if (!localAddress.equals(that.localAddress)) return false;
        return deviceServiceAddress.equals(that.deviceServiceAddress);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + version.hashCode();
        result = 31 * result + versionDate.hashCode();
        result = 31 * result + serverName.hashCode();
        result = 31 * result + serverIp.hashCode();
        result = 31 * result + localAddress.hashCode();
        result = 31 * result + deviceServiceAddress.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SystemSettings{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", versionDate=" + versionDate +
                ", serverName='" + serverName + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", localAddress='" + localAddress + '\'' +
                ", deviceServiceAddress='" + deviceServiceAddress + '\'' +
                '}';
    }
}
