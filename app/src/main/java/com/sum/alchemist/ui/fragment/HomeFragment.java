package com.sum.alchemist.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Banner;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.model.impl.NewsImpl;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.activity.MainActivity;
import com.sum.alchemist.ui.activity.MissionDetailActivity;
import com.sum.alchemist.ui.activity.NewsActivity;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.BannerAdapter;
import com.sum.alchemist.ui.adapter.HomeAdapter;
import com.sum.alchemist.utils.EventParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.activity.MissionDetailActivity.ID_KEY;
import static com.sum.alchemist.ui.activity.MissionDetailActivity.MISSION_TYPE_KEY;
import static com.sum.alchemist.ui.activity.NewsActivity.NEWS_ID_KEY;
import static com.sum.alchemist.ui.fragment.MissionFragment.PROVISION;
import static com.sum.alchemist.ui.fragment.MissionFragment.REQUIREMENT;

/**
 * Created by Qiu on 2016/10/16.
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    private ViewPager mainBannerPager;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private BannerAdapter bannerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeAdapter = new HomeAdapter();
        bannerAdapter = new BannerAdapter();

        homeAdapter.setAdapterItemListener(adapterItemListener);

        getBanner();
        loadBanner();
        getHomeList(null);
        loadHomeList();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);

        View header = inflater.inflate(R.layout.header_view_pager, null);
        mainBannerPager = (ViewPager) header.findViewById(R.id.main_banner_pager);
        mainBannerPager.setAdapter(bannerAdapter);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomeList();
                loadBanner();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeAdapter.setHaederView(header);
        recyclerView.setAdapter(homeAdapter);

        view.findViewById(R.id.search_layout).setOnClickListener(listener);

        return view;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search_layout:
//                    startActivity(new Intent(getActivity(), ForumDetailActivity.class).putExtra(FORUM_ID_KEY, 4));
//                    startActivity(new Intent(getActivity(), SearchForumActivity.class));
                    ((MainActivity)getActivity()).gotoPage(1);
                    break;
            }
        }
    };

    AdapterItemListener<Object> adapterItemListener = new AdapterItemListener<Object>() {
        @Override
        public void onItemClickListener(Object data, int position, int id) {
            if(data instanceof Provision && id == 0){
                Provision pro = (Provision) data;

                startActivity(new Intent(getActivity(), MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, PROVISION).putExtra(ID_KEY, pro.id));
            }else if(data instanceof Requirement && id == 0){
                Requirement req = (Requirement) data;

                startActivity(new Intent(getActivity(), MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, REQUIREMENT).putExtra(ID_KEY, req.id));
            }else if(data instanceof News && id == 0){
                News news = (News) data;

                startActivity(new Intent(getActivity(), NewsActivity.class)
                        .putExtra(NEWS_ID_KEY, news.id));
            }
        }
    };


    private void sign() {
        compositeSubscription.add(UserImpl.getInstance().sign()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog(getString(R.string.loading), true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<JsonObject, Observable<User>>() {
                    @Override
                    public Observable<User> call(JsonObject jsonObject) {
                        return UserImpl.getInstance().getUserInfo();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));

                    }

                    @Override
                    public void onNext(User user) {
                        hideProgressDialog();
                        showToastMsg("签到成功");
                        EventBus.getDefault().post(new EventParams(EventParams.USER_INFO_CHANGE));
                    }
                }));
    }

    private Observer<List<Banner>> initBanner = new Observer<List<Banner>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Banner> banners) {
            if (banners != null && banners.size() > 0) {
                bannerAdapter.setData(banners);
                bannerAdapter.notifyDataSetChanged();
            }


        }
    };

    private void getBanner() {
        compositeSubscription.add(NewsImpl.getInstance().getBannerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initBanner));
    }

    private void loadBanner() {
        compositeSubscription.add(NewsImpl.getInstance().loadBannerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initBanner));
    }

    private void loadHomeList() {


        compositeSubscription.add(MissionImpl.getInstance().loadProvision(null, 0, 10).subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<Provision>, Observable<List<Requirement>>>() {
                    @Override
                    public Observable<List<Requirement>> call(List<Provision> provisions) {
                        return MissionImpl.getInstance().loadRequirement(null, 0, 10);
                    }
                })
                .flatMap(new Func1<Object, Observable<List<News>>>() {
                    @Override
                    public Observable<List<News>> call(Object o) {
                        return NewsImpl.getInstance().loadNews(0, 20);
                    }
                })
                .subscribe(new Subscriber<List<News>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getHomeList(null);
                    }

                    @Override
                    public void onNext(List<News> newses) {
                        getHomeList(null);
                    }
                }));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHomeList(EventParams eventParams) {
        if (eventParams == null || eventParams.getCode() == EventParams.NEWS_LIST_CHANGE) {
            compositeSubscription.add(MissionImpl.getInstance().getHomeList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Object>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onNext(List<Object> objects) {
                            if (objects != null && objects.size() > 0) {
                                homeAdapter.setDatas(objects);
                                homeAdapter.notifyDataSetChanged();
                            }

                            if(refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
