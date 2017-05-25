package com.sum.alchemist.model.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Qiu on 2016/10/16.
 */
@Table(name = "Banner")
public class Banner {

    @Column(name = "id", isId = true, autoGen = false)
    public int id;

    @Column(name = "url")
    public String url;

    @Column(name = "img")
    public String img;

    @Column(name = "click")
    public String click;

    @Column(name = "clicknext")
    public String clicknext;

    @Column(name = "title")
    public String title;

    @Column(name = "version")
    public String version;

    @Column(name = "r_01")
    public String r_01;

    @Column(name = "r_02")
    public String r_02;

    @Column(name = "r_03")
    public String r_03;

    @Column(name = "r_04")
    public String r_04;

    @Column(name = "r_05")
    public String r_05;

    @Column(name = "r_06")
    public String r_06;

    @Column(name = "r_07")
    public String r_07;

    @Column(name = "r_08")
    public String r_08;


}
