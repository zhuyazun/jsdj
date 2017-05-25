package com.sum.alchemist.model.entity;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 用户类
 * Created by TUS on 2016/5/24.
 */
@Table(name = "User")
public class User {
    /**
     * 用户名
     */
    @Column(name = "username")
    public String username;

    /**
     * 密码
     */
    @Column(name = "password")
    public String password;

    /**
     * 服务器ID
     */
    @Column(name = "id", isId = true, autoGen = false)
    public String id;

    /**
     * 登录状态
     */
    @Column(name = "isLogin")
    public boolean isLogin;

    /**
     * 上次登录时间
     */
    @Column(name = "lastLoginTime")
    public long lastLoginTime;
    /**
     * 用户头像
     */
    @Column(name = "avatar")
    public String avatar;
    /**
     * 名字
     */
    @Column(name = "nickname")
    public String nickname;

    /**
     * 名字
     */
    @Column(name = "full_name")
    public String full_name;
    @Column(name = "gender")
    public String gender;
    @Column(name = "age")
    public String age;
    @Column(name = "card_number")
    public String card_number;
    @Column(name = "contact")
    public String contact;
    @Column(name = "qq")
    public String qq;
    @Column(name = "wechat")
    public String wechat;
    @Column(name = "company")
    public String company;
    @Column(name = "job_duties")
    public String job_duties;
    @Column(name = "job_title")
    public String job_title;
    @Column(name = "graduated_school")
    public String graduated_school;
    @Column(name = "education")
    public String education;
    @Column(name = "profession")
    public String profession;
    @Column(name = "research_areas")
    public String research_areas;
    @Column(name = "paper_works")
    public String paper_works;
    @Column(name = "patented_soft")
    public String patented_soft;
    @Column(name = "completed_technology_projects")
    public String completed_technology_projects;
    @Column(name = "doing_technology_projects")
    public String doing_technology_projects;

    /**
     * 金币
     */
    @Column(name = "gold")
    public int gold;

    @Column(name = "r_01")
    public String r_01;
    @Column(name = "r_02")
    public String r_02;
    @Column(name = "r_03")
    public String r_03;

    public static User create(String username, String password, String id, boolean isLogin){
        User user = new User();
        user.username = username;
        user.password = password;
        user.isLogin = isLogin;
        user.id = id;
        user.lastLoginTime = System.currentTimeMillis() / 1000;
        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", isLogin=" + isLogin +
                ", lastLoginTime=" + lastLoginTime +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", full_name='" + full_name + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", card_number='" + card_number + '\'' +
                ", contact='" + contact + '\'' +
                ", qq='" + qq + '\'' +
                ", wechat='" + wechat + '\'' +
                ", company='" + company + '\'' +
                ", job_duties='" + job_duties + '\'' +
                ", job_title='" + job_title + '\'' +
                ", graduated_school='" + graduated_school + '\'' +
                ", education='" + education + '\'' +
                ", profession='" + profession + '\'' +
                ", research_areas='" + research_areas + '\'' +
                ", paper_works='" + paper_works + '\'' +
                ", patented_soft='" + patented_soft + '\'' +
                ", completed_technology_projects='" + completed_technology_projects + '\'' +
                ", doing_technology_projects='" + doing_technology_projects + '\'' +
                ", gold=" + gold +
                ", r_01='" + r_01 + '\'' +
                ", r_02='" + r_02 + '\'' +
                ", r_03='" + r_03 + '\'' +
                '}';
    }

    public String getDiaplayName(){
        return TextUtils.isEmpty(nickname)?username:nickname;
    }

    public boolean isPhoneUser(){
        return username != null && username.length() == 11;
    }
}
