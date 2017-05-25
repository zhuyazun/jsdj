package com.sum.alchemist.widget.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sum.xlog.core.XLog;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;

/**
 * 授权代理中心
 */
public class ShareCenter{

    public static final String TAG = "ShareUtil";
    public static final int REGISTER = 1001;
    public static final int LOGIN = 1002;

    public Handler handler;
    public Context context;
    public ShareCallback shareCallback;

    public ShareCenter(Context context){
        this.context = context;
        ShareSDK.initSDK(context);
    }



    /*---------------------- 第三方授权事件处理 ------------------------*/
    boolean isAuthorizeRealy = true;
    /**
     * 发起授权获取用户信息
     * progressDialog(可选)
     */
    public void authorize(Platform plat, Dialog progressDialog) {
        if (plat == null || !isAuthorizeRealy) {
            return ;
        }
        isAuthorizeRealy = false;
        LoginHandler loginHandler = new LoginHandler(context);
        loginHandler.progressDialog = progressDialog;

        shareCallback = loginHandler;
        handler = new Handler(loginHandler);

        try {
            if (plat.isValid()) {
                shareCallback.onValid(plat);
                return;
            }
//

            plat.setPlatformActionListener(platformActionListener);
            // 是否关闭SSO授权
            plat.SSOSetting(false);
            plat.showUser(null);
        } catch (Exception e) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
            XLog.e(TAG, "=== 分享出错 ===", e);
            ShareSDK.stopSDK();
        }
        isAuthorizeRealy = true;
    }


    /**
     * 授权并分享
     * @param isShowHint false : 静默分享,授权情况没有任何提示
     */
    public void authorize(Platform plat, Platform.ShareParams sp, boolean isShowHint){
        authorize(plat, sp, isShowHint, false);
    }
    public void authorize(Platform plat, Platform.ShareParams sp, boolean isShowHint, boolean isGift){
        if(plat == null)
            return ;

        ShareHandler shareHandler = new ShareHandler();
        shareHandler.sp = sp;
        shareHandler.isShowHint = isShowHint;
        shareHandler.isGift = isGift;
        shareCallback = shareHandler;
        handler = new Handler(shareHandler);

//        if(plat.isValid()){
//
//            shareCallback.onValid(plat);
//            return ;
//        }

        plat.setPlatformActionListener(platformActionListener); // 设置授权事件回调
        plat.SSOSetting(!isShowHint);
        // 执行图文分享
        plat.share(sp);
    }

    public static final int MSG_AUTH_CANCEL = 2;
    public static final int MSG_AUTH_ERROR= 3;
    public static final int MSG_AUTH_COMPLETE = 4;



    /**
     * ShareSDK的分享、登录公共回调
     */
    public PlatformActionListener platformActionListener = new PlatformActionListener() {

        public void onComplete(Platform platform, int action, HashMap<String, Object> res) {

            if (action == Platform.ACTION_USER_INFOR) {
                Message msg = Message.obtain();
                msg.what = MSG_AUTH_COMPLETE;
                msg.obj = new Object[] {platform.getName(), res};
                handler.sendMessage(msg);
            }else if(action == Platform.ACTION_SHARE){
                handler.sendEmptyMessage(MSG_AUTH_COMPLETE);
            }
        }

        public void onError(Platform platform, int action, Throwable t) {
            Message msg =  Message.obtain(handler, MSG_AUTH_ERROR);
            if(t instanceof WechatClientNotExistException) {
                msg.obj = "微信客户端不存在";
            }
            handler.sendMessage(msg);
            XLog.e(TAG, "=== 分享出错 ===", t);
        }

        public void onCancel(Platform platform, int action) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    };

    public static interface ShareCallback{
        public void onSuccess();
        public void onValid(Platform platform);
        public void onFinish();
    }

    public void onFinish(){
        shareCallback.onFinish();
        ShareSDK.stopSDK();
    }


}
