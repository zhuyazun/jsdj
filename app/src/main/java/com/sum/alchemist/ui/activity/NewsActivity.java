package com.sum.alchemist.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.impl.NewsImpl;
import com.sum.xlog.core.XLog;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/6.
 */

public class NewsActivity extends BaseActivity {
    private static final String TAG = "NewsActivity";

    public static final String NEWS_ID_KEY = "news_id";

    private News news;
    private WebView mWebView;
    private ProgressBar pb;
    private TextView title;
    private TextView showTimes;
    private TextView likeTimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(NEWS_ID_KEY, -1);
        if(id == -1){
            showToastMsg("无效信息");
            finish();
        }else{
            loadNews(id);
        }
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

    @Override
    protected int getContentView() {
        return R.layout.activity_news;
    }

    @Override
    protected void initViewAndListener() {
        mWebView = (WebView) findViewById(R.id.webView);
        pb = (ProgressBar) findViewById(R.id.pb);
        showTimes = findView(R.id.see_number);
        likeTimes = findView(R.id.like_number);
        title = findView(R.id.news_title);

        pb.setMax(100);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDefaultTextEncodingName("UTF-8");
        mWebView.setWebChromeClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    view.loadUrl(url);
                }else {
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }catch (Exception e){
                        XLog.e(TAG, "=== 跳转链接失效 ===", e);
                    }
                }
                return true;
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        findViewById(R.id.like_layout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                likeNew();
            }
        });



    }

    private void loadNews(int id){
        showProgressDialog(getString(R.string.loading), false);
        addSubscrebe(NewsImpl.getInstance().loadNews(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<News>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    hideProgressDialog();
                    showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                }

                @Override
                public void onNext(News news) {
                    hideProgressDialog();
                    if(news != null && !TextUtils.isEmpty(news.content)) {
                        NewsActivity.this.news = news;
                        mWebView.loadData(news.content, "text/html; charset=UTF-8", null);
                        title.setText(news.title);
                        likeTimes.setText(String.valueOf(news.like_times));
                        showTimes.setText(String.valueOf(news.show_times));
                    }else{
                        showToastMsg("信息已经不存在");
                        finish();
                    }

                }
            }));
    }

    private void likeNew(){
        if(news == null)
            return;
        showProgressDialog(getString(R.string.loading), true);
        addSubscrebe(NewsImpl.getInstance().putNewsLike(news.id)
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
                        showToastMsg(aBoolean?"点赞成功":"点赞取消");
                        likeTimes.setText(String.valueOf(news.like_times + (aBoolean?1:-1)));
                    }
                }));
    }
    private class MyWebViewClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if(newProgress==100){
                pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
