package com.sum.alchemist.model.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Qiu on 2016/10/31.
 */
@Table(name = "Patent")
public class Patent {

    @Column(name = "id", isId = true, autoGen = false)
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private String price;
    @Column(name = "logo_img")
    private String logo_img;
    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private String created_at;
    @Column(name = "updated_at")
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLogo_img() {
        return logo_img;
    }

    public void setLogo_img(String logo_img) {
        this.logo_img = logo_img;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
