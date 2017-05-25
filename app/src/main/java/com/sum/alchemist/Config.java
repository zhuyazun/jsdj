package com.sum.alchemist;

import android.text.TextUtils;

import com.sum.alchemist.utils.SpUtil;

public class Config {
    public static class Test{

        public static final boolean TEST_HTTP = false;

        public static final boolean TEST_SMS = false;

        public static final boolean TEST_OS = false;

    }

    public static class HttpConfig{
        public static final String PARAMS_CHARSET = "utf-8";

        /**
         * 默认so time out 12秒 (读取数据时间)
         */
        public static final int HTTP_DEFAULT_SO_TIME_OUT = 12;
        /**
         * 默认 time out 5秒
         */
        public static final int HTTP_DEFAULT_TIME_OUT = 5;

        public static final String CACHE_PATH = MyApp.getInstance().getFilesDir().getPath();

//        public static final String BASE_URL = "http://jishuduijie.xinghankj.com/";
        public static final String BASE_URL = "http://hebes.hebut.edu.cn/jsdj/public/";

        public static final String BEARER = "Bearer ";

        public static final String client_id = "android";

        public static final String client_secret = "23474a96f19f7bebf5f0efd10414e2c9";

        private static String TOKEN = SpUtil.getInstance().getStringValue("user_token", "");
        private static String REFRESH_TOKEN = SpUtil.getInstance().getStringValue("refresh_token", "");

        public static void setToken(String token, String refreshToken){
            TOKEN = token;
            REFRESH_TOKEN = refreshToken;
            SpUtil.getInstance().saveStringToSp("user_token", token);
            SpUtil.getInstance().saveStringToSp("refresh_token", refreshToken);
        }

        public static String getToken(){
            if(!TextUtils.isEmpty(TOKEN))
                return BEARER + TOKEN;
            return null;
        }

        public static String getRefreshToken(){
            return BEARER + REFRESH_TOKEN;
        }
    }
    public static class DbConfig{
        /**
         * 数据库名字
         */
        public static final String DB_NAME = "TaiyiData.db";
        /**
         * 数据库版本号
         */
        public static final int DB_VERSION = 1;
    }
    public static class Common{
        public static final String DEFUALT_PASSWORD = "5a78e17318d02";

        public static final String WX_APP_ID = "wxef346128cb948fc3";
        public static final String WX_MD5 = "AGBbbGpMUUjTlBGsmIXceDkSflDjRlcs";
        public static final String WX_PARTNER_ID = "1420093702";

    }

}
