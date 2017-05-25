package com.sum.alchemist.model.impl;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.db.ForumDao;
import com.sum.alchemist.model.db.Topic;
import com.sum.alchemist.model.entity.Forum;
import com.sum.xlog.core.XLog;

import java.util.List;

import rx.Observable;

import static com.sum.alchemist.Config.HttpConfig.getToken;

/**
 * Created by Qiu on 2016/11/6.
 */

public class ForumImpl{

    private static final String TAG = "ForumImpl";

    public static ForumImpl mInstance;
    public static ForumImpl getInstance(){
        if(mInstance == null){
            synchronized(ForumImpl.class){
                if(mInstance == null)
                    mInstance = new ForumImpl();
            }
        }
        return mInstance;
    }

    private ForumDao forumDao;

    private ForumImpl(){
        forumDao = new ForumDao();
    }

    public ForumDao getForumDao(){
        return forumDao;
    }


    /**
     * 获取社区信息
     * @param token 用户token 存在时返回用户帖子
     * @param type 类型 <li>all所有</li> <li>img晒图</li> <li>admin公告</li> <li>my我参与的</li>
     * @param offset 偏移量
     * @param limit 条数
     */
    public Observable<List<Forum>> loadForumList(String token, String type, int offset, int limit) {
        XLog.startMethod(TAG, "getForumList");
        return RetrofitHelper.getInstance().getForumApiService().getForumList(token, type, offset, limit);
    }


    /**
     * 获取社区信息
     * @param token 用户token 存在时返回用户帖子
     * @param id 帖子id
     */
    public Observable<Forum> loadForum(String token, int id) {
        XLog.startMethod(TAG, "getForum");
        return RetrofitHelper.getInstance().getForumApiService().getForum(token, id);
    }

    /**
     * 获取社区信息
     * @param title 标题
     * @param location 位置信息
     * @param content 内容
     * @param img 图片
     */
    public Observable<JsonObject> putForum(String title, String location, String content, int topic_id, String img) {
        XLog.startMethod(TAG, "putForum");
        return RetrofitHelper.getInstance().getForumApiService().putForum(getToken(), title, location, content, topic_id, img);
    }

    /**
     * 评论
     * @param tid 帖子Id
     * @param content 评论内容
     */
    public Observable<JsonObject> putForumComment(int tid, String content) {
        XLog.startMethod(TAG, "putForum");
        return RetrofitHelper.getInstance().getForumApiService().putForumComment("application/x-www-form-urlencoded;charset=utf-8", getToken(), tid, content);
    }

    /**
     * 喜欢/取消喜欢
     * @param tid 帖子Id
     */
    public Observable<JsonObject> putForumLike(int tid) {
        XLog.startMethod(TAG, "putForum");
        return RetrofitHelper.getInstance().getForumApiService().putForumLike(getToken(), tid);
    }

    /**
     * 根据Title key搜索帖子
     */
    public Observable<List<Forum>> search(String key, int offset, int limit){
        XLog.startMethod(TAG, "search");
        return RetrofitHelper.getInstance().getForumApiService().search(key, offset, limit);
    }

    public Observable<JsonObject> commentLike(int cid){
        XLog.startMethod(TAG, "commentLike");
        return RetrofitHelper.getInstance().getForumApiService().commentLike(getToken(), cid);
    }

    public Observable<List<Topic>> getTopic(String topic, int offset, int limit){
        XLog.startMethod(TAG, "getTopic");
        return RetrofitHelper.getInstance().getForumApiService().getTopic(offset, limit, topic);
    }

    public Observable<List<Forum>> getForumByTopic(String token, String topic, int offset, int limit){
        XLog.startMethod(TAG, "getForumByTopic");
        return RetrofitHelper.getInstance().getForumApiService().getForumByTopic(token, topic, offset, limit);
    }
}
