package com.dc.lockphone.model;

/**
 * Created by dcoellar on 10/2/15.
 */
public class UserInfo {

    private String fullname;
    private String address;
    private String phone;
    private String ruc_ci;
    private String email;
    private String password;

    public UserInfo(){};

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRuc_ci() {
        return ruc_ci;
    }

    public void setRuc_ci(String ruc_ci) {
        this.ruc_ci = ruc_ci;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
