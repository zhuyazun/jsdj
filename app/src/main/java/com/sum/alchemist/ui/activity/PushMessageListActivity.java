package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.db.PushMsgDao;
import com.sum.alchemist.model.entity.PushMsg;
import com.sum.alchemist.model.entity.WebContent;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.PushMessageAdapter;


/**
 * Created by Administrator on 2016/6/8.
 */
public class PushMessageListActivity extends BaseActivity {


    private PushMessageAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        recyclerView = findView(R.id.recycler_view);
        refreshLayout = findView(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PushMessageAdapter();
        mAdapter.setAdapterItemListener(adapterItemListener);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setDatas(new PushMsgDao().queryList("receiverMillis", true));
    }

    AdapterItemListener<PushMsg> adapterItemListener = new AdapterItemListener<PushMsg>() {
        @Override
        public void onItemClickListener(PushMsg data, int position, int id) {
            if(TextUtils.isEmpty(data.getContent()))
                return;
            WebContent webContent = new WebContent();
            webContent.content = data.getContent();
            Intent intent = new Intent(PushMessageListActivity.this, WebActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(WebActivity.WEB_CONTENT_KEY, webContent);
            startActivity(intent);

        }
    };

    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = findView(R.id.toolbar_title_tv);
        title.setText("消息中心");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_list;
    }
}
