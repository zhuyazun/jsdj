package com.sum.alchemist.model.entity;

import java.util.List;

/**
 * Created by Qiu on 2016/11/6.
 */
public class Forum {


    /**
     * id : 1
     * uid : 1
     * type :
     * title : 测试看看
     * location : 陕西西安
     * img : http://115.28.64.6:8089/upload/2016/10/30/5815805a6c9cd6.99134625.jpeg
     * content : 测试看看测试看看
     * status : accept
     * created_at : 2016-10-30 15:39:27
     * updated_at : 2016-10-30 15:39:27
     * comments : [{"id":1,"tid":1,"uid":1,"content":"评论","created_at":"2016-10-30 15:50:58","updated_at":"2016-10-30 15:50:58"}]
     */
    public int id;
    /**
     * 用户ID
     */
    public int uid;
    public String type;
    public String title;
    public String location;
    public String img;
    public String content;
    public String status;
    public String created_at;
    public String updated_at;
    public User user;
    public int comment_num;
    public int like_num;
    public int show_times;
    public String desc;
    public String like;
    /**
     * id : 1
     * tid : 1
     * uid : 1
     * content : 评论
     * created_at : 2016-10-30 15:50:58
     * updated_at : 2016-10-30 15:50:58
     */

    public List<CommentsBean> comments;

    public static class CommentsBean {
        public int id;
        public int tid;
        public int uid;
        public String content;
        public String created_at;
        public String updated_at;
        public User user;
        public int like_times;
        public String like;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", uid=" + uid +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", img='" + img + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", user=" + user +
                ", comment_num=" + comment_num +
                ", like_num=" + like_num +
                ", like='" + like + '\'' +
                ", comments=" + comments +
                '}';
    }
}
