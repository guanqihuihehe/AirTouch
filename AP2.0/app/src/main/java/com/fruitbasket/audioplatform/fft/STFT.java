/* Copyright 2014 Eddy Xiao <bewantbe@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fruitbasket.audioplatform.fft;

import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;

import java.util.Arrays;

/**
 * Short Time Fourier Transform
 */
public class STFT {
    private static final String TAG="STFT";

    public static final int FFT_LENGTH_512=512;
    public static final int FFT_LENGTH_1024=1024;
    public static final int FFT_LENGTH_2048=2048;
    public static final int FFT_LENGTH_4096=4096;
    public static final int FFT_LENGTH_8192=8192;

    public static final int FFT_AVERAGE_1=1;
    public static final int FFT_AVERAGE_2=2;
    public static final int FFT_AVERAGE_3=3;

    // data for frequency Analysis
    private double[] spectrumAmpOutCum;
    private double[] spectrumAmpOutTmp;
    private double[] spectrumAmpOut;
    private double[] spectrumAmpOutDB;

    private double[] spectrumAmpIn;//存放第一次预处理的数据
    private double[] spectrumAmpInTmp;//存放第二次预处理的数据
    private int spectrumAmpPt;//Pointer for spectrumAmpIn

    private double[] wnd;
    private double wndEnergyFactor = 1;           // used to keep energy invariant under different window

    private int sampleRate;
    private int fftLen;

    private double[][] spectrumAmpOutArray;
    private int spectrumAmpOutArrayPt = 0;        // Pointer for spectrumAmpOutArray
    private int nAnalysed = 0;//记录执行FFT的次数

    private RealDoubleFFT spectrumAmpFFT;
    private boolean boolAWeighting = false;

    private double cumRMS = 0;
    private int cntRMS = 0;
    private double outRMS = 0;

    private double[] dBAFactor;    // multiply to power spectrum to get A-weighting

    public double maxAmpFreq = Double.NaN, maxAmpDB = Double.NaN;

    public STFT(int fftLen){
        init(fftLen, AppCondition.DEFAULE_SIMPLE_RATE,1,"Hanning");
    }

    public STFT(int fftLen,int sampleRate){
        init(fftLen,sampleRate,1,"Hanning");
    }

    public STFT(int fftlen, int sampleRate, String wndName) {
        init(fftlen, sampleRate, 1, wndName);
    }

    public STFT(int fftlen, int sampleRate, int minFeedSize, String wndName) {
        init(fftlen, sampleRate, minFeedSize, wndName);
    }

    /**
     * 初始化对象
     * @param fftLen 将要进行FFT的数据长度
     * @param sampleRate 声音数据的样本频率
     * @param minFeedSize
     * @param wndName
     */
    private void init(int fftLen, int sampleRate, int minFeedSize, String wndName) {
        Log.i(TAG,"init()");
        if (minFeedSize <= 0) {
            throw new IllegalArgumentException("STFT::init(): should minFeedSize >= 1.");
        }
        if (((-fftLen) & fftLen) != fftLen) {
            // error: fftLen should be power of 2
            throw new IllegalArgumentException("STFT::init(): Currently, only power of 2 are supported in fftLen");
        }

        this.sampleRate = sampleRate;
        this.fftLen = fftLen;

        spectrumAmpOutCum = new double[fftLen / 2 + 1];
        spectrumAmpOutTmp = new double[fftLen / 2 + 1];
        spectrumAmpOut = new double[fftLen / 2 + 1];
        spectrumAmpOutDB = new double[fftLen / 2 + 1];

        spectrumAmpIn = new double[fftLen];
        spectrumAmpInTmp = new double[fftLen];

        spectrumAmpFFT = new RealDoubleFFT(spectrumAmpIn.length);

        spectrumAmpOutArray = new double[(int) Math.ceil((double) minFeedSize / (fftLen / 2))][]; // /2 since half overlap
        for (int i = 0; i < spectrumAmpOutArray.length; i++) {
            spectrumAmpOutArray[i] = new double[fftLen / 2 + 1];
        }

        initWindowFunction(fftLen, wndName);
        initDBAFactor(fftLen, sampleRate);
        boolAWeighting = false;
    }

    /**
     *
     * @param fftlen
     * @param wndName
     */
    private void initWindowFunction(int fftlen, String wndName) {
        wnd = new double[fftlen];
        if (wndName.equals("Bartlett")) {
            for (int i = 0; i < wnd.length; i++) {  // Bartlett
                wnd[i] = Math.asin(Math.sin(Math.PI * i / wnd.length)) / Math.PI * 2;
            }
        } else if (wndName.equals("Hanning")) {
            for (int i = 0; i < wnd.length; i++) {  // Hanning, hw=1
                wnd[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i / (wnd.length - 1.))) * 2;
            }
        } else if (wndName.equals("Blackman")) {
            for (int i = 0; i < wnd.length; i++) {  // Blackman, hw=2
                wnd[i] = 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (wnd.length - 1)) + 0.08 * Math.cos(4 * Math.PI * i / (wnd.length - 1));
            }
        } else if (wndName.equals("Blackman Harris")) {
            for (int i = 0; i < wnd.length; i++) {  // Blackman_Harris, hw=3
                wnd[i] = (0.35875 - 0.48829 * Math.cos(2 * Math.PI * i / (wnd.length - 1)) + 0.14128 * Math.cos(4 * Math.PI * i / (wnd.length - 1)) - 0.01168 * Math.cos(6 * Math.PI * i / (wnd.length - 1))) * 2;
            }
        } else if (wndName.equals("Kaiser, a=2.0")) {
            double a = 2.0;
            double dn = BesselCal.i0(Math.PI * a);
            for (int i = 0; i < wnd.length; i++) {  // Kaiser, a=2.0
                wnd[i] = BesselCal.i0(Math.PI * a * Math.sqrt(1 - (2.0 * i / (wnd.length - 1) - 1.0) * (2.0 * i / (wnd.length - 1) - 1.0))) / dn;
            }
        } else if (wndName.equals("Kaiser, a=3.0")) {
            double a = 3.0;
            double dn = BesselCal.i0(Math.PI * a);
            for (int i = 0; i < wnd.length; i++) {  // Kaiser, a=3.0
                wnd[i] = BesselCal.i0(Math.PI * a * Math.sqrt(1 - (2.0 * i / (wnd.length - 1) - 1.0) * (2.0 * i / (wnd.length - 1) - 1.0))) / dn;
            }
        } else if (wndName.equals("Kaiser, a=4.0")) {
            double a = 4.0;
            double dn = BesselCal.i0(Math.PI * a);
            for (int i = 0; i < wnd.length; i++) {  // Kaiser, a=4.0
                wnd[i] = BesselCal.i0(Math.PI * a * Math.sqrt(1 - (2.0 * i / (wnd.length - 1) - 1.0) * (2.0 * i / (wnd.length - 1) - 1.0))) / dn;
            }
        } else {
            for (int i = 0; i < wnd.length; i++) {
                wnd[i] = 1;
            }
        }

        double normalizeFactor = 0;//平均值
        for (int i = 0; i < wnd.length; i++) {
            normalizeFactor += wnd[i];
        }
        normalizeFactor = wnd.length / normalizeFactor;

        wndEnergyFactor = 0;
        for (int i = 0; i < wnd.length; i++) {
            wnd[i] *= normalizeFactor;//数组乘以平均值
            wndEnergyFactor += wnd[i] * wnd[i];
        }
        wndEnergyFactor = wnd.length / wndEnergyFactor;//数组长度除以元素的平方和
    }

    /**
     *  Generate multiplier for A-weighting
     * @param fftlen
     * @param sampleRate
     */
    private void initDBAFactor(int fftlen, double sampleRate) {
        dBAFactor = new double[fftlen / 2 + 1];
        for (int i = 0; i < fftlen / 2 + 1; i++) {
            double f = (double) i / fftlen * sampleRate;
            double r = sqr(12200) * sqr(sqr(f)) / ((f * f + sqr(20.6)) * Math.sqrt((f * f + sqr(107.7)) * (f * f + sqr(737.9))) * (f * f + sqr(12200)));
            dBAFactor[i] = r * r * 1.58489319246111;  // 1.58489319246111 = 10^(1/5)
        }
    }

    public void setAWeighting(boolean e_isAWeighting) {
        boolAWeighting = e_isAWeighting;
    }

    public boolean getAWeighting() {
        return boolAWeighting;
    }

    public int nElemSpectrumAmp() {
        return nAnalysed;
    }

    public void feedData(short[] ds) {
        feedData(ds, ds.length);
    }

    /**
     * 对输入数据，执行FFT处理
     * @param ds 将要进行FFT处理的数据
     * @param dsLen 将要处理的数据的长度
     */
    public void feedData(short[] ds, int dsLen) {
        Log.i(TAG,"feedData()");
        if (dsLen > ds.length) {
            Log.e(TAG, "dsLen > ds.length !");
            dsLen = ds.length;
        }

        int inLen = spectrumAmpIn.length;
        int outLen = spectrumAmpOut.length;

        int dsPt = 0;           // input data point to be read
        while (dsPt < dsLen) {
            while (spectrumAmpPt < inLen && dsPt < dsLen) {
                double s = ds[dsPt++] / 32768.0;//对输入的数据进行第一次预处理
                spectrumAmpIn[spectrumAmpPt++] = s;

                cumRMS += s * s;
                cntRMS++;
            }

            if (spectrumAmpPt == inLen) {    // enough data for one FFT
                for (int i = 0; i < inLen; i++) {
                    spectrumAmpInTmp[i] = spectrumAmpIn[i] * wnd[i];//进行第二次预处理
                }
                spectrumAmpFFT.ft(spectrumAmpInTmp);//执行FFT处理

                fftToAmp(spectrumAmpOutTmp, spectrumAmpInTmp);
                System.arraycopy(
                        spectrumAmpOutTmp,
                        0,
                        spectrumAmpOutArray[spectrumAmpOutArrayPt],
                        0,
                        spectrumAmpOutTmp.length
                );
                spectrumAmpOutArrayPt = (spectrumAmpOutArrayPt + 1) % spectrumAmpOutArray.length;//指针循环+1
                for (int i = 0; i < outLen; i++) {
                    spectrumAmpOutCum[i] += spectrumAmpOutTmp[i];
                }
                nAnalysed++;

                //将spectrumAmpIn的后半段元素复制到前半段
                // half overlap  (set spectrumAmpPt = 0 for no overlap)
                int n2 = spectrumAmpIn.length / 2;
                System.arraycopy(spectrumAmpIn, n2, spectrumAmpIn, 0, n2);

                spectrumAmpPt = n2;
            }

        }
    }

    /**
     *
     * @param dataOut 目标数据
     * @param data 源数据
     */
    private void fftToAmp(double[] dataOut, double[] data) {
        // data.length should be even number
        double scaler = 2.0 * 2.0 / (data.length * data.length);  // *2 since there are positive and negative frequency part

        dataOut[0] = data[0] * data[0] * scaler / 4.0;
        int j = 1;
        for (int i = 1; i < data.length - 1; i += 2, j++) {
            dataOut[j] = (data[i] * data[i] + data[i + 1] * data[i + 1]) * scaler;
        }
        dataOut[j] = data[data.length - 1] * data[data.length - 1] * scaler / 4.0;
    }

    /**
     * 对音频数据进行FFT后，取得处理结果
     * @return
     */
    final public double[] getSpectrumAmp() {
        if (nAnalysed != 0) {    // no new result
            int outLen = spectrumAmpOut.length;
            double[] sAOC = spectrumAmpOutCum;
            for (int j = 0; j < outLen; j++) {
                sAOC[j] /= nAnalysed;
            }

            if (boolAWeighting) {
                for (int j = 0; j < outLen; j++) {
                    sAOC[j] *= dBAFactor[j];
                }
            }

            System.arraycopy(sAOC, 0, spectrumAmpOut, 0, outLen);
            Arrays.fill(sAOC, 0.0);
            nAnalysed = 0;
            for (int i = 0; i < outLen; i++) {
                spectrumAmpOutDB[i] = 10.0 * Math.log10(spectrumAmpOut[i]);
            }
        }
        return spectrumAmpOut;
    }

    /**
     * 对音频数据进行FFT后，取得以分贝为单位纵轴单位的处理结果
     * @return
     */
    final public double[] getSpectrumAmpDB() {
        getSpectrumAmp();
        return spectrumAmpOutDB;
    }

    /**
     *
     * @return
     */
    public double getRMS() {
        if (cntRMS > 8000 / 30) {
            outRMS = Math.sqrt(cumRMS / cntRMS * 2.0);  // "* 2.0" normalize to sine wave.
            cumRMS = 0;
            cntRMS = 0;
        }
        return outRMS;
    }

    /**
     *
     * @return
     */
    public double getRMSFromFT() {
        getSpectrumAmpDB();
        double s = 0;
        for (int i = 1; i < spectrumAmpOut.length; i++) {
            s += spectrumAmpOut[i];
        }
        return Math.sqrt(s * wndEnergyFactor);
    }

    /**
     *
     */
    public void calculatePeak() {
        getSpectrumAmpDB();
        // Find and show peak amplitude
        maxAmpDB = 20 * Math.log10(0.125 / 32768);
        maxAmpFreq = 0;
        for (int i = 1; i < spectrumAmpOutDB.length; i++) {  // skip the direct current term
            if (spectrumAmpOutDB[i] > maxAmpDB) {
                maxAmpDB = spectrumAmpOutDB[i];
                maxAmpFreq = i;
            }
        }
        maxAmpFreq = maxAmpFreq * sampleRate / fftLen;

        // Slightly better peak finder
        // The peak around spectrumDB should look like quadratic curve after good window function
        // a*x^2 + b*x + c = y
        // a - b + c = x1
        //         c = x2
        // a + b + c = x3
        if (sampleRate / fftLen < maxAmpFreq && maxAmpFreq < sampleRate / 2 - sampleRate / fftLen) {
            int id = (int) (Math.round(maxAmpFreq / sampleRate * fftLen));
            double x1 = spectrumAmpOutDB[id - 1];
            double x2 = spectrumAmpOutDB[id];
            double x3 = spectrumAmpOutDB[id + 1];
            double c = x2;
            double a = (x3 + x1) / 2 - x2;
            double b = (x3 - x1) / 2;
            if (a < 0) {
                double xPeak = -b / (2 * a);
                if (Math.abs(xPeak) < 1) {
                    maxAmpFreq += xPeak * sampleRate / fftLen;
                    maxAmpDB = (4 * a * c - b * b) / (4 * a);
                }
            }
        }
    }

    /**
     * 清空对象存储的数据
     */
    public void clear() {
        spectrumAmpPt = 0;
        Arrays.fill(spectrumAmpOut, 0.0);
        Arrays.fill(spectrumAmpOutDB, Math.log10(0));
        Arrays.fill(spectrumAmpOutCum, 0.0);
        for (int i = 0; i < spectrumAmpOutArray.length; i++) {
            Arrays.fill(spectrumAmpOutArray[i], 0.0);
        }
    }

    private static double sqr(double x) {
        return x * x;
    }
}
