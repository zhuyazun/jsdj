package com.sum.alchemist.ui.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Banner;
import com.sum.alchemist.ui.activity.WebActivity;

import java.util.List;

import static com.sum.alchemist.ui.activity.WebActivity.URL_ID_KEY;

/**
 * Created by Qiu on 2016/10/16.
 */
public class BannerAdapter extends PagerAdapter {

    private List<Banner> banners;

    public Banner getItem(int position) {
        if (getCount() > position && position >= 0)
            return banners.get(position);
        return null;
    }

    public void setData(List<Banner> banners) {
        this.banners = banners;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        final View view = View.inflate(container.getContext(), R.layout.item_banner, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        final Banner banner = getItem(position);
        Glide.with(container.getContext()).load(banner.img).into(imageView);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.getContext().startActivity(new Intent(view.getContext(), WebActivity.class)
                        .putExtra(URL_ID_KEY, banner.url));
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return banners != null ? banners.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
