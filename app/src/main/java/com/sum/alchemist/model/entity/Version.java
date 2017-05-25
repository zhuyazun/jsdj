package com.sum.alchemist.model.entity;

/**
 * Created by Qiu on 2016/11/26.
 */

public class Version {


    /**
     * id : 1
     * now_version : 1.0
     * upgrade_version : 1.1
     * msg_title : 有新版本
     * msg_content : 检测到新版本，请尽快升级！
     * force_upgrade : no
     * upgrade_action : https://www.google.com.hk
     * created_at : 2015-08-07 00:00:00
     * updated_at : 2015-08-07 00:00:00
     */

    public int id;
    public String now_version;
    public String upgrade_version;
    public String msg_title;
    public String msg_content;
    public String force_upgrade;
    public String upgrade_action;
    public String created_at;
    public String updated_at;
}
