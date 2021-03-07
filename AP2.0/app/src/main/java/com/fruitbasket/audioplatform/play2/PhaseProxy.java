package com.fruitbasket.audioplatform.play2;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/6/21.
 */

public class PhaseProxy {


    //Speed adjust
    private static float SPEED_ADJ        = (float)1.5;

    public static PhaseProcess stPhaseProcess = new PhaseProcess();
    public static PhaseProcessI ppi ;
    private static float  iDistance = 0;
    private static double  fMaxDis = 0;
    private static double  fMinDis = 0;
    private static String sRecordFile ;
    private static BufferedWriter RecordBufferedWriter ;
    //PhaseProcessThread pthread = new PhaseProcessThread();
    ComputeThread stComputeThread = new ComputeThread();
    public Thread thread;
    public static File fRecordTxtFile ;
    public static File fReadRecordTxtFile ;
    public void init(){
        stPhaseProcess.RangeFinder(GlobalConfig.MAX_FRAME_SIZE , GlobalConfig.NUM_FREQ, GlobalConfig.START_FREQ, GlobalConfig.FREQ_INTERVAL);
        long lTime = 0;
        ppi = new PhaseProcessI(GlobalConfig.MAX_FRAME_SIZE , GlobalConfig.NUM_FREQ, GlobalConfig.START_FREQ, GlobalConfig.FREQ_INTERVAL);

        sRecordFile = GlobalConfig.stWaveFileUtil.getRecordTxtFileName();
        //RecordBufferedWriter = GlobalConfig.stWaveFileUtil.getFileBufferedWriter(sRecordFile);

        fRecordTxtFile = GlobalConfig.stWaveFileUtil.createFile(sRecordFile);
        fReadRecordTxtFile = new File(GlobalConfig.stWaveFileUtil.ReadRecordTxtFileName);
    }

    public void destroy(){
        //stPhaseProcess.destroy();
    }

    class ComputeThread implements  Runnable {
        ComputeThread() {

        }

        public void run() {
            Log.i("play", "run:");
            while (true) {
                while (GlobalConfig.isRecording == false) {
                }

                while (GlobalConfig.isRecording) {
                    //saveRecordDataToFile();
                    //computeByFileData();
                    computeByRecordData();
                    //compareIos();
                }
            }
        }
    }
        public void computeByFileData() {
             readTxtDataToShort(GlobalConfig.stWaveFileUtil.ReadRecordTxtFileName,512);
        }

    public void saveRecordDataToFile() {
        if(GlobalConfig.bRecord  && GlobalConfig.bPlayDataReady) {
            try {
                byte[] recData = GlobalConfig.getInstance().popByteRecData();
                if (recData != null) {
                    long lBeginTime = System.currentTimeMillis();
                    writeByte(recData);
                    writeBytePcm(recData);
                    long lEndTime = System.currentTimeMillis();
                    Log.i("cost", "run begin:" + lBeginTime + "|cost:" + (lEndTime - lBeginTime));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

            public void computeByRecordData(){
                if(GlobalConfig.bRecord  && GlobalConfig.bPlayDataReady) {
                    try {
                        byte[] recData = GlobalConfig.getInstance().popByteRecData();
                        if (recData != null) {
                            long lBeginTime = System.currentTimeMillis();
                            writeByte(recData);
                            writeBytePcm(recData);
                            short[] shortData = WaveFileUtil.byteArray2ShortArray(recData);
                            getDistance(shortData);
                            long lEndTime = System.currentTimeMillis();
                            Log.i("cost", "run begin:" + lBeginTime + "|cost:" + (lEndTime - lBeginTime));
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


    }

    public void compareIos(){
        for(int i=0; i<GlobalConfig.FRAME_NUM; i++)
        {
            getDistance(GlobalConfig.vvIosData[i]);
        }
    }


    public void readTxtDataToShort(String sFileName, int iFrameSize) {
        try {
            File filename = new File(sFileName);
            FileInputStream in = new FileInputStream(sFileName);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            long totalAudioLen = in.getChannel().size();
            short[] dData = new short[iFrameSize];
            short iTmp = 0;
            int i = 0;
            int iCount=0;
            String sDataBuf="";
            while ((line = br.readLine()) != null) {

                if(i>=iFrameSize) {
                    i= 0;
                    long lBeginTime = System.currentTimeMillis();
                    getDistance(dData);
                    long lEndTime = System.currentTimeMillis();
                    Log.e("cost", "GetDistanceChange begin:"+lBeginTime + "|cost:" + (lEndTime - lBeginTime));
                    Log.e("sData", "iCount["+iCount+"],sData:"+sDataBuf );
                    sDataBuf = "";
                }
                String sData = line;
                iTmp = Short.valueOf(sData);
                dData[i] = iTmp;
                sDataBuf += dData[i] + ",";
                iCount++;
                i++;
            }

            System.out.println("readTxtData dataNum is " + String.valueOf(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void writeByte(byte[] recData){
        if (GlobalConfig.bByte && GlobalConfig.bSaveWavFile) {
            int iReadSize = recData.length;
            Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);
            if (iReadSize > 0 && recData != null) {

                if (GlobalConfig.recTxtDos == null) {
                    Log.i("record", "resdos is null");
                    if (fRecordTxtFile != null) {
                        try {
                            GlobalConfig.recTxtDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fRecordTxtFile)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //循环将buffer中的音频数据写入到OutputStream中
                if (GlobalConfig.recTxtDos != null) {
                    for (int i = 0; i < iReadSize; i=i+2) {
                        try {
                            short iData = (short) ((recData[i] & 0xff) | (recData[i+ 1] & 0xff) << 8);
                            String sData = String.valueOf(iData);
                            GlobalConfig.recTxtDos.writeBytes(sData);
                            GlobalConfig.recTxtDos.writeByte('\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void writeBytePcm(byte[] recData){
        if (GlobalConfig.bByte && GlobalConfig.bSaveWavFile) {
            int iReadSize = recData.length;
            Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);
            if (iReadSize > 0 && recData != null) {

                if (GlobalConfig.recDos2 == null) {
                    Log.i("record", "resdos is null");
                    if (GlobalConfig.fPcmRecordFile2 != null) {
                        try {
                            GlobalConfig.recDos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(GlobalConfig.fPcmRecordFile2)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //循环将buffer中的音频数据写入到OutputStream中
                if (GlobalConfig.recDos2 != null) {
                    for (int i = 0; i < iReadSize; i++) {
                        try {
                            GlobalConfig.recDos2.writeByte(recData[i]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void start(){
        if(GlobalConfig.bDataProcessThreadFlag) {
            thread = new Thread(stComputeThread);
            thread.start();
        }

    }



    public float getDistance(short[] recordData){

        float     distancechange = 0;
        long lBeginTime = System.currentTimeMillis();
        //distancechange = stPhaseProcess.GetDistanceChange(recordData);
         Log.e("Jni", ppi.getJniString());
        distancechange            = ppi.getDistanceChange(ppi.nativePerson, recordData, recordData.length);
        float[] iqDatas = ppi.getBaseBand(ppi.nativePerson, GlobalConfig.NUM_FREQ);
        int iIQFrameSize = 32;
        int iIQRealImageSize = 64;
        float[] real = new float[iIQFrameSize];
        float[] image = new float[iIQFrameSize];
        for(int i = 0;i < iqDatas.length;i++) {
            int iFreqNum = i/64;
            int iIndex = i % 64;
            if( iIndex == 0&& iFreqNum> 0)
            {

                Log.i("bobo", "Line" + String.valueOf(iFreqNum) + " ");
                for(int j=0; j<iIQFrameSize; j++)
                {
                    String sBuf = "IQ["+ iFreqNum+"]["+ j + "]," + real[j] + ","+image[j];
                    //if( i == 0){
                    //Log.i("distance", sBuf);
                    // }
                    //GlobalConfig.stWaveFileUtil.writeTxtData(vBufferedWriter[i], sBuf);
                    GlobalConfig.stWaveFileUtil.writeToTxtFileFast(GlobalConfig.stWaveFileUtil.vIQTxtFile[iFreqNum-1],GlobalConfig.stWaveFileUtil.vIQDos[iFreqNum-1],sBuf);
                }

            }
            Log.i("bobo", "IQ" + String.valueOf(i % 64) + "=" + String.valueOf(iqDatas[i]));
            if(iIndex < 32){
                real[iIndex] = iqDatas[i];
            }
            else if(iIndex  >= 32){
                int index = iIndex - 32;
                image[index] = iqDatas[i];
            }
        }
        int iFreqNum = iqDatas.length/64;
        for(int j=0; j<iIQFrameSize; j++)
        {
            String sBuf = "IQ["+ iFreqNum+"]["+ j + "]," + real[j] + ","+image[j];
            //if( i == 0){
            //Log.i("distance", sBuf);
            // }
            //GlobalConfig.stWaveFileUtil.writeTxtData(vBufferedWriter[i], sBuf);
            GlobalConfig.stWaveFileUtil.writeToTxtFileFast(
                    GlobalConfig.stWaveFileUtil.vIQTxtFile[iFreqNum-1],
                    GlobalConfig.stWaveFileUtil.vIQDos[iFreqNum-1],
                    sBuf
            );
        }

        iDistance = iDistance + distancechange*SPEED_ADJ;
        if(iDistance < 0)
        {
            iDistance = 0;
        }
        else if(iDistance > 500)
        {
            iDistance = 500;
        }

        double x;
        x= iDistance*(iDistance+100)/100*5;
        long lEndTime = System.currentTimeMillis();
        //Log.i("cost", "run begin:"+lBeginTime + "cost:" + (lEndTime - lBeginTime) );

        Log.i("phasex", "distancechange:" +distancechange +"|iDistance:" + iDistance +" | x:" +x  + "|cost:" +  (lEndTime - lBeginTime));

       /* fMaxDis = Math.max((double) x, fMaxDis);
        fMinDis = Math.max((double) x, fMinDis);*/

        Log.i("distancex", " | x:" +x );



        //Log.i("play","sendMessage");

        return distancechange;
    }

    public   short[] GetPlayBuffer(){
        return stPhaseProcess.GetPlayBuffer();
    }




}
