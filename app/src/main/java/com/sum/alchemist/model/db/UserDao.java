package com.sum.alchemist.model.db;

import android.text.TextUtils;

import com.sum.alchemist.model.entity.User;
import com.sum.xlog.core.XLog;

import org.xutils.db.Selector;
import org.xutils.db.common.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;

import java.util.ArrayList;

/**
 * User DAO
 * Created by TUS on 2016/5/24.
 */
public class UserDao extends BaseDaoImpl<User> {

    private static final String TAG = "UserDao";

    public UserDao(){
        super(User.class);
    }

    /**
     * 获取登录用户(多个用户同时登录时,取第一条).
     */
    public User queryLoginUser(){
        User user = null;
        try {
            Selector<User> selector = DaoManager.getDbManager().selector(User.class);
            selector.where("isLogin", "=", true);
            user = selector.findFirst();
        } catch (Exception e) {
            XLog.e(TAG, "queryLoginUser", e);
        }

        XLog.d(TAG, "当前登录账户:%s", user);
        return user;
    }

    /**
     * 获取上次登录用户
     */
    public User queryLastUser(){
        User user = null;
        try {
            Selector<User> selector = DaoManager.getDbManager().selector(User.class);
            selector.orderBy("lastLoginTime", true);
            user = selector.findFirst();
        } catch (Exception e) {
            XLog.e(TAG, "queryLoginUser", e);
        }

        XLog.d(TAG, "最近登录账户:%s", user);
        return user;
    }

    /**
     * 用户信息更新
     * @return boolean
     */
    public boolean updateUserState(User user){
        if(user == null)
            return false;
        boolean result = false;
        try {
            WhereBuilder whereBuilder = null;
            ArrayList<KeyValue> keyValues = new ArrayList<>();
            KeyValue[] keyValueArray = null;
            if(!TextUtils.isEmpty(user.id)){
                whereBuilder = WhereBuilder.b("id", "=", user.id);
            }else{
                whereBuilder = WhereBuilder.b("isLogin", "=", true);
            }
            if(user.password != null){
                keyValues.add(new KeyValue("password", user.password));
            }
            if(user.lastLoginTime != 0){
                keyValues.add(new KeyValue("lastLoginTime", user.lastLoginTime));
            }
            if(user.avatar != null){
                keyValues.add(new KeyValue("avatar", user.avatar));
            }
            if(user.full_name != null){
                keyValues.add(new KeyValue("full_name", user.full_name));
            }
            if(user.gender != null){
                keyValues.add(new KeyValue("gender", user.gender));
            }
            if(user.age != null){
                keyValues.add(new KeyValue("age", user.age));
            }
            if(user.card_number != null){
                keyValues.add(new KeyValue("card_number", user.card_number));
            }
            if(user.contact != null){
                keyValues.add(new KeyValue("contact", user.contact));
            }
            if(user.qq != null){
                keyValues.add(new KeyValue("qq", user.qq));
            }
            if(user.wechat != null){
                keyValues.add(new KeyValue("wechat", user.wechat));
            }
            if(user.company != null){
                keyValues.add(new KeyValue("company", user.company));
            }
            if(user.job_duties != null){
                keyValues.add(new KeyValue("job_duties", user.job_duties));
            }
            if(user.job_title != null){
                keyValues.add(new KeyValue("job_title", user.job_title));
            }
            if(user.graduated_school != null){
                keyValues.add(new KeyValue("graduated_school", user.graduated_school));
            }
            if(user.education != null){
                keyValues.add(new KeyValue("education", user.education));
            }
            if(user.profession != null){
                keyValues.add(new KeyValue("profession", user.profession));
            }
            if(user.research_areas != null){
                keyValues.add(new KeyValue("research_areas", user.research_areas));
            }
            if(user.paper_works != null){
                keyValues.add(new KeyValue("paper_works", user.paper_works));
            }
            if(user.patented_soft != null){
                keyValues.add(new KeyValue("patented_soft", user.patented_soft));
            }
            if(user.completed_technology_projects != null){
                keyValues.add(new KeyValue("completed_technology_projects", user.completed_technology_projects));
            }
            if(user.doing_technology_projects != null){
                keyValues.add(new KeyValue("doing_technology_projects", user.doing_technology_projects));
            }
            if(user.nickname != null){
                keyValues.add(new KeyValue("nickname", user.nickname));
            }
            if(user.gold != 0){
                keyValues.add(new KeyValue("gold", user.gold));
            }
            if(keyValues.size() > 0){
                keyValueArray = new KeyValue[keyValues.size()];
                for (int i = 0; i < keyValues.size(); i++) {
                    keyValueArray[i] = keyValues.get(i);
                }
            }else{
                return false;
            }
            XLog.d(TAG, "=== 更新用户信息 %s===", keyValues);
            DaoManager.getDbManager().update(User.class, whereBuilder, keyValueArray);
            result = true;
        } catch (Exception e) {
            XLog.e(TAG, "updateUserState", e);
        }

        return result;
    }


    /**
     * 所有用户都设置为未登录
     */
    public boolean updateUserLogoutState(){
        boolean result = false;
        try {

            DaoManager.getDbManager().update(User.class, null, new KeyValue("isLogin", false));
            result = true;
        } catch (Exception e) {
            XLog.e(TAG, "updateUserLoginState", e);
        }

        return result;
    }

}
