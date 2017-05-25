package com.sum.alchemist.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sum.alchemist.MyApp;
import com.sum.xlog.core.XLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.ReentrantLock;


/**
 * 管理程序里Sp存储
 */
public class SpUtil {
	
	public static final String TAG = SpUtil.class.getSimpleName();
	
	private SharedPreferences mSp;
	private static ReentrantLock mLock = new ReentrantLock();
	private static SpUtil mInstance;
	
	public static SpUtil getInstance(){
		try{
			mLock.lock();
			if(null == mInstance){
				mInstance = new SpUtil();
			}
			return mInstance;
		}finally{
			mLock.unlock();
		}
	}
	
	private static final String SP_NAME = "config"; 
	private static final int SP_OPEN_MODE = 0; 
	
	private SpUtil() {
		mSp = MyApp.getInstance().getSharedPreferences(SP_NAME, SP_OPEN_MODE);
	}
	
	/**
	 * 保存字符串到sp文件
	 * @param key 字符串对应的key
	 * @param value 字符串的vaule
	 * @return true 保存成功  false 保存失败
	 */
	public boolean saveStringToSp(String key , String value){

		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
			XLog.e(TAG, "=== saveStringToSp(),保存失败,key不能为null, 自动删除 === ");
			remove(key);
			return false;
		}
		mSp.edit().putString(key, value).apply();
		return true;
	}

	/**
	 * 删除字段
	 */
	public boolean remove(String key){

		if(TextUtils.isEmpty(key)){
			XLog.e(TAG, "=== remove(),删除失败,key不能为null === ");
			return false;
		}
		mSp.edit().remove(key).apply();
		return true;
	}
	
	/**
	 * 
	 * 保存整形值到sp
	 * @param key 整形值对应的key
	 * @param value 整形值对应的value
	 * @return true 保存成功  false 保存失败
	 */
	public boolean saveIntToSp(String key , int value){
		
		if(TextUtils.isEmpty(key)){
			XLog.e(TAG, "=== saveIntToSp(),保存失败,key不能为null === ");
			return false;
		}
		mSp.edit().putInt(key, value).apply();
		return true;
	}
	
	/**
	 * 
	 * 保存整形值到sp
	 * @param key 整形值对应的key
	 * @param value 整形值对应的value
	 * @return true 保存成功  false 保存失败
	 */
	public boolean saveLongToSp(String key , long value){
		
		if(TextUtils.isEmpty(key)){
			XLog.e(TAG , "=== saveLongToSp(),保存失败,key不能为null === ");
			return false;
		}
		mSp.edit().putLong(key, value).apply();
		return true;
	}
	
	/**
	 * 
	 * 保存布尔值到sp
	 * @param key 布尔值对应的key
	 * @param value 布尔值对应的value
	 * @return true 保存成功 fasle 保存失败
	 */
	public boolean saveBoolenTosp(String key , boolean value){
		
		if(TextUtils.isEmpty(key)){
			XLog.e(TAG , "=== saveBoolenTosp(),保存失败,key不能为null === ");
			return false;
		}
		mSp.edit().putBoolean(key, value).apply();
		return true;
	}
	
	/**
	 * 根据保存到sp的key 取出String value
	 * @param key  String对应的key
	 * @param defaultValue String对应的Value
	 */
	public String getStringValue(String key , String defaultValue){
		
		if(null == defaultValue){
			defaultValue = "";
		}
		if(TextUtils.isEmpty(key)){
			return defaultValue;
		}
		return mSp.getString(key, defaultValue);
	}

	public JSONArray getJSONArray(String key){
		if (TextUtils.isEmpty(key)){
			return new JSONArray();
		}

		String jsonStr = mSp.getString(key, "[]");
		JSONArray json;
		try {
			json = new JSONArray(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONArray();
		}
		return json;
	}

	public JSONObject getJSONObject(String key){
		if (TextUtils.isEmpty(key)){
			return new JSONObject();
		}

		String jsonStr = mSp.getString(key, "{}");
		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject();
		}
		return json;
	}
	
	/**
	 * 根据保存到sp的key 取出Int value
	 * @param key 保存的key
	 * @param defaultValue 默认值
	 * @return key 对应的 int value
	 */
	public int getIntValue(String key , int defaultValue){
		
		if(TextUtils.isEmpty(key)){
			return defaultValue;
		}
		return mSp.getInt(key, defaultValue);
		
	}
	
	/**
	 * 根据保存到sp的key 取出Int value
	 * @param key 保存的key
	 * @param defaultValue 默认值
	 * @return key 对应的 int value
	 */
	public long getLongValue(String key , long defaultValue){
		
		if(TextUtils.isEmpty(key)){
			return defaultValue;
		}
		return mSp.getLong(key, defaultValue);
		
	}
	
	/**
	 * 根据保存到sp的key Boolean value
	 * @param key 保存的key
	 * @param defaultValue 默认值
	 * @return key 对应的 Boolean value
	 */
	public boolean getBooleanValue(String key , boolean defaultValue){
		if(TextUtils.isEmpty(key)){
			return defaultValue;
		}
		return mSp.getBoolean(key, defaultValue);
	}

}
