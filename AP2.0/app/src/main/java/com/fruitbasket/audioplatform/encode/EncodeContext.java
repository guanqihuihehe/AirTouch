package com.fruitbasket.audioplatform.encode;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public class EncodeContext {

    private Encoder encoder;

    public EncodeContext(Encoder encoder){
        this.encoder=encoder;
    }

    public Object getAudioData(){
        return encoder.getAudioData();
    }
}
