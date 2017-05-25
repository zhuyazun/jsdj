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
import com.sum.alchemist.model.entity.Forum;
import com.sum.alchemist.model.impl.ForumImpl;
import com.sum.alchemist.ui.activity.ForumDetailActivity;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.ForumAdapter;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.Config.HttpConfig.getToken;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.COMMEND_ABOLISH_KEY;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.FORUM_ID_KEY;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.USER_AVATAR_KEY;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.USER_NAME_KEY;

/**
 * Created by Qiu on 2016/11/9.
 */

public class ForumAllFragment extends BaseFragment {
    public final static String LOAD_TYPE_KEY = "load_type";
    public final static String IS_MY_KEY = "is_my";
    private String loadType = "all";
    private boolean isMy = false;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private ForumAdapter mAdapter;
    private int lastVisibleItem = 0;


    public static ForumAllFragment newInstance(String loadType) {
        return newInstance(loadType, false);
    }

    /**
     * 生成实例
     * @param loadType 加载类型<li>all</li><li>admin</li><li>image</li><li>topicValue</li>
     * @return {@link ForumAllFragment}
     */
    public static ForumAllFragment newInstance(String loadType, boolean isMy) {
        ForumAllFragment fragment = new ForumAllFragment();
        Bundle args = new Bundle();
        args.putString(LOAD_TYPE_KEY, loadType);
        args.putBoolean(IS_MY_KEY, isMy);
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
        if(savedInstanceState.containsKey(IS_MY_KEY))
            isMy = savedInstanceState.getBoolean(IS_MY_KEY);
        mAdapter = new ForumAdapter();
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

        mAdapter.setAdapterItemListener(new AdapterItemListener() {
            @Override
            public void onItemClickListener(Object data, int position, int id) {
                if(data instanceof Forum && id == 0){
                    Forum forum = (Forum) data;
                    Intent intent = new Intent(getActivity(), ForumDetailActivity.class)
                            .putExtra(FORUM_ID_KEY, forum.id)
                            .putExtra(USER_AVATAR_KEY, forum.user.avatar)
                            .putExtra(USER_NAME_KEY, forum.user.getDiaplayName());
                    if("admin".equals(loadType))
                        intent.putExtra(COMMEND_ABOLISH_KEY, false);
                    startActivity(intent);
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
        Observable<List<Forum>> observable = null;
        if("all".equals(loadType) || "img".equals(loadType) || "admin".equals(loadType) || "my".equals(loadType)){
            observable = ForumImpl.getInstance().loadForumList(isMy?getToken():null, loadType, 0, 10);
        }else{
            observable = ForumImpl.getInstance().getForumByTopic(isMy?getToken():null, loadType, 0, 10);
        }
        compositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Forum>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<Forum> datas) {
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
        Observable<List<Forum>> observable = null;
        if("all".equals(loadType) || "img".equals(loadType) || "admin".equals(loadType) || "my".equals(loadType)){
            observable = ForumImpl.getInstance().loadForumList(isMy?getToken():null, loadType, (pageIndex+1) * 10, 10);
        }else{
            observable = ForumImpl.getInstance().getForumByTopic(isMy?getToken():null, loadType, (pageIndex+1) * 10, 10);
        }
        compositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Forum>>() {
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
                    public void onNext(List<Forum> datas) {
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
