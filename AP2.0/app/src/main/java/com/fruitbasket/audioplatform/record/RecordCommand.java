package com.fruitbasket.audioplatform.record;

import java.util.Queue;

/**
 * Created by FruitBasket on 2017/5/31.
 */

public abstract class RecordCommand {
    public abstract void start();
    public abstract void stop();
}
