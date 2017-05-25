package com.sum.alchemist.widget.share;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.sum.alchemist.MyApp;
import com.sum.alchemist.R;
import com.sum.alchemist.utils.CommonUtil;
import com.sum.xlog.core.XLog;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

/**
 * 分享Handler
 */
class ShareHandler implements Handler.Callback, ShareCenter.ShareCallback{

    private static final String TAG = "ShareHandler";

    public Platform.ShareParams sp;
    public boolean isShowHint = true;
    public boolean isGift = false;

    public boolean handleMessage(Message msg) {
        String hintString = null;
        switch(msg.what) {
            case ShareCenter.MSG_AUTH_CANCEL: {
                //取消授权
                hintString = MyApp.getInstance().getResources().getString(R.string.share_cancel);
            } break;
            case ShareCenter.MSG_AUTH_ERROR: {
                //授权失败
                if(msg.obj != null && msg.obj instanceof String){
                    hintString = (String) msg.obj;
                }else{
                    hintString = MyApp.getInstance().getResources().getString(R.string.share_error);
                }
            } break;
            case ShareCenter.MSG_AUTH_COMPLETE: {
                //授权成功
                onSuccess();
                hintString = MyApp.getInstance().getResources().getString(R.string.share_complete);

            } break;

        }
        if(isShowHint && !TextUtils.isEmpty(hintString))
            Toast.makeText(MyApp.getInstance(), hintString, Toast.LENGTH_SHORT).show();

        ShareSDK.stopSDK();
        return false;
    }

    @Override
    public void onValid(Platform platform) {
        if(CommonUtil.isNetworkAvailable(MyApp.getInstance())) {
            platform.share(sp);
            if(isShowHint)
                Toast.makeText(MyApp.getInstance(), R.string.share_complete, Toast.LENGTH_SHORT).show();
            onSuccess();
        }else if(isShowHint){
            Toast.makeText(MyApp.getInstance(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }
        ShareSDK.stopSDK();
    }

    @Override
    public void onFinish() {

    }

    /**
     * 分享成功回调
     * 送会员,记录等操作
     */
    @Override
    public void onSuccess() {


        if(isGift) {
            XLog.d(TAG, "===分享成功, 送送送===");
        }else{
            XLog.d(TAG, "===分享成功===");
        }
    }

}
