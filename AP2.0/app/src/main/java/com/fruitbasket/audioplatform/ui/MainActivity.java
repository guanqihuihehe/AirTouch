package com.fruitbasket.audioplatform.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.demo.WriteTypeActivity;
import com.demo.bar.BarService;
import com.demo.dialing.DialingMainActivity;
import com.demo.dialing.TelephoneBookActivity;
import com.demo.jump.AppMainActivity;
import com.demo.mymusic.SongSheetActivity;
import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.AudioService;
import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.MyApp;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.WaveProducer;
import com.fruitbasket.audioplatform.play.Player;
import com.fruitbasket.audioplatform.play.WavePlayer;
import com.fruitbasket.audioplatform.play2.GlobalConfig;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.Python;

import static com.fruitbasket.audioplatform.Constents.applist;
import static com.fruitbasket.audioplatform.Constents.canjump;
import static com.fruitbasket.audioplatform.Constents.predicting;
import static com.fruitbasket.audioplatform.Constents.ready1;


final public class MainActivity extends Activity {
    private static final String TAG=".MainActivity";

    private ToggleButton waveProducerTB;
    private SeekBar waveRateSB;
    private ToggleButton recorderTB;

    private ToggleButton startbutton;
    private LinearLayout AnotherAPP,music,phonebook;
    private Button showBar;
    private Interpreter tflite = null;
    private boolean load_result = false;
    private TextView result_text;
    private ImageView picture1;
    private EditText path_box;

    private List<String> resultLabel = new ArrayList<>();
    private Python py;
    private PyObject obj1;
    //add 10.15
    public boolean ispredicting;
    PredictHandler1 predictHandler1=new PredictHandler1();
    //add 10.15
    private int channelOut= Player.CHANNEL_OUT_BOTH;
    private int channelIn= AudioFormat.CHANNEL_IN_MONO;
    //    private int channelIn = AudioFormat.CHANNEL_IN_STEREO;
    private int waveRate;//声波的频率
    public  int iBeginHz = 19000;
    public  int iStepHz=0;
    public  int ifreqNum = 1;
    public  int iSimpleHz = 44100;
    public int jumpNum ;
    private int[] ddims = {1, 3, 56, 56};
    private String[] texttype = {"digits","letter"};


    private Intent intent;
    private AudioService audioService;
    private ServiceConnection serviceConnection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG,"ServiceConnection.onServiceConnection()");
            audioService =((AudioService.AudioServiceBinder)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,"ServiceConnection.onServiceDisConnection()");
            audioService =null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate()");
        canjump=false;
        setContentView(R.layout.activity_main);
        startbutton=findViewById(R.id.start_button);

        readCacheLabelFromLocalFile();
        load_model( "model_v782");
        initializeViews();
        long start1 = System.currentTimeMillis();
        initPython();
        this.obj1 = this.py.getModule("function");
        long end1 = System.currentTimeMillis();
        long time = end1 - start1;
        Log.d(TAG,"time used :"+ time);
        intent=new Intent(this,AudioService.class);
        if(audioService ==null) {
            Log.i(TAG,"begin to bind service");
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        AnotherAPP=findViewById(R.id.jump_app);
        music=findViewById(R.id.music);
        phonebook=findViewById(R.id.phonebook);
        AnotherAPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(MainActivity.this,"AnotherAPP",Toast.LENGTH_SHORT).s;
                Intent intent1= new Intent(MainActivity.this, AppMainActivity.class);
                intent1.putExtra("flag",2);
                startActivity(intent1);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent(MainActivity.this, SongSheetActivity.class);
                startActivity(intent1);
            }
        });
        phonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent(MainActivity.this, DialingMainActivity.class);
                startActivity(intent1);            }
        });
        startbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    predicting = true;
                    ready1 = true;
                    startbutton.setChecked(true);
                    startPlayWav();
                    Constents.user_path = "AirTouch";
                    startFloatingService(recorderTB);
                    Log.d(TAG, "onCheckedChanged:+ float " );
                    startRecordWav();
                    startbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner4));
                }
                else{
                    predicting = false;
                    startbutton.setChecked(false);
                    Log.d(TAG, "onCheckedChanged: " + "线程结束");
                    startbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner5));
                    try {
                        stopRecord();
                        stopService(new Intent(MainActivity.this, BarService.class));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stopPlay();
                }

            }


            public void startPlayWav(){
                Log.i(TAG,"starPlayWav()");
                if(audioService !=null){

                    GlobalConfig.START_FREQ=iBeginHz;
                    GlobalConfig.FREQ_INTERVAL=iStepHz;
                    GlobalConfig.NUM_FREQ=ifreqNum;
                    GlobalConfig.stPhaseProxy.init();//处理相位数据

                    audioService.startPlayWav(channelOut, waveRate, WaveProducer.COS, iSimpleHz, iBeginHz, iStepHz, ifreqNum);
                }
                else{
                    Log.w(TAG,"audioService==null");
                }
            }

            public void stopPlay(){
                Log.i(TAG,"stopPlay()");
                if(audioService !=null){
                    audioService.stopPlay();
                }
                else{
                    Log.w(TAG,"audioService==null");
                }
            }

            public void startRecordWav(){
                Log.i(TAG,"startRecordWav()");
                // add 10.14
                GetPredictPath();
                //add 10.14
                if(audioService!=null){
                    audioService.startRecordWav(
                            channelIn,
                            AppCondition.DEFAULE_SIMPLE_RATE,
                            AudioFormat.ENCODING_PCM_16BIT
                    );
                }
                else{
                    Log.w(TAG,"audioService==null");
                }

            }

            public void stopRecord() throws FileNotFoundException {
                Log.i(TAG,"stopRecord()");
                //add 10.14
                ispredicting=false;
                //add 10.14
                if(audioService!=null){
                    audioService.stopRecord();
//            added code6.11
                    Log.i(TAG,"path is :" + Constents.file_path);
                }
                else{
                    Log.w(TAG,"audioService==null");
                }

            }


            //add 10.14
            public void GetPredictPath()
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ispredicting=true;
                        while(ispredicting)
                        {
                            String current_path=Constents.pathqueue.poll();
                            if(current_path!=null&&!current_path.equals(""))
                            {
                                System.out.println("时间点:"+current_path);
                                PredictWav(current_path);
                            }

                        }
                    }
                }).start();
            }
            //add 10.14
        });
    }
    // 初始化Python环境
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        this.py = Python.getInstance();
    }

    @Override
    protected void onStart(){
        super.onStart();
        canjump=false;
        Log.i(TAG,"onStart()");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop()");

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        canjump=false;
        System.out.println("onRestart()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        canjump=false;
        Log.i(TAG,"onResume()");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG,"onPause()");

    }


    @Override
    protected void onDestroy(){
        Log.i(TAG,"onDestroy()");
        //在断开声音服务前，释放播放器资源
        if(audioService!=null){
            audioService.releasePlayer();
        }
        unbindService(serviceConnection);
        stopService(intent);//must stop the Service
        super.onDestroy();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingService(View view) {
        Log.d(TAG, "startFloatingService: " );
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "startFloatingService:3 " );
                startForegroundService(new Intent(MainActivity.this, BarService.class));
            }
        }
    }

     @RequiresApi(api = Build.VERSION_CODES.M)
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode,resultCode,data);
         if (requestCode == 0) {
             if (!Settings.canDrawOverlays(MainActivity.this)) {
                 Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     Log.d(TAG, "startFloatingService:2 " );
                     startForegroundService(new Intent(MainActivity.this, BarService.class));
                 }
             }
         }
     }
    private void initializeViews(){
        channelOut= WavePlayer.CHANNEL_OUT_LEFT;
        waveRateSB =(SeekBar)findViewById(R.id.waverate_sb);
        waveRateSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveRate =progress*1000;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        waveRate = waveRateSB.getProgress()*1000;

        channelIn=AudioFormat.CHANNEL_IN_MONO;

    }

//    added code6.10
    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(String model) throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd(model + ".tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // load infer model
    private void load_model(String model) {
        try {
            tflite = new Interpreter(loadModelFile(model).asReadOnlyBuffer());
            Toast.makeText(MainActivity.this, model + " model load success", Toast.LENGTH_SHORT).show();
            Log.d(TAG, model + " model load success");
//            tflite.setNumThreads(4);
            load_result = true;
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, model + " model load fail", Toast.LENGTH_SHORT).show();
            Log.d(TAG, model + " model load fail");
            load_result = false;
            e.printStackTrace();
        }
    }


    public static ByteBuffer getScaledMatrix(Bitmap bitmap, int[] ddims) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3] * 4);
        imgData.order(ByteOrder.nativeOrder());
        // get image pixel
        int[] pixels = new int[ddims[2] * ddims[3]];
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, ddims[2], ddims[3], false);
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, ddims[2], ddims[3]);
        int pixel = 0;
//        Log.i(TAG,"first pixel is :" +pixels[0]);
        for (int i = 0; i < ddims[2]; ++i) {
            for (int j = 0; j < ddims[3]; ++j) {
                final int val = pixels[pixel++];
                imgData.putFloat(((val & 0x000000ff) )/255f);
                imgData.putFloat(((val & 0x0000ff00)>>8 )/255f);
                imgData.putFloat(((val & 0x00ff0000) >>16)/255f);
//                Log.i(TAG,"rgb:" +((val & 0x000000ff) )/255f+ " ,"+ ((val & 0x0000ff00)>>8 )/255f + " ," + ((val & 0x00ff0000) >>16)/255f);
            }
        }
//
        if (bm.isRecycled()) {
            bm.recycle();
        }
        return imgData;
    }
    //
//    // compress picture
    public static Bitmap getScaleBitmap(String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        int bmpWidth = opt.outWidth;
        int bmpHeight = opt.outHeight;
        int maxSize = 500;
        // compress picture with inSampleSize
        opt.inSampleSize = 1;
        while (true) {
            if (bmpWidth / opt.inSampleSize < maxSize || bmpHeight / opt.inSampleSize < maxSize) {
                break;
            }
            opt.inSampleSize += 2;
        }
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, opt);
    }


    // get max probability label
    // predict image
    private int[] get_max_result(float[] result) {
        float[] temp = new float[result.length];
        System.arraycopy(result, 0, temp, 0, result.length);
        Arrays.sort(temp);
        int[] top_five = new int[5];
        for(int i=0;i<5;i++){
            float max = temp[result.length-i-1];
            for(int j=0;j<result.length;j++){
                if (result[j]==max){
                    top_five[i] = j;
                }
            }
        }
        return top_five;
    }

    private void readCacheLabelFromLocalFile() {
        try {
            AssetManager assetManager = getApplicationContext().getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("30labels.txt")));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                resultLabel.add(readLine);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("labelCache", "error " + e);
        }
    }

    //add 10.15
    public class PredictHandler1 extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                int res = (int) msg.obj;
                Toast.makeText(getApplicationContext(), String.valueOf(res), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class PredictHandler2 extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                String res = (String) msg.obj;
                result_text.setText(res);
            }
        }
    }

    public class PredictHandler3 extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                Bitmap bitmap = (Bitmap) msg.obj;
                picture1.setImageBitmap(bitmap);
            }
        }
    }
    //add 10.15

    private void PredictWav(String path){
        File f = new File(path);
        while (true){
            if(f.exists()){
                //change 10.15
                String str=path + " make success";

                //change 10.15


                Python py = Python.getInstance();
                String pic_path = path.substring(0,path.length()-4)+ ".png";
               // PyObject obj1 = py.getModule("function").callAttr("wav2picture",new Kwarg("wav_path", path),new Kwarg("pic_path", pic_path));
                long start1 = System.currentTimeMillis();
                this.obj1.callAttr("wav2picture",new Kwarg("wav_path",path),new Kwarg("pic_path",pic_path));
                long end1 = System.currentTimeMillis();
                long time = end1 - start1;
                Log.i(TAG,"python plot fft picture time used :" +time);
                Bitmap bmp = getScaleBitmap(pic_path);
                ByteBuffer inputData = getScaledMatrix(bmp, ddims);
                try {
                    float[][] labelProbArray = new float[1][14];
                    long start = System.currentTimeMillis();
                    tflite.run(inputData, labelProbArray);
                    long end = System.currentTimeMillis();
                    time = end - start;

                    float[] new_resluts = new float[labelProbArray[0].length];
                    System.arraycopy(labelProbArray[0], 0, new_resluts, 0, labelProbArray[0].length);
                    //                  add code 8.14
                 /*   float[] new_resluts= new float[10];
                    for(int i=0;i<10;i++){
                        new_resluts[i] = resluts[i*3] + resluts[i*3+1] + resluts[i*3+2];
                    }*/
//                   add code 8.14
                    // show predict result and time
                    int[] r = get_max_result(new_resluts);
                   /* String show_text = "You might write：\n" + resultLabel.get(r[0]) +"\t\t\t\t\t\t\tProbability:\t\t"+ new_resluts[r[0]]*100+"%\n"+ resultLabel.get(r[1]) +
                            "\t\t\t\t\t\t\tProbability:\t\t"+ new_resluts[r[1]]*100+"%\n" + resultLabel.get(r[2]) +"\t\t\t\t\t\t\tProbability:\t\t"+ new_resluts[r[2]]*100+
                            "%\n" + resultLabel.get(r[3]) +"\t\t\t\t\t\t\tProbability:\t\t"+ new_resluts[r[3]]*100+"%\n" + resultLabel.get(r[4]) +"\t\t\t\t\t\t\tProbability:\t\t"+ new_resluts[r[4]]*100+
                            "%\nPredict Used:"+time + "ms"+ "\n\nMake wav file Used:"+Constents.makewavfiletime + "ms";
*/
                    Constents.currentNum = Integer.parseInt(resultLabel.get(r[0]));
                    Message message1=new Message();
                    message1.what=100;
                    message1.obj=Constents.currentNum;
                    predictHandler1.sendMessage(message1);
                    Log.d(TAG, "keyrun: " + resultLabel.get(r[0]));
                    Constents.dataReady = true;
//                    callpythonadd();//add code
//                    result_text.setText(show_text);
//                    Log.i(TAG,show_text);
                    //add 10.15
 /*                   Message message2=new Message();
                    message2.what=100;
                    message2.obj=show_text;
                    predictHandler2.sendMessage(message2);
                    //add 10.15
//                    展示频谱图
                    //change 10.15
                    Bitmap bitmap = BitmapFactory.decodeFile(pic_path);
//                    picture1.setImageBitmap(bitmap);
                    //change 10.15

                    //add 10.15
                    Message message3=new Message();
                    message3.what=100;

                    message3.obj=bitmap;
                    predictHandler3.sendMessage(message3);*/
                    //add 10.15
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

//    added code6.10
}
