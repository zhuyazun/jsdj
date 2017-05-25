package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sum.alchemist.Config;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.utils.CommonUtil;
import com.sum.alchemist.utils.EventParams;
import com.sum.alchemist.widget.share.ShareCenter;

import org.greenrobot.eventbus.EventBus;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/5/23.
 */
public class LoginActivity extends BaseActivity {

    private EditText mUserNameEt;
    private EditText mPasswordEt;
    private CheckBox savePasswordCb;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        mUserNameEt = (EditText) findViewById(R.id.user_name_et);
        mPasswordEt = (EditText) findViewById(R.id.password_et);
        savePasswordCb = (CheckBox) findViewById(R.id.save_password_cb);

        findViewById(R.id.forget_password_tv).setOnClickListener(listener);
        findViewById(R.id.login_bt).setOnClickListener(listener);
        findViewById(R.id.register_tv).setOnClickListener(listener);
        findViewById(R.id.login_weibo).setOnClickListener(listener);
        findViewById(R.id.login_wechat).setOnClickListener(listener);
        findViewById(R.id.login_qq).setOnClickListener(listener);




        User lastUser = UserImpl.getInstance().getUserDao().queryLastUser();

        if(lastUser != null){
            mUserNameEt.setText(lastUser.username);
            mPasswordEt.setText(lastUser.password);
        }
    }



    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("登录");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.forget_password_tv:
                    startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                    break;
                case R.id.login_bt:
                    doLogin();
                    break;
                case R.id.login_weibo:
                    doRegister(SinaWeibo.NAME);
                    break;
                case R.id.login_wechat:
                    doRegister(Wechat.NAME);
                    break;
                case R.id.login_qq:
                    doRegister(QQ.NAME);
                    break;
                case R.id.register_tv:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    break;
            }
        }
    };
    private void doLogin(){
        String account = mUserNameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        boolean isSavePassword = savePasswordCb.isChecked();
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
            showToastMsg("账号密码不能为空");
            return;
        }
        showProgressDialog("登录中...", true);
        addSubscrebe(UserImpl.getInstance().doLogin(account, password, isSavePassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(User user) {
                        hideProgressDialog();
                        if(getIntent().hasExtra("from")) {
                            showToastMsg("请补充完整信息");
                            startActivity(new Intent(LoginActivity.this, UserProInfoActivity.class).putExtra("isFirst", true));
                        }
                        EventBus.getDefault().post(new EventParams(EventParams.USER_LOGIN_CHANGE));
                        finish();
                    }
                }));
    }

    private ShareCenter shareCenter;
    public void doRegister(String plat){
        if(!CommonUtil.isNetworkAvailable(this)){
            showToastMsg(getString(R.string.network_error));
            return;
        }

        if(Config.Test.TEST_OS){
            showToastMsg("无效Key, 请到官网申请");
            return;
        }

        if(shareCenter == null)
            shareCenter = new ShareCenter(this);

        shareCenter.authorize(ShareSDK.getPlatform(plat), showProgressDialog("授权中...", true));
    }

    @Override
    protected void parserIntent(Intent intent) {
        super.parserIntent(intent);

        if(intent != null && intent.hasExtra("username") && intent.hasExtra("password")){
            mUserNameEt.setText(intent.getStringExtra("username"));
            mPasswordEt.setText(intent.getStringExtra("password"));
            savePasswordCb.setChecked(true);
            doLogin();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(shareCenter != null)
            shareCenter.onFinish();
        shareCenter = null;
    }
}
