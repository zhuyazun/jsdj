package com.sum.alchemist.widget.share;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.sum.alchemist.Config;
import com.sum.alchemist.MyApp;
import com.sum.xlog.core.XLog;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMSCenter {

    private static final String TAG = "SMSCenter";

    public interface CheckSMSCode{
        void onResult(boolean result);
    }

    private CheckSMSCode checkSMSCode;
    public SMSCenter(Context context, String appKey, String appSecret){
        SMSSDK.initSDK(context, appKey, appSecret);

        EventHandler eh = new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
    }

    public void getSMSCode(String username){
        if(Config.Test.TEST_SMS) {
            Toast.makeText(MyApp.getInstance(), "目前为测试环境,验证码不会发送 可以随便填", Toast.LENGTH_LONG).show();
            return;
        }
        SMSSDK.getVerificationCode("86",username);
    }

    public void checkSMSCode(String username, String smsCode, @NonNull CheckSMSCode checkSMSCode){
        if(Config.Test.TEST_SMS){
            checkSMSCode.onResult(true);
            return;
        }
        SMSSDK.submitVerificationCode("86", username, smsCode);
        this.checkSMSCode = checkSMSCode;
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            XLog.d(TAG, "event = %s, result = %s", event, result);
            if (result == SMSSDK.RESULT_COMPLETE) {

                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && checkSMSCode != null) {//提交验证码成功
                    checkSMSCode.onResult(true);

                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Toast.makeText(MyApp.getInstance(), "验证码已发送至您的手机", Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                XLog.e(TAG, "获取短信验证码失败", (Throwable) data);
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && checkSMSCode != null) {
                    checkSMSCode.onResult(false);
                }

            }
        }

    };

    public void onDestroy(){
        SMSSDK.unregisterAllEventHandler();
    }
}
