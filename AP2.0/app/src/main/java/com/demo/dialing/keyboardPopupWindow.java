package com.demo.dialing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;

import static com.fruitbasket.audioplatform.Constents.ispop;

public class keyboardPopupWindow extends PopupWindow implements View.OnClickListener {
    public static String TAG = "keyboardPopupWindow";
    private View key;
    public Button change;
    public Button delete;
    public Button num1,num2,num3,num4,num5,num6,num7,num8,num9;
    public Button[] buttons ;
    public int type = 0;
    public static int NUM_TYPE = 0;
    public static int ENG_TYPE = 1;
    public static int ENG_TYPE_big = 2;
    public int range =-1;
    private EditText currentEditText;
    private String currentActivity;
    private MyThread mt=new MyThread();
    private Thread AirThread = new Thread(mt);
    private AirHandler mHandler = new AirHandler();
    private String[][] smallLetter = {{"a","b","c"},{"d","e","f"},{"g","h","i"},
            {"j","k","l"},{"m","n","o"},{"p","q","r","s"},{"t","u","v"},{"w","x","y","z"},{".","*","#"}};
    private String[][] largeLetter = {{"A","B","C"},{"D","E","F"},{"G","H","I"},
            {"J","K","L"},{"M","N","O"},{"P","Q","R","S"},{"T","U","V"},{"W","X","Y","Z"},{".","*","#"}};
    public keyboardPopupWindow(Activity context, EditText editText){
       super(context);
        Constents.ispop = true;
        currentEditText = editText;
        currentActivity = context.toString();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        key = inflater.inflate(R.layout.writingtype,null);
        initView();
       // this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);  //设置宽度占满
        this.setContentView(key);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.keyboardStyleBottom);
      //  this.showAsDropDown(key);

        key.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = key.findViewById(R.id.keyboard_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){

                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
        startHandControl();

    }
    void startHandControl(){
        AirThread.start();
    }

    private class AirHandler extends Handler {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    if(type == NUM_TYPE){
                        String str = currentEditText.getText().toString() + "0";
                        currentEditText.setText(str);
                    }
                    break;
                case 1:
                    gestureFunc(1);
                    break;
                case 2:
                    gestureFunc(2);
                    break;
                case 3:
                    gestureFunc(3);
                    break;
                case 4:
                    gestureFunc(4);
                    break;
                case 5:
                    gestureFunc(5);
                    break;
                case 6:
                    gestureFunc(6);
                    break;
                case 7:
                    gestureFunc(7);
                    break;
                case 8:
                    gestureFunc(8);
                    break;
                case 9:
                    gestureFunc(9);
                    break;
                case 10://上下
                   // if(ispop)
                   //     changeType();
                    break;
                case 12:
                    break;
                default:
                    break;
            }
        };
    };

    private void gestureFunc(int num){
        Log.d(TAG, "gestureFunc: " + num);
        if(type == NUM_TYPE){
            addNum(num);
        }else if(type == ENG_TYPE){
            if(range>=0 && num<=smallLetter[range].length)
                addLetter(smallLetter[range][num-1]);
            else
                setNumButton(num);
        }else if(type == ENG_TYPE_big){
            if(range >= 0 &&  num<=largeLetter[range].length)
                addLetter(largeLetter[range][num-1]);
            else
                setNumButton(num);

        }
    }
    private class MyThread implements Runnable{

        @Override
        public void run() {
            while(ispop){
                if(Constents.dataReady == true){
                    Log.d(TAG, "keyrun: " + Constents.currentNum);
                    Message msg = new Message();
                    msg.what = Constents.currentNum;
                    mHandler.sendMessage(msg);
                    Constents.dataReady = false;
                }
            }
        }
    }
    public void initView(){
        change = (Button)key.findViewById(R.id.turn_to_button);
        delete = (Button)key.findViewById(R.id.delete_Button);
        num1 = (Button) key.findViewById(R.id.No1_button);
        num2 = (Button) key.findViewById(R.id.No2_button);
        num3 = (Button) key.findViewById(R.id.No3_button);
        num4 = (Button) key.findViewById(R.id.No4_button);
        num5 = (Button) key.findViewById(R.id.No5_button);
        num6 = (Button) key.findViewById(R.id.No6_button);
        num7 = (Button) key.findViewById(R.id.No7_button);
        num8 = (Button) key.findViewById(R.id.No8_button);
        num9 = (Button) key.findViewById(R.id.No9_button);
       Button[]  temp =  {num1,num2,num3,num4,num5,num6,num7,num8,num9};
       buttons = temp;
        change.setOnClickListener(this);
        delete.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.turn_to_button:
                changeType();
                break;
            case R.id.delete_Button:
                deleteOnLetter();
                break;
            case R.id.No1_button:
                if(type == NUM_TYPE){
                    addNum(1);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(1);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(1);
                    }
                }
                break;
            case R.id.No2_button:
                if(type == NUM_TYPE){
                    addNum(2);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(2);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(2);
                    }
                }
                break;
            case R.id.No3_button:
                if(type == NUM_TYPE){
                    addNum(3);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(3);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(3);
                    }
                }
                break;
            case R.id.No4_button:
                if(type == NUM_TYPE){
                    addNum(4);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][2]);
                    }else{
                        setNumButton(4);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(4);
                    }
                }
                break;
            case R.id.No5_button:
                if(type == NUM_TYPE){
                    addNum(5);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(5);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(5);
                    }
                }
                break;
            case R.id.No6_button:
                if(type == NUM_TYPE){
                    addNum(6);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(6);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(6);
                    }
                }
                break;
            case R.id.No7_button:
                if(type == NUM_TYPE){
                    addNum(7);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][1]);
                    }else{
                        setNumButton(7);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(7);
                    }
                }
                break;
            case R.id.No8_button:
                if(type == NUM_TYPE){
                    addNum(0);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(0);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(0);
                    }
                }
                break;
            case R.id.No9_button:
                if(type == NUM_TYPE){
                    addNum(9);
                }else if(type == ENG_TYPE){
                    if(range >= 0){
                        addLetter(smallLetter[range][0]);
                    }else{
                        setNumButton(9);
                    }

                }else if(type == ENG_TYPE_big){
                    if(range >= 0){
                        addLetter(largeLetter[range][0]);
                    }else{
                        setNumButton(9);
                    }
                }
                break;
        }
    }
    private void addNum(int num){
        String str = currentEditText.getText().toString() + "" + num;
        Log.d(TAG, "addNum: " + str);
        currentEditText.setText(str);
    }
    private void addLetter(String s){
        String str = currentEditText.getText().toString()+s;
        currentEditText.setText(str);
        Button temp;
        temp = buttons[range];
        // temp.setBackgroundColor(Color.parseColor("#BEBEBE"));
        temp.setBackgroundResource(R.drawable.numbutton_shape);
        range = -1;
    }
    private void setNumButton(int num){
        Button temp;
        if(range>=0){
            temp = buttons[range];
           // temp.setBackgroundColor(Color.parseColor("#BEBEBE"));
            temp.setBackgroundResource(R.drawable.numbutton_shape);
        }
        range = num-1;
        temp = buttons[range];
        temp.setBackgroundResource(R.drawable.numbutton_shape2);


    }
    private void changeType(){
        Log.d(TAG, "changeType: ");
        if (range!=-1){
            Button temp;
            temp = buttons[range];
            temp.setBackgroundResource(R.drawable.numbutton_shape);

            range = -1;
        }

        if(type == NUM_TYPE){
            type = ENG_TYPE;
            change.setText("abc");
        }else if(type == ENG_TYPE_big){
            change.setText("123");
            num1.setText("1\nabc");
            num2.setText("2\ndef");
            num3.setText("3\nghi");
            num4.setText("4\njkl");
            num5.setText("5\nmno");
            num6.setText("6\npqrs");
            num7.setText("7\ntuv");
            num8.setText("8\nwxyz");
            type = NUM_TYPE;

        }else {
            type = ENG_TYPE_big;
            change.setText("ABC");
            num1.setText("1\nABC");
            num2.setText("2\nDEF");
            num3.setText("3\nGHI");
            num4.setText("4\nJKL");
            num5.setText("5\nMNO");
            num6.setText("6\nPQRS");
            num7.setText("7\nTUV");
            num8.setText("8\nWXYZ");
         //   num9.setText("ABC");
        }

    }
    private void deleteOnLetter(){
        String str = currentEditText.getText().toString();
        Button temp;
        if(range!=-1){
            temp = buttons[range];
            // temp.setBackgroundColor(Color.parseColor("#BEBEBE"));
            temp.setBackgroundResource(R.drawable.numbutton_shape);
            range = -1;
            return;
        }
        if(str.equals("")||str==null)
            return;
        currentEditText.setText(str.substring(0,str.length()-1));
    }
}
