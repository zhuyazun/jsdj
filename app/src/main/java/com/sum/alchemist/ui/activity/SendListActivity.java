package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.SendLogAdapter;
import com.sum.xlog.core.XLog;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.activity.MissionDetailActivity.ID_KEY;
import static com.sum.alchemist.ui.activity.MissionDetailActivity.MISSION_TYPE_KEY;
import static com.sum.alchemist.ui.fragment.MissionFragment.PROVISION;
import static com.sum.alchemist.ui.fragment.MissionFragment.REQUIREMENT;

/**
 * Created by Qiu on 2016/11/3.
 */

public class SendListActivity extends BaseActivity {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private SendLogAdapter mAdapter;
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
        findViewById(R.id.detail).setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        mAdapter = new SendLogAdapter();
        mAdapter.setAdapterItemListener(adapterItemListener);

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
        titleTv.setText("我的发布");
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
    AdapterItemListener<Object> adapterItemListener = new AdapterItemListener<Object>() {
        @Override
        public void onItemClickListener(Object data, int position, int id) {
            if(data instanceof Provision && id == 0){
                Provision pro = (Provision) data;

                startActivity(new Intent(SendListActivity.this, MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, PROVISION).putExtra(ID_KEY, pro.id));
            }else if(data instanceof Requirement && id == 0){
                Requirement req = (Requirement) data;

                startActivity(new Intent(SendListActivity.this, MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, REQUIREMENT).putExtra(ID_KEY, req.id));
            }else if(data instanceof Provision && id == R.id.menu_delete){
                Provision pro = (Provision) data;
                delete(0, pro.id, position);
            }else if(data instanceof Requirement && id == R.id.menu_delete){
                Requirement req = (Requirement) data;
                delete(1, req.id, position);
            }
        }
    };
    /**
     * 刷新列表数据
     */
    public void onRefreshData() {
        addSubscrebe(UserImpl.getInstance().getUserSend(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<Object> datas) {
                        refreshLayout.setRefreshing(false);
                        pageIndex = 0;
                        mAdapter.setDatas(datas);
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
        addSubscrebe(UserImpl.getInstance().getUserSend((pageIndex+1) * 10, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Object>>() {
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
                    public void onNext(List<Object> datas) {
                        refreshLayout.setRefreshing(false);
                        pageIndex++;
                        mAdapter.addDatas(datas);
                        mAdapter.notifyDataSetChanged();
                        isLoad = false;
                    }
                }));
    }

    public void delete(int type, int id, final int position){
        addSubscrebe(UserImpl.getInstance().deleteMission(type, id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog("正在删除...", false);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLog.e("sen", e);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        hideProgressDialog();
                    }

                    @Override
                    public void onNext(JsonObject o) {
                        hideProgressDialog();
                        mAdapter.getDatas().remove(position);
                        mAdapter.notifyItemRemoved(position);

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
