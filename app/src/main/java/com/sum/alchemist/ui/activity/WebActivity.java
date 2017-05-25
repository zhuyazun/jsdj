package com.sum.alchemist.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import com.sum.alchemist.model.entity.WebContent;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2016/11/6.
 */

public class WebActivity extends BaseActivity {
    private static final String TAG = "WebActivity";
    public static final String URL_ID_KEY = "url";
    public static final String WEB_CONTENT_KEY = "web_content";
    private WebView mWebView;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra(URL_ID_KEY)){
            loadByUrl();
        }else if(getIntent().hasExtra(WEB_CONTENT_KEY)){
            loadByWebContent();
        }else{
            showToastMsg("无效信息");
            finish();
        }
    }

    private void loadByUrl(){
        String url = getIntent().getStringExtra(URL_ID_KEY);
        mWebView.loadUrl(url);
    }

    private void loadByWebContent(){
        WebContent webContent = (WebContent) getIntent().getSerializableExtra(WEB_CONTENT_KEY);
        mWebView.loadData(webContent.content, "text/html; charset=UTF-8", null);
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
        return R.layout.activity_web;
    }

    @Override
    protected void initViewAndListener() {
        mWebView = (WebView) findViewById(R.id.webView);
        pb = (ProgressBar) findViewById(R.id.pb);

        pb.setMax(100);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
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



    }


    private class MyWebViewClient extends WebChromeClient {

        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if(newProgress > 98){
                pb.setVisibility(View.GONE);
            }else{
                pb.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }

        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
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
