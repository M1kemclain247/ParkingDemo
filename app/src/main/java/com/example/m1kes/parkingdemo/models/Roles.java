package com.example.m1kes.parkingdemo.models;



public class Roles {

    public static final String ROLE_MARSHAL = "Marshall";
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_ADMIN = "Admin";

    private String roleName;

    public Roles() {
    }

    public Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}
