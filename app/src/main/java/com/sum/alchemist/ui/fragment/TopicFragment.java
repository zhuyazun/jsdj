package com.sum.alchemist.ui.fragment;

import android.content.Intent;
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
import com.sum.alchemist.model.db.Topic;
import com.sum.alchemist.model.impl.ForumImpl;
import com.sum.alchemist.ui.activity.ForumByTopicActivity;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.TopicAdapter;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.activity.ForumByTopicActivity.TOPIC_ID;
import static com.sum.alchemist.ui.fragment.ForumAllFragment.LOAD_TYPE_KEY;

/**
 * Created by Qiu on 2016/11/9.
 */

public class TopicFragment extends BaseFragment {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private TopicAdapter mAdapter;
    private int lastVisibleItem = 0;
    private String loadType = "社区论坛";


    /**
     * 生成实例
     * @param loadType 加载类型<li>专家空间</li><li>社区论坛</li>
     * @return {@link TopicFragment}
     */
    public static TopicFragment newInstance(String loadType) {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        args.putString(LOAD_TYPE_KEY, loadType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null)
            savedInstanceState = getArguments();
        if(savedInstanceState.containsKey(LOAD_TYPE_KEY))
            loadType = savedInstanceState.getString(LOAD_TYPE_KEY);
        mAdapter = new TopicAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, null);

        refreshLayout = findView(view, R.id.refresh_layout);
        emptyView = findView(view, R.id.empty);
        recyclerView = findView(view, R.id.recycler_view);
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

        mAdapter.setAdapterItemListener(new AdapterItemListener<Topic>() {
            @Override
            public void onItemClickListener(Topic data, int position, int id) {
                if(id == 0){
                    startActivity(new Intent(getActivity(), ForumByTopicActivity.class)
                            .putExtra(ForumByTopicActivity.TOPIC, data.topic).putExtra(TOPIC_ID, data.id));
                }
            }
        });

        recyclerView.setAdapter(mAdapter);

        if(mAdapter.getItemCount() == 0){
            forceFreshData();
        }
        return view;
    }


    int pageIndex = 0;
    private void onRefreshData(){
        compositeSubscription.add(ForumImpl.getInstance().getTopic(loadType, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Topic>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<Topic> datas) {
                        refreshLayout.setRefreshing(false);
                        pageIndex = 0;
                        mAdapter.setDatas(datas);
                        mAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }
                }));
    }
    private boolean isLoad;
    private void loadMoreDate(){
        if (isLoad)
            return;

        isLoad = true;
        compositeSubscription.add(ForumImpl.getInstance().getTopic(loadType, (pageIndex+1) * 10, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Topic>>() {
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
                    public void onNext(List<Topic> datas) {
                        refreshLayout.setRefreshing(false);
                        pageIndex++;
                        mAdapter.addDatas(datas);
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
