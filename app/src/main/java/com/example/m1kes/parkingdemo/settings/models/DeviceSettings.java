package com.example.m1kes.parkingdemo.settings.models;



public class DeviceSettings {


    private String deviceModel;
    private String deviceIMEI;
    private int terminalID;
    private boolean activationStatus;

    public DeviceSettings() {
    }

    public DeviceSettings(String deviceModel, String deviceIMEI, int terminalID, boolean activationStatus) {
        this.deviceModel = deviceModel;
        this.deviceIMEI = deviceIMEI;
        this.terminalID = terminalID;
        this.activationStatus = activationStatus;
    }


    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceIMEI() {
        return deviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        this.deviceIMEI = deviceIMEI;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    public boolean isActivated() {
        return activationStatus;
    }

    public void setActivationStatus(boolean activationStatus) {
        this.activationStatus = activationStatus;
    }

    @Override
    public String toString() {
        return "DeviceSettings{" +
                " deviceModel='" + deviceModel + '\'' +
                ", deviceIMEI='" + deviceIMEI + '\'' +
                ", terminalID='" + terminalID + '\'' +
                ", isActive=" + activationStatus +
                '}';
    }
}
