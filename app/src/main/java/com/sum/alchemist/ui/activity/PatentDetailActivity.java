package com.sum.alchemist.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Patent;
import com.sum.alchemist.model.impl.NewsImpl;
import com.sum.alchemist.utils.DialogUtil;
import com.sum.xlog.core.XLog;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/6.
 */

public class PatentDetailActivity extends BaseActivity {
    private static final String TAG = "PatentDetailActivity";

    public static final String PATENT_ID_KEY = "patent_id";

    private ImageView imageView;
    private TextView title;
    private TextView price;
    private WebView mWebView;

    private Patent patent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(PATENT_ID_KEY, -1);
        if(id == -1){
            showToastMsg("无效信息");
            finish();
        }else{
            loadPatent(id);
        }
    }
    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("专利详情");
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
        return R.layout.activity_patent_detail;
    }

    @Override
    protected void initViewAndListener() {
        mWebView = (WebView) findViewById(R.id.web_view);

        imageView = findView(R.id.image);
        title = findView(R.id.title);
        price = findView(R.id.price);

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



    }

    private void loadPatent(int id){
        showProgressDialog(getString(R.string.loading), false);
        addSubscrebe(NewsImpl.getInstance().loadPatent(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Patent>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    hideProgressDialog();
                    showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                }

                @Override
                public void onNext(Patent patent) {
                    hideProgressDialog();
                    PatentDetailActivity.this.patent = patent;
                    if(patent != null && !TextUtils.isEmpty(patent.getContent())) {

                        Glide.with(PatentDetailActivity.this).load(patent.getLogo_img()).into(imageView);
                        title.setText(patent.getTitle());
                        price.setText(patent.getPrice());
                        mWebView.loadData(patent.getContent(), "text/html; charset=UTF-8", null);
                    }else{
                        showToastMsg("信息已经不存在");
                        finish();
                    }

                }
            }));
    }

    private void contact(){
        if(patent != null) {
            showProgressDialog(getString(R.string.loading), false);
            addSubscrebe(NewsImpl.getInstance().loadPatentContact(patent.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(String s) {
                            hideProgressDialog();
                            DialogUtil.showContectDialog(PatentDetailActivity.this, String.format("联系方式:%s", s));
                        }
                    }));
        }
    }


    private class MyWebViewClient extends WebChromeClient {
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PatentDetailActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            contact();
        }
        return super.onOptionsItemSelected(item);
    }
}
