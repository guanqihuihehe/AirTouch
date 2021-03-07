package com.fruitbasket.audioplatform.record;

import java.util.Queue;

/**
 * Author: FruitBasket
 * Time: 2017/7/8
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */
public class RecorderTestCommand extends RecordCommand {

    private RecorderTest recorderTest;

    public RecorderTestCommand(RecorderTest recorderTest){
        this.recorderTest = recorderTest;
    }

    @Override
    public void start() {
        recorderTest.start();
    }


    @Override
    public void stop() {
        recorderTest.stop();
    }
}
