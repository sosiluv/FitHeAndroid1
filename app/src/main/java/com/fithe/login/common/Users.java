package com.fithe.login.common;

public class Users {

    private String id;
    private String password;
    private String email;
    private String gender;
    private int enabled;
    private int idCheck;

    public Users() {}

    public Users(String id, String password, String email, String gender, int enabled, int idCheck) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.enabled = enabled;
        this.idCheck = idCheck;
    }
    public String getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getGender() {
        return gender;
    }
    public int getEnabled() {
        return enabled;
    }
    public int getIdCheck() {
        return idCheck;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
    public void setIdCheck(int idCheck) {
        this.idCheck = idCheck;
    }



}

