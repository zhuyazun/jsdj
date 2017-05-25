package com.sum.alchemist.widget.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sum.alchemist.MyApp;
import com.sum.alchemist.model.db.PushMsgDao;
import com.sum.alchemist.model.entity.PushMsg;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.activity.MainActivity;
import com.sum.alchemist.ui.activity.WelcomeActivity;
import com.sum.alchemist.utils.DateUtil;
import com.sum.xlog.core.XLog;

import cn.jpush.android.api.JPushInterface;

public class JpushReciver extends BroadcastReceiver{
	
	public  static final boolean MSG_PROCESS_ON_SINGLE_THREAD = false;
	
	public static final String TAG = "JpushReciver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		    
	        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	             //收到JpushID
	        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
	        	//收到自定义消息
				processMessage(context, intent.getExtras());

	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
	        	//收到状态栏消息,未打开


	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
	        	//状态栏打开消息
				if(MyApp.isActivity()){
					if(UserImpl.getInstance().getUserDao().queryLoginUser() != null) {
						context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
					}
//					else {
//						context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
//					}
				}else{
					context.startActivity(new Intent(context, WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
	        }
	}

	private void processMessage(Context context, Bundle bundle){
		try{
			XLog.d(TAG, "push message:%s", bundle.toString());
			PushMsg pushMsg = new PushMsg();
			pushMsg.setTitle(bundle.getString(JPushInterface.EXTRA_TITLE));
			pushMsg.setContent(bundle.getString(JPushInterface.EXTRA_MESSAGE));
			pushMsg.setReceiverMillis(String.valueOf(System.currentTimeMillis()));
			pushMsg.setReceiverTime(DateUtil.millis2String(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss", false));
			new PushMsgDao().replace(pushMsg);
		}catch (Exception e){
			XLog.e(TAG, "推送消息解析失败", e);
		}
	}

//	private void grabRefresh(Context context, String title, String text){
//
//		if(!MyApp.isActivity()){
//			Log.d(TAG, "JPUSH后台推送通知");
//			NotifyUtil.showNotify(context, title, text, 1001, new Intent(context, GrabListActivity.class).addFlags(
//					Intent.FLAG_ACTIVITY_NEW_TASK));
//		}else if (!GrabedFragment.isForegound) {
//			Log.d(TAG, "APP后台推送通知");
//			NotifyUtil.showNotify(context, title, text, 1001, new Intent(context, GrabListActivity.class).addFlags(
//					Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		} else {
//			Log.d(TAG, "APP内通知");
//			EventBus.getDefault().post(new EventParams(EventParams.GRAB_LIST_CHANGE));
//		}
//	}
}
