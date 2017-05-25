package com.sum.alchemist.widget.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sum.alchemist.MyApp;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.activity.LoginActivity;
import com.sum.alchemist.utils.SpUtil;
import com.sum.xlog.core.XLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.sum.alchemist.Config.Common.DEFUALT_PASSWORD;


/**
 * 登录Handler
 */
class LoginHandler implements Handler.Callback, ShareCenter.ShareCallback {

    public static final String TAG = "LoginHandler";

    public String platform;
    public String userId;

    public Dialog progressDialog;

    private Context context;
    private CompositeSubscription subscription;

    public LoginHandler(Context context) {
        this.context = context;
        subscription = new CompositeSubscription();
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ShareCenter.MSG_AUTH_CANCEL: {
                //取消授权
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(MyApp.getInstance(), "取消授权", Toast.LENGTH_LONG).show();

            }
            break;
            case ShareCenter.MSG_AUTH_ERROR: {
                //授权失败
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(MyApp.getInstance(), "授权错误", Toast.LENGTH_LONG).show();
            }
            break;
            case ShareCenter.MSG_AUTH_COMPLETE: {
                //授权成功
                Object[] objs = (Object[]) msg.obj;
                platform = (String) objs[0];
                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                XLog.d(TAG, "=== 授权用户信息: %s===", res.toString());
                if (progressDialog != null)
                    progressDialog.show();
                saveUserInfoMap(res);

                doRegister(userId, (String) res.get("nickname"));
            }
            break;

        }
        ShareSDK.stopSDK();
        return false;
    }

    @Override
    public void onValid(Platform platform) {
        try {
            this.platform = platform.getName();
            JSONObject json = getUserInfoMap();
            if (json != null) {
                userId = json.getString("userId");
            } else {
                userId = null;
                platform.removeAccount(true);
            }

            doRegister(userId, json.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ShareSDK.stopSDK();
    }

    @Override
    public void onFinish() {
        subscription.unsubscribe();
    }

    @Override
    public void onSuccess() {

    }


    /**
     * 针对不同平台信息key值处理，保存成同一个key的json
     */
    public void saveUserInfoMap(HashMap<String, Object> res) {
        try {
            String nickname;
            String city;
            String province;
            String headimgurl;
            String sex;
            if (platform.equals(QQ.NAME)) { // QQ信息处理
                String figureurl = res.get("figureurl_qq_1").toString();
                userId = figureurl.substring(figureurl.length() - 35, figureurl.length() - 3);
                nickname = res.get("nickname").toString();
                city = res.get("city").toString();
                province = res.get("province").toString();
                headimgurl = res.get("figureurl_2").toString();
//                sex = "男".equals(res.get("gender").toString()) ?
//                        RegisterActivity.MALE : RegisterActivity.FEMALE;

            } else if (platform.equals(Wechat.NAME)) { // 微信信息处理
                userId = res.get("openid").toString();
                nickname = res.get("nickname").toString();
                city = res.get("city").toString();
                province = res.get("province").toString();
                headimgurl = res.get("headimgurl").toString();
//                sex = "1".equals(res.get("sex").toString()) ? RegisterActivity.MALE : RegisterActivity.FEMALE;;


            } else { // 微博信息处理
                userId = res.get("id").toString();
                nickname = res.get("name").toString();
                city = res.get("location").toString();
                province = res.get("location").toString();
                headimgurl = res.get("avatar_large").toString();
//                sex = "m".equals(res.get("gender").toString()) ?
//                        RegisterActivity.MALE : RegisterActivity.FEMALE;
            }

            JSONObject jsonObject = new JSONObject();
            userId = userId + "@" + platform + ".com";
            jsonObject.put("userId", userId);
            jsonObject.put("nickname", nickname);
            jsonObject.put("city", city);
            jsonObject.put("province", province);
            jsonObject.put("headimgurl", headimgurl);
//            jsonObject.put("sex", sex);

            SpUtil.getInstance().saveStringToSp(platform, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getUserInfoMap() {
        JSONObject json = null;
        try {
            String str = SpUtil.getInstance().getStringValue(platform, "");
            if (!TextUtils.isEmpty(str))
                json = new JSONObject(str);
        } catch (Exception e) {
            XLog.e(TAG, "=== 获取用户信息失败 ===", e);
        }

        return json;
    }

    private void doRegister(final String username, String nickname){
        subscription.add(UserImpl.getInstance().doRegister(DEFUALT_PASSWORD, username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        int errorCode = RetrofitHelper.getHttpErrorCode(e);

                        if(errorCode == 1007){
                            doLogin(username);
                        }else{
                            if(progressDialog != null)
                                progressDialog.dismiss();
                            String errorMsg = RetrofitHelper.getHttpErrorMessage(errorCode, e);
                            Toast.makeText(MyApp.getInstance(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                    }
                }));
    }


    private void doLogin(String username){
        subscription.add(UserImpl.getInstance().doLogin(username, DEFUALT_PASSWORD, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(progressDialog != null)
                            progressDialog.dismiss();
                        Toast.makeText(MyApp.getInstance(), RetrofitHelper.getHttpErrorMessage(e), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(User user) {
                        if(progressDialog != null)
                            progressDialog.dismiss();
                        if(context instanceof LoginActivity){
                            ((LoginActivity)context).finish();
                        }
                    }
                }));
    }
}
