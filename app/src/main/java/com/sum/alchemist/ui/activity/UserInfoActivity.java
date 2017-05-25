package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.utils.EventParams;
import com.sum.alchemist.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/4.
 */

public class UserInfoActivity extends BaseActivity {


    private CircleImageView userAvatar;
    private TextView userPhoneTv;
    /**
     * 修改密码
     */
    private View change_password;

    private static final String TAG = "UserInfoActivity";

    @Override
    protected int getContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        userAvatar = (CircleImageView) findViewById(R.id.user_avatar);
        change_password = findViewById(R.id.user_change_password_layout);
        userPhoneTv = findView(R.id.user_phone);
//        findViewById(R.id.user_logout_tv).setOnClickListener(listener);
        findViewById(R.id.user_pro_info_layout).setOnClickListener(listener);

        change_password.setOnClickListener(listener);
        userAvatar.setOnClickListener(listener);

        loadUserInfo(null);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUserInfo(EventParams eventParams){
        if(eventParams != null && eventParams.getCode() != EventParams.USER_INFO_CHANGE)
            return;
        User user = UserImpl.getInstance().getUserDao().queryLoginUser();
        if(user != null && user.isPhoneUser()){
            change_password.setVisibility(View.VISIBLE);
            userPhoneTv.setText(user.username);
        }else{
            change_password.setVisibility(View.GONE);
            userPhoneTv.setText(null);
        }

        if(user != null && !TextUtils.isEmpty(user.avatar))
            Glide.with(UserInfoActivity.this).load(user.avatar).into(userAvatar);
        else
            userAvatar.setImageResource(R.mipmap.user_ico);
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.user_pro_info_layout:
                    startActivity(new Intent(UserInfoActivity.this, UserProInfoActivity.class));
                    break;

                case R.id.user_change_password_layout:
                    startActivity(new Intent(UserInfoActivity.this, ChangePasswordActivity.class));
                    break;

                case R.id.user_avatar:
                    Intent intent = new Intent(UserInfoActivity.this, UImageActivity.class);
                    startActivityForResult(intent, UImageActivity.UPLOAD_IMAGE);
                    break;

            }
        }
    };

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("用户中心");
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


    private void logout(){
        addSubscrebe(UserImpl.getInstance().doLogout()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    showProgressDialog(getString(R.string.loading), true);
                }
            })
            .subscribeOn(AndroidSchedulers.mainThread())
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
                    EventBus.getDefault().post(new EventParams(EventParams.USER_LOGIN_CHANGE));
                    finish();
                }
            }));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UImageActivity.UPLOAD_IMAGE && resultCode == RESULT_OK && data != null){
//            String url = data.getStringExtra(UImageActivity.IMAGE_URL_KEY);
            EventBus.getDefault().post(new EventParams(EventParams.USER_INFO_CHANGE));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
