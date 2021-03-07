package com.fruitbasket.audioplatform.play2;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/7/6.
 */

public class PhaseAudioRecord {

    private int wavCopyChannelNum = 1;
    private short wavCopyBitsPerSample = 16;
    public static long lRecordNum = 0;
    AudioRecord recorder;
    recordThread stRecordThread = new  recordThread();                                               //录制的Pcm文件名称
    // 录制的PCM文件句柄
    WavAudioRecord stWavAudioRecord = new WavAudioRecord();
    public static int  offsetInBytes = 0;
    public static boolean   bFirstSimulatePlay = true;
    public static boolean   bSimulate = true;

    public class WavAudioRecord{
        //初始设置
        public int audioSource = MediaRecorder.AudioSource.MIC;
        public int sampleRateInHz = GlobalConfig.AUDIO_SAMPLE_RATE;
        public int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        public int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    }

    public void initRecord(){

        int bufferSize = AudioRecord.getMinBufferSize(
                stWavAudioRecord.sampleRateInHz,
                stWavAudioRecord.channelConfig,
                stWavAudioRecord.audioFormat
        );

        recorder = new AudioRecord(
                stWavAudioRecord.audioSource,
                stWavAudioRecord.sampleRateInHz,
                stWavAudioRecord.channelConfig,
                stWavAudioRecord.audioFormat,
                bufferSize*10
        );//初始化录音对象

        if(GlobalConfig.bRecordThreadFlag) {
            stRecordThread = new recordThread();//创建新的播放线程
            stRecordThread.start();//开始运行线程
        }
    }

    /**
     * 停止的时候才写入
     * @throws IOException
     */
    public void stopRecording() throws IOException {
        Log.i("timer","audio Stopped");
        //record release
        GlobalConfig.isRecording = false;//录音标记位
        if(GlobalConfig.bSaveWavFile) {//一个是AutoRecording，一个是ProxyRecording
            String sFile = GlobalConfig.fPcmRecordFile.getAbsolutePath();//获得存储PCM文件的路径
            String sWavPath = WaveFileUtil.getWaveFile(sFile);//获得Wav文件的路径
            int bufferSize = AudioRecord.getMinBufferSize(
                    stWavAudioRecord.sampleRateInHz,
                    stWavAudioRecord.channelConfig,
                    stWavAudioRecord.audioFormat
            );//获得buffer长度

            //为什么分两个文件？
            WaveFileUtil.copyWaveFile(sFile, sWavPath, stWavAudioRecord.sampleRateInHz, wavCopyChannelNum, bufferSize, wavCopyBitsPerSample);//给裸数据加上头文件

            String sFile2 = GlobalConfig.fPcmRecordFile2.getAbsolutePath();
            String sWavPath2 = WaveFileUtil.getWaveFile(sFile2);

            WaveFileUtil.copyWaveFile(
                    sFile2,
                    sWavPath2,
                    stWavAudioRecord.sampleRateInHz,
                    wavCopyChannelNum,
                    bufferSize,
                    wavCopyBitsPerSample
            );//给裸数据加上头文件

        }
        recorder.stop();
        recorder.release();
    }

    /**
     * 录音线程
     */
    private class recordThread  extends Thread {

        @Override
        public void run() {
            //Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            int bufferSize = AudioRecord.getMinBufferSize(
                    stWavAudioRecord.sampleRateInHz,
                    stWavAudioRecord.channelConfig,
                    stWavAudioRecord.audioFormat
            );

            while (GlobalConfig.isRecording!=true){}//如果不在录音状态就一直等待

            try {
                if(GlobalConfig.fPcmRecordFile != null){//设置输出的文件的File对象
                    GlobalConfig.recDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(GlobalConfig.fPcmRecordFile)));
                }
                //Log.i("audio","buffersize:"+bufferSize);
                recordByte(GlobalConfig.RECORD_FRAME_SIZE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void recordByte(int bufferSize) throws IOException, InterruptedException {

            recorder.startRecording();//正式开始录音
            GlobalConfig.isRecording = true;

            while (GlobalConfig.isRecording ) {//isRecording一直处于录音的状态
                if (GlobalConfig.bPlayDataReady) {
                    //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    byte[] rec = new byte[bufferSize];
                    int iReadSize = 0;                 
                    iReadSize = recorder.read(rec, 0, rec.length);//不断从recorder中读出数据
                    long lNow = System.currentTimeMillis();
                    lRecordNum = lRecordNum+iReadSize;//可以忽略
                    //Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);

                    if (GlobalConfig.bSupportLLAP) {//处理相位
                        int iMaxData = MatrixProcess.max(rec, rec.length);//找出一个buffer之中的最大值
                        //Log.i("speed","record:"+lRecordNum + "|lNow:"+lNow + "|max:"+iMaxData +"|iFrame:" + iFrame);
                        if (iMaxData != 0) {                     //
                            //Log.i("mFreqPower","==================iframe:"+iFrame+"======================");
                            //Log.i("read ", "readdata:" + iReadSize);
                            GlobalConfig.getInstance().pushRecData(rec);//保存数据
                            /*for(int i=0; i<rec.length;i++){
                                Log.i("recdata","record["+i+"],"+rec[i]);
                            }*/
                            //writeByte(rec);
                        }
                    }
                }
            }
        }

        public void writeByte(byte[] recData){
            if (GlobalConfig.bByte && GlobalConfig.bSaveWavFile) {
                int iReadSize = recData.length;
                //Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);

                if (GlobalConfig.recDos == null) {
                    //Log.i("record", "resdos is null");
                    if (GlobalConfig.fPcmRecordFile != null) {
                        try {
                            GlobalConfig.recDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(GlobalConfig.fPcmRecordFile)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //循环将buffer中的音频数据写入到OutputStream中
                if (GlobalConfig.recDos != null) {
                    for (int i = 0; i < iReadSize; i++) {
                        try {
                            GlobalConfig.recDos.writeByte(recData[i]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
