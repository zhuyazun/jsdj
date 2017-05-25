package com.sum.alchemist.model.db;

import com.sum.alchemist.model.entity.News;

/**
 * PushMsg DAO
 * Created by TUS on 2016/5/24.
 */
public class NewsDao extends BaseDaoImpl<News> {

    private static final String TAG = "NewsDao";

    public NewsDao(){
        super(News.class);
    }


}
