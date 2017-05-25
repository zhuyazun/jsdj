package com.sum.alchemist.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.sum.alchemist.MyApp;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends SwipeBackActivity{

    private ProgressDialog mProgressDialog;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = new CompositeSubscription();

        setContentView(getContentView());
        parserIntent(getIntent());
        initTitle();
        initViewAndListener();
        MyApp.addActivityList(getClass().getName());
    }

    abstract protected int getContentView();

    protected void initViewAndListener(){}

    protected void initTitle(){}

    protected void parserIntent(Intent intent){

    }

    public void addSubscrebe(Subscription subscription){

        compositeSubscription.add(subscription);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parserIntent(intent);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.removeActivityList(getClass().getName());
        if(compositeSubscription != null) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    public <T extends View> T findView(int id){
        return (T)findViewById(id);
    }

    public void showToastMsg(int resource){
        showToastMsg(getString(resource));
    }

    public void showToastMsg(String msg){
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, msg.length() > 20?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
        }
    }

    public ProgressDialog showProgressDialog(String msg, boolean isCancel){
        hideProgressDialog();
        mProgressDialog = ProgressDialog.show(this, null, msg, true, isCancel);
        return mProgressDialog;
    }

    public void hideProgressDialog(){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

}
