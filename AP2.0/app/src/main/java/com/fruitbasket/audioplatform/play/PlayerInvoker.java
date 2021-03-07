package com.fruitbasket.audioplatform.play;

import android.util.Log;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public class PlayerInvoker {
    private static final String TAG=".play.PlayerInvoker";
    private PlayCommand playCommand;

    public void setCommand(PlayCommand playCommand){
        this.playCommand = playCommand;
    }

    public void play(){
        if(playCommand !=null){
            playCommand.play();
        }
        else{
            Log.w(TAG,"playCommand==null");
        }
    }

    public void stop(){
        if(playCommand !=null){
            playCommand.stop();
        }
        else{
            Log.w(TAG,"playCommand==null");
        }
    }

    public void release(){
        if(playCommand !=null){
            playCommand.release();
        }
        else{
            Log.w(TAG,"playCommand==null");
        }
    }
}
