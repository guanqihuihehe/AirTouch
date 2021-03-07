package com.fruitbasket.audioplatform.play;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public class WavePlayCommand extends PlayCommand {

    private  WavePlayer wavePlayer;

    public WavePlayCommand(WavePlayer wavePlayer){
        this.wavePlayer=wavePlayer;
    }

    @Override
    public void play() {
        wavePlayer.play();
    }

    @Override
    public void stop() {
        wavePlayer.stop();
    }

    @Override
    public void release() {

    }
}
