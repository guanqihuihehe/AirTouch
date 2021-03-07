package com.fruitbasket.audioplatform;


import com.demo.jump.look_app_item;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public class Constents {
    static public int LENCHUNKDESCRIPTOR = 4;
    static public int LENWAVEFLAG = 4;
    static public int LENFMTSUBCHUNK = 4;
    static public int LENDATASUBCHUNK = 4;
    public static String file_path = null;
    public static String user_path = null;
    public static int[] datalist = null;
    public static int dataLength = 0;
    public static long makewavfiletime = 0;
    //11.22
    public static int currentNum ;
    public static boolean dataReady = false;
    public static boolean ispop  = false;//软键盘是否弹出
    public static boolean predicting = false;//是否开启预测
    //11.22
    public static int UPDOWN = 10;
    public static int LEFTRIGHT = 11;
    public static int V = 13;
    public static Queue<String> pathqueue = new LinkedTransferQueue<>();
    public static boolean ready1=true;
    public static  List<look_app_item> applist=new ArrayList<>();

    public static Boolean canjump;
}
