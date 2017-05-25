package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.widget.share.SMSCenter;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 忘记密码
 * Created by Qiu on 2016/5/25.
 */
public class ForgetPasswordActivity extends BaseActivity{

    private static final String TAG = "ForgetPasswordActivity";

    private EditText phoneEdit;
    private EditText verifyCodeEdit;
    private EditText passwordEdit;
    private SMSCenter smsCenter;
    private TextView sendCodeView;

    @Override
    protected int getContentView() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smsCenter = new SMSCenter(this, "18f84f66c1b30", "e2f1a0afa1a08bd9f05b5fdd1bb76ca1");
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        phoneEdit = findView(R.id.account_phone_et);
        verifyCodeEdit = findView(R.id.phone_code_et);
        passwordEdit = findView(R.id.new_password_et);
        sendCodeView = findView(R.id.send_code_tv);

        sendCodeView.setOnClickListener(listener);
        findViewById(R.id.submit_bt).setOnClickListener(listener);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("忘记密码");
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


    private void doSubmit(){
        final String phone = phoneEdit.getText().toString();
        String code = verifyCodeEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || TextUtils.isEmpty(password)){
            showToastMsg("请填写完整信息");
            return;
        }
        showProgressDialog("正在提交", true);
        smsCenter.checkSMSCode(phone, code, new SMSCenter.CheckSMSCode() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    forgetPassword(password, phone);
                } else {
                    showToastMsg("获取验证码失败,请稍后再试");
                    hideProgressDialog();
                }
            }
        });
    }

    private void forgetPassword(String password, String phone){
        addSubscrebe(UserImpl.getInstance().doFindPassword(password, phone)
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
                        showToastMsg("修改成功");
                        hideProgressDialog();
                        finish();
                    }
                }));
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
