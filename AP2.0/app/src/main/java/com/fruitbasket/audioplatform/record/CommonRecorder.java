package com.fruitbasket.audioplatform.record;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public class CommonRecorder extends Recorder {
    private static final String TAG=".record.CommonRecorder";

    private String audioFullPath=null;//录音文件的存放路径
    private boolean isRecording;

    public CommonRecorder(){
        super();
        this.audioFullPath=getRecordedFileName();
    }

    public CommonRecorder(int channelIn, int sampleRate, int encoding,String audioFullPath){
        super(channelIn,sampleRate,encoding);
        this.audioFullPath=audioFullPath;
    }

    @Override
    public boolean start() {
        File audioFile= new File(audioFullPath);

        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelIn,
                encoding);
        if(bufferSize==AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
            return false;
        }
        else if(bufferSize==AudioRecord.ERROR){
            Log.e(TAG,"recordingBufferSize==AudioRecord.ERROR");
            return false;
        }
        short[] buffer=new short[bufferSize];

        try {
            DataOutputStream output=new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(audioFile)
                    )
            );
            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelIn,
                    encoding,
                    bufferSize);
            audioRecord.startRecording();

            isRecording = true;
            while (isRecording) {
                int readResult = audioRecord.read(buffer, 0, bufferSize);
                if(readResult==AudioRecord.ERROR_INVALID_OPERATION){
                    Log.e(TAG,"readState==AudioRecord.ERROR_INVALID_OPERATION");
                    return false;
                }
                else if(readResult==AudioRecord.ERROR_BAD_VALUE){
                    Log.e(TAG,"readState==AudioRecord.ERROR_BAD_VALUE");
                    return false;
                }
                else{
                    for(int i=0;i<readResult;i++){
                        output.writeShort(buffer[i]);
                    }
                }
            }
            //结束以上循环后就停止播放并释放资源
            audioRecord.stop();
            output.flush();
            output.close();
            audioRecord.release();
            audioRecord=null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean stop() {
        isRecording=false;
        return true;
    }

}
