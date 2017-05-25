package com.sum.alchemist.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Forum;
import com.sum.alchemist.model.impl.ForumImpl;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.CommentAdapter;
import com.sum.alchemist.ui.adapter.ImageViewAdapter;
import com.sum.alchemist.utils.DateUtil;
import com.sum.alchemist.utils.DialogUtil;
import com.sum.alchemist.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.Config.HttpConfig.getToken;
import static com.sum.alchemist.R.id.comment_layout;

/**
 * Created by Qiu on 2016/11/12.
 */

public class ForumDetailActivity extends BaseActivity implements CommentAdapter.CommentCall {

    private TextView title;
    private TextView dateTv;
    private TextView userNameTv;
    private ImageView userAvatarIv;
    private WebView content;
    private GridView gridView;
    private TextView likeTv;
    private TextView showTv;
    private TextView commentTv;
    private TextView commentCountTv;
    private RecyclerView recyclerView;

    private CommentAdapter adapter;
    private Forum forum;


    private SwipeRefreshLayout refreshLayout;
    private EditText editText;

    public final static String FORUM_ID_KEY = "forum_id";
    public final static String USER_AVATAR_KEY = "user_avatar";
    public final static String USER_NAME_KEY = "user_name";
    public final static String COMMEND_ABOLISH_KEY = "commend_abolish";

    int id;
    boolean isCommend;

    @Override
    protected int getContentView() {
        return R.layout.activity_forum_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getIntExtra(FORUM_ID_KEY, -1);
        String userAvatar = getIntent().getStringExtra(USER_AVATAR_KEY);
        String userName = getIntent().getStringExtra(USER_NAME_KEY);
        if(id == -1){
            showToastMsg("无效信息");
            finish();
        }else{
            if(!TextUtils.isEmpty(userAvatar)) {
                Glide.with(this).load(userAvatar).into(userAvatarIv);
            }
            if(!TextUtils.isEmpty(userName))
                userNameTv.setText(userName);
            showProgressDialog(getString(R.string.loading), false);
            loadForum(id);
        }
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        isCommend = getIntent().getBooleanExtra(COMMEND_ABOLISH_KEY, true);
        if(!isCommend){
            findViewById(R.id.comment_layout).setVisibility(View.GONE);
        }

        View header = getLayoutInflater().inflate(R.layout.item_form_detail, null);

        title = (TextView) header.findViewById(R.id.title);
        content = (WebView) header.findViewById(R.id.content);
        gridView = (GridView) header.findViewById(R.id.grid_view);
        likeTv = (TextView) header.findViewById(R.id.likeTv);
        dateTv = (TextView) header.findViewById(R.id.date);
        userNameTv = (TextView) header.findViewById(R.id.user_name);
        userAvatarIv = (ImageView) header.findViewById(R.id.user_avatar);
        commentTv = (TextView) header.findViewById(R.id.commentTv);
        showTv = (TextView) header.findViewById(R.id.showTv);
        commentCountTv = (TextView) header.findViewById(R.id.commentCount);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ForumDetailActivity.this));

        WebSettings webSettings = content.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");

        adapter = new CommentAdapter(this);
        adapter.setHaederView(header);
        adapter.setAdapterItemListener(adapterItemListener);
        recyclerView.setAdapter(adapter);
        refreshLayout = findView(R.id.refresh_layout);
        editText = findView(R.id.comment_edit);
        findViewById(R.id.send).setOnClickListener(listener);
        likeTv.setOnClickListener(listener);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadForum(id);
            }
        });

    }

    AdapterItemListener<Forum.CommentsBean> adapterItemListener = new AdapterItemListener<Forum.CommentsBean>() {
        @Override
        public void onItemClickListener(Forum.CommentsBean data, int position, int id) {
            if(id == R.id.likeTv){
                doCommentLike(data);
            }
        }
    };

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.send:
                    doComment();
                    break;
                case R.id.likeTv:
                    doLike();
                    break;
                case comment_layout:
                    if(forum != null) {
                        if(UserImpl.getInstance().getUserDao().queryLoginUser() != null) {
                            DialogUtil.showCommentDialog(ForumDetailActivity.this, forum.id, new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    addComment(s);
                                }
                            });
                        }else{
                            startActivity(new Intent(ForumDetailActivity.this, LoginActivity.class));
                        }
                    }
                    break;
            }
        }
    };

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

    private void loadForum(int id){

        addSubscrebe(ForumImpl.getInstance().loadForum(getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Forum>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        if(refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(Forum forum) {
                        hideProgressDialog();
                        if(refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                        if(forum != null) {
                            initForum(forum);
                        }else{
                            showToastMsg("信息已经不存在");
                            finish();
                        }

                    }
                }));
    }

    private void initForum(Forum forum){
        this.forum = forum;
        title.setText(forum.title);
        dateTv.setText(StringUtil.getShortDate(forum.updated_at));
        content.loadData(forum.content, "text/html; charset=UTF-8", null);
        showTv.setText(String.valueOf(forum.show_times));
        likeTv.setText(String.valueOf(forum.like_num));
        commentTv.setText(String.valueOf(forum.comment_num));
        if(!TextUtils.isEmpty(forum.img)) {
            ListAdapter adapter = gridView.getAdapter();
            if (adapter == null) {
                adapter = new ImageViewAdapter();
                gridView.setAdapter(adapter);
                ((ImageViewAdapter)adapter).setItemListener(new AdapterItemListener<String>() {
                    @Override
                    public void onItemClickListener(String data, int position, int id) {
                        Intent intent = ImageViewActivity.getIntent(ForumDetailActivity.this, data);
                        if(intent != null){
                            startActivity(intent);
                        }
                    }
                });
            }
            ((ImageViewAdapter) adapter).setData(Arrays.asList(forum.img.split("\\|")));
            ((ImageViewAdapter) adapter).notifyDataSetChanged();
        }
        if(isCommend){
            adapter.setDatas(forum.comments);
            adapter.notifyDataSetChanged();
        }
    }

    private void doComment(){
        final String string = editText.getText().toString();
        if(TextUtils.isEmpty(string)){
            showToastMsg("评论不能为空");
        }else{
            showProgressDialog(getString(R.string.loading), true);
            addSubscrebe(ForumImpl.getInstance().putForumComment(forum.id, string).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(JsonObject s) {
                            hideProgressDialog();
                            addComment(string);
                            showToastMsg("评论成功");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            editText.setText("");
                            title.setFocusable(true);
                            title.setFocusableInTouchMode(true);
                        }
                    }));
        }
    }

    private void addComment(String s){
        Forum.CommentsBean commentsBean = new Forum.CommentsBean();
        commentsBean.user = UserImpl.getInstance().getUserDao().queryLoginUser();
        commentsBean.content = s;
        String date = DateUtil.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss", false);
        commentsBean.created_at = date;
        commentsBean.updated_at = date;
        commentsBean.like = "false";
        commentsBean.like_times = 0;

        List<Forum.CommentsBean> datas = adapter.getDatas();
        if(datas == null)
            datas = new ArrayList<>();

        datas.add(0, commentsBean);
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }


    private void doCommentLike(final Forum.CommentsBean commentsBean){
        if(forum != null) {
            showProgressDialog(getString(R.string.loading), false);
            addSubscrebe(ForumImpl.getInstance().commentLike(commentsBean.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            hideProgressDialog();
                            if(jsonObject != null){

                                boolean isLike = "true".equals(jsonObject.get("liked").getAsString());
                                if(jsonObject.get("liked").getAsString().equals(commentsBean.like)){
                                    adapter.setLikeText(String.valueOf(commentsBean.like_times));
                                }else{
                                    adapter.setLikeText(String.valueOf(commentsBean.like_times + (isLike?1:-1)));
                                }
                                showToastMsg(isLike?"点赞成功":"取消点赞");
                            }
                        }
                    }));
        }
    }

    private void doLike(){
        if(forum != null) {
            showProgressDialog(getString(R.string.loading), false);
            addSubscrebe(ForumImpl.getInstance().putForumLike(forum.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JsonObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            hideProgressDialog();
                            if(jsonObject != null){

                                boolean isLike = "true".equals(jsonObject.get("liked").getAsString());
                                if(jsonObject.get("liked").getAsString().equals(forum.like)){
                                    likeTv.setText(String.valueOf(forum.like_num));
                                }else{
                                    likeTv.setText(String.valueOf(forum.like_num + (isLike?1:-1)));
                                }
                                showToastMsg(isLike?"点赞成功":"取消点赞");
                            }
                        }
                    }));
        }
    }

    @Override
    public void setCommentCount(int count) {
        if(commentCountTv != null)
            commentCountTv.setText(String.format("留言(%s)", count));
    }
}
