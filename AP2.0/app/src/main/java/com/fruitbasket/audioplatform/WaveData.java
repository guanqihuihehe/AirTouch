package com.fruitbasket.audioplatform;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import uk.me.berndporr.iirj.Butterworth;


public class WaveData {
    public static String path;
    public static double[] data = null;
    public static double first_data = 0;
    public static double last_data = 0;
    private double[][] plotData = null;
    private long SR = 0;
    public WaveData(String path) {
        this.path = path;
        this.init();
    }

    private  void init(){
        WaveFileReader reader = new WaveFileReader(path);
        this.SR = reader.getSampleRate();
        if (reader.isSuccess()) {
            data = reader.getData()[0]; //Get the first channel, data is not normalized
        }
        else {
            System.err.println(path + "not a wav");
        }
        for (int i =0;i<data.length;i++){
            data[i] /=32767.0d;
        }

        data = butter_bandpass_filter(data,18700,19300,44100,6);
        data = butter_stop_filter(data,18985,19015,44100,3);
        if (data.length>=70000){
            data = Arrays.copyOfRange(data, 12288+1,data.length-5120);
        }

        int data_length = data.length;
        //initialize parameters for FFT
        int WS = 8192; //WS = window size
        //            int OF = 8;    //OF = overlap factor
        int windowStep = 1024;

        //initialize plotData array
        int nX = (data_length-WS)/windowStep;
        int nY = WS/2+1;
        if (nX<0){
            return;
        }
        this.plotData = new double[nX][113];

        double amp_square;

        double[] inputImag = new double[data_length];

        for (int i = 0; i < nX; i++){
            Arrays.fill(inputImag, 0.0);
            double[] WS_array = FFT.fft(Arrays.copyOfRange(data, i*windowStep, i*windowStep+WS), inputImag, true);
            for (int j = 0; j < nY; j++){
                amp_square = (WS_array[2*j]*WS_array[2*j]) + (WS_array[2*j+1]*WS_array[2*j+1]);
                if(j>=3474&&j<3587){
                    if (amp_square == 0.0){
                        this.plotData[i][j-3474] = amp_square;
                    }
                    else{
                        this.plotData[i][j-3474] = 10 * Math.log10(amp_square);
                    }
                }
            }
        }
//        this.plotData = resize(this.plotData);

    }

    public double[][] getData(){
        return this.plotData;
    }


    public long getSR() {
        return this.SR;
    }

    public static double[] butter_bandpass_filter(double[] data, int lowcut, int highcut, int sampleRate, int order) {
        Butterworth butterworth = new Butterworth();
        double widthFrequency = highcut - lowcut;
        double centerFrequency = (highcut + lowcut) / 2;
        butterworth.bandPass(order, sampleRate, centerFrequency, widthFrequency);
        double[] list = new double[data.length];
        int in = 0;
        for (double v : data) {
            double f = butterworth.filter(v);
            list[in] = f;
            in++;
        }
        return list;
    }

    public static double[] butter_stop_filter(double[] data, int lowcut, int highcut, int sampleRate, int order) {
        Butterworth butterworth = new Butterworth();
        double widthFrequency = highcut - lowcut;
        double centerFrequency = (highcut + lowcut) / 2;
        butterworth.bandStop(order, sampleRate, centerFrequency, widthFrequency);
        double[] list = new double[data.length];
        int in = 0;
        for (double v : data) {
            double f = butterworth.filter(v);
            list[in] = f;
            in++;
        }
        return list;
    }

    public static double[][] resize(double[][] org_arr){
        double[][] new_arr = new double[113][113];
        int time_lenth = org_arr.length;
        if(time_lenth<113){
            int one_dis = (113/(Math.abs(time_lenth-113)+1));
            for(int i =0;i<113;i++){
                if (i%one_dis==0) {
                    for (int j = 0; j < 113; j++){
                        new_arr[i][j] = (org_arr[i-i/one_dis+1][j] + org_arr[i-i/one_dis][j]) / 2;
                    }
                }
                else{
                    for (int j = 0; j < 113; j++) {
                        new_arr[i][j] = org_arr[i - i / one_dis][j];
                    }
                }
            }
        }
        else if (time_lenth>113){
            int one_dis = (time_lenth/(Math.abs(time_lenth-113)));
            for(int i =0,k=0;i<113;i++,k++){
                if(k%one_dis==0&&k/one_dis>0&&k/one_dis<=Math.abs(time_lenth-113)){
                    k++;
                }
                for (int j = 0; j < 113; j++){
                    new_arr[i][j] = org_arr[k][j];
                }
            }
        }
        else {
            return org_arr;
        }

        return new_arr;
    }


    public static ByteBuffer getScaledMatrix(Bitmap bitmap, int[] ddims) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3] * 4);
        imgData.order(ByteOrder.nativeOrder());
        // get image pixel
        int[] pixels = new int[ddims[2] * ddims[3]];
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, ddims[2], ddims[3], false);
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, ddims[2], ddims[3]);
        int pixel = 0;
        for (int i = 0; i < ddims[2]; ++i) {
            for (int j = 0; j < ddims[3]; ++j) {
                final int val = pixels[pixel++];
                imgData.putFloat(((((val >> 16) & 0xFF) - 128f) / 128f));
                imgData.putFloat(((((val >> 8) & 0xFF) - 128f) / 128f));
                imgData.putFloat((((val & 0xFF) - 128f) / 128f));
            }
        }

        if (bm.isRecycled()) {
            bm.recycle();
        }
        return imgData;
    }
}
