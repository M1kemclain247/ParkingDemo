package com.example.m1kes.parkingdemo.modules.supervisor.models;


public class JsonExports {


    private int id;
    private String filePath;
    private long creationDateTime;

    public JsonExports() {
    }

    public JsonExports(int id, String filePath, long creationDateTime) {
        this.id = id;
        this.filePath = filePath;
        this.creationDateTime = creationDateTime;
    }
    public JsonExports(String filePath, long creationDateTime) {
        this.filePath = filePath;
        this.creationDateTime = creationDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(long creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @Override
    public String toString() {
        return "JsonExports{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", creationDateTime=" + creationDateTime +
                '}';
    }
}
