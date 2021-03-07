package com.demo.dialing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.Demo_Constants;
import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.fruitbasket.audioplatform.Constents.predicting;

public class TelephoneBookActivity extends Activity implements View.OnClickListener{
    public static String TAG = "TelephoneBookActivity";
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=0;
    public List<Contacts_member> Phone_List=new ArrayList<>();
    public ImageView home;
    public ImageView little_add;
//    public TextView title_name;
    private MyThread AirThread;
    private AirHandler mHandler = new AirHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏，取消原有标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.phone_book);
        initTitleView();
      //  initPhone_List();
        addNewContact();
        initlist();
        startHandControl();

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
           /*  switch (msg.what){
               case 12:
                    goToMainLayout();
                    break;
                default:
                    break;
            }*/
        };
    };
    private class MyThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    if (this.interrupted()){
                        throw new InterruptedException();
                    }
                    if(Constents.dataReady == true){
                        // switchHandControl();
                        Message msg = new Message();
                        msg.what = Constents.currentNum;
                        mHandler.sendMessage(msg);
                        Constents.dataReady = false;
                    }
                }catch (InterruptedException e){
                    Log.d(TAG, "终端任务 ");
                    return;
                }
            }
        }
    }
    private void addNewContact(){
        Contacts_member person2 =new Contacts_member("24205","John");
        Phone_List.add(person2);
        Contacts_member person1 =new Contacts_member("84783","Cindy");
        Phone_List.add(person1);
        Contacts_member person3 =new Contacts_member("34573","Amy");
        Phone_List.add(person3);
        Intent intent = getIntent();
        String name;
        String telenum;
        if(intent!=null){
            if(intent.getStringExtra("activity")!=null){
                if(intent.getStringExtra("activity").equals("AddContactActivity")) {
                    name = intent.getStringExtra("name");
                    telenum = intent.getStringExtra("num");
                    if(!name.equals("") & !telenum.equals("")){
                        Contacts_member person =new Contacts_member(telenum,name);
                        Log.d(TAG, "addNewContact: "+ name + " "+ telenum);
                        Phone_List.add(person);
                    }

                }
            }
        }
    }
    private void initTitleView(){
        home = (ImageView) findViewById(R.id.goback_from_phonebook);
        little_add = (ImageView) findViewById(R.id.add_phonebook);
//        title_name = (TextView)findViewById(R.id.title_content);
//        title_name.setText("通讯录");
        home.setOnClickListener(this);
        little_add.setOnClickListener(this);
//        title_name.setOnClickListener(this);
    }
    private void initPhone_List(){//初始化通讯录
        for(int i=0;i<2;i++){
            Contacts_member Firstpeople=new Contacts_member("1234","Person");
            Phone_List.add(Firstpeople);
        }
    }

    private void initlist(){
        ContactAdapter adapter=new ContactAdapter(TelephoneBookActivity.this,R.layout.contact_item,Phone_List);
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击item触发事件 拨打电话
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeACalling(position);

            }
        });
    }

    private void makeACalling(int position){
        Contacts_member personi= Phone_List.get(position);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+personi.get_PhoneNumber()));
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goback_from_phonebook:
                goToMainLayout();
                break;
            case R.id.add_phonebook:
                goToAddLayout();
                break;

        }
    }
    public void goToMainLayout(){
//        Intent intent1= new Intent(TelephoneBookActivity.this, MainActivity.class);
//        startActivity(intent1);
        finish();
    }
    public void goToAddLayout(){
          Intent intent2 = new Intent(TelephoneBookActivity.this, AddContactActivity.class);
          startActivity(intent2);
    }
    @Override
    protected void onDestroy(){
        Log.i(TAG,"onDestroy()");
        if (predicting)
            AirThread.interrupt();
        super.onDestroy();
    }
}
