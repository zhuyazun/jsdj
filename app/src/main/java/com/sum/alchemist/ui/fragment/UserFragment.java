package com.sum.alchemist.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sum.alchemist.BuildConfig;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.entity.Version;
import com.sum.alchemist.model.impl.NewsImpl;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.activity.GoldListActivity;
import com.sum.alchemist.ui.activity.LikeListActivity;
import com.sum.alchemist.ui.activity.LoginActivity;
import com.sum.alchemist.ui.activity.MyForumActivity;
import com.sum.alchemist.ui.activity.PayActivity;
import com.sum.alchemist.ui.activity.PushMessageListActivity;
import com.sum.alchemist.ui.activity.SendListActivity;
import com.sum.alchemist.ui.activity.UserInfoActivity;
import com.sum.alchemist.utils.CommonUtil;
import com.sum.alchemist.utils.EventParams;
import com.sum.alchemist.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.utils.EventParams.USER_INFO_CHANGE;
import static com.sum.alchemist.utils.EventParams.USER_LOGIN_CHANGE;

/**
 * Created by Qiu on 2016/10/16.
 */
public class UserFragment extends BaseFragment {

    private CircleImageView userAvatar;
    private View userGoalInfoLayout;
    private View logoutView;
    private TextView userName;
    private TextView userGold;
    private User user;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);

        userAvatar = (CircleImageView) view.findViewById(R.id.user_avatar);
        userGoalInfoLayout = view.findViewById(R.id.user_goal_info_layout);
        userName = (TextView) view.findViewById(R.id.user_name);
        userGold = (TextView) view.findViewById(R.id.user_gold);
        logoutView = view.findViewById(R.id.user_logout_layout);


        userAvatar.setOnClickListener(listener);
        view.findViewById(R.id.user_gold_layout).setOnClickListener(listener);
        view.findViewById(R.id.user_forum_layout).setOnClickListener(listener);
        view.findViewById(R.id.user_send_layout).setOnClickListener(listener);
        view.findViewById(R.id.user_info_layout).setOnClickListener(listener);
        view.findViewById(R.id.user_message_layout).setOnClickListener(listener);
        view.findViewById(R.id.user_favorite_layout).setOnClickListener(listener);
        view.findViewById(R.id.update_version_layout).setOnClickListener(listener);
        logoutView.setOnClickListener(listener);

        view.findViewById(R.id.pay).setOnClickListener(listener);



        loadUserInfo(null);
        return view;
    }



    View.OnClickListener listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.pay:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), PayActivity.class));
                    }
                    break;
                case R.id.user_gold_layout:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), GoldListActivity.class));
                    }
                    break;
                case R.id.user_forum_layout:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), MyForumActivity.class));
                    }
                    break;
                case R.id.user_send_layout:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), SendListActivity.class));
                    }
                    break;
                case R.id.user_message_layout:

                    startActivity(new Intent(getActivity(), PushMessageListActivity.class));

                    break;
                case R.id.user_favorite_layout:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), LikeListActivity.class));
                    }
                    break;
                case R.id.update_version_layout:
                    loadNewVersion();
                    break;
                case R.id.user_logout_layout:
                    showProgressDialog(getString(R.string.loading), true);
                    compositeSubscription.add(UserImpl.getInstance().doLogout().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                                    loadUserInfo(null);
                                }
                            }));
                    break;
                case R.id.user_avatar:
                case R.id.user_info_layout:
                    if(UserImpl.getInstance().getLoginUserAccount() == null){
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), UserInfoActivity.class));
                    }
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUserInfo(EventParams eventParams){
        if(eventParams == null || (eventParams.getCode() & (USER_INFO_CHANGE|USER_LOGIN_CHANGE)) != 0) {
            user = UserImpl.getInstance().getUserDao().queryLoginUser();
            if(user == null){
                userGoalInfoLayout.setVisibility(View.GONE);
                logoutView.setVisibility(View.GONE);
                userName.setText("请登录");
            }else{
                userGoalInfoLayout.setVisibility(View.VISIBLE);
                logoutView.setVisibility(View.VISIBLE);
                userName.setText(user.isPhoneUser()?user.username:user.nickname);
                userGold.setText(String.valueOf(user.gold));
            }

            if(user != null && !TextUtils.isEmpty(user.avatar)){
                Glide.with(this).load(user.avatar).into(userAvatar);
            }else{
                userAvatar.setImageResource(R.mipmap.user_ico);
            }
        }


    }

    public void loadNewVersion(){
        showProgressDialog(getString(R.string.loading), true);
        compositeSubscription.add(NewsImpl.getInstance().loadNewVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Version>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(Version version) {
                        hideProgressDialog();
                        try {
                            if (version != null && Integer.valueOf(version.upgrade_version) > BuildConfig.VERSION_CODE)
                                CommonUtil.downloadServiceTask(getActivity(), version.upgrade_action);

                        }catch (Exception e){
                            e.printStackTrace();
                            showToastMsg("版本号错误");
                        }
                    }
                })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
