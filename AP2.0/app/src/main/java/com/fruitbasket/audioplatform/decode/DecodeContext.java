package com.fruitbasket.audioplatform.decode;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public class DecodeContext {

    private Decoder decoder;

    public DecodeContext(Decoder decoder){
        this.decoder=decoder;
    }

    public String decode(){
        return decoder.decode();
    }
}
