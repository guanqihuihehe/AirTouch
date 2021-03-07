package com.demo.jump;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.fruitbasket.audioplatform.Constents.applist;
import static com.fruitbasket.audioplatform.Constents.canjump;
import static com.fruitbasket.audioplatform.Constents.ispop;
import static com.fruitbasket.audioplatform.Constents.predicting;
import static java.lang.Thread.sleep;

public class AppMainActivity extends Activity {
    public static String TAG = "AppMainActivity";
    private Button Change;
    private Button Jump;
    private Button Clear;
    private ListView listView;
    private App_Adapter adapter;
    private EditText editText;
    public ImageView goback;
    private MyThread AirThread;
    public int jumpNum ;
    private Boolean ifinside = true;
    private AirHandler mHandler = new AirHandler();
    static private List<look_app_item> jump_list=new ArrayList<>();//用于存储jump_Activity中的链表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canjump=true;
        setContentView(R.layout.app_main);
        Log.i(TAG,"on:Creat()");
        main_init();
      //  startHandControl();
        Change=(Button)findViewById(R.id.Change_Button);
        //Jump=(Button)findViewById(R.id.Jump_Button);
        Clear=(Button)findViewById(R.id.Clear_Button);
        goback=findViewById(R.id.goback2);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifinside = true;
                finish();
            }
        });
      //  editText=(EditText)findViewById(R.id.Jump_Text);
        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到jump_Activity
                ifinside = true;
                Intent intent=new Intent(AppMainActivity.this,Jump_Activity.class);
                startActivity(intent);
            }
        });
        listView=(ListView)findViewById(R.id.List_layout);
        adapter=new App_Adapter(AppMainActivity.this,R.layout.app_item,applist);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
     /*   Jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String my_text=editText.getText().toString();
                editText.setText("");//清空输入框
                int i=0;
                for(i=0;i<applist.size();i++){//遍历list寻找与输入数字相同的序号
                    if(my_text.equals(applist.get(i).getControl_num()+"")){
                        break;
                    }
                }
                if(i==applist.size()){//如果找不到对应的快捷键
                    Toast.makeText(AppMainActivity.this, "该快捷键无效", Toast.LENGTH_LONG).show();
                    return;
                }
                startAPP(applist.get(i).getPackagename());
                Thread mythread=Thread.currentThread();
                try {
                    String s[]={"com.tencent.mobileqq","net.csdn.csdnplus","com.baidu.BaiduMap"};
                    for(int j=0;j<3;j++){
                        mythread.sleep(8000);
                        startAPP(s[j]);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/
        Clear.setOnClickListener(new View.OnClickListener() {//清除当前所有快捷键
            @Override
            public void onClick(View v) {
                applist.clear();
                jump_list.clear();
                SharedPreferences sdf=PreferenceManager.getDefaultSharedPreferences(AppMainActivity.this);
                SharedPreferences.Editor editor = sdf.edit();
                editor.clear();
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged(); //Ui线程中更新listview
                    }
                });
            }
        });
    }
    void startHandControl(){
        Constents.dataReady = false;
        ifinside = false ;
        if(predicting){
            Log.d(TAG, "startHandControl:MainActivity: on");
            AirThread = new MyThread();
            AirThread.start();
        }
    }
    private class MyThread extends Thread{
        @Override
        public void run() {
            while(canjump){
                 Log.d(TAG, "run: 22222222222");
                try{
                    if (this.interrupted()){
                        throw new InterruptedException();
                    }
                    if(Constents.dataReady == true){
                        // switchHandControl();
                        // Log.d(TAG, "jumpinfact" + Constents.currentNum);
                        Message msg = new Message();
                        msg.what = Constents.currentNum;
                        mHandler.sendMessage(msg);
                        Constents.dataReady = false;
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                    //edit 11.28
                    Log.d(TAG, "中断任务 ");
                    //edit 11.28
                    return;
                }

            }
        }
    }
    //edit 11.28
    private class AirHandler extends Handler {
        public void handleMessage(Message msg){
            jumpNum = msg.what;
            Log.d(TAG, "jump: " + jumpNum);
            if(jumpNum<=12){
//                new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: yes");

                jumpToOtherApp(String.valueOf(jumpNum));
//                    }
//                }).start();

            }


        };
    };
    public void jumpToOtherApp(String mynum){
        Log.d(TAG, "jumpToOtherApp: "+mynum);
        int i=0;
        for(i=0;i<applist.size();i++){//遍历list寻找与输入数字相同的序号
            if(mynum.equals(applist.get(i).getControl_num()+"")){
                ifinside = false;
                break;
            }
        }
        if(i==applist.size()){
            if(mynum.equals("12")) {
                System.out.println("jump_restart");
                Intent tempIntent = new Intent();
//                Intent intent = new Intent(Intent.ACTION_MAIN);
                tempIntent= this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
//                tempIntent.setAction("com.demo.jump.Jump_Restart");
                if(tempIntent !=null){
                    ifinside=true;
                    tempIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(tempIntent);
                }else{
                    Log.d(TAG, "startAPP: null");
                }

            }
            else{//如果找不到对应的快捷键
                // Toast.makeText(MainActivity.this, "该快捷键无效", Toast.LENGTH_LONG).show();
//                return;
            }
        }
        else{
            Log.d(TAG, "jumpToOtherApp: " + "准备跳转" + applist.get(i).getPackagename());
            startAPP(applist.get(i).getPackagename());
        }
//edit 11.28

    }
    @Override
    public void onStart() {
        super.onStart();
        canjump=true;
        if(ifinside)//从内部其他界面跳转过来需要重新开线程
            startHandControl();
        else//从外部
            ifinside = true;
        Log.i(TAG,"on:Start()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        canjump=true;
        Log.i(TAG,"on:Restart()");
    }

    @Override
    public void onResume(){//从jump_activity中返回前刷新界面
        Log.i(TAG,"on:Resume()");
        super.onResume();
        canjump=true;
        runOnUiThread(new Runnable() {//在ui线程中刷新以防线程不安全导致崩溃
            @Override
            public void run() {
                adapter.notifyDataSetChanged(); //Ui线程中更新listview
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG,"on:Pause()");
        if (predicting & ifinside)
            AirThread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (predicting & ifinside)
            AirThread.interrupt();
        Log.i(TAG,"on:Stop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on:Destroy: ");
        if (predicting)
            AirThread.interrupt();
    }

    public void main_init(){
        jump_list.clear();
        applist.clear();
        Resources resources = getApplicationContext().getResources();
        Drawable drawable = resources.getDrawable(R.drawable.add);
        SharedPreferences spt= PreferenceManager.getDefaultSharedPreferences(AppMainActivity.this);
        for(int i=0;i<10;i++){
            String dataKey1="used"+i;
            boolean flag=spt.getBoolean(dataKey1,false);
            if(flag){
                String packagename=spt.getString("packageName"+i,"");
                String name=spt.getString("name"+i,"");
                jump_list.add(new look_app_item(name,getIcon(packagename),i));
                applist.add(new look_app_item(name,getIcon(packagename),i,packagename));
            }
            else{
                jump_list.add(new look_app_item("",drawable,i));
            }
        }
    }
    public void startAPP(String appPackageName) {//根据对应的包名运行app
        try {
            Intent tempIntent ;
            //Intent intent = new Intent(Intent.ACTION_MAIN);
            tempIntent= this.getPackageManager().getLaunchIntentForPackage(appPackageName);
            if(tempIntent !=null){
                //edit 11.28
                tempIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                //edit 11.28
                startActivity(tempIntent);
            }else{
                Log.d(TAG, "startAPP: null");
            }

        } catch (Exception e) {
            // Toast.makeText(this, "没有安装该程序", Toast.LENGTH_LONG).show();
        }
    }
    static public List<look_app_item> getJump_list(){
        return jump_list;
    }
    static void setJump_list(List<look_app_item> templist){
        jump_list.clear();
        for(int i=0;i<templist.size();i++){
            jump_list.add(i,templist.get(i));
        }
    }
    private Drawable getIcon(String pakgename) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(pakgename, PackageManager.GET_META_DATA);
            Drawable appIcon = pm.getApplicationIcon(appInfo);
            return appIcon;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
