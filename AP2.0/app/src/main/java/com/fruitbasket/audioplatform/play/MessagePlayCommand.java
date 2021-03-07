package com.fruitbasket.audioplatform.play;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public class MessagePlayCommand extends PlayCommand {

    private MessageAudioPlayer messageAudioPlayer;

    public MessagePlayCommand(MessageAudioPlayer messageAudioPlayer){
        this.messageAudioPlayer=messageAudioPlayer;
    }

    @Override
    public void play() {
        messageAudioPlayer.play();
    }

    @Override
    public void stop() {
        messageAudioPlayer.stop();
    }

    @Override
    public void release() {
        messageAudioPlayer.release();
    }
}
