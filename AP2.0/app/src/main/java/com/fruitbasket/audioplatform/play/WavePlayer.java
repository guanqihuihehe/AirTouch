package com.fruitbasket.audioplatform.play;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.WaveProducer;
import com.fruitbasket.audioplatform.play2.AudioTrackPlay;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public class WavePlayer extends Player {
    private final static String TAG = ".play.WavePlayer";

    private int waveRate;//声波的频率
    @WaveProducer.WaveType
    private int waveType;//声波的类型///此变量暂时使用
    private int sampleRate;//设备实际的发声频率

    private int iBeginHz;
    private int iStepHz;
    private int ifreqNum;

    private AudioTrack audioTrack;///暂时弃用

    AudioTrackPlay player= new AudioTrackPlay();

    public WavePlayer(int channelOut, int waveRate, int iBeginHz, int iStepHz, int ifreqNum) {
        this(channelOut, waveRate, WaveProducer.SIN, AppCondition.DEFAULE_SIMPLE_RATE, iBeginHz, iStepHz, ifreqNum);
    }

    public WavePlayer(int channelOut, int waveRate, @WaveProducer.WaveType int waveType, int sampleRate, int iBeginHz, int iStepHz, int ifreqNum) {
        super(channelOut);
        this.waveType = waveType;
        this.waveRate = waveRate;
        this.sampleRate = sampleRate;
        this.iBeginHz = iBeginHz;
        this.iStepHz = iStepHz;
        this.ifreqNum = ifreqNum;
    }

    //play 2
    @Override
    public void play(){
        //同步播放
        player.play();
        /*
       异步播放
        new Thread(new Runnable() {
            @Override
            public void run() {
                player.play();
            }
        });*/
    }

    @Override
    public void stop(){
        player.stop();
    }

    @Override
    public void release() {
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }

    //play 1
    /*@Override
    public void play() {
        final int bufferSize = 6 * AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                //AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.i(TAG, "play() : bufferSize==" + bufferSize);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                //AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        switch (channelOut) {
            case CHANNEL_OUT_LEFT:
                audioTrack.setStereoVolume(0.5f, 0.0f);
                break;
            case CHANNEL_OUT_RIGHT:
                audioTrack.setStereoVolume(0.0f, 0.5f);
                break;
            case CHANNEL_OUT_BOTH:
                audioTrack.setVolume(0.5f);
                break;
            default:
                audioTrack.setVolume(0.5f);
                Log.w(TAG, "play() : channel error");
        }

        audioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {

                int writeBytes;
                //放声方法一stream
                *//*double sampleCountInWave = sampleRate / (double) waveRate;//每一个波中，包含的样本点数量
                short[] wave = new short[bufferSize];
                int index = 0;
                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {


                        for (int i = 0; i < wave.length; ++i, ++index) {
                            wave[i] = (short) (Short.MAX_VALUE/2 *
                                    Math.sin(2.0 * Math.PI * index / sampleCountInWave)
                            );
                        }


                    writeBytes= audioTrack.write(wave, 0, wave.length);
                    Log.d(TAG,"play() : writeBytes="+writeBytes);
                }*//*

                //发出多个频率的波形
               *//*short[] FreqsNum = new short[bufferSize];
                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    for (int i = 0; i < FreqsNum.length; i++) {
                        double sum = 0;
                        for (int j = 0; j < ifreqNum; j++) {
                            //FreqsNum[i] += Math.sin(2.0 * Math.PI * (iBeginHz + j * ifreqNum)/ sampleRate);
                            double sampleCountInWave = sampleRate / (double) (iBeginHz + j * iStepHz);
                            sum += Short.MAX_VALUE / 2 *
                                    Math.sin(2.0 * Math.PI * i / sampleCountInWave);
                        }
                        FreqsNum[i] = (short) (sum / ifreqNum);
                    }
                    writeBytes = audioTrack.write(FreqsNum, 0, FreqsNum.length);
                    Log.d(TAG, "play() : writeBytes=" + writeBytes);
                }*//*

                *//*short[] FreqsNum;
                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    FreqsNum = getMulripleFreqSignal(bufferSize,sampleRate,iBeginHz,iStepHz,ifreqNum);
                    writeBytes = audioTrack.write(FreqsNum, 0, FreqsNum.length);
                    Log.d(TAG, "play() : writeBytes=" + writeBytes);
                }*//*


                //发声方法二static
                *//*short[] wave= WaveProducer.getWave(waveType,waveRate,sampleRate,bufferSize);

                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    writeBytes=audioTrack.write(wave, 0, wave.length);
                    Log.d(TAG,"play() : writeBytes="+writeBytes);
                }*//*

            }
        }).start();

    }*/

    /*@Override
    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
        }
    }

    private native short[] getMulripleFreqSignal(int bufferSize,int sampleRate,int iBeginHz,int iStepHz,int ifreqNum);
    */
}
