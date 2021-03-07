package com.fruitbasket.audioplatform.record;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.MyApp;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

/**
 * Author: FruitBasket
 * Time: 2017/7/8
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */
public class RecorderTest extends Recorder {
    private static final String TAG="..RecorderTest";

    private MediaRecorder recorder;
    private String audioName;//录音文件的名字

    private String subDir;//用于存放录音文件的子目录

    public RecorderTest(){
        super();
    }

    public RecorderTest(int channelIn, int sampleRate, int encoding){
        super(channelIn,sampleRate,encoding);
    }

    @Override
    public boolean start() {

        Log.i(TAG,"start()");
        //使用异步的方法录制音频
        new Thread(new Runnable() {
            @Override
            public void run() {

                //创建子目录
                File subFile=new File(AppCondition.getAppExternalDir()+File.separator+subDir+File.separator);
                boolean state=(subFile).mkdir();
                Log.d(TAG,"create sub dir state=="+state);

                String[] files=subFile.list();
                int fileNumber;
                if(files==null){
                    fileNumber=0;
                }
                else{
                    fileNumber=subFile.list().length;
                }

                if (Environment.getExternalStorageState()// 如果外存存在
                        .equals(android.os.Environment.MEDIA_MOUNTED)){
                    Log.i(TAG,"make1: if the device has got a external storage");

                    audioName=AppCondition.getAppExternalDir()+File.separator+subDir+File.separator+getRecordedFileName();//命名方式有些奇怪，后续要改进

                }
                else{//否则
                    Log.i(TAG,"mark2: the device has not got a external storage");
                    audioName=subDir+File.separator+getRecordedFileName();//命名方式有些奇怪，后续要改进
                }

                try {

                    recorder=new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setAudioSamplingRate(44100);

                    /*
                    DEFAULT+VORBIS:fault
                    WEBM+VORBIS:fault
                     */
                    recorder.setOutputFile(audioName);
                    recorder.prepare();
                    recorder.start();
                } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }).start();

        return true;
    }


    @Override
    public boolean stop() {
        if(recorder!=null){
            recorder.stop();
            recorder.release();
        }
        return true;
    }
}
