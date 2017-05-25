package com.sum.alchemist.widget.push;

import com.sum.alchemist.MyApp;
import com.sum.xlog.core.XLog;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushClient implements PushClient, TagAliasCallback {

	public static final String TAG = JPushClient.class.getSimpleName();
	public static final boolean JPUSH_DEBUG_MODE = false;
	private static JPushClient mInstance;
	private boolean initFlag = false;

	public static synchronized JPushClient getInstance() {
		if (null == mInstance) {
			mInstance = new JPushClient();
		}
		return mInstance;
	}

	@Override
	public void pushInit() {
		XLog.d(TAG, "=== 初始化Jpush ===");
		try {
			JPushInterface.init(MyApp.getInstance());
			JPushInterface.setDebugMode(JPUSH_DEBUG_MODE);
			initFlag = true;
		}catch (Exception e){
			XLog.e(TAG, "初始化Jpush失败", e);
		}
	}
	

	@Override
	public void shutDown() {
		// •JPush Service 不在后台运行
		// •收不到推送消息
		// •不能通过 JPushInterface.init 恢复，需要调用resumePush恢复。
		// •极光推送所有的其他 API 调用都无效
		if(initFlag) {
			XLog.i(TAG, "=== 停止Jpush ===");
			JPushInterface.stopPush(MyApp.getInstance());
		}
	}

	@Override
	public void resumePush() {
		if(initFlag) {
			XLog.i(TAG, "=== 恢复Jpush ===");
			JPushInterface.resumePush(MyApp.getInstance());
		}
	}


	@Override
	public void gotResult(int arg0, String arg1, Set<String> arg2) {
		XLog.i(TAG, "=== 客户端tag，设置结果 : " + arg0 + " , " + arg1 + " ===");
	}

	public void setAlias(String name){
		if(initFlag){
			XLog.i(TAG, "=== 客户端Alias:%s ===", name);
			if(name == null)
				name = "";
			JPushInterface.setAlias(MyApp.getInstance(), name, this);

		}
	}


	

}
