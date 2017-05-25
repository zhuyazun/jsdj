package com.sum.alchemist.model.entity;

/**
 * Created by Qiu on 2016/10/21.
 */
public class Label {

    /**
     * 标签名字
     */
    public String name;

    /**
     * 标签类别0:category 1:more
     */
    public int type;

    public int icon;

    public Label(String name, int type, int icon){
        this.name = name;
        this.type = type;
        this.icon = icon;

    }



}
