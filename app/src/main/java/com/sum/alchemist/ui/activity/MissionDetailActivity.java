package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.utils.DialogUtil;
import com.sum.alchemist.utils.StringUtil;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.Config.HttpConfig.getToken;
import static com.sum.alchemist.ui.fragment.MissionFragment.PROVISION;

/**
 * Created by Qiu on 2016/11/6.
 */

public class MissionDetailActivity extends BaseActivity{

    private TextView titleTv;
    private TextView categoryTv;
    private TextView locationTv;
    private TextView companyTv;
    private TextView moneyTv;
    private TextView companySizeTv;
    private TextView priceTv;
    private TextView usernameTv;
    private TextView dateTv;

    private TextView showTimes;
    private TextView likeTimes;

    private TextView missionTypeTv;
    private WebView webView;
    private Object data;

    public static final String MISSION_TYPE_KEY = "mission_type";
    public static final String ID_KEY = "id";
    private int mission_type;
    private int id;

    @Override
    protected int getContentView() {
        return R.layout.activity_mission_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getIntExtra(ID_KEY, -1);
        mission_type = getIntent().getIntExtra(MISSION_TYPE_KEY, -1);

        if(id == -1 || mission_type == -1){
            showToastMsg("对不起, 找不到对应的资源了");
            finish();
        }

        loadDatail();


    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        titleTv = (TextView) findViewById(R.id.title_tv);
        categoryTv = (TextView) findViewById(R.id.category_tv);
        locationTv = (TextView) findViewById(R.id.location_tv);
        companyTv = (TextView) findViewById(R.id.company_tv);
        moneyTv = (TextView) findViewById(R.id.money_tv);
        companySizeTv = (TextView) findViewById(R.id.company_size_tv);
        priceTv = (TextView) findViewById(R.id.price);
        missionTypeTv = (TextView) findViewById(R.id.type);
        webView = (WebView) findViewById(R.id.web_view);
        usernameTv = findView(R.id.user_name);
        dateTv = findView(R.id.data);
        showTimes = findView(R.id.see_number);
        likeTimes = findView(R.id.like_number);


        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact();
            }
        });
        findViewById(R.id.like_layout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                likeNew();
            }
        });
        findViewById(R.id.collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection();
            }
        });
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("详情");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadDatail(){
        Subscription subscription;

        Subscriber<Object> subscriber = new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                finish();
            }

            @Override
            public void onNext(Object obj) {
                hideProgressDialog();
                initData(obj);
            }
        };

        if(mission_type == PROVISION){
            subscription = MissionImpl.getInstance().loadProvision(getToken(), id)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                showProgressDialog(getString(R.string.loading), true);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
        }else{
            subscription = MissionImpl.getInstance().loadRequirement(getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog(getString(R.string.loading), true);
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }



        addSubscrebe(subscription);
    }

    private void likeNew(){
        if(data != null) {
            showProgressDialog(getString(R.string.loading), false);
            Observable<Boolean> observable = null;
            if (data instanceof Provision) {
                Provision provision = (Provision) data;
                int id = provision.id;
                observable = MissionImpl.getInstance().putProvisionLike(id);
            } else if (data instanceof Requirement) {
                Requirement requirement = (Requirement) data;
                int id = requirement.id;
                observable = MissionImpl.getInstance().putRequirementLike(id);
            }
            addSubscrebe(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            hideProgressDialog();
                            showToastMsg(aBoolean ? "点赞成功" : "点赞取消");

                            if(data instanceof Provision){
                                Provision provision = (Provision) data;
                                provision.like_times += (aBoolean ? 1 : -1);
                                likeTimes.setText(String.valueOf(provision.like_times));
                            }else if(data instanceof Requirement){
                                Requirement requirement = (Requirement) data;
                                requirement.like_times += (aBoolean ? 1 : -1);
                                likeTimes.setText(String.valueOf(requirement.like_times));
                            }

                        }
                    }));
        }
    }

    private void collection(){
        if(data != null) {
            showProgressDialog(getString(R.string.loading), false);
            Observable<Boolean> observable = null;
            if (data instanceof Provision) {
                Provision provision = (Provision) data;
                int id = provision.id;
                observable = MissionImpl.getInstance().addProvisionCollection(id);
            } else if (data instanceof Requirement) {
                Requirement requirement = (Requirement) data;
                int id = requirement.id;
                observable = MissionImpl.getInstance().addRequirementCollection(id);
            }
            addSubscrebe(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            hideProgressDialog();
                            showToastMsg(aBoolean ? "收藏成功" : "取消收藏");
                        }
                    }));
        }
    }

    private void initData(Object object){
        if(object != null){
            data = object;
            if(object instanceof Provision){
                Provision pro = (Provision) object;

                titleTv.setText(pro.title);
                categoryTv.setText(pro.type);
                priceTv.setText(pro.price);
                missionTypeTv.setText("供应");
                webView.loadData(pro.content, "text/html; charset=UTF-8", null);
                showTimes.setText(String.valueOf(pro.show_times));
                likeTimes.setText(String.valueOf(pro.like_times));
                if(pro.user != null) {
                    usernameTv.setText(pro.user.getDiaplayName());
                }
                dateTv.setText(StringUtil.getDate(pro.updated_at));

                locationTv.setText(pro.location);
                companyTv.setText(pro.company_property);
                moneyTv.setText(pro.price_range);
                companySizeTv.setText(pro.company_extent);

            }else if(object instanceof Requirement){
                Requirement req = (Requirement) object;

                titleTv.setText(req.title);
                categoryTv.setText(req.type);
                priceTv.setText(req.price);
                missionTypeTv.setText("求购");
                showTimes.setText(String.valueOf(req.show_times));
                likeTimes.setText(String.valueOf(req.like_times));
                if(req.user != null) {
                    usernameTv.setText(req.user.getDiaplayName());
                }
                dateTv.setText(StringUtil.getDate(req.updated_at));

                locationTv.setText(req.location);
                companyTv.setText(req.company_property);
                moneyTv.setText(req.price_range);
                companySizeTv.setText(req.company_extent);

                webView.loadData(req.content, "text/html; charset=UTF-8", null);

            }
        }else{
            showToastMsg("对不起, 找不到对应的资源了");
            finish();
        }
    }

    private void contact(){
        if(data != null) {
            showProgressDialog(getString(R.string.loading), false);
            Observable<String> observable = null;
            if(data instanceof Provision){
                Provision provision = (Provision) data;
                int id = provision.id;
                observable = MissionImpl.getInstance().loadProvisionContact(id);
            }else if(data instanceof Requirement){
                Requirement requirement = (Requirement) data;
                int id = requirement.id;
                observable = MissionImpl.getInstance().loadRequirementContact(id);
            }

            addSubscrebe(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(String s) {
                            hideProgressDialog();
                            DialogUtil.showContectDialog(MissionDetailActivity.this, String.format("联系方式:%s", s));
                        }
                    }));
        }
    }



}
