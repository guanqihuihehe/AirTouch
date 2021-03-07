package com.fruitbasket.audioplatform;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fruitbasket.audioplatform.play.PlayCommand;
import com.fruitbasket.audioplatform.play.PlayerInvoker;
import com.fruitbasket.audioplatform.play.WavePlayCommand;
import com.fruitbasket.audioplatform.play.WavePlayer;
import com.fruitbasket.audioplatform.record.RecordCommand;
import com.fruitbasket.audioplatform.record.RecorderInvoker;
import com.fruitbasket.audioplatform.record.RecorderTest;
import com.fruitbasket.audioplatform.record.RecorderTestCommand;
import com.fruitbasket.audioplatform.record.WavRecordCommand;
import com.fruitbasket.audioplatform.record.WavRecorder;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by FruitBasket on 2017/5/26.
 */

final public class AudioService extends Service {
    private final static String TAG=".AudioService";

    private PlayerInvoker playerInvoker=new PlayerInvoker();
    private RecorderInvoker recorderInvoker=new RecorderInvoker();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind()");
        return new AudioServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i(TAG,"onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        Log.i(TAG,"onDestroy()");
        super.onDestroy();
    }

    /**
     * 开始播放声波
     * @param channelOut
     * @param waveRate
     */
    public void startPlayWav(int channelOut,int waveRate, int iBeginHz, int iEndHz, int ifreqNum){
        PlayCommand wavePlayCommand =new WavePlayCommand(
                new WavePlayer(
                        channelOut,
                        waveRate,
                        iBeginHz,
                        iEndHz,
                        ifreqNum
                )
        );
        playerInvoker.setCommand(wavePlayCommand);
        playerInvoker.play();
    }

    public void startPlayWav(int channelOut, int waveRate, int waveType, int sampleRate, int iBeginHz, int iEndHz, int ifreqNum){
        Log.i(TAG,"startPlayWav");
        Log.d(TAG,"channelOut:"+channelOut);
        Log.d(TAG,"waveRate:"+waveRate);
        Log.d(TAG,"waveType:"+waveType);
        Log.d(TAG,"iBeginHz:"+iBeginHz);
        Log.d(TAG,"iEndHz:"+iEndHz);
        Log.d(TAG,"ifreqNum:"+ifreqNum);
        PlayCommand wavePlayCommand =new WavePlayCommand(
                new WavePlayer(
                        channelOut,
                        waveRate,
                        waveType,
                        sampleRate,
                        iBeginHz,
                        iEndHz,
                        ifreqNum
                )
        );
        playerInvoker.setCommand(wavePlayCommand);
        playerInvoker.play();
    }

    /**
     * 停止播放声波
     */
    public void stopPlay(){
        playerInvoker.stop();
    }

    /**
     * 释放播放器资源
     */
    public void releasePlayer(){
        playerInvoker.release();
    }

    public void startRecordTest(){
        if(recorderInvoker.getRecordCommand()!=null && recorderInvoker.getRecordCommand() instanceof RecorderTestCommand){

        }
        else{
            RecorderTestCommand recorderTestCommand=new RecorderTestCommand(
                    new RecorderTest()
            );
            recorderInvoker.setCommand(recorderTestCommand);
        }
        recorderInvoker.start();
    }

    /**
     * 开始录制wav格式的音频
     * @param channelIn
     * @param sampleRate
     * @param encoding
     */
    public void startRecordWav(int channelIn,int sampleRate,int encoding){
//    public String startRecordWav(int channelIn,int sampleRate,int encoding){
        Log.i(TAG,"startRecordWav(channelIn,sampleRate,encoding");
        //这里似乎也存在问题。 参数问题
        if(recorderInvoker.getRecordCommand()!=null &&recorderInvoker.getRecordCommand() instanceof WavRecordCommand){
            Log.i(TAG,"startRecordWav(): recorderInvoker.getRecordCommand()!=null &&recorderInvoker.getRecordCommand() instanceof WavRecordCommand");
        }
        else{
            Log.i(TAG,"create a new RecordCommand");

            RecordCommand recordCommand=new WavRecordCommand(
                    new WavRecorder(channelIn,sampleRate,encoding)
            );
            recorderInvoker.setCommand(recordCommand);
        }
        recorderInvoker.start();
    }


    /**
     * 停止录制音频
     */
    public void stopRecord(){
        Log.i(TAG,"stopRecord()");
        recorderInvoker.stop();
    }

    public class AudioServiceBinder extends Binder {
        public AudioService getService(){
            return AudioService.this;
        }
    }
}
