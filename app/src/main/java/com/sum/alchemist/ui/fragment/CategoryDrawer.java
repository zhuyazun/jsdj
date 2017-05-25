package com.sum.alchemist.ui.fragment;

import android.view.Gravity;
import android.view.View;

import com.sum.alchemist.R;

/**
 * Created by Qiu on 2016/11/20.
 */

public class CategoryDrawer {

    private View root;
    private FilterDrawer.DrawerInterface drawerInterface;
    private String category;

    public String getCategory() {
        return category;
    }

    public CategoryDrawer(View root, FilterDrawer.DrawerInterface drawerInterface) {
        this.root = root;
        this.drawerInterface = drawerInterface;
        assignViews();
    }


    private void assignViews() {


        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.back:
                        drawerInterface.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.all:
                        category = "";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_1:
                        category = "人才";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_2:
                        category = "技术";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_3:
                        category = "技术";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_4:
                        category = "设备";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_5:
                        category = "专利";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                    case R.id.category_6:
                        category = "其他";
                        drawerInterface.save(Gravity.LEFT);
                        break;
                }
            }
        };

        root.findViewById(R.id.back).setOnClickListener(listener);
        root.findViewById(R.id.all).setOnClickListener(listener);
        root.findViewById(R.id.category_1).setOnClickListener(listener);
        root.findViewById(R.id.category_2).setOnClickListener(listener);
        root.findViewById(R.id.category_3).setOnClickListener(listener);
        root.findViewById(R.id.category_4).setOnClickListener(listener);
        root.findViewById(R.id.category_5).setOnClickListener(listener);
        root.findViewById(R.id.category_6).setOnClickListener(listener);



    }

}
