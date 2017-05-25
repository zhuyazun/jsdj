package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Forum;
import com.sum.alchemist.model.impl.ForumImpl;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.ForumAdapter;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.activity.ForumDetailActivity.FORUM_ID_KEY;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.USER_AVATAR_KEY;
import static com.sum.alchemist.ui.activity.ForumDetailActivity.USER_NAME_KEY;

/**
 * Created by Qiu on 2016/11/16.
 */

public class SearchForumActivity extends BaseActivity{

    private ImageView backIv;
    private EditText searchEdit;
    private RecyclerView recyclerView;
    private ForumAdapter mAdapter;
    private View empty;
    private int lastVisibleItem = 0;
    private String key;

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        backIv = (ImageView) findViewById(R.id.back_iv);
        searchEdit = (EditText) findViewById(R.id.search_edit);
        empty = findViewById(R.id.empty);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.search_iv).setOnClickListener(listener);
        backIv.setOnClickListener(listener);

        mAdapter = new ForumAdapter();
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
                    startActivity(new Intent(SearchForumActivity.this, ForumDetailActivity.class)
                            .putExtra(FORUM_ID_KEY, forum.id)
                            .putExtra(USER_AVATAR_KEY, forum.user.avatar)
                            .putExtra(USER_NAME_KEY, forum.user.getDiaplayName()));
                }
            }
        });

        recyclerView.setAdapter(mAdapter);



    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_iv:
                    finish();
                    break;
                case R.id.search_iv:
                    onRefreshData(searchEdit.getText().toString());
                    break;
            }
        }
    };


    int pageIndex = 0;
    private void onRefreshData(String key){
        if(TextUtils.isEmpty(key))
            return;
        this.key = key;
        addSubscrebe(ForumImpl.getInstance().search(key, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Forum>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<Forum> datas) {
                        pageIndex = 0;
                        mAdapter.setDatas(datas);
                        mAdapter.notifyDataSetChanged();
                        empty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }
                }));
    }
    private boolean isLoad;
    private void loadMoreDate(){
        if (isLoad || TextUtils.isEmpty(key))
            return;

        isLoad = true;
        addSubscrebe(ForumImpl.getInstance().search(key, (pageIndex+1) * 10, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Forum>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        isLoad = false;
                    }

                    @Override
                    public void onNext(List<Forum> datas) {
                        pageIndex++;
                        mAdapter.addDatas(datas);
                        mAdapter.notifyDataSetChanged();
                        isLoad = false;
                    }
                }));
    }
}
