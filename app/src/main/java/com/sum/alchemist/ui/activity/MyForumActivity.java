package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.ui.fragment.ForumAllFragment;
import com.sum.alchemist.ui.fragment.MessageFragment;

/**
 * Created by Qiu on 2016/11/12.
 */

public class MyForumActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();


        viewPager = findView(R.id.view_pager);
        tabLayout = findView(R.id.tab_layout);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position){
                    default:
                        fragment = ForumAllFragment.newInstance("all", true);
                        break;
                    case 1:
                        fragment = ForumAllFragment.newInstance("my", true);
                        break;
                    case 2:
                        fragment = new MessageFragment();
                        break;
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title;
                switch (position){
                    default:
                        title = "我的发帖";
                        break;
                    case 1:
                        title = "我参与的";
                        break;
                    case 2:
                        title = "消息通知";
                        break;
                }
                return title;
            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("我的社区");
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
        return R.layout.activity_my_forum;
    }


}
