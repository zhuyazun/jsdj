package com.sum.alchemist.service;

import android.app.IntentService;
import android.content.Intent;

import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.model.impl.NewsImpl;
import com.sum.xlog.core.XLog;

import rx.Subscriber;

/**
 * Created by Qiu on 2016/5/8.
 */
public class LazyService extends IntentService {

    private final static String TAG = "LazyService";

    public LazyService(){
        this(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LazyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }
}
