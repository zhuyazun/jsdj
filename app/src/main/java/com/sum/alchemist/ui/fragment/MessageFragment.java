package com.sum.alchemist.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Message;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.adapter.MessageAdapter;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/12.
 */

public class MessageFragment extends BaseFragment {

    private MessageAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private int lastVisibleItem;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new MessageAdapter();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        emptyView = view.findViewById(R.id.empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });

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
        recyclerView.setAdapter(adapter);
        forceFreshData();

        return view;
    }

    private int page = 0;

    /**
     * 刷新列表数据
     */
    public void onRefreshData() {
        compositeSubscription.add(UserImpl.getInstance().getUserMessage(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<Message> datas) {
                        refreshLayout.setRefreshing(false);
                        page = 0;
                        adapter.setDatas(datas);
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }
                }));
    }

    private boolean isLoad;

    public void loadMoreDate() {
        if (isLoad)
            return;

        isLoad = true;
        compositeSubscription.add(UserImpl.getInstance().getUserMessage((page + 1) * 10, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
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
                    public void onNext(List<Message> datas) {
                        refreshLayout.setRefreshing(false);
                        page++;
                        adapter.addDatas(datas);
                        adapter.notifyDataSetChanged();
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
