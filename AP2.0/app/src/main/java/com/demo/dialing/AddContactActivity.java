package com.demo.dialing;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.lang.reflect.Method;

import static com.fruitbasket.audioplatform.Constents.ispop;
import static com.fruitbasket.audioplatform.Constents.predicting;

public class AddContactActivity extends Activity implements View.OnClickListener {
    public static String TAG = "AddContactActivity";
    public ImageView home;
//    public Button little_add;
    public EditText name;
    public ImageView addToList;
    public TextView title_name;
    public EditText tele_num;
    private MyThread AirThread;

    private AirHandler mHandler = new AirHandler();
    private keyboardPopupWindow keyWindow ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.new_contact);
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
    public void initView(){
        //设置不弹出软键盘
        home = (ImageView)findViewById(R.id.goback_from_add);
//        little_add = (Button)findViewById(R.id.goto_addlayout);
        name = (EditText)findViewById(R.id.name_edit);
        tele_num = (EditText)findViewById(R.id.num_edit);
        addToList = (ImageView)findViewById(R.id.addto_list_image);

//        title_name = (TextView)findViewById(R.id.title_content);
//        title_name.setText("创建联系人");


        if (android.os.Build.VERSION.SDK_INT <= 10) {
            name.setInputType(InputType.TYPE_NULL);
            tele_num.setInputType(InputType.TYPE_NULL);
        } else {
            AddContactActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(name, false);
                setSoftInputShownOnFocus.invoke(tele_num, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        home.setOnClickListener(this);
//        little_add.setOnClickListener(this);
        name.setOnClickListener(this);
        tele_num.setOnClickListener(this);
        addToList.setOnClickListener(this);
        Intent intent = getIntent();
        String telenum = intent.getStringExtra("telenumber");
        tele_num.setText(telenum);
    }
    void startHandControl(){
        Constents.dataReady = false;
        if(predicting){
            AirThread = new MyThread();
            AirThread.start();
        }
    }
    private class AirHandler extends Handler {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 11:
                    goTolistLayout();
                    break;
            /*    case 12:
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
                Log.d(TAG, "run: 22222");
                try{
                    if (this.interrupted()){
                        throw new InterruptedException();
                    }
                    if(Constents.dataReady == true){
                        // switchHandControl();
                        Message msg = new Message();
                        msg.what = Constents.currentNum;
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goback_from_add:
                goToMainLayout();
                break;
            case R.id.goto_addlayout:
                goTolistLayout();
                break;
            case R.id.name_edit:
                showKeyboard(name);
                break;
            case R.id.num_edit:
                showKeyboard(tele_num);
                break;
            case R.id.addto_list_image:
                goTolistLayout();

        }
    }
    public void goToMainLayout(){
        finish();

    }
    public void goTolistLayout(){
        Intent intent = new Intent(AddContactActivity.this, TelephoneBookActivity.class);
        String name_str = name.getText().toString();
        String num_str = tele_num.getText().toString();
        intent.putExtra("activity","AddContactActivity");
        intent.putExtra("name",name_str);
        intent.putExtra("num",num_str);
        startActivity(intent);
    }
    public void showKeyboard(EditText editText){
        keyWindow = new keyboardPopupWindow(AddContactActivity.this,editText);
        //显示窗口
        keyWindow.showAtLocation(AddContactActivity.this.findViewById(R.id.add_contact),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        keyWindow.setOnDismissListener(onDismissListener);
    }
    private PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            ispop = false;
        }
    };
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
          //  keyWindow.dismiss();

        }
    };
    @Override
    protected void onDestroy(){
        Log.i(TAG,"onDestroy()");
        super.onDestroy();
    }

}
