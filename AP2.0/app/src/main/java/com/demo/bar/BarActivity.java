package com.demo.bar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;

public class BarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        Intent intent=new Intent(this,BarService.class);
        startForegroundService(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.d("Bar Activity","is Destroy");
    }
}
