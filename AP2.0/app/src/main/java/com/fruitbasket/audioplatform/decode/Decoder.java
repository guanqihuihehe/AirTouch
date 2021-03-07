package com.fruitbasket.audioplatform.decode;

/**
 * Created by FruitBasket on 2017/5/27.
 */

public interface Decoder {
    /**
     * 解码音频数据得到信息，信息以字符串表示
     * @return 结果字符串
     */
    public abstract String decode();
}
