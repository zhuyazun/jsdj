package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.impl.UserImpl;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 修改密码
 * Created by Qiu on 2016/5/25.
 */
public class ChangePasswordActivity extends BaseActivity {

    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;
    private EditText confirmPasswordEdit;

    @Override
    protected int getContentView() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        oldPasswordEdit = findView(R.id.old_password_et);
        newPasswordEdit = findView(R.id.new_password_et);
        confirmPasswordEdit = findView(R.id.confirm_password_et);

        findViewById(R.id.submit_bt).setOnClickListener(listener);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("修改密码");
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

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.submit_bt:
                    doSubmit();
                    break;
            }
        }
    };

    private void doSubmit(){
        final String oldPassword = oldPasswordEdit.getText().toString();
        final String newPassword = newPasswordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();

        if(TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)){
            showToastMsg("请填写完整信息");
            return;
        }

        if(!newPassword.equals(confirmPassword)){
            showToastMsg("两次密码不一致");
        }

        addSubscrebe(Observable.just(UserImpl.getInstance().getLoginUserAccount())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog("提交中...", true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(String s) {
                        return UserImpl.getInstance().doChangePassword(s, oldPassword, newPassword);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
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

}
