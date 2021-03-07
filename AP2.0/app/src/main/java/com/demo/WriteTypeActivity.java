package com.demo;

import android.app.Activity;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fruitbasket.audioplatform.R;

public class WriteTypeActivity extends Activity implements View.OnClickListener {
    public RelativeLayout layout ;
    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writingtype);
        initView();
    }
    public void initView(){
        layout = (RelativeLayout) findViewById(R.id.keyboard_layout);
        button = (Button) findViewById(R.id.No1_button);
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener( this);
    }
    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.No1_button:
                break;
            default:
                break;
        }
        finish();
    }
}
