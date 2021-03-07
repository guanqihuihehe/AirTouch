package com.demo.bar;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import android.widget.ProgressBar;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;

//import static com.fruitbasket.audioplatform.Constents.progress;
import static com.fruitbasket.audioplatform.Constents.ready1;

public class BarService extends Service {
    public static final int UPDATE_BAR=1;
    public String TAG = "BarService";
    Notification notification;
    private Context mContext;
    private static Thread showBarThread;
    private WindowManager windowManager;
    private boolean isrun = true;
    public  int progress=0;
    private View displayView;
    private ProgressBar progressBar;
    public MyHandler myHandler=new MyHandler();

    public BarService() {
    }
    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code=msg.what;//接受处理码
            switch (code){
                //change 11.12
                case UPDATE_BAR:
                    if(progress<98){
                        progress+=2;
                    }
                    //change 11.12
                    else progress=0;
                    progressBar.setProgress(progress);//给进度条的当前进度赋值
                    break;
                    default:break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final long start = System.currentTimeMillis();
        Log.d(TAG, "showFloatingWindow: 1111");
        mContext = this;

        showFloatingWindow();

      //  NotificationChannel channel = new NotificationChannel("1",
     //           "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel channel = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            channel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setShowBadge(true);
        }
        int notificationId = 0x1234;
        Intent notificationIntent = new Intent(this, BarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       // progressBar.setProgressDrawable(Drawable.createFromPath("@drawable/barcolor2"));
        //1.通知栏占用，不清楚的看官网或者音乐类APP的效果
        notification = new NotificationCompat.Builder(mContext,"1")
                .setSmallIcon(R.drawable.autoflag)
                .setWhen(System.currentTimeMillis())
                .setTicker("进度条测试")
                .setContentTitle("进度条测试标题")
                .setContentText("进度条测试内容")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();
        /*使用startForeground,如果id为0，那么notification将不会显示*/
        startForeground(notificationId, notification);
        //2.开启线程（或者需要定时操作的事情）
        if(showBarThread == null){
            showBarThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //这里用死循环就是模拟一直执行的操作
                    while (isrun){
                        long end = System.currentTimeMillis();
                   //   Log.d(TAG, "时间点" + ready1);
                        if(ready1)
                        {
                            Log.d(TAG, "run: 时间点");
                            try {
                                for(int i=0;i<50;i++){

                                    Message msg=new Message();
                                    msg.what=UPDATE_BAR;
                                    myHandler.sendMessage(msg);
                                    Thread.sleep(40);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ready1=false;
                        }
                        //change 11.12


                    }
                }
            });
            showBarThread.start();
        }

        return START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {

        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // 新建悬浮窗控件
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            displayView = layoutInflater.inflate(R.layout.mybar, null);


            progressBar=displayView.findViewById(R.id.record_progress);


            // 设置LayoutParam
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity= Gravity.TOP;
            layoutParams.width =500;
            layoutParams.height = 100;
            layoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.x = 10;
            layoutParams.y = 10;

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(displayView, layoutParams);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        isrun = false;
        stopForeground(true);

        windowManager.removeView(displayView);
        showBarThread.interrupt();
        showBarThread=null;
        stopSelf();
        super.onDestroy();
    }

}
