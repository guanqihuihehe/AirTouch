package com.demo.dialing;

public class Contacts_member{//通讯录成员
    private String PhoneNumber;//电话号码
    private String Name;//姓名
    Contacts_member(String PhoneNumber,String Name){
        this.PhoneNumber=PhoneNumber;
        this.Name=Name;
    }
    public String get_PhoneNumber(){
        return PhoneNumber;
    }
    public String get_Name(){
        return Name;
    }
    public void setPhoneNumber(String PhoneNumber){
        this.PhoneNumber=PhoneNumber;
    }
    public void setName(String Name){
        this.Name=Name;
    }
    public void set_all(String PhoneNumber,String Name){
        this.PhoneNumber=PhoneNumber;
        this.Name=Name;
    }
}