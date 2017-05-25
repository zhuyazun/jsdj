package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.sum.alchemist.R;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/12/19.
 */

public class TestActivity extends BaseActivity{
    @Override
    protected int getContentView() {
        return R.layout.activity_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Subscription sub = Observable.just("Hi rx").subscribeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                try{
                    Thread.sleep(5000);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                Log.d("test", s);
                return s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

                Log.d("test", s);
            }
        });
        Log.d("test", "isUnsubscribed :" + sub.isUnsubscribed());
        if(!sub.isUnsubscribed())
            sub.unsubscribe();
        Log.d("test", "isUnsubscribed :" + sub.isUnsubscribed());


    }
}
