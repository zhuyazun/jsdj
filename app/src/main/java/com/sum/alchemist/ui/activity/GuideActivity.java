package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.sum.alchemist.R;
import com.sum.alchemist.utils.SpUtil;
import com.sum.alchemist.widget.PageIndexView;

import java.util.ArrayList;
import java.util.List;

/**
 * on 2016/6/6 11:38 by QIU
 * 引导页
 */
public class GuideActivity extends BaseActivity{

    ViewPager viewPager;
    PagerAdapter mAdapter;
    PageIndexView pageIndexView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        viewPager = findView(R.id.view_pager);
        pageIndexView = findView(R.id.pager_index);

        View pager01 = getLayoutInflater().inflate(R.layout.view_guide1, null);
        View pager02 = getLayoutInflater().inflate(R.layout.view_guide2, null);
        View pager03 = getLayoutInflater().inflate(R.layout.view_guide3, null);

        pager03.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
                SpUtil.getInstance().saveBoolenTosp("guide_state", true);
            }
        });

        final List<View> views = new ArrayList<View>();

        views.add(pager01);
        views.add(pager02);
        views.add(pager03);

        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        viewPager.setAdapter(mAdapter);
        pageIndexView.setTotalPage(views.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndexView.setCurrentPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

}
