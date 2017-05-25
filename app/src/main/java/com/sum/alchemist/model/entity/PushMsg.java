package com.sum.alchemist.model.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "PushMsg")
public class PushMsg {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "receiverTime")
    private String receiverTime;
    @Column(name = "receiverMillis")
    private String receiverMillis;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;

    public int tid;
    public String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiverTime() {
        return receiverTime;
    }

    public void setReceiverTime(String receiverTime) {
        this.receiverTime = receiverTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiverMillis() {
        return receiverMillis;
    }

    public void setReceiverMillis(String receiverMillis) {
        this.receiverMillis = receiverMillis;
    }
}
