
package com.fruitbasket.audioplatform.record;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.MyApp;
import com.fruitbasket.audioplatform.WavHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static com.fruitbasket.audioplatform.Constents.ready1;


/**
 * Created by FruitBasket on 2017/6/5.
 */

public class WavRecorder extends Recorder {
    private static final String TAG="..WavRecorder";
    private static double eachRecordTime = 2000;
    private static int num = 0;
    private static double step = 0.5;
    private static double whileTime = 24*step;
    private static final int readResultSize = 3584;
    public static Byte[] recordData = new Byte[3000000];
    public static int sleepTime=500;//检测到有动作后的线程暂停时长，单位ms

    private boolean isRecording;
    public static String audioName;//录音文件的名字
    public String subDir;//用于存放录音文件的子目录
    public static String path = null;
    public WavRecorder(){
        super();
    }

    public WavRecorder(int channelIn, int sampleRate, int encoding){
        super(channelIn,sampleRate,encoding);
    }

    @Override
    public boolean start() {
        Log.i(TAG,"start()");
        //使用异步的方法录制音频
        new Thread(new Runnable() {
            @Override
            public void run() {

                int bufferSize = AudioRecord.getMinBufferSize(
                        sampleRate,
                        channelIn,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
                    return;
                } else if (bufferSize == AudioRecord.ERROR) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR");
                    return;
                }
                byte[] buffer = new byte[bufferSize];

                try {
                    //创建子目录
                    File subFile2=new File(AppCondition.getAppExternalDir()+File.separator + Constents.user_path+"2");
                    boolean state2=(subFile2).mkdir();
                    AudioRecord audioRecord = new AudioRecord(
                            MediaRecorder.AudioSource.MIC,
                            sampleRate,
                            channelIn,
                            encoding,
                            bufferSize);
                    audioRecord.startRecording();

                    isRecording = true;
                    long starttime = System.currentTimeMillis();
                    long endtime = starttime;
                    int length=0;
                    Log.d(TAG, "时间点：开始");
                    while (isRecording) {
                    //    currentWhileTime++;
                        int readResult = audioRecord.read(buffer, 0, bufferSize);
                        if (readResult == AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.e(TAG, "readState==AudioRecord.ERROR_INVALID_OPERATION");
                            return;
                        } else if (readResult == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(TAG, "readState==AudioRecord.ERROR_BAD_VALUE");
                            return;
                        } else {
                            for (int i = 0; i < readResult; i++) {
                                recordData[length++] = buffer[i];
                            }
                        }
                        endtime = System.currentTimeMillis();
                        if(endtime-starttime >= eachRecordTime){

                            String audioName2 =getRecordedFileName()+"-"+getRecordedFileName2();//文件名加入时间方便测试看
                            //change 10.15
                            File audioFile2;
                            DataOutputStream output2;
                            String file_path = AppCondition.getAppExternalDir()+File.separator + Constents.user_path+"2"+File.separator+audioName2 + ".pcm";
                            if (Environment.getExternalStorageState()// 如果外存存在
                                    .equals(Environment.MEDIA_MOUNTED)){
                                Log.i(TAG,"make1: if the device has got a external storage");

                                audioFile2=new File(file_path);
                                output2= new DataOutputStream(
                                        new BufferedOutputStream(
                                                new FileOutputStream(audioFile2)
                                        )
                                );
                            }
                            else{//否则
                                Log.i(TAG,"mark2: the device has not got a external storage");
                                String string=audioName2;
                                output2= new DataOutputStream(
                                        new BufferedOutputStream(
                                                MyApp.getContext().openFileOutput(string, Context.MODE_PRIVATE)
                                        )
                                );
                                audioFile2=MyApp.getContext().getFileStreamPath(string);
                            }
                           isActing(recordData,file_path,length,output2);
                           length = 0;
                           output2.flush();
                           output2.close();
                            System.out.println("before:"+getRecordedFileName());
                           Thread.sleep(1000);
                            //add 11.12
                           ready1=true;
                            //add 11.12
                            System.out.println("after:"+getRecordedFileName());
                            Log.d(TAG, "时间点：开始" + (endtime-starttime));
                            starttime = System.currentTimeMillis();

                        }
                    }
                    Log.d(TAG, "run: 结束了" );
                    //结束以上循环后就停止播放并释放资源
                    audioRecord.stop();

                    audioRecord.release();
                    Log.i(TAG, "successful create pcm file");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();
        return true;
    }
    public String getRecordedFileName2(){
        num++;
        int temp = num;
        return String.valueOf(temp);
    }
    public void display(Byte[] bytes,int l,DataOutputStream bw) throws IOException {
        //  File file=new File("D:\\a.txt");//创建文件对象
        for(int i=0;i<l;i++){
            bw.write(bytes[i]);

        }
    }
    @Override
    public boolean stop() {
        isRecording = false;
        return true;
    }

    //add 10.15
    public boolean isActing(Byte[] bytes,String path,int T,DataOutputStream output2){
        boolean isacting=true;
        System.out.println("wavrecord:"+path);
        if(isacting)
        {
            try {
                display(bytes,T,output2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constents.pathqueue.add(path);
        }
        return true;
    }
    //add 10.15
}