package com.fruitbasket.audioplatform.play2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Administrator on 2017/6/21.
 */

public class AudioTrackPlay {
    private AudioTrack audioTrack;
    private byte generatedSound[];

    public AudioTrackPlay() {
        init();
    }
    public void init(){
        generatedSound = new byte[2 * GlobalConfig.stPhaseProxy.GetPlayBuffer().length];

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                GlobalConfig.AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSound.length,
                AudioTrack.MODE_STATIC
        );

        int bufferSize = AudioTrack.getMinBufferSize(GlobalConfig.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        setFrequency();
    }

    //sets to new frequency and continues playing
    public void changeFrequency() {
        setFrequency();
        play();
    }

    //sets frequency and stops sound
    public void setFrequency() {
        genTone();
        audioTrack.write(generatedSound, 0, generatedSound.length);
        audioTrack.setLoopPoints(0, generatedSound.length/2, -1);
        //audioTrack.setLoopPoints(0, 512, -1);
    }

    public void play() {
        //16 bit because it's supported by all phones
        audioTrack.play();
    }

    public void pause() {
        audioTrack.pause();
    }

    public void stop() {
        audioTrack.stop();
        audioTrack.release();
    }

    void genTone(){
        // fill out the array

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final short val : GlobalConfig.stPhaseProxy.GetPlayBuffer()) {
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[idx++] = (byte) (val & 0x00ff);
            //Log.i("playdata","["+idx+"],"+generatedSound[idx-1] );
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
            //Log.i("playdata","["+idx+"],"+generatedSound[idx-1] );

        }

        ///Test1: add some control
        //final int[] CONTROL={ 1,1,	1,	1,	1,	1,	-1,	-1,	-1,	1,	-1,	1,	1,	-1,	1,	1,	-1,	-1,	1,	1,	-1,	-1,	-1,	1,	1,	-1,	1,	-1,	-1,	1,	1,	-1,	1,	1,	1,	1,	1,	1,	1,	-1,	1,	1,	1,	-1,	-1,	1,	1,	1,	1,	-1	};
        /*final int[] CONTROL={1};
        final int UNIT_NUM=10;

        int counter;
        int controlIndex;
        int i,j;

        for(i=0,counter=0,controlIndex=0;
            i<generatedSound.length;
            ++i){

            generatedSound[i]*=CONTROL[controlIndex];

            counter++;
            if(counter>=UNIT_NUM){
                counter=0;
                controlIndex++;
                if(controlIndex>=CONTROL.length){
                    controlIndex=0;
                }
            }
        }*/
        //Test1 End

        //Test2: add some control
        /*final int NUM=320;
        int i,j;

        for(i=0,j=0;i<generatedSound.length;++i,++j){
            if(j<NUM/2){
                ;
            }
            else if(j<NUM){
                generatedSound[i]=0;
            }
            else{
                j=-1;
            }
        }*/
        //Test2



        ///GlobalConfig.stWaveFileUtil.saveDataToWav(generatedSound,GlobalConfig.stWaveFileUtil.getAndroidPlayFileName(),(long)(GlobalConfig.AUDIO_SAMPLE_RATE),1,generatedSound.length,(short)16);
    }
}
