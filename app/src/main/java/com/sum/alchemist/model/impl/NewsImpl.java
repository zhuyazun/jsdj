package com.sum.alchemist.model.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sum.alchemist.BuildConfig;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.db.DaoManager;
import com.sum.alchemist.model.db.NewsDao;
import com.sum.alchemist.model.entity.Banner;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.entity.Patent;
import com.sum.alchemist.model.entity.Version;
import com.sum.xlog.core.XLog;

import org.xutils.ex.DbException;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.sum.alchemist.Config.HttpConfig.getToken;

/**
 * Created by Qiu on 2016/10/21.
 */
public class NewsImpl {

    public static NewsImpl mInstance;

    public static NewsImpl getInstance() {
        if (mInstance == null) {
            synchronized (NewsImpl.class) {
                if (mInstance == null)
                    mInstance = new NewsImpl();
            }
        }
        return mInstance;
    }

    private static final String TAG = "NewsImpl";
    private NewsDao newsDao;
    private NewsImpl() {
        newsDao = new NewsDao();
    }

    /**
     * 加载本地Banner数据
     */
    public Observable<List<Banner>> getBannerList() {
        XLog.d(TAG, "=== 获取本地Banner ===");
        return Observable.create(new Observable.OnSubscribe<List<Banner>>() {
            @Override
            public void call(Subscriber<? super List<Banner>> subscriber) {
                try {
                    List<Banner> banners = DaoManager.getDbManager().selector(Banner.class).findAll();
                    subscriber.onNext(banners);
                } catch (DbException e) {
                    subscriber.onNext(null);
                    XLog.e(TAG, "=== 获取本地Banner失败 ===", e);
                }
            }
        });
    }

    /**
     * 加载网络Banner数据,自动保存至DB
     */
    public Observable<List<Banner>> loadBannerList() {
        XLog.d(TAG, "=== 获取网络Banner ===");

        return RetrofitHelper.getInstance().getNewsApiService().getBanner()
                .map(new Func1<List<Banner>, List<Banner>>() {
                    @Override
                    public List<Banner> call(List<Banner> banners) {
                        try {
                            if (banners != null && banners.size() > 0) {
                                DaoManager.getDbManager().delete(Banner.class);
                                DaoManager.getDbManager().save(banners);
                            }
                        } catch (DbException e) {
                            XLog.e(TAG, "=== 保存Banner失败 ===", e);
                        }
                        return banners;
                    }
                });
    }


    public List<News> getNewsList(){
        return newsDao.queryList("updated_at", false, 0, 20);
    }

    /**
     * 加载资讯
     * @param offset offset
     * @param limit limit
     */
    public Observable<List<News>> loadNews(int offset, int limit){
        return RetrofitHelper.getInstance().getNewsApiService().getArticleList(offset, limit)
                .map(new Func1<List<News>, List<News>>() {
                    @Override
                    public List<News> call(List<News> newses) {
                        XLog.d(TAG, "=== 成功获取%s条News数据===", newses!=null?newses.size():0);
                        newsDao.insert(newses);
                        return newses;
                    }
                });
    }

    /**
     * 加载资讯
     * @param id id
     */
    public Observable<News> loadNews(int id){
        return RetrofitHelper.getInstance().getNewsApiService().getArticle(id);
    }


    public Observable<Boolean> putNewsLike(int id){
        XLog.startMethod(TAG, "putNewsLike");
        return RetrofitHelper.getInstance().getNewsApiService().putArticleLike(getToken(), id)
                .map(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        String liked = jsonObject.get("liked").getAsString();
                        return "true".equals(liked);
                    }
                });
    }



    /**
     * 加载专利列表
     * @param offset 偏移位置
     * @param limit 数量
     */
    public Observable<List<Patent>> loadPatentList(int offset, int limit){
        XLog.startMethod(TAG, "loadPatentList");
        return RetrofitHelper.getInstance().getNewsApiService().getPatentList(offset, limit);
    }

    /**
     * 查看联系方式
     */
    public Observable<String> loadPatentContact(int id){
        XLog.startMethod(TAG, "loadPatentContact");
        return RetrofitHelper.getInstance().getNewsApiService().getPatentContact(getToken(), id).map(new Func1<JsonObject, String>() {
            @Override
            public String call(JsonObject patent) {
                return patent.get("contact").getAsString();
            }
        });
    }

    /**
     * 加载专利信息
     * @param id 专利ID
     */
    public Observable<Patent> loadPatent(int id){
        XLog.startMethod(TAG, "loadPatent");
        return RetrofitHelper.getInstance().getNewsApiService().getPatent(id)
                .map(new Func1<JsonObject, Patent>() {
                    @Override
                    public Patent call(JsonObject jsonObject) {
                        JsonObject patent = jsonObject.getAsJsonObject("patent");
                        return new Gson().fromJson(patent, Patent.class);
                    }
                });
    }

    public Observable<Version> loadNewVersion(){
        XLog.startMethod(TAG, "loadNewVersion");
        return RetrofitHelper.getInstance().getNewsApiService().getNewVersion(BuildConfig.VERSION_CODE);
    }

}
