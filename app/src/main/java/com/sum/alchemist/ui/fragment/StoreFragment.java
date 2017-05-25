package com.sum.alchemist.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sum.alchemist.R;
import com.sum.alchemist.ui.activity.SearchForumActivity;

/**
 * Created by Qiu on 2016/10/16.
 */
public class StoreFragment extends BaseFragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, null);

        viewPager = findView(view, R.id.view_pager);
        tabLayout = findView(view, R.id.tab_layout);


        findView(view, R.id.search_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchForumActivity.class));
            }
        });
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position){
                    default:
                        fragment = TopicFragment.newInstance("社区论坛");
                        break;
                    case 1:
                        fragment = ForumAllFragment.newInstance("admin");
                        break;
                    case 2:
                        fragment = TopicFragment.newInstance("专家空间");
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
                        title = "社区论坛";
                        break;
                    case 1:
                        title = "消息通知";
                        break;
                    case 2:
                        title = "专家空间";
                        break;
                }
                return title;
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
