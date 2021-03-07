package com.demo.jump;

import android.graphics.drawable.Drawable;
//宝座处的app信息
public class look_app_item {
    private String app_name;//app名字
    private Drawable image_resource;//app图片
    private int control_num;//控制的快捷键
    private String packagename;//app的包名
    look_app_item(String app_name,Drawable image_resource,int control_num){
        this.app_name=app_name;
        this.image_resource=image_resource;
        this.control_num=control_num;
        packagename="";
    }
    look_app_item(String app_name,Drawable image_resource,int control_num,String packagename){
        this.app_name=app_name;
        this.image_resource=image_resource;
        this.control_num=control_num;
        this.packagename=packagename;
    }
    public String get_app_name(){
        return app_name;
    }
    public Drawable get_image_resourece(){
        return image_resource;
    }
    public int getControl_num(){
        return control_num;
    }
    public void setApp_name(String app_name){
        this.app_name=app_name;
    }
    public void setImage_resource(Drawable image_resource){
        this.image_resource=image_resource;
    }
    public void setControl_num(int control_num){
        this.control_num=control_num;
    }
    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
    public String getPackagename() {
        return packagename;
    }
}
