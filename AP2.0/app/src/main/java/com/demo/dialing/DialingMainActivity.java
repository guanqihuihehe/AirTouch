package com.demo.dialing;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.demo.mymusic.SongSheetActivity;
import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.lang.reflect.Method;

import static com.fruitbasket.audioplatform.Constents.ispop;
import static com.fruitbasket.audioplatform.Constents.predicting;


public class DialingMainActivity extends Activity implements View.OnClickListener{
    public ImageView goToCall;
    public EditText writeBox;
    public ImageView home;
    public ImageView little_add;
    public Button change;
    public Button delete;
    public Button num1,num2,num3,num4,num5,num6,num7,num8,num9;
    public final static String TAG = "DialingMainActivity" ;
    public int type = 0;
    public static int NUM_TYPE = 0;
    public static int ENG_TYPE = 1;
    private MyThread AirThread;
    private AirHandler mHandler = new AirHandler();
    private keyboardPopupWindow keyWindow;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if	(ContextCompat.checkSelfPermission (DialingMainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED   )	{
            ActivityCompat.requestPermissions(DialingMainActivity.this,	new String[]{
                    Manifest.permission.CALL_PHONE	},	1);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //透明状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        setContentView(R.layout.dialing_main);
        //getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart()");
        startHandControl();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop()");
        if(ispop)
            keyWindow.dismiss();
        if(predicting)
            AirThread.interrupt();
    }

    @Override
    protected void onDestroy(){
        Log.i(TAG,"onDestroy()");
        super.onDestroy();
    }
    void startHandControl(){
        Constents.dataReady = false;
        if(predicting){
            AirThread = new MyThread();
            AirThread.start();
        }
    }
    private class AirHandler extends Handler{
        public void handleMessage(Message msg){
          switch (msg.what){
              case 12:
                  String s = writeBox.getText().toString();
                  if(!ispop && !s.equals(""))
                      goToAddLayout();
                  else if(!ispop && s.equals(""))
                      gotobook();
                  break;
              case 1:
                  if(!ispop){
                      Constents.dataReady = false;
                      makeACalling();
                  }
                  break;
              case 10:
                  if(!ispop)
                      showKeyboard();
                  break;
              /*case 11:
                  Log.d(TAG, "handleMessage: " + ispop);
                  String s = writeBox.getText().toString();
                  if(!ispop && !s.equals(""))
                      goToAddLayout();
                  else if(!ispop && s.equals(""))
                      gotobook();
                  else{
                      Constents.dataReady = false;
                      makeACalling();}
                  break;*/
           /*  case 12:
                  goToMainLayout();
                  break;*/
              default:
                  break;
          }
        };
    };
    private class MyThread extends Thread{
        @Override
        public void run() {
            while(true){
              //  Log.d(TAG, "run:DialingMain 11111" );
                try{
                    if (this.interrupted()){
                        throw new InterruptedException();
                    }
                    if(Constents.dataReady == true){
                        // switchHandControl();
                        Message msg = new Message();
                        msg.what = Constents.currentNum;
                        Log.d(TAG, "Constents.currentNum" + Constents.currentNum);
                        mHandler.sendMessage(msg);
                        if(!ispop)
                        Constents.dataReady = false;
                    }
                }catch (InterruptedException e){
                    Log.d(TAG, "终端任务 ");
                    return;
                }
            }
        }
    }
    public void initView(){
        goToCall = (ImageView) findViewById(R.id.make_call_image);
        writeBox = (EditText)findViewById(R.id.num_box);
        home = (ImageView)findViewById(R.id.goback_from_dia);
        little_add = (ImageView) findViewById(R.id.add_dia);
        change = (Button)findViewById(R.id.turn_to_button);
        delete = (Button)findViewById(R.id.delete_Button);
        num1 = (Button)findViewById(R.id.No1_button);
        num2 = (Button)findViewById(R.id.No2_button);
        num3 = (Button)findViewById(R.id.No3_button);
        num4 = (Button)findViewById(R.id.No4_button);
        num5 = (Button)findViewById(R.id.No5_button);
        num6 = (Button)findViewById(R.id.No6_button);
        num7 = (Button)findViewById(R.id.No7_button);
        num8 = (Button)findViewById(R.id.No8_button);
        num9 = (Button)findViewById(R.id.No9_button);



        //设置不弹出软键盘
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            writeBox.setInputType(InputType.TYPE_NULL);
        } else {
            DialingMainActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(writeBox, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writeBox.setOnClickListener(this);//输入框
        goToCall.setOnClickListener(this);//拨号
        home.setOnClickListener(this);//去主界面
        little_add.setOnClickListener(this);//去创建联系人
    }

    public void changeType(){
        Log.d(TAG, "changeType: ");
        if(type == NUM_TYPE){
            type = ENG_TYPE;
            change.setText("En");
        }else{
            type = NUM_TYPE;
            change.setText("num");
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.num_box:
                showKeyboard();
                break;
            case R.id.make_call_image:
                makeACalling();
                break;
            case R.id.goto_mainlayout:
                goToMainLayout();
                break;
            case R.id.add_dia:
                goToAddLayout();
                break;
            case R.id.goback_from_dia:
                finish();
                break;
            default:
                break;
        }

    }
    public void gotobook(){
        Intent intent1= new Intent(DialingMainActivity.this, TelephoneBookActivity.class);
        startActivity(intent1);
    }
    public void goToMainLayout(){
       finish();
    }
    public void goToAddLayout(){
        Intent intent2 = new Intent(DialingMainActivity.this, AddContactActivity.class);
        String telenumber = writeBox.getText().toString();
        //writeBox.setText("");
        intent2.putExtra("telenumber",telenumber);
        startActivity(intent2);
    }

    public void makeACalling(){
        Log.d(TAG, "makeACalling: ");
        String telephoneNum = writeBox.getText().toString();
        Intent callingIntent=new Intent();
        callingIntent.setAction(Intent.ACTION_CALL);
        callingIntent.setData( Uri.parse("tel:" + telephoneNum));
        startActivity(callingIntent);
    }
    public void showKeyboard(){
       /* if (predicting)
            AirThread.interrupt();*/
        Log.d(TAG, "showKeyboard: ");
        keyWindow = new keyboardPopupWindow(DialingMainActivity.this,writeBox);
        //显示窗口
        keyWindow.showAtLocation(DialingMainActivity.this.findViewById(R.id.dialing_main),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        keyWindow.setOnDismissListener(onDismissListener);

    }
    private PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            ispop = false;
          /*  if(predicting){
                AirThread = new MyThread();
                AirThread.start();
            }*/
        }
    };

}
