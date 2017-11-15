package com.example.m1kes.parkingdemo.models;


import android.os.Build;
import android.support.annotation.RequiresApi;

import org.w3c.dom.ls.LSInput;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class User implements Comparable<User> {

    private String id;
    private String username;
    private String password;
    private int empNo;
    private String firstname;
    private String surname;
    private List<String> roles;



    private boolean isLoggedIn;




    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password,boolean isLoggedIn) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }

    public User(String id, String username, String password, boolean isLoggedIn) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }

    public User(String id, String username, String password, int empNo, String firstname, String surname,List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.empNo = empNo;
        this.firstname = firstname;
        this.surname = surname;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getEmpNo() {
        return empNo;
    }

    public void setEmpNo(int empNo) {
        this.empNo = empNo;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof User)) {
            return false;
        }
        User user = (User) obj;
        return user.username.equals(username) &&
                user.password.equals(password);
    }




    public static Comparator<User> UserNameComparator
            = new Comparator<User>() {

        public int compare(User user1,User user2) {

            String username1 = user1.getUsername().toUpperCase();
            String username2 = user2.getUsername().toUpperCase();

            //ascending order
            return username1.compareTo(username2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };


    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", empNo=" + empNo +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", roles=" + roles +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }

    @Override
    public int compareTo(User o) {
        String username = ((User) o).getUsername();

        //ascending order
        return this.username.compareTo(username);
        //descending order
        //return compareQuantity - this.quantity;
    }
}
