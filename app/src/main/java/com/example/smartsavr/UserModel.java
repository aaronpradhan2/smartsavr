package com.example.smartsavr;

public class UserModel {
    private String name, email;
    private int userid;


    public UserModel()
    {

    }

    public UserModel(int userid,String name, String email) {
        this.userid = userid;
        this.name = name;
        this.email = email;
    }

    public void setUserid(int userid){this.userid=userid;}

    public int getUserid(){
        return userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
