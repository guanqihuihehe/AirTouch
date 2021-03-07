package com.fruitbasket.audioplatform.play2;

import android.util.Log;

/**
 * Created by Administrator on 2017/6/19.
 */

public class PhaseProcess {
     //max number of frequency
    private int MAX_NUM_FREQS = 16;
    //pi
    private double PI = 3.1415926535;
    //audio sample rate
    private int AUDIO_SAMPLE_RATE =  GlobalConfig.AUDIO_SAMPLE_RATE; //should be the same as in controller, will add later
    //default temperature
    private int TEMPERATURE  =  20;
    //volume
    private double VOLUME = 0.2;
    //cic filter stages
    private int CIC_SEC  = 4;
    //cic filter decimation
    private int CIC_DEC  = 16;
    //cic filter delay
    private int CIC_DELAY  = 17;
    //socket buffer length
    private int SOCKETBUFLEN = 40960;
    //power threshold
    private int  POWER_THR  = 15000;
    //peak threshold
    private int PEAK_THR  = 220;
    //dc_trend threshold
    private double DC_TREND = 0.25;
    private int numFreqs;//

    private int iFrame = 0;


    private int     mNumFreqs;//number of frequency
    private int           mCurPlayPos = 0;//current play position
    private int           mCurProcPos;//current process position
    private int           mCurRecPos;//current receive position
    private int           mLastCICPos;//last cic filter position
    private int           mBufferSize;//buffer size
    private int           mRecDataSize;//receive data size
    private int           mDecsize;//buffer size after decimation
    private float          mFreqInterv;//frequency interval
    private float          mSoundSpeed;//sound speed
    private float[]          mFreqs= new float[MAX_NUM_FREQS];//frequency of the ultsound signal
    private float[]          mWaveLength = new float[MAX_NUM_FREQS];//wave length of the ultsound signal
    //ptr begin
    private short[]        mPlayBuffer;
    private short[]        mRecDataBuffer;
    private float []        mFRecDataBuffer;
    private float [][]        mSinBuffer = new float[MAX_NUM_FREQS][];
    private float [][]        mCosBuffer = new float[MAX_NUM_FREQS][];
    private float [][]        mBaseBandReal = new float[MAX_NUM_FREQS][];
    private float [][]        mBaseBandImage = new float[MAX_NUM_FREQS][];
    private float []        mTempBuffer;
    private float[][][][]     mCICBuffer = new float[MAX_NUM_FREQS][CIC_SEC][2][];
    //ptr end
    private byte[]          mSocketBuffer = new byte[SOCKETBUFLEN];
    private float[][]       mDCValue = new float[2][MAX_NUM_FREQS];
    private float[][]       mMaxValue = new float[2][MAX_NUM_FREQS];
    private float[][]       mMinValue = new float[2][MAX_NUM_FREQS];
    private float[]         mFreqPower = new float[MAX_NUM_FREQS];
    public  int             mSocBufPos;

    public static MatrixProcess stMatrixProcess = new MatrixProcess();
    public static WaveFileUtil stWaveFileUtil = new WaveFileUtil();

    public static long  lBeginTime = 0;
    public static long  lEndTime = 0;


    private short[]        mSquareBuffer;

    PhaseProcess(){

    }

    public void RangeFinder( int inMaxFramesPerSlice , int inNumFreq, float inStartFreq, float inFreqInterv )
    {
        //Number of frequency
        mNumFreqs = inNumFreq;
        //Buffer size
        mBufferSize = inMaxFramesPerSlice;
        //Frequency interval
        mFreqInterv = inFreqInterv;
        //Receive data size
        mRecDataSize = 4*inMaxFramesPerSlice;
        //Sound speed
        mSoundSpeed = (float)(331.3 + 0.606 * TEMPERATURE);
        //Init buffer
        for(int i=0; i<MAX_NUM_FREQS; i++){
            mSinBuffer[i]=new float[2*inMaxFramesPerSlice];
            mCosBuffer[i]=new float[2*inMaxFramesPerSlice];

            mFreqs[i]=inStartFreq+i*inFreqInterv;

            mWaveLength[i]=mSoundSpeed/mFreqs[i]*1000; //all distance is in mm

            mBaseBandReal[i]= new float[mRecDataSize/CIC_DEC];
            mBaseBandImage[i]= new float[mRecDataSize/CIC_DEC];
            for(int k=0;k<CIC_SEC;k++)
            {
                mCICBuffer[i][k][0]=new float[mRecDataSize/CIC_DEC+CIC_DELAY];
                mCICBuffer[i][k][1]=new float[mRecDataSize/CIC_DEC+CIC_DELAY];
            }
        }

        mPlayBuffer = new short[2*inMaxFramesPerSlice];

        mRecDataBuffer = new short[mRecDataSize];
        mFRecDataBuffer = new float[mRecDataSize];
        mTempBuffer = new float[mRecDataSize];
        mCurPlayPos = 0;
        mCurRecPos = 0;
        mCurProcPos= 0;
        mLastCICPos =0;
        mDecsize=0;
        mSocBufPos=0;

        InitBuffer();

    }


    void InitBuffer()
    {
        for(int i=0; i<mNumFreqs; i++){
            for(int n=0;n<mBufferSize*2;n++){
                mCosBuffer[i][n]=(float) Math.cos(2*PI*n/AUDIO_SAMPLE_RATE*mFreqs[i]);
                mSinBuffer[i][n]=(float)-Math.sin(2*PI*n/AUDIO_SAMPLE_RATE*mFreqs[i]);
            }
            mDCValue[0][i]=0;
            mMaxValue[0][i]=0;
            mMinValue[0][i]=0;
            mDCValue[1][i]=0;
            mMaxValue[1][i]=0;
            mMinValue[1][i]=0;
        }

        float mTempSample;
        for(int n=0;n<mBufferSize*2;n++){
            mTempSample=0;
            for(int i=0; i<mNumFreqs; i++){
                mTempSample+=mCosBuffer[i][n]*VOLUME;
            }
            mPlayBuffer[n]=(short) (mTempSample/mNumFreqs*32767);
        }
        //mSquareBuffer = new short[mBufferSize*2];
        //createSquare(mBufferSize*2,mSquareBuffer);
        //Log.i("phase", "mPlayBuffer:"+mPlayBuffer[0]+ "|" + mPlayBuffer[1] + "|" + mPlayBuffer[2]);
        GlobalConfig.bPlayDataReady = true;
        GlobalConfig.stPhaseProxy.start();
    }



        /*
        for (int i=0;i<mNumFreqs; i++)
        {
            if(mSinBuffer[i]!=NULL)
            {
                free(mSinBuffer[i]);
                mSinBuffer[i]=NULL;
            }
            if(mCosBuffer[i]!=NULL)
            {
                free(mCosBuffer[i]);
                mCosBuffer[i]=NULL;
            }
            if(mBaseBandReal[i]!=NULL)
            {
                free(mBaseBandReal[i]);
                mBaseBandReal[i]=NULL;
            }
            if(mBaseBandImage[i]!=NULL)
            {
                free(mBaseBandImage[i]);
                mBaseBandImage[i]=NULL;
            }
            for(int k=0;k<CIC_SEC;k++)
            {
                if(mCICBuffer[i][k][0]!=NULL)
                {
                    free(mCICBuffer[i][k][0]);
                    mCICBuffer[i][k][0]=NULL;
                }
                if(mCICBuffer[i][k][1]!=NULL)
                {
                    free(mCICBuffer[i][k][1]);
                    mCICBuffer[i][k][1]=NULL;
                }
            }
        }
        if(mPlayBuffer!=NULL)
        {
            free(mPlayBuffer);
            mPlayBuffer= NULL;
        }
        if(mTempBuffer!=NULL)
        {
            free(mTempBuffer);
            mTempBuffer= NULL;
        }

        if(mRecDataBuffer!=NULL)
        {
            free(mRecDataBuffer);
            mRecDataBuffer= NULL;
        }
        if(mFRecDataBuffer!=NULL)
        {
            free(mFRecDataBuffer);
            mFRecDataBuffer= NULL;
        }
*/

     /* Gets the subarray from <tt>array</tt> that starts at <tt>offset</tt>.
            */
    public static short[] getsub(short[] array, int offset) {
        return get(array, offset, array.length - offset);
    }

    /**
     * Gets the subarray of length <tt>length</tt> from <tt>array</tt>
     * that starts at <tt>offset</tt>.
     */
    public static short[] get(short[] array, int offset, int length) {
        short[] result = new short[length];
        System.arraycopy(array, offset, result, 0, length);
        return result;
    }

    public static float[] getfloatSub(float[] array, int offset) {
        return getfloat(array, offset, array.length - offset);
    }

    /**
     * Gets the subarray of length <tt>length</tt> from <tt>array</tt>
     * that starts at <tt>offset</tt>.
     */
    public static float[] getfloat(float[] array, int offset, int length) {
        float[] result = new float[length];
        System.arraycopy(array, offset, result, 0, length);
        return result;
    }

    public static byte[] getbytesub(byte[] array, int offset) {
        return getbyte(array, offset, array.length - offset);
    }

    /**
     * Gets the subarray of length <tt>length</tt> from <tt>array</tt>
     * that starts at <tt>offset</tt>.
     */
    public static byte[] getbyte(byte[] array, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(array, offset, result, 0, length);
        return result;
    }

    public short[] GetPlayBuffer(){
        //return mSquareBuffer;
        //return mPlayBuffer;

        return mPlayBuffer;
    }




    public short[] GetPlayBuffer(int inSamples)
    {
        short[] playDataPointer = new short[inSamples];

        System.arraycopy(mPlayBuffer, mCurPlayPos, playDataPointer, 0, inSamples);

        mCurPlayPos =  mCurPlayPos + inSamples;
        if(mCurPlayPos >=mBufferSize)
            mCurPlayPos =mCurPlayPos -mBufferSize;

        return playDataPointer;
    }

    byte[] GetSocketBuffer()
    {
        return mSocketBuffer;
    }

    void AdvanceSocketBuffer(long length)
    {
        if(length>0)
        {
            if(length>=mSocBufPos)
            {
                mSocBufPos=0;
                return;
            }
            else
            {   mSocBufPos= mSocBufPos-(int) length;
                //translate
                stMatrixProcess.memmoveByte(mSocketBuffer,getbytesub(mSocketBuffer,(int)length),mSocBufPos);
                return;
            }
        }

    }


    void GetRecDataBuffer(short[] recordData, int inSamples)
    {
        int iOldPos = mCurRecPos;
        int iTmpPos =  mCurRecPos + inSamples;
        if(iTmpPos >= mRecDataSize) //over flowed RecBuffer
        {
            mCurRecPos=0;
            iOldPos = 0;
        }
        else
        {
            mCurRecPos += inSamples;
        }
        System.arraycopy(recordData, 0, mRecDataBuffer, iOldPos, inSamples);
        //Log.i("phase", "rec:" + mRecDataBuffer[iOldPos+1] + "|" + mRecDataBuffer[iOldPos+2] + "|" +  mRecDataBuffer[iOldPos+3] + "|" +  mRecDataBuffer[iOldPos+4]);
    }


    public float GetDistanceChange(short[] recordData)
    {
        float distancechange=0;
        lBeginTime = System.currentTimeMillis();

        //each time we process the data in the RecDataBuffer and clear the mCurRecPos
        GetRecDataBuffer(recordData,recordData.length);
        //Get base band signal
        GetBaseBand();
        //saveIQData();

        //Remove dcvalue from the baseband signal
        RemoveDC();

        //Send baseband singal via socket
        //SendSocketData();

        //Calculate distance from the phase change
        distancechange=CalculateDistance();
        //Log.i("phasedis", "dis:" + distancechange);
        lEndTime = System.currentTimeMillis();
        //Log.e("cost", "GetDistanceChange begin:"+lBeginTime + "|cost:" + (lEndTime - lBeginTime));
        return distancechange;
    }

    float CalculateDistance()
    {
        float distance=0;
        // translate
        // DSPSplitComplex tempcomplex;
        float[] tempdata = new float[4096];
        float[] tempdata2 = new float[4096];
        float[] tempdata3 = new float[4096];
        float temp_val = 0;
        float[][]   phasedata = new float[MAX_NUM_FREQS][4096];
        int[]     ignorefreq = new int[MAX_NUM_FREQS];


        if(mDecsize>4096)
            return 0;

        for(int f=0;f<mNumFreqs;f++)
        {
            ignorefreq[f]=0;
            //get complex number
            //tempcomplex.realp=mBaseBandReal[f];
            //tempcomplex.imagp=mBaseBandImage[f];

            //get magnitude
            stMatrixProcess.vDSP_zvmags(mBaseBandReal[f],mBaseBandImage[f],tempdata,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata,mDecsize);
            if(temp_val/mDecsize>POWER_THR) //only calculate the high power vectors
            {
                stMatrixProcess.vDSP_zvphas(mBaseBandReal[f],mBaseBandImage[f],phasedata[f],mDecsize);
                //for(int jj=0; jj<mDecsize; jj++) {
                    //Log.i("mFreqPower","=====ooooooo===phasedata[:"+f+"]["+jj+"]"+phasedata[f][jj]);
                //}

                //phase unwarp
                for(int i=1;i<mDecsize;i++)
                {
                    while(phasedata[f][i]-phasedata[f][i-1]>PI)
                        phasedata[f][i]=(float)(phasedata[f][i]-2*PI);
                    while(phasedata[f][i]-phasedata[f][i-1]<-PI)
                        phasedata[f][i]=(float)(phasedata[f][i]+2*PI);
                }

                //Log.i("mFreqPower","=====ooooooo===phasedata delta:"+(phasedata[f][mDecsize-1]-phasedata[f][0]));
                if(Math.abs(phasedata[f][mDecsize-1]-phasedata[f][0])>PI/4)
                {
                    for(int i=0;i<=1;i++) {
                        mDCValue[i][f] =(float)((1 - DC_TREND * 2) * mDCValue[i][f] +
                                (mMinValue[i][f] + mMinValue[i][f]) / 2 * DC_TREND * 2);
                    }
                }

                //prepare linear regression
                //remove start phase
                temp_val=-phasedata[f][0];
                stMatrixProcess.vDSP_vsadd(phasedata[f],temp_val,tempdata,mDecsize);
                //divide the constants
                temp_val=(float)(2*PI/mWaveLength[f]);
                stMatrixProcess.vDSP_vsdiv(tempdata,temp_val,phasedata[f],mDecsize);

                 //Log.i("mFreqPower","=====phasedata[:"+f+"][0]"+phasedata[f][0] + " phasedata["+f+"][31]:"+phasedata[f][31]);


            }
            else //ignore the low power vectors
            {
                ignorefreq[f]=1;
            }

        }

        //linear regression
        for(int i=0;i<mDecsize;i++)
            tempdata2[i]=i;
        float sumxy=0;
        float sumy=0;
        int     numfreqused=0;
        for(int f=0;f<mNumFreqs;f++)
        {
            if(ignorefreq[f] > 0)
            {
                continue;
            }

            numfreqused++;

            stMatrixProcess.vDSP_vmul(phasedata[f],tempdata2,tempdata,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata,mDecsize);
            sumxy+=temp_val;
            temp_val = stMatrixProcess.vDSP_sve(phasedata[f],mDecsize);
            sumy+=temp_val;

        }
        if(numfreqused==0)
        {
            distance=0;
            return distance;
        }

        float deltax=deltax=mNumFreqs*((mDecsize-1)*mDecsize*(2*mDecsize-1)/6-(mDecsize-1)*mDecsize*(mDecsize-1)/4);
        float delta=(float)(sumxy-sumy*(mDecsize-1)/2.0)/deltax*mNumFreqs/numfreqused;

        float varsum=0;
        float [] var_val = new float[MAX_NUM_FREQS];
        for(int i=0;i<mDecsize;i++)
            tempdata2[i]=i*delta;

        //get variance of each freq;
        for(int f=0;f<mNumFreqs;f++)
        {
            var_val[f]=0;
            if(ignorefreq[f] > 0)
            {
                continue;
            }
            stMatrixProcess.vDSP_vsub(tempdata2,phasedata[f],tempdata,mDecsize);
            stMatrixProcess.vDSP_vsq(tempdata,tempdata3,mDecsize);

            var_val[f] = stMatrixProcess.vDSP_sve(tempdata3,mDecsize);
            varsum+=var_val[f];
        }
        varsum=varsum/numfreqused;
        for(int f=0;f<mNumFreqs;f++)
        {
            if(ignorefreq[f] >0)
            {
                continue;
            }
            if(var_val[f]>varsum)
                ignorefreq[f]=1;
        }

        //linear regression
        for(int i=0;i<mDecsize;i++)
            tempdata2[i]=i;

        sumxy=0;
        sumy=0;
        numfreqused=0;
        for(int f=0;f<mNumFreqs;f++)
        {
            if(ignorefreq[f] > 0)
            {
                continue;
            }
            numfreqused++;
            stMatrixProcess.vDSP_vmul(phasedata[f],tempdata2,tempdata,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata,mDecsize);
            sumxy+=temp_val;
            temp_val = stMatrixProcess.vDSP_sve(phasedata[f],mDecsize);
            sumy+=temp_val;

        }
        if(numfreqused==0)
        {
            distance=0;
            return distance;
        }

        delta=(float)(sumxy-sumy*(mDecsize-1)/2.0)/deltax*mNumFreqs/numfreqused;

        distance=-delta*mDecsize/2;
        Log.i("distance","numfreqused:"+numfreqused+"distancd:"+distance);
        return distance;
    }



    void RemoveDC()
    {
        int f,i;
        float[] tempdata = new float[4096];
        float[] tempdata2 = new float[4096];
        float temp_val = 0;
        float vsum = 0;
        float dsum = 0;
        float max_valr = 0;
        float min_valr = 0;
        float max_vali = 0;
        float min_vali = 0;
        if(mDecsize>4096)
            return;


        //'Levd' algorithm to calculate the DC value;
        for(f=0;f<mNumFreqs;f++)
        {
            vsum=0;
            dsum=0;
            //real part
            max_valr = stMatrixProcess.vDSP_maxv(mBaseBandReal[f],mDecsize);
            min_valr = stMatrixProcess.vDSP_minv(mBaseBandReal[f],mDecsize);
            //getvariance,first remove the first value
            temp_val=-mBaseBandReal[f][0];
            stMatrixProcess.vDSP_vsadd(mBaseBandReal[f],temp_val,tempdata,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata,mDecsize);
            dsum=dsum+Math.abs(temp_val)/mDecsize;
            stMatrixProcess.vDSP_vsq(tempdata,tempdata2,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata2,mDecsize);
            vsum=vsum+Math.abs(temp_val)/mDecsize;

            //imag part
            max_vali = stMatrixProcess.vDSP_maxv(mBaseBandImage[f],mDecsize);
            min_vali = stMatrixProcess.vDSP_minv(mBaseBandImage[f],mDecsize);
            //getvariance,first remove the first value
            temp_val=-mBaseBandImage[f][0];
            stMatrixProcess.vDSP_vsadd(mBaseBandImage[f],temp_val,tempdata,mDecsize);
            temp_val = stMatrixProcess.vDSP_sve(tempdata,mDecsize);
            dsum=dsum+Math.abs(temp_val)/mDecsize;
            stMatrixProcess.vDSP_vsq(tempdata,tempdata2,mDecsize );
            temp_val = stMatrixProcess.vDSP_sve(tempdata2,mDecsize);
            vsum=vsum+Math.abs(temp_val)/mDecsize;

            mFreqPower[f]=(vsum+dsum*dsum);///Math.abs(vsum-dsum*dsum)*vsum;
            //Log.i("mFreqPower","mFreqPower["+f+"]:"+mFreqPower[f]);
            //Log.i("mFreqPower","======max_valr:"+max_valr + "========min_valr:"+min_valr);
            //Log.i("mFreqPower","======max_vali:"+max_vali + "========min_vali:"+min_vali);
            //Get DC estimation
            if(mFreqPower[f]>POWER_THR)
            {
                if ( max_valr > mMaxValue[0][f] ||
                        (max_valr > mMinValue[0][f]+PEAK_THR &&
                                (mMaxValue[0][f]-mMinValue[0][f]) > PEAK_THR*4) )
                {
                    mMaxValue[0][f]=max_valr;
                }

                if ( min_valr < mMinValue[0][f] ||
                        (min_valr < mMaxValue[0][f]-PEAK_THR &&
                                (mMaxValue[0][f]-mMinValue[0][f]) > PEAK_THR*4) )
                {
                    mMinValue[0][f]=min_valr;
                }

                if ( max_vali > mMaxValue[1][f] ||
                        (max_vali > mMinValue[1][f]+PEAK_THR &&
                                (mMaxValue[1][f]-mMinValue[1][f]) > PEAK_THR*4) )
                {
                    mMaxValue[1][f]=max_vali;
                }

                if ( min_vali < mMinValue[1][f] ||
                        (min_vali < mMaxValue[1][f]-PEAK_THR &&
                                (mMaxValue[1][f]-mMinValue[1][f]) > PEAK_THR*4) )
                {
                    mMinValue[1][f]=min_vali;
                }


                if ( (mMaxValue[0][f]-mMinValue[0][f]) > PEAK_THR &&
                        (mMaxValue[1][f]-mMinValue[1][f]) > PEAK_THR )
                {
                    for(i=0;i<=1;i++){
                        mDCValue[i][f]=(float)((1-DC_TREND)*mDCValue[i][f]+
                                (mMaxValue[i][f]+mMinValue[i][f])/2*DC_TREND);
                    }

                }
               // Log.i("mFreqPower","======rr mMaxValue :"+mMaxValue[0][f] + "========min_valr:"+mMinValue[0][f]);
               // Log.i("mFreqPower","======ii mMaxValue:"+mMaxValue[1][f] + "========min_valr:"+mMinValue[1][f]);

            }

            //remove DC
            for(i=0;i<mDecsize;i++)
            {
                mBaseBandReal[f][i]=mBaseBandReal[f][i]-mDCValue[0][f];
                mBaseBandImage[f][i]=mBaseBandImage[f][i]-mDCValue[1][f];
            }

        }
/*
        for(int ii=0; ii<8;ii++) {
            for (int jj = 0; jj < 32; jj++) {
                String sBuf = "remove dc IQ["+ ii+"]["+ jj + "]," + mBaseBandReal[ii][jj] + "," + mBaseBandImage[ii][jj];
                Log.i("distance", sBuf);

                //GlobalConfig.stWaveFileUtil.writeTxtData(vBufferedWriter[i], sBuf);
                //GlobalConfig.stWaveFileUtil.writeToTxtFileFast(vIQTxtFile[i],vIQDos[i],sBuf);
            }
        }
        */
    }

    void SendSocketData()
    { int   i,index;

        //send baseband to matlab
        {
            index=mSocBufPos;
            for(i=0;i<16; i++) //number of frequencies
            {
                for(int k=0;k<mDecsize;k++) //iterate through samples
                {
                    if(index<SOCKETBUFLEN-4) //ensure enough buffer
                    {
                        mSocketBuffer[index++]=(byte) (((short) mBaseBandReal[i][k]) &0xFF);
                        mSocketBuffer[index++]=(byte) (((short) mBaseBandReal[i][k]) >> 8 );
                        mSocketBuffer[index++]=(byte) (((short) mBaseBandImage[i][k]) &0xFF);
                        mSocketBuffer[index++]=(byte) (((short) mBaseBandImage[i][k]) >> 8 );
                    }
                }

            }
            mSocBufPos=index-1;
        }
    }

    void saveIQData()
    {
        int   i,index;
        //Log.i("mFreqPower","==================iframe:"+iFrame+"======================");
        //send baseband to matlab
        {
            double[] dmBaseBandReal = new double[mDecsize*8];
            for(i=0;i<8; i++) //number of frequencies
            {
                for(int k=0;k<mDecsize;k++) //iterate through samples
                {
                    dmBaseBandReal[i*mDecsize +k] = mBaseBandReal[i][k];
                }
            }
            //stWaveFileUtil.writeTxtData(stWaveFileUtil.getBaseBandRealFileName(), dmBaseBandReal);
        }

        {
            double[] dmBaseBandImage = new double[mDecsize*8];
            for(i=0;i<8; i++) //number of frequencies
            {
                for(int k=0;k<mDecsize;k++) //iterate through samples
                {
                    dmBaseBandImage[i*mDecsize +k] = mBaseBandImage[i][k];
                }
            }
            //stWaveFileUtil.writeTxtData(stWaveFileUtil.getBaseBandImageFileName(), dmBaseBandImage);
        }
        iFrame++;

    }

    void GetBaseBand()
    {

        ////////////FOR DEBUG//////////////////////
        //mCurRecPos=512;
        //mCurProcPos=512;
        //mLastCICPos=32;
        //////////////////////////////////////////
        int i,index,decsize,cid;
        decsize=mCurRecPos/CIC_DEC;
        mDecsize=decsize;
        //Log.i("mFreqPower", "GetBaseBand");
        //stWaveFileUtil.saveDataToWav(mRecDataBuffer,stWaveFileUtil.getAndroidRecordFileName(),(long)(AUDIO_SAMPLE_RATE),1,mCurRecPos*2,(short)16);
        //change data from int to float
        //String  sFileName1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/phase/rec_512_2.txt";
        //short[] tmpRecData = new short[mCurRecPos];
        //WaveFileUtil stWaveFileUtil1 = new WaveFileUtil();
        //stWaveFileUtil1.readTxtDataToShort(sFileName1,tmpRecData);
        //System.arraycopy(tmpRecData, 0, mRecDataBuffer, 0, mCurRecPos);
        double [] dRecData = new double[mCurRecPos];

        for(i=0;i<mCurRecPos; i++)
        {
            mFRecDataBuffer[i]= (float) (mRecDataBuffer[i]/32767.0);

            //dRecData[i] = (double)mRecDataBuffer[i];
        }
       // stWaveFileUtil.writeTxtData(stWaveFileUtil.getRecordFileName(), dRecData);
        for(i=0;i<mNumFreqs; i++)//mNumFreqs
        {
            //cos
            stMatrixProcess.vDSP_vmul(mFRecDataBuffer,getfloatSub(mCosBuffer[i],mCurProcPos),mTempBuffer,mCurRecPos); //multiply the cos
            cid=0;
            //sum CIC_DEC points of data, put into CICbuffer
            //void * memmove(void *dest, const void *src, size_t num);
            //arraycopy src srcpos dst dstpos
            System.arraycopy(mCICBuffer[i][0][cid], mLastCICPos, mCICBuffer[i][0][cid], 0, CIC_DELAY);
            index=CIC_DELAY;
            for(int k=0;k<mCurRecPos;k+=CIC_DEC)
            {
                mCICBuffer[i][0][cid][index] = stMatrixProcess.vDSP_sve(getfloatSub(mTempBuffer,k),CIC_DEC);
                index++;
            }

            //prepare CIC first level
            System.arraycopy(mCICBuffer[i][1][cid], mLastCICPos, mCICBuffer[i][1][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][0][cid],0,mCICBuffer[i][1][cid],CIC_DELAY,decsize,CIC_DELAY);


            //prepare CIC second level
            System.arraycopy(mCICBuffer[i][2][cid], mLastCICPos, mCICBuffer[i][2][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][1][cid],0,mCICBuffer[i][2][cid],CIC_DELAY,decsize,CIC_DELAY);

            //prepare CIC third level
             System.arraycopy(mCICBuffer[i][3][cid], mLastCICPos, mCICBuffer[i][3][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][2][cid],0,mCICBuffer[i][3][cid],CIC_DELAY,decsize,CIC_DELAY);
            //CIC last level to Baseband
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][3][cid],0,mBaseBandReal[i],0,decsize,CIC_DELAY);

            //sin
            stMatrixProcess.vDSP_vmul(mFRecDataBuffer,getfloatSub(mSinBuffer[i],mCurProcPos),mTempBuffer,mCurRecPos); //multiply the sin
            cid=1;
            //sum CIC_DEC points of data, put into CICbuffer
            System.arraycopy(mCICBuffer[i][0][cid], mLastCICPos, mCICBuffer[i][0][cid], 0, CIC_DELAY);
            index=CIC_DELAY;
            for(int k=0;k<mCurRecPos;k+=CIC_DEC)
            {
                mCICBuffer[i][0][cid][index] = stMatrixProcess.vDSP_sve(getfloatSub(mTempBuffer,k),CIC_DEC);
                index++;
            }

            //prepare CIC first level
            System.arraycopy(mCICBuffer[i][1][cid], mLastCICPos, mCICBuffer[i][1][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][0][cid],0, mCICBuffer[i][1][cid],CIC_DELAY, decsize,CIC_DELAY);
            //prepare CIC second level
            System.arraycopy(mCICBuffer[i][2][cid], mLastCICPos, mCICBuffer[i][2][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][1][cid],0, mCICBuffer[i][2][cid],CIC_DELAY, decsize,CIC_DELAY);
            //prepare CIC third level
            System.arraycopy(mCICBuffer[i][3][cid], mLastCICPos, mCICBuffer[i][3][cid], 0, CIC_DELAY);
            //Sliding window sum
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][2][cid],0, mCICBuffer[i][3][cid],CIC_DELAY, decsize,CIC_DELAY);
            //CIC last level to Baseband
            stMatrixProcess.vDSP_vswsum(mCICBuffer[i][3][cid],0,mBaseBandImage[i],0,decsize,CIC_DELAY);

            /*for(int jj=0; jj<32;jj++){
                String sBuf = "IQ["+ i+"]["+ jj + "]," + mBaseBandReal[i][jj] + ","+mBaseBandImage[i][jj];
                //if( i == 0){
                    //Log.i("distance", sBuf);
               // }
                //GlobalConfig.stWaveFileUtil.writeTxtData(vBufferedWriter[i], sBuf);
                GlobalConfig.stWaveFileUtil.writeToTxtFileFast(vIQTxtFile[i],vIQDos[i],sBuf);
            }*/


        }

        mCurProcPos=mCurProcPos+mCurRecPos;
        if(mCurProcPos >= mBufferSize)
            mCurProcPos= mCurProcPos - mBufferSize;
        mLastCICPos=decsize;
        mCurRecPos=0;
    }


}
