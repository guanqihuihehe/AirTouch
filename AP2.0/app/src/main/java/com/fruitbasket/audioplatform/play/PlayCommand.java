package com.fruitbasket.audioplatform.play;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public abstract class PlayCommand {
    public abstract void play();
    public abstract void stop();
    public abstract void release();
}
