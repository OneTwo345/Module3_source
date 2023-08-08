package com.example.test_module.model;

import com.example.test_module.model.enums.EGender;

import java.sql.Date;

public class Teacher {
    private Integer id;
    private String name;
    private Date dob;
    private String hobby;
    private EGender gender;
    private Category category;

    public Teacher(Integer id, String name, Date dob, String hobby, EGender gender, Category category) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.hobby = hobby;
        this.gender = gender;
        this.category = category;
    }

    public Teacher() {
    }

    public Integer getId() {
        return id;
    }

    public EGender getGender() {
        return gender;
    }

    public void setGender(EGender gender) {
        this.gender = gender;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        if(dob == null) return "";
        return dob.toString();    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
