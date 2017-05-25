package com.sum.alchemist;

import android.app.Application;

import com.sum.alchemist.widget.push.JPushClient;
import com.sum.xlog.core.LogLevel;
import com.sum.xlog.core.XLog;
import com.sum.xlog.core.XLogConfiguration;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    private static final String TAG = "MyApp";
    private static MyApp mInstance;

    public static MyApp getInstance(){
        if(mInstance == null)
            synchronized (MyApp.class){
                if(mInstance == null)
                    mInstance = new MyApp();
            }
        return mInstance;
    }

    public static List<String> activitys;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        XLog.init(new XLogConfiguration.Builder(this)
                .setConsoleLogLevel(LogLevel.D)
                .setFileLogLevel(LogLevel.D)
                .setCrashHandlerOpen(false)
                .setFileLogRetentionPeriod(7)
                .build());

        x.Ext.init(mInstance);


        JPushClient.getInstance().pushInit();


    }

    public static void addActivityList(String name){
        if(activitys == null)
            activitys = new ArrayList<>();
        activitys.add(name);
    }

    public static void removeActivityList(String name){
        if(activitys == null)
            activitys = new ArrayList<>();
        activitys.remove(name);
        if(activitys.size() == 0)
            XLog.destroy();
    }

    public static boolean isActivity(){
        return activitys != null && !activitys.isEmpty();
    }


}
