package com.fruitbasket.audioplatform.record;

import android.util.Log;

import java.util.Queue;

/**
 * Created by FruitBasket on 2017/5/31.
 */

public class RecorderInvoker {
    private static final String TAG=".record.RecorderInvoker";
    private RecordCommand recordCommand;

    public void setCommand(RecordCommand recordCommand){
        this.recordCommand = recordCommand;
    }

    public RecordCommand getRecordCommand(){
        return recordCommand;
    }

    public void start(){
        if(recordCommand!=null){
            recordCommand.start();
        }
        else{
            Log.w(TAG,"recordCommand==null");
        }
    }


    public void stop(){
        if(recordCommand!=null){
            recordCommand.stop();
        }
        else{
            Log.w(TAG,"recordCommand==null");
        }

    }

}
