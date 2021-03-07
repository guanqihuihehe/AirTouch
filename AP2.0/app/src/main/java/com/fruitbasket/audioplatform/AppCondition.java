package com.fruitbasket.audioplatform;

import android.os.Environment;
import android.util.Log;

import java.io.File;

final public class AppCondition {
	private static final String TAG=".AppCondition";
	private static final AppCondition APP_CONDITION =new AppCondition();
	/*
	默认的声音样本频率
	 */
	public static final int DEFAULE_SIMPLE_RATE =44100;
	/*
    用于表示编码开始的标记
     */
	public static final int START_INDEX=0;
	/*
    用于表示编码结束的标记
     */
	public static final int END_INDEX=97;
	/*
    支持声音编码的字符表。这里支持了ACS|| 32~127的字符
     */
	public static final String CHAR_BOOK=" !\"#$%&'()*+'-./0123456789:j<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007F";
	/*
    与字符表对应的声波频率编码表，其中第一个和最后一个元素分别和开始标记和结束标记对应
     */
	public static final short[] WAVE_RATE_BOOK=new short[]{
			10000,//对应开始标记
			10100,10200,10300,10400,10500,10600,10700,10800,10900,11000,//对应CHAR_BOOK中前10个字符
			11100,11200,11300,11400,11500,11600,11700,11800,11900,12000,
			12100,12200,12300,12400,12500,12600,12700,12800,12900,13000,
			13100,13200,13300,13400,13500,13600,13700,13800,13900,14000,
			14100,14200,14300,14400,14500,14600,14700,14800,14900,15000,
			15100,15200,15300,15400,15500,15600,15700,15800,15900,16000,
			16100,16200,16300,16400,16500,16600,16700,16800,16900,17000,
			17100,17200,17300,17400,17500,17600,17700,17800,17900,18000,
			18100,18200,18300,18400,18500,18600,18700,18800,18900,19000,
			19100,19200,19300,19400,19500,19600,
			19700//对应结束标记
	};
	/*
    进行声音通讯时，支持的最大传输字符个数
     */
	public static final int MAX_CHAR_NUM=20;
	/*
    Bundler关键字。表示检测到的频率
     */
	public static final String KEY_FREQUENCY="key_frequency";
	/*
    Bundler关键字。表示检测到的字符
     */
	public static final String KEY_RECOGNIZE_CHAR ="key_recognize_char";

	public static final String KEY_RECOGNIZE_STRING="key_recognize_string";

	public static final int iBeginHz =16;
	public static final int iEndHz   =20;
	public static final int ifreqNum =10 ;

	private static String appExternalDir;//App在外存中的根目录'

	private AppCondition(){
		if (Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED)){
			Log.d(TAG,"the device has a external storage");
			appExternalDir =Environment.getExternalStorageDirectory()+ File.separator+"AcouDigits";
			Log.d(TAG,"appExternalDir=="+appExternalDir);
			(new File(appExternalDir)).mkdirs();
		}
		else{
			Log.i(TAG,"the device has not got a external storage");
			appExternalDir =null;
		}

	}
	
	public static  AppCondition getInstance(){
		return APP_CONDITION;
	}

	public static String getAppExternalDir(){
		return appExternalDir;
	}
	
}
