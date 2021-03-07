package com.fruitbasket.audioplatform.play2;

import  java.lang.Math;
/**
 * Created by Administrator on 2017/6/20.
 */

public class MatrixProcess {


    public static  int vDSP_zvmags(float[] real,
                               float[] image,
                               float[] result,
                               int iLen)
    {
        for (int i= 0; i< iLen; i++)
        {
            result[i] = real[i]*real[i] + image[i]*image[i];
        }
        return 0;
    }

    public static int vDSP_zvphas(float[] real,
                                   float[] image,
								   float[] result,
                                   int iLen)
    {

        for (int i= 0; i< iLen; i++)
        {
            result[i] = (float) Math.atan2((double)image[i],(double) real[i]);
        }


        return 0;
    }


    public static int vDSP_vsadd(float[] phasedata,
								   float temp_val,
								   float[] result,
                                   int iLen)
    {

        for (int i= 0; i< iLen; i++)
        {
            result[i] = phasedata[i] + temp_val;
        }
        return 0;
    }

    public static int  vDSP_vsdiv(float [] A, float B, float[] result,int iLen)
    {

        for (int i= 0; i< iLen; i++)
        {
            result[i] = A[i]/B;
        }
        return 0;
    }

    public static int  vDSP_vsub(float [] A, float[] B, float[] result, int iLen)
    {

        for (int i= 0; i< iLen; i++)
        {
            result[i] = A[i] - B[i];
        }
        return 0;
    }

    public static int vDSP_vsq(float[] A, float[] result,int iLen)
	{
        for (int i= 0; i< iLen; i++)
        {
            result[i] = A[i]*A[i];
        }
        return 0;
    }

    public static int  vDSP_vswsum(float[] A, int srcIndex, float[] result, int iDestIndex, int iNum, int iDelay)
    {
        for (int i= srcIndex; i< iNum; i++)
        {
            float fResult = 0;
            for(int j=0; j<iDelay; j++)
            {
                fResult += A[i + j];
            }
            result[i + iDestIndex] = fResult;
        }
        return 0;
    }

    public static float  vDSP_sve(float[] A, int iLen)
    {
        float result = 0;
        for (int i= 0; i< iLen; i++)
        {
            result += A[i] ;
        }
        return result;
    }

    public static int vDSP_vmul(float [] A, float[] B, float[] result, int iLen)
    {
        for (int i= 0; i< iLen; i++)
        {
            result[i] = A[i]*B[i] ;
        }
        return 0;
    }
	
    public static float  vDSP_maxv(float [] A, int iLen)
    {
        float result = A[0];
        for (int i= 0; i< iLen; i++)
        {
            if (result < A[i])
            {
                result = A[i];
            }
        }

        return result;
    }
    public static short  max(short [] A, int iLen)
    {
        short result = A[0];
        for (int i= 0; i< iLen; i++)
        {
            if (result < A[i])
            {
                result = A[i];
            }
        }

        return result;
    }
    public static byte  max(byte [] A, int iLen)
    {
        byte result = A[0];
        for (int i= 0; i< iLen; i++)
        {
            if (result < A[i])
            {
                result = A[i];
            }
        }

        return result;
    }



    public static float vDSP_minv(float [] A, int iLen)
    {
        float result = A[0];
        for (int i= 0; i< iLen; i++)
        {
            if (A[i]< result)
            {
                result = A[i];
            }
        }

        return result;
    }

    public static int  moveArrayElem(float[] src, int k, float[] result)
    {
        int length = src.length;
        int newk = k % length;
        //float[] result = new float[length];
        for(int i = 0; i < length; i++) {
            int newPosition = (i + newk) % length;
            result[newPosition] = src[i];
        }

        return 0;
    }

    public static void memmove(float[] dest, float[] src,  int length)
    {
        //float[] newArray = new float[length];
        System.arraycopy(src, 0, dest, 0, src.length);
        //return newArray;
    }
	
    public static void memmoveByte(byte[] dest, byte[] src,  int length)
    {
        //float[] newArray = new float[length];
        System.arraycopy(src, 0, dest, 0, length);
        //return newArray;
    }

}
