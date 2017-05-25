package com.sum.alchemist.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;

/**
 * Created by Qiu on 2016/11/17.
 */

public class ImageViewActivity extends BaseActivity{

    ImageView imageView;

    public static Intent getIntent(Context context, String url){
        if(TextUtils.isEmpty(url) || context == null){
            return null;
        }
        return new Intent(context, ImageViewActivity.class).putExtra("url", url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Glide.with(this).load(getIntent().getStringExtra("url")).into(imageView);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        imageView = findView(R.id.image_view);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_image;
    }
}
