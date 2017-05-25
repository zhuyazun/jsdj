package com.sum.alchemist.model.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sum.alchemist.Config;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.db.UserDao;
import com.sum.alchemist.model.entity.GoldLog;
import com.sum.alchemist.model.entity.Message;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.entity.WebContent;
import com.sum.alchemist.utils.CommonUtil;
import com.sum.alchemist.widget.push.JPushClient;
import com.sum.xlog.core.XLog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.sum.alchemist.Config.HttpConfig.client_id;
import static com.sum.alchemist.Config.HttpConfig.client_secret;
import static com.sum.alchemist.Config.HttpConfig.getRefreshToken;
import static com.sum.alchemist.Config.HttpConfig.getToken;
import static com.sum.alchemist.Config.HttpConfig.setToken;

/**
 * Created by TUS on 2016/5/24.
 */
public class UserImpl {


    private static final String TAG = "UserImpl";

    private static UserImpl mInstance;

    public static synchronized UserImpl getInstance(){
        if(mInstance == null){
            mInstance = new UserImpl();
        }
        return mInstance;
    }

    private UserDao userDao;

    private UserImpl(){
        userDao = new UserDao();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * 用户登录(保存Token)
     * @param username 手机号
     * @param password 密码
     */
    public Observable<User> doLogin(final String username, final String password, final boolean isSavePassword){
        XLog.startMethod(TAG, "doLogin");

        return RetrofitHelper.getInstance().getUserApiService().accessToken("password", client_secret, username, password, client_id)
                .flatMap(new Func1<JsonObject, Observable<User>>() {
                    @Override
                    public Observable<User> call(JsonObject jsonObject){
                        XLog.d(TAG, "accessToken Result:%s", jsonObject.toString());
                        Config.HttpConfig.setToken(jsonObject.get("access_token").getAsString(),
                                jsonObject.get("refresh_token").getAsString());

                        return RetrofitHelper.getInstance().getUserApiService().getUserInfo(Config.HttpConfig.getToken());
                    }
                })
                .map(new Func1<User, User>() {
                    @Override
                    public User call(User user) {
                        XLog.d(TAG, "getUserInfo Result:%s", user.toString());
                        user.password = isSavePassword?password:null;
                        user.isLogin = true;
                        user.lastLoginTime = System.currentTimeMillis() / 1000;
                        JPushClient.getInstance().setAlias(username);
                        userDao.replace(user);
                        return user;
                    }
        });
    }

    /**
     * 刷新Token
     */
    public Observable<JsonObject> refreshToken(){
        XLog.startMethod(TAG, "refreshToken");

        return RetrofitHelper.getInstance().getUserApiService().refreshToken("refresh_token", client_secret, getRefreshToken(), client_id)
                .map(new Func1<JsonObject, JsonObject>() {
                    @Override
                    public JsonObject call(JsonObject jsonObject) {
                        XLog.d(TAG, "=== refreshToken Result:%s===", jsonObject.toString());
                        Config.HttpConfig.setToken(jsonObject.get("access_token").getAsString(),
                                jsonObject.get("refresh_token").getAsString());

                        return null;
                    }
                });
    }

    /**
     * 注册账号
     */
    public Observable<JsonObject> doRegister(String password, String username){
        XLog.startMethod(TAG, "doRegister");
        long timestamp = System.currentTimeMillis() / 1000;
        return RetrofitHelper.getInstance().getUserApiService().reg(username, client_id, client_secret, CommonUtil.getSign(username, timestamp), timestamp, password);


    }

    /**
     * 忘记密码
     */
    public Observable<JsonObject> doFindPassword(String password, String account){
        XLog.startMethod(TAG, "doFindPassword");
        long timestamp = System.currentTimeMillis() / 1000;
        return RetrofitHelper.getInstance().getUserApiService().changePassword(getToken(), account, password,CommonUtil.getSign(account, timestamp), timestamp, null);

    }

    /**
     * 修改密码
     */
    public Observable<JsonObject> doChangePassword(String account, String passwordOld, String passwordNew){
        XLog.startMethod(TAG, "doChangePassword");
        long timestamp = System.currentTimeMillis() / 1000;
        return RetrofitHelper.getInstance().getUserApiService().changePassword(getToken(), account, passwordNew, CommonUtil.getSign(account, timestamp), timestamp, passwordOld);

    }

    /**
     * 获取用户资料
     */
    public Observable<User> getUserInfo(){

        XLog.startMethod(TAG, "getUserInfo");
        return RetrofitHelper.getInstance().getUserApiService().getUserInfo(getToken())
                .map(new Func1<User, User>() {
                    @Override
                    public User call(User user) {
                        userDao.updateUserState(user);
                        return user;
                    }
                });
    }

    public Observable<String> putUserInfo(final String avatar, final String nickName, final String full_name, final String gender,
                                          final String age, final String card_number, final String contact, final String qq,
                                          final String wechat, final String company, final String job_duties, final String job_title,
                                          final String graduated_school, final String education, final String profession, final String research_areas,
                                          final String paper_works, final String patented_soft, final String completed_technology_projects, final String doing_technology_projects){
        XLog.startMethod(TAG, "putUserInfo");
        return RetrofitHelper.getInstance().getUserApiService().putUserInfo(getToken(), nickName, avatar, full_name, gender, age, card_number, contact, qq, wechat, company, job_duties, job_title,
                graduated_school, education, profession, research_areas, paper_works, patented_soft, completed_technology_projects, doing_technology_projects)
                .map(new Func1<JsonObject, String>() {
                    @Override
                    public String call(JsonObject jsonObject) {
                        User user = new User();
                        user.nickname = nickName;
                        user.avatar = avatar;
                        user.full_name = full_name;
                        user.age = age;
                        user.card_number = card_number;
                        user.contact = contact;
                        user.qq = qq;
                        user.wechat = wechat;
                        user.company = company;
                        user.job_duties = job_duties;
                        user.job_title = job_title;
                        user.graduated_school = graduated_school;
                        user.education = education;
                        user.profession = profession;
                        user.research_areas = research_areas;
                        user.paper_works = paper_works;
                        user.patented_soft = patented_soft;
                        user.completed_technology_projects = completed_technology_projects;
                        user.doing_technology_projects = doing_technology_projects;
                        userDao.updateUserState(user);
                        return TextUtils.isEmpty(avatar)?nickName:avatar;
                    }
                });
    }

    /**
     * 获取用户金币记录
     * @param offset offset
     * @param limit limit
     */
    public Observable<List<GoldLog>> getGoldLogUser(int offset, int limit){
        XLog.startMethod(TAG, "getGoldLogUser");
        return RetrofitHelper.getInstance().getUserApiService().getGold(getToken(), offset, limit);
    }

    public Observable<List<Message>> getUserMessage(int offset, int limit){
        XLog.startMethod(TAG, "getUserMessage");
        return RetrofitHelper.getInstance().getUserApiService().getMessageLogs(getToken(), "1", offset, limit);


    }


    /**
     * 退出登录
     */
    public Observable<JsonObject> doLogout(){
        XLog.startMethod(TAG, "doLogout");
        return RetrofitHelper.getInstance().getUserApiService().logout(getToken())
                .map(new Func1<JsonObject, JsonObject>() {
                    @Override
                    public JsonObject call(JsonObject jsonObject) {
                        setToken(null, null);
                        userDao.updateUserLogoutState();
                        JPushClient.getInstance().setAlias(null);
                        return jsonObject;
                    }
                });

    }

    /**
     * 获取登录账号
     */
    public String getLoginUserAccount(){
        User user = userDao.queryLoginUser();
        return user != null ? user.username:null;
    }

    /**
     * 用户签到
     */
    public Observable<JsonObject> sign(){
        XLog.startMethod(TAG, "sign");
        return RetrofitHelper.getInstance().getUserApiService().sign(getToken());

    }

    public Observable<List<Object>> getUserLike(int offset, int limit){
        XLog.startMethod(TAG, "getUserLike");
        return RetrofitHelper.getInstance().getUserApiService().getUserLike(getToken(), offset, limit).map(new Func1<JsonObject, List<Object>>() {
            @Override
            public List<Object> call(JsonObject jsonObject) {
                List<Object> objects = new ArrayList<Object>();
                List<Requirement> requirements = new Gson().fromJson(jsonObject.get("requirement"), new TypeToken<List<Requirement>>(){}.getType());
                List<Provision> provisions = new Gson().fromJson(jsonObject.get("provision"), new TypeToken<List<Provision>>(){}.getType());
                if(requirements != null)
                    objects.addAll(requirements);
                if(provisions != null)
                    objects.addAll(provisions);
                return objects;
            }
        });
    }

    public Observable<List<Object>> getUserSend(int offset, int limit){
        XLog.startMethod(TAG, "getUserSend");
        return RetrofitHelper.getInstance().getUserApiService().getUserSendList(getToken(), offset, limit).map(new Func1<JsonObject, List<Object>>() {
            @Override
            public List<Object> call(JsonObject jsonObject) {
                List<Object> objects = new ArrayList<Object>();
                List<Requirement> requirements = new Gson().fromJson(jsonObject.get("requirement"), new TypeToken<List<Requirement>>(){}.getType());
                List<Provision> provisions = new Gson().fromJson(jsonObject.get("provision"), new TypeToken<List<Provision>>(){}.getType());
                if(requirements != null)
                    objects.addAll(requirements);
                if(provisions != null)
                    objects.addAll(provisions);
                return objects;
            }
        });
    }

    /**
     * 删除发布
     * @param type 0:provision 1: requirement
     * @param id id
     */
    public Observable<JsonObject> deleteMission(int type, int id){
        XLog.startMethod(TAG, "deleteMission");
        return RetrofitHelper.getInstance().getUserApiService().deleteMission(getToken(), type == 0?"provision":"requirement", id);
    }


    public Observable<WebContent> getUserUseTk(){
        XLog.startMethod(TAG, "getUserUseTk");
        return RetrofitHelper.getInstance().getUserApiService().getUserUseTk();
    }


}
