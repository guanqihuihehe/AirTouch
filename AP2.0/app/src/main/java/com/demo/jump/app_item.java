package com.demo.jump;

import android.graphics.drawable.Drawable;
//缩减版app信息
public class app_item {
    private Drawable appIcon;//app图标
    private String name;//app名字
    private String packagename;//app包名
    public app_item(String name,Drawable appIcon,String packagename){
        this.name=name;
        this.appIcon=appIcon;
        this.packagename=packagename;
    }
    public app_item(String name,Drawable appIcon){
        this.name=name;
        this.appIcon=appIcon;
    }
    public String getName(){
        return name;
    }
    public Drawable getAppIcon(){
        return appIcon;
    }
    public String getPackagename(){
        return packagename;
    }
    public void setAppIcon(Drawable appIcon){
        this.appIcon=appIcon;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPackagename(String packagename){
        this.packagename=packagename;
    }

}
