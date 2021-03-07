package com.fruitbasket.audioplatform;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.demo.mymusic.SongSheetActivity;
import com.fruitbasket.audioplatform.play2.GlobalConfig;
import com.demo.mymusic.SongSheetActivity.MyHandler;

import java.util.LinkedList;
import java.util.List;

import uk.me.berndporr.iirj.Butterworth;

/**
 * Created by FruitBasket on 2017/5/26.
 */

final public class MyApp extends Application {


    public SongSheetActivity.MyHandler handler = null;

    // set方法
    public void setHandler(SongSheetActivity.MyHandler handler) {
        this.handler = handler;
    }

    // get方法
    public SongSheetActivity.MyHandler getHandler() {
        return handler;
    }

    private static final String TAG=".MyApp";

    private static Context context;

    //edit 11.26
    public List<Activity> activityList = new LinkedList<Activity>();
    private static MyApp instance;
    //edit 11.26
    static{
        System.loadLibrary("jni");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"onCreate()");
        context=getApplicationContext();

        GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹
        GlobalConfig.stWaveFileUtil.initIQFile();//创建IQTXT文件
        //GlobalConfig.stPhaseProxy.init();//处理相位数据
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        Log.i(TAG,"onTerminate()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        Log.i(TAG,"onConfigurationChanged()");
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        Log.i(TAG,"onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level){
        super.onTrimMemory(level);
        Log.i(TAG,"onTrimMemory()");
    }
    public static Context getContext(){
        return context;
    }

    //edit 11.26
    public static MyApp getInstance()
    {
        if(null == instance)
        {
            instance = new MyApp();
        }
        return instance;

    }

    public void addActivity(Activity activity)
    {
        activityList.add(activity);
    }

    public void remove(Activity activity)
    {
        activityList.remove(activity);
    }

    public void exit()
    {
        for(Activity activity:activityList)
        {
            activity.finish();
        }
    }
    //edit 11.26
}
