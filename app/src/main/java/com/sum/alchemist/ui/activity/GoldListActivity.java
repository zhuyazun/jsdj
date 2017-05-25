package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.GoldLog;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.adapter.GoldLogAdapter;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/3.
 */

public class GoldListActivity extends BaseActivity {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private GoldLogAdapter mAdapter;
    private int lastVisibleItem = 0;

    @Override
    protected int getContentView() {
        return R.layout.activity_gold_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        emptyView = findViewById(R.id.empty);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        mAdapter = new GoldLogAdapter();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && lastVisibleItem >= recyclerView.getAdapter().getItemCount() - 1
                        && lastVisibleItem >= 9) {
                    loadMoreDate();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
        recyclerView.setAdapter(mAdapter);
        forceFreshData();
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("我的金币");
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

    int pageIndex = 0;

    /**
     * 刷新列表数据
     */
    public void onRefreshData() {
        addSubscrebe(UserImpl.getInstance().getGoldLogUser(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GoldLog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<GoldLog> requirements) {
                        refreshLayout.setRefreshing(false);
                        pageIndex = 0;
                        mAdapter.setDatas(requirements);
                        mAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }
                }));
    }

    private boolean isLoad;

    public void loadMoreDate() {
        if (isLoad)
            return;

        isLoad = true;
        addSubscrebe(UserImpl.getInstance().getGoldLogUser((pageIndex + 1) * 10, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GoldLog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        isLoad = false;
                    }

                    @Override
                    public void onNext(List<GoldLog> goldLogs) {
                        refreshLayout.setRefreshing(false);
                        pageIndex++;
                        mAdapter.addDatas(goldLogs);
                        mAdapter.notifyDataSetChanged();
                        isLoad = false;
                    }
                }));
    }

    /**
     * 带动画强制刷新
     */
    public void forceFreshData() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    onRefreshData();
                }
            });
        }
    }
}
