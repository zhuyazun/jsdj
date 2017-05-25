package com.sum.alchemist.model.entity;

import com.google.gson.annotations.SerializedName;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Qiu on 2016/10/21.
 */
@Table(name = "Requirement")
public class Requirement {

    /**
     * ID
     */
    @Column(name = "id", isId = true, autoGen = false)
    public int id;
    /**
     * 标题
     */
    @Column(name = "title")
    public String title;
    /**
     * 时间
     */
    @Column(name = "updated_at")
    public String updated_at;
    /**
     * 图片
     */
    @Column(name = "imgs")
    public String[] imgs;

    /**
     * 详情
     */
    @Column(name = "content")
    public String content;

    /**
     * 分类
     */
    @Column(name = "type")
    public String type;

    @Column(name = "location")
    public String location;

    @Column(name = "company_extent")
    public String company_extent;

    @Column(name = "price_range")
    public String price_range;

    @Column(name = "company_property")
    public String company_property;


    /**
     * 价格
     */
    @Column(name = "price")
    public String price;

    @Column(name = "status")
    public String status;
    @Column(name = "logo_img")
    public String logo_img;

    @Column(name = "desc")
    public String desc;
    @Column(name = "show_times")
    public int show_times;
    @Column(name = "like")
    public String like;
    @Column(name = "like_times")
    @SerializedName(value = "like_times", alternate = {"like_num"})
    public int like_times;

    public User user;
}
