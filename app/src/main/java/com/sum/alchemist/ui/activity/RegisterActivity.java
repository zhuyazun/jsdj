package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.WebContent;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.widget.share.SMSCenter;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户注册
 * Created by Qiu on 2016/5/25.
 */
public class RegisterActivity extends BaseActivity{

    private static final String TAG = "RegisterActivity";

    private EditText phoneEdit;
    private EditText verifyCodeEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;

    private TextView sendCodeView;

    private SMSCenter smsCenter;

    private CheckBox checkBox;

    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smsCenter = new SMSCenter(this, "18f84f66c1b30", "e2f1a0afa1a08bd9f05b5fdd1bb76ca1");
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        phoneEdit = findView(R.id.phone_et);
        verifyCodeEdit = findView(R.id.sms_code_et);
        passwordEdit = findView(R.id.password_et);
        confirmPasswordEdit = findView(R.id.confirm_password_et);
        sendCodeView = findView(R.id.send_code_tv);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        sendCodeView.setOnClickListener(listener);
        findViewById(R.id.submit_bt).setOnClickListener(listener);
        findViewById(R.id.user_use_tk).setOnClickListener(listener);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("注册账号");
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
                case R.id.send_code_tv:
                    doSendCode();
                    break;
                case R.id.submit_bt:
                    doSubmit();
                    break;
                case R.id.user_use_tk:
                    loadUserUseTk();
                    break;
            }
        }
    };

    private void doSendCode(){
        String phone = phoneEdit.getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            smsCenter.getSMSCode(phone);
            countTask.cancel();
            countTask.start();
            sendCodeView.setBackgroundResource(R.drawable.rec_solid_corners_grey);
            sendCodeView.setClickable(false);
        }else{
            showToastMsg("请填写手机号");
        }
    }

    private void loadUserUseTk(){
        showProgressDialog("正在加载", true);
        addSubscrebe(UserImpl.getInstance().getUserUseTk()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WebContent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(WebContent webContent) {
                        hideProgressDialog();
                        Intent intent = new Intent(RegisterActivity.this, WebActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(WebActivity.WEB_CONTENT_KEY, webContent);
                        startActivity(intent);
                    }
                }));
    }


    private void doSubmit(){
        final String phone = phoneEdit.getText().toString();
        String code = verifyCodeEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();


        if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)){
            showToastMsg("请填写完整信息");
            return;
        }

        if(!checkBox.isChecked()){
            showToastMsg("请阅读并同意用户使用协议");
            return;
        }
        if(!password.equals(confirmPassword)){
            showToastMsg("两次密码不一致");
            return;
        }
        showProgressDialog("正在提交", true);
        smsCenter.checkSMSCode(phone, code, new SMSCenter.CheckSMSCode() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    addSubscrebe(UserImpl.getInstance().doRegister(password, phone)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<JsonObject>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    hideProgressDialog();
                                    showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                                }

                                @Override
                                public void onNext(JsonObject jsonObject) {
                                    hideProgressDialog();
                                    showToastMsg("注册成功");
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra("username", phone);
                                    intent.putExtra("password", password);
                                    intent.putExtra("form", "register");
                                    startActivity(intent);
                                    finish();
                                }
                            }));
                } else {
                    showToastMsg("获取验证码失败,请稍后再试");
                    hideProgressDialog();
                }
            }
        });

        }

        private CountDownTimer countTask = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                sendCodeView.setText(String.format("获取验证码(%s秒)", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                sendCodeView.setText("获取验证码");
                sendCodeView.setBackgroundResource(R.drawable.rec_solid_corners_red);
                sendCodeView.setClickable(true);
            }
        };
    }
