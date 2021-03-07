package com.fruitbasket.audioplatform.play;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.text.TextUtils;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.encode.EncodeContext;
import com.fruitbasket.audioplatform.encode.FrequencyEncoder;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public class MessageAudioPlayer extends Player {
    private final static String TAG="..MessageAudioPlayer";

    private AudioTrack audioTrack;
    private String text;
    private boolean isRepeat;//是否重复播放
    private int muteInterval;//重复播放时的时间间隔，以毫秒为单位

    public MessageAudioPlayer(String text,boolean isRepeat,int muteInterval){
        this.text=text;
        this.isRepeat=isRepeat;
        this.muteInterval=muteInterval;
    }

    public MessageAudioPlayer(int channelOut,String text,boolean isRepeat,int muteInterval){
        super(channelOut);
        this.text=text;
        this.isRepeat=isRepeat;
        this.muteInterval=muteInterval;
    }

    @Override
    public void play() {
        Log.i(TAG,"play()");

        if(TextUtils.isEmpty(text)){
            Log.e(TAG,"play(): TextUtils.isEmpty(text)==ture");
            return;
        }
        else if(muteInterval<0||muteInterval>1000){
            Log.e(TAG,"play(): muteInterval<0||muteInterval>1000");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int bufferSize = AudioTrack.getMinBufferSize(
                        AppCondition.DEFAULE_SIMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if(audioTrack==null){
                    audioTrack=new AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            AppCondition.DEFAULE_SIMPLE_RATE,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize,
                            AudioTrack.MODE_STREAM);
                }
                audioTrack.play();

                short [][]data=(short[][])(
                        new EncodeContext(
                            new FrequencyEncoder(text)
                        )
                ).getAudioData();

                if(data!=null){
                    int i;
                    do{
                        for(i=0;i<data.length;++i){
                            audioTrack.write(data[i],0,data[i].length);
                        }
                        try {
                            Thread.sleep(muteInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }while(isRepeat&&
                            audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING);
                    if(isRepeat==false){
                        audioTrack.stop();
                    }
                    audioTrack.flush();
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        if(audioTrack!=null){
            audioTrack.stop();
        }
    }

    @Override
    public void release() {
        if(audioTrack !=null){
            audioTrack.release();
        }
    }
}
