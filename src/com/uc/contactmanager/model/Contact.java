package com.uc.contactmanager.model;

/**
 * Created by lvrh on 2015/5/9.
 */
public class Contact {
    public Contact(String code, String name, String mobile, String address) {
        this.code = code;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    private String code;
    private String name;
    private String mobile;
    private String address;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
