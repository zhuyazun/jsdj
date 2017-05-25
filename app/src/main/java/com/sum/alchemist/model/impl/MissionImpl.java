package com.sum.alchemist.model.impl;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.db.ProvisionDao;
import com.sum.alchemist.model.db.RequirementDao;
import com.sum.alchemist.model.entity.Label;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.entity.Province;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.xlog.core.XLog;

import org.json.JSONArray;
import org.xutils.db.common.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.sum.alchemist.Config.HttpConfig.getToken;

/**
 * 供求\专利
 * Created by Qiu on 2016/10/21.
 */
public class MissionImpl {

    public static MissionImpl mInstance;

    public static MissionImpl getInstance() {
        if (mInstance == null) {
            synchronized (MissionImpl.class) {
                if (mInstance == null)
                    mInstance = new MissionImpl();
            }
        }
        return mInstance;
    }

    private ProvisionDao provisionDao;
    private RequirementDao requirementDao;

    private static final String TAG = "MissionImpl";

    public RequirementDao getRequirementDao() {
        return requirementDao;
    }

    public ProvisionDao getProvisionDao() {
        return provisionDao;
    }

    private MissionImpl() {
        provisionDao = new ProvisionDao();
        requirementDao = new RequirementDao();
    }

    /**
     * 获取主页数据
     */
    public Observable<List<Object>> getHomeList() {
        return Observable.create(new Observable.OnSubscribe<List<Object>>() {
            @Override
            public void call(Subscriber<? super List<Object>> subscriber) {
                List<Object> homeList = new ArrayList<>();
                List<Requirement> requirements = requirementDao.queryList("updated_at", true, 0, 5);
                List<Provision> provisions = provisionDao.queryList("updated_at", true, 0, 5);
                if (provisions != null && provisions.size() > 0) {
                    homeList.add(new Label("热点技术", 0, R.mipmap.tag2));
                    homeList.addAll(provisions);
                }

                if (requirements != null && requirements.size() > 0) {
                    homeList.add(new Label("焦点需求", 0, R.mipmap.new_tag));
                    homeList.addAll(requirements);
                }

                List<News> newsList = NewsImpl.getInstance().getNewsList();
                if (newsList != null && newsList.size() > 0) {
                    homeList.add(new Label("科技资讯", 0, R.mipmap.news_tag2));
                    homeList.addAll(newsList);
                }

                subscriber.onNext(homeList);
            }
        });
    }

    /**
     * 加载供应
     *
     * @param token  用户Token 可选(存在时只返回用户发布的供应)
     * @param offset offset
     * @param limit  limit
     */
    public Observable<List<Provision>> loadProvision(String token, int offset, int limit) {
        return RetrofitHelper.getInstance().getProvisionApiService().getProvisionList(token, offset, limit)
                .map(new Func1<List<Provision>, List<Provision>>() {
                    @Override
                    public List<Provision> call(List<Provision> provisions) {
                        provisionDao.insert(provisions);
                        return provisions;
                    }
                });
    }

    /**
     * 加载求购
     *
     * @param token 用户Token 可选(存在时只返回用户发布的求购)
     * @param id    id
     */
    public Observable<Requirement> loadRequirement(String token, int id) {
        return RetrofitHelper.getInstance().getRequirementApiService().getRequirement(token, id)
                .map(new Func1<Requirement, Requirement>() {
                    @Override
                    public Requirement call(Requirement requirement) {
                        requirementDao.insert(requirement);
                        return requirement;
                    }
                });
    }

    /**
     * 加载供应
     *
     * @param token 用户Token 可选(存在时只返回用户发布的供应)
     * @param id    id
     */
    public Observable<Provision> loadProvision(String token, int id) {
        return RetrofitHelper.getInstance().getProvisionApiService().getProvision(token, id)
                .map(new Func1<Provision, Provision>() {
                    @Override
                    public Provision call(Provision provisions) {
                        provisionDao.insert(provisions);
                        return provisions;
                    }
                });
    }

    /**
     * 加载求购
     *
     * @param token  用户Token 可选(存在时只返回用户发布的求购)
     * @param offset offset
     * @param limit  limit
     */
    public Observable<List<Requirement>> loadRequirement(String token, int offset, int limit) {
        return RetrofitHelper.getInstance().getRequirementApiService().getRequirementList(token, offset, limit)
                .map(new Func1<List<Requirement>, List<Requirement>>() {
                    @Override
                    public List<Requirement> call(List<Requirement> requirements) {
                        requirementDao.insert(requirements);
                        return requirements;
                    }
                });
    }

    public Observable<List<Requirement>> searchRequirement(String search_type, String created_at, String location, String company_extent, String price_range,
                                                           String company_property, String str, int offset, int limit) {
        return RetrofitHelper.getInstance().getSearchApiService().getRequirementList("requirement", search_type,
                created_at, location, company_extent, price_range, company_property, str, offset, limit)
                .map(new Func1<List<Requirement>, List<Requirement>>() {
                    @Override
                    public List<Requirement> call(List<Requirement> requirements) {
                        requirementDao.insert(requirements);
                        return requirements;
                    }
                });
    }

    public Observable<List<Provision>> searchProvision(String search_type, String created_at, String location, String company_extent, String price_range,
                                                       String company_property, String str, int offset, int limit) {
        return RetrofitHelper.getInstance().getSearchApiService().getProvisionList("provision", search_type,
                created_at, location, company_extent, price_range, company_property, str, offset, limit)
                .map(new Func1<List<Provision>, List<Provision>>() {
                    @Override
                    public List<Provision> call(List<Provision> provisions) {
                        provisionDao.insert(provisions);
                        return provisions;
                    }
                });
    }

    /**
     * 收藏
     */
    public Observable<Boolean> addProvisionCollection(int id) {
        XLog.startMethod(TAG, "addProvisionCollection");
        return RetrofitHelper.getInstance().getProvisionApiService().addProvisionCollection(getToken(), id).map(new Func1<JsonObject, Boolean>() {
            @Override
            public Boolean call(JsonObject patent) {
                return "true".equals(patent.get("collection").getAsString());
            }
        });
    }

    /**
     * 收藏
     */
    public Observable<Boolean> addRequirementCollection(int id) {
        XLog.startMethod(TAG, "addRequirementCollection");
        return RetrofitHelper.getInstance().getRequirementApiService().addRequirementCollection(getToken(), id).map(new Func1<JsonObject, Boolean>() {
            @Override
            public Boolean call(JsonObject patent) {
                return "true".equals(patent.get("collection").getAsString());
            }
        });
    }


    /**
     * 查看联系方式
     */
    public Observable<String> loadProvisionContact(int id) {
        XLog.startMethod(TAG, "loadProvisionContact");
        return RetrofitHelper.getInstance().getProvisionApiService().getProvisionContact(getToken(), id).map(new Func1<JsonObject, String>() {
            @Override
            public String call(JsonObject patent) {
                return patent.get("contact").getAsString();
            }
        });
    }


    /**
     * 查看联系方式
     */
    public Observable<String> loadRequirementContact(int id) {
        XLog.startMethod(TAG, "loadRequirementContact");
        return RetrofitHelper.getInstance().getRequirementApiService().getRequirementContact(getToken(), id).map(new Func1<JsonObject, String>() {
            @Override
            public String call(JsonObject patent) {
                return patent.get("contact").getAsString();
            }
        });
    }


    /**
     * 点赞
     */
    public Observable<Boolean> putRequirementLike(int id) {
        XLog.startMethod(TAG, "putRequirementLike");
        return RetrofitHelper.getInstance().getRequirementApiService().putRequirementLike(getToken(), id)
                .map(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        String liked = jsonObject.get("liked").getAsString();
                        return "true".equals(liked);
                    }
                });
    }

    /**
     * 点赞
     */
    public Observable<Boolean> putProvisionLike(int id) {
        XLog.startMethod(TAG, "putProvisionLike");
        return RetrofitHelper.getInstance().getProvisionApiService().putProvisionLike(getToken(), id)
                .map(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        String liked = jsonObject.get("liked").getAsString();
                        return "true".equals(liked);
                    }
                });
    }


    /**
     * 上传文件
     *
     * @param file File
     * @return String url
     */
    public Observable<String> uploadFile(File file) {

        try {
            return RetrofitHelper.getInstance().getProvisionApiService().putFile(getToken(), MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)))
                    .map(new Func1<JsonObject, String>() {
                        @Override
                        public String call(JsonObject jsonObject) {
                            return jsonObject.get("url").getAsString();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onError(new Throwable("文件解析失败"));
            }
        });
    }

    /**
     * 发布供应
     *
     * @param type         分类
     * @param location     位置
     * @param company      公司性质
     * @param money        金额
     * @param company_size 公司规模
     * @param title        标题
     * @param price        价格
     * @param content      内容
     * @param contact      联系电话
     */
    public Observable<JsonObject> putProvision(String type, String location, String company, String money, String company_size, String title, String price, String content, String contact) {
        XLog.startMethod(TAG, "putProvision");
        return RetrofitHelper.getInstance().getProvisionApiService().putProvision(getToken(), type, location, money, company, company_size, contact, title, price, content);
    }

    /**
     * 发布求购
     *
     * @param type         分类
     * @param location     位置
     * @param company      公司性质
     * @param money        金额
     * @param company_size 公司规模
     * @param title        标题
     * @param price        价格
     * @param content      内容
     * @param contact      联系电话
     */
    public Observable<JsonObject> putRequirement(String type, String location, String company, String money, String company_size, String title, String price, String content, String contact) {
        XLog.startMethod(TAG, "putRequirement");
        return RetrofitHelper.getInstance().getRequirementApiService().putRequirement(getToken(), type, location, money, company, company_size, contact, title, price, content);
    }

    public Observable<List<Province>> getProvince(final Context context) {
        XLog.startMethod(TAG, "getProvince");
        return Observable.create(new Observable.OnSubscribe<List<Province>>() {
            @Override
            public void call(Subscriber<? super List<Province>> subscriber) {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bf = null;
                try {
                    AssetManager assetManager = context.getAssets();
                    bf = new BufferedReader(new InputStreamReader(
                            assetManager.open("province2.json")));
                    String line;
                    while ((line = bf.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                    ArrayList<Province> detail = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Province entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), Province.class);
                        detail.add(entity);
                    }

                    subscriber.onNext(detail);
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    IOUtil.closeQuietly(bf);
                }
            }
        });
    }


    public void parseProvince(List<Province> provinces, List<String> options1Items,
                              List<List<String>> options2Items, List<List<List<String>>> options3Items) {


        for (int i = 0; i < provinces.size(); i++) {//遍历省份
            List<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            List<List<String>> areaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < provinces.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = provinces.get(i).getCityList().get(c).getName();
                cityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空数据，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (provinces.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                }
                for (int d = 0; d < provinces.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                    String AreaName = provinces.get(i).getCityList().get(c).getArea().get(d);

                    City_AreaList.add(AreaName);//添加该城市所有地区数据
                }
                areaList.add(City_AreaList);//添加该省所有地区数据
            }
            options1Items.add(provinces.get(i).getName());
            options2Items.add(cityList);
            options3Items.add(areaList);
        }

    }

}
