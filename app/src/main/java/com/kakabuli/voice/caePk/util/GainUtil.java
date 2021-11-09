package com.kakabuli.voice.caePk.util;

import android.util.Log;

public class GainUtil {
 
	private static float gainValue =1.0f;
	
	/**
	 * 
	 * @param dbValue -96db -94db 。。。 -2db 0db
	 */
	public static void initValue(int dbValue)
	{
		float dbGain = ((float)dbValue)/20.0f;
		gainValue= (float) Math.pow(10,dbGain);
		Log.e("main", "gainValue:"+gainValue+" dbGain:"+dbGain);
	}
	
	public static short GainDown(short data){
		float pcmval = data* gainValue;
		
		short temp = (short) (pcmval);
		Log.e("main", "handleData pcmval:"+pcmval +"  shortVal:"+temp);
		return temp;
	}
	public static short GainUp(short data,float fv){
         float pcmval = data* fv;
		short temp = (short) (pcmval);
		//Log.e("main", "handleData pcmval:"+pcmval +"  shortVal:"+temp +" upGain:"+fv);
		return temp;
	}
}
