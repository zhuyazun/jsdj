package com.sum.alchemist.model.api;

import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sum.alchemist.BuildConfig;
import com.sum.alchemist.Config;
import com.sum.alchemist.MyApp;
import com.sum.alchemist.R;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.activity.LoginActivity;
import com.sum.xlog.core.XLog;

import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.Config.HttpConfig.CACHE_PATH;
import static com.sum.alchemist.Config.HttpConfig.HTTP_DEFAULT_SO_TIME_OUT;
import static com.sum.alchemist.Config.HttpConfig.HTTP_DEFAULT_TIME_OUT;
import static com.sum.alchemist.Config.HttpConfig.getToken;

/**
 * Created by Qiu on 2016/10/25.
 */

public class RetrofitHelper {


    public OkHttpClient okHttpClient;
    public Retrofit retrofit;
    public static RetrofitHelper mInstance;

    private static final String TAG = "RetrofitHelper";

    public static RetrofitHelper getInstance(){
        if(mInstance == null){
            synchronized(RetrofitHelper.class){
                if(mInstance == null)
                    mInstance = new RetrofitHelper();
            }
        }
        return mInstance;
    }
    private RetrofitHelper(){
        initOkHttp();
        retrofit = new Retrofit.Builder()
                .baseUrl(Config.HttpConfig.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private void initOkHttp(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            //调试模式 请求拦截器打印出详细参数 结果
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        //缓存配置
        File cacheFile = new File(CACHE_PATH);
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
//        Interceptor cacheInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                if (!CommonUtil.isNetworkAvailable(MyApp.getInstance())) {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_CACHE)
//                            .build();
//                }
//                Response response = chain.proceed(request);
//                if (CommonUtil.isNetworkAvailable(MyApp.getInstance())) {
//                    int maxAge = 0;
//                    // 有网络时, 不缓存, 最大保存时长为0
//                    response.newBuilder()
//                            .header("Cache-Control", "public, max-age=" + maxAge)
//                            .removeHeader("Pragma")
//                            .build();
//                } else {
//                    // 无网络时，设置超时为4周
//                    int maxStale = 60 * 60 * 24 * 28;
//                    response.newBuilder()
//                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                            .removeHeader("Pragma")
//                            .build();
//                }
//                return response;
//            }
//        };

//        builder.addNetworkInterceptor(cacheInterceptor);
//        builder.addInterceptor(cacheInterceptor);
        builder.cache(cache);
        //设置超时
        builder.connectTimeout(HTTP_DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(HTTP_DEFAULT_SO_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(HTTP_DEFAULT_SO_TIME_OUT, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }


    private static UserApi userApi;
    public UserApi getUserApiService(){
        if(userApi == null)
            userApi = retrofit.create(UserApi.class);
        return userApi;
    }

    private static NewsApi newsApi;
    public NewsApi getNewsApiService(){
        if(newsApi == null)
            newsApi = retrofit.create(NewsApi.class);
        return newsApi;
    }

    private static ProvisionApi provisionApi;
    public ProvisionApi getProvisionApiService(){
        if(provisionApi == null)
            provisionApi = retrofit.create(ProvisionApi.class);
        return provisionApi;
    }

    private static ForumApi forumApi;
    public ForumApi getForumApiService(){
        if(forumApi == null)
            forumApi = retrofit.create(ForumApi.class);
        return forumApi;
    }

    private static RequirementApi requirementApi;
    public RequirementApi getRequirementApiService(){
        if(requirementApi == null)
            requirementApi = retrofit.create(RequirementApi.class);
        return requirementApi;
    }

    private static SearchApi searchApi;
    public SearchApi getSearchApiService(){
        if(searchApi == null)
            searchApi = retrofit.create(SearchApi.class);
        return searchApi;
    }

    private static PayApi payApi;
    public PayApi getPayApiService(){
        if(payApi == null)
            payApi = retrofit.create(PayApi.class);
        return payApi;
    }

    public static String getHttpErrorMessage(Throwable e){
        XLog.e("sen", e);
        int errorCode = getHttpErrorCode(e);

        return getHttpErrorMessage(errorCode, e);

    }

    public static int getHttpErrorCode(Throwable e){
        int errorCode = 0;
        try {
            if (e instanceof HttpException) {

                HttpException httpException = (HttpException) e;
                switch (httpException.code()){
                    case 401:
                        if(getToken() == null){
                            MyApp.getInstance().startActivity(new Intent(MyApp.getInstance(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                            errorCode = 1113;
                        }else{
                            UserImpl.getInstance().refreshToken().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<JsonObject>() {
                                        @Override
                                        public void call(JsonObject jsonObject) {

                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if(getHttpErrorCode(throwable) ==  1113){
                                                Toast.makeText(MyApp.getInstance(), R.string.login_timeout, Toast.LENGTH_LONG).show();
                                                MyApp.getInstance().startActivity(new Intent(MyApp.getInstance(), LoginActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
                                            }
                                        }
                                    });
                            errorCode = 997;
                        }



                        break;
                    default:
                        String resultString = httpException.response().errorBody().string();
                        XLog.d(TAG, "=== returnString :%s ====", resultString);
                        JSONObject jsonObject = new JSONObject(resultString);
                        errorCode = jsonObject.getInt("error_code");
                        break;
                }
            }else if(e instanceof ConnectException || e instanceof SocketException){
                XLog.e(TAG, "=== 链接超时 ===", e);
            }
        }catch (Throwable ex){
            XLog.e(TAG, "=== 服务器返回错误 ===", ex);
            errorCode = 998;
        }
        return errorCode;
    }

    public static String getHttpErrorMessage(int errorCode, Throwable e){
        String error = null;
        switch (errorCode){
            case 0: //非网络错误
                error = e.getMessage();
                break;
            case 998:
                error = MyApp.getInstance().getString(R.string.service_error);
                break;
            case 999:
                error = "未知错误";
                break;
            case 1000:
                error = "用户名错误";
                break;
            case 1001:
                error = "密码错误";
                break;
            case 1019:
                error = "上传文件类型大小不符合要求";
                break;
            case 1110:
                error = "用户名密码不匹配";
                break;
            case 1111:
                error = "无效客户端";
                break;
            case 1112:
                error = "授权方式有误";
                break;
            case 1113:
                error = "刷新token无效";
                break;
            case 1114:
                error = "请求参数有误";
                break;
            case 1007:
                error = "用户名已存在";
                break;
            case 1008:
                error = "sign错误";
                break;
            case 1009:
                error = "注册失败";
                break;
            case 1010:
                error = "用户不存在";
                break;
            case 1011:
                error = "用户昵称错误";
                break;
            case 1012:
                error = "更新失败";
                break;
            case 1013:
                error = "旧密码错误";
                break;
            case 1014:
                error = "金币不足";
                break;
            default:
                error = MyApp.getInstance().getString(R.string.network_error);
                break;
        }

        return error;
    }


}
