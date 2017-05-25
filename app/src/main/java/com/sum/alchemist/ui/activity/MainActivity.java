package com.sum.alchemist.ui.activity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.sum.alchemist.R;
import com.sum.alchemist.ui.fragment.HomeFragment;
import com.sum.alchemist.ui.fragment.MissionFragment;
import com.sum.alchemist.ui.fragment.StoreFragment;
import com.sum.alchemist.ui.fragment.UserFragment;
import com.sum.alchemist.utils.DialogUtil;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    public static final String SELECT_INDEX = "select_index";

    private ViewPager mViewPager;
    private LinearLayout homeTab;
    private LinearLayout missionTab;
    private LinearLayout storeTab;
    private LinearLayout userTab;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSwipeBackEnable(false);


    }
      //左右滑动
    @Override
    protected void parserIntent(Intent intent) {
        super.parserIntent(intent);
        int select_index = intent.getIntExtra(SELECT_INDEX, 0);
        if (select_index != index && mViewPager != null) {
            mViewPager.setCurrentItem(index = select_index);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initTitle() {
        super.initTitle();


    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_bottom_main:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.home_bottom_mission:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.home_bottom_send:
                    startActivity(new Intent(MainActivity.this, SendActivity.class));//跳转页面
                    break;
                case R.id.home_bottom_store:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.home_bottom_me:
                    mViewPager.setCurrentItem(3);
                    break;
            }
        }
    };

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        homeTab = (LinearLayout) findViewById(R.id.home_bottom_main);
        missionTab = (LinearLayout) findViewById(R.id.home_bottom_mission);
        storeTab = (LinearLayout) findViewById(R.id.home_bottom_store);
        userTab = (LinearLayout) findViewById(R.id.home_bottom_me);

        homeTab.setOnClickListener(listener);
        missionTab.setOnClickListener(listener);
        findViewById(R.id.home_bottom_send).setOnClickListener(listener);
        storeTab.setOnClickListener(listener);
        userTab.setOnClickListener(listener);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position) {
                    default:
                        fragment = new HomeFragment();
                        break;
                    case 1:
                        fragment = new MissionFragment();
                        break;
                    case 2:
                        fragment = new StoreFragment();
                        break;
                    case 3:
                        fragment = new UserFragment();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        switchTab(index);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switchTab(index = position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void switchTab(int index) {
        homeTab.setSelected(false);
        missionTab.setSelected(false);
        storeTab.setSelected(false);
        userTab.setSelected(false);
        switch (index) {
            case 0:
                homeTab.setSelected(true);
                break;
            case 1:
                missionTab.setSelected(true);
                break;
            case 2:
                storeTab.setSelected(true);
                break;
            case 3:
                userTab.setSelected(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showExitDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }


    public void gotoPage(int index) {
        if (index >= 0 && index < 4) {
            mViewPager.setCurrentItem(index);
        }
    }


}
