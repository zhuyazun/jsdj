package com.sum.alchemist.model.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Qiu on 2016/10/21.
 */
@Table(name = "News")
public class News {
    @Column(name = "id", isId = true, autoGen = false)
    public int id;
    @Column(name = "title")
    public String title;
    @Column(name = "created_at")
    public String created_at;
    @Column(name = "updated_at")
    public String updated_at;
    @Column(name = "logo_img")
    public String logo_img;
    @Column(name = "desc")
    public String desc;
    @Column(name = "content")
    public String content;
    @Column(name = "show_times")
    public int show_times;
    @Column(name = "like")
    public String like;
    @Column(name = "like_times")
    public int like_times;



}
