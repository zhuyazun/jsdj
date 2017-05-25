package com.sum.alchemist.ui.fragment;

import android.app.ProgressDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Province;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.utils.DateUtil;
import com.sum.alchemist.utils.DialogUtil;
import com.sum.alchemist.widget.MyRadioGroup;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by Qiu on 2016/11/19.
 */

public class FilterDrawer {

    public interface DrawerInterface{
        void save(int index);
        void closeDrawer(int index);
    }

    private View root;
    private DrawerInterface drawerInterface;

    private MyRadioGroup sendDateType;
    private MyRadioGroup moneyType;
    private MyRadioGroup companyType;
    private MyRadioGroup companySizeType;
    private TextView locationTv;
    
    private String sendDate;
    private String money;
    private String location;
    private String company;
    private String companySize;

    public String getSendDate() {
        return sendDate;
    }

    public String getMoney() {
        return money;
    }

    public String getLocation() {
        return location;
    }

    public String getCompany() {
        return company;
    }

    public String getCompanySize() {
        return companySize;
    }

    public FilterDrawer(View root, DrawerInterface drawerInterface){
        this.root = root;
        this.drawerInterface = drawerInterface;
        assignViews();
    }


    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.send_date_cb:
                    sendDateType.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    break;
                case R.id.money_cb:
                    moneyType.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    break;
                case R.id.company_cb:
                    companyType.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    break;
                case R.id.company_size_cb:
                    companySizeType.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    break;
            }
        }
    };

    private void assignViews() {
        CheckBox sendDateCb = (CheckBox) root.findViewById(R.id.send_date_cb);
        sendDateType = (MyRadioGroup) root.findViewById(R.id.send_date_type);
        sendDateType.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.time_all:
                        sendDate = "";
                        break;
                    case R.id.time_2:
                        sendDate = DateUtil.millis2String(DateUtil.addDateByDay(-1, System.currentTimeMillis()).getTime(),
                                "yyyy-MM-dd HH:mm:ss", true);
                        break;
                    case R.id.time_3:
                        sendDate = DateUtil.millis2String(DateUtil.addDateByDay(-3, System.currentTimeMillis()).getTime(),
                                "yyyy-MM-dd HH:mm:ss", true);
                        break;
                    case R.id.time_4:
                        sendDate = DateUtil.millis2String(DateUtil.addDateByDay(-7, System.currentTimeMillis()).getTime(),
                                "yyyy-MM-dd HH:mm:ss", true);
                        break;
                    case R.id.time_5:
                        sendDate = DateUtil.millis2String(DateUtil.addDateByDay(-30, System.currentTimeMillis()).getTime(),
                                "yyyy-MM-dd HH:mm:ss", true);
                        break;
                }
            }
        });
        CheckBox moneyCb = (CheckBox) root.findViewById(R.id.money_cb);
        moneyType = (MyRadioGroup) root.findViewById(R.id.money_type);
        moneyType.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.money_all:
                        money = "";
                        break;
                    case R.id.money_2:
                        money = "2K/月";
                        break;
                    case R.id.money_3:
                        money = "2-3K/月";
                        break;
                    case R.id.money_4:
                        money = "3-4.5K/月";
                        break;
                    case R.id.money_5:
                        money = "4.5-8K/月";
                        break;
                    case R.id.money_6:
                        money = "8-10K/月";
                        break;
                    case R.id.money_7:
                        money = "10K/月以上";
                        break;
                }
            }
        });
        locationTv = (TextView) root.findViewById(R.id.location_tv);

        CheckBox companyCb = (CheckBox) root.findViewById(R.id.company_cb);
        companyType = (MyRadioGroup) root.findViewById(R.id.company_type);
        companyType.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.company_all:
                        company = "";
                        break;
                    case R.id.company_2:
                        company = "合资";
                        break;
                    case R.id.company_3:
                        company = "国企";
                        break;
                    case R.id.company_4:
                        company = "民营";
                        break;
                    case R.id.company_5:
                        company = "上市公司";
                        break;
                }
            }
        });
        CheckBox companySizeCb = (CheckBox) root.findViewById(R.id.company_size_cb);
        companySizeType = (MyRadioGroup) root.findViewById(R.id.company_size_type);
        companyType.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.company_size_all:
                        companySize = "";
                        break;
                    case R.id.company_size_2:
                        companySize = "少于50人";
                        break;
                    case R.id.company_size_3:
                        companySize = "50-150人";
                        break;
                    case R.id.company_size_4:
                        companySize = "150-500人";
                        break;
                    case R.id.company_size_5:
                        companySize = "500人以上";
                        break;
                }
            }
        });

        sendDateCb.setOnCheckedChangeListener(onCheckedChangeListener);
        moneyCb.setOnCheckedChangeListener(onCheckedChangeListener);
        companyCb.setOnCheckedChangeListener(onCheckedChangeListener);
        companySizeCb.setOnCheckedChangeListener(onCheckedChangeListener);
        locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(options1Items != null){
                    parseProvince(null);
                    return;
                }
                MissionImpl.getInstance().getProvince(v.getContext())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                progressDialog = ProgressDialog.show(root.getContext(), null, "正在加载", false, false);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<Province>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if(progressDialog != null)
                                    progressDialog.dismiss();
                            }

                            @Override
                            public void onNext(List<Province> provinces) {
                                if(progressDialog != null)
                                    progressDialog.dismiss();
                                parseProvince(provinces);
                            }
                        });
            }
        });


        root.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDateType.clearCheck();
                moneyType.clearCheck();
                companyType.clearCheck();
                companySizeType.clearCheck();
                locationTv.setText("所在地区");
                sendDate = money = location = company = companySize = "";
            }
        });

        root.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerInterface.save(Gravity.RIGHT);
            }
        });
    }

    private List<String> options1Items;
    private List<List<String>> options2Items;
    private List<List<List<String>>> options3Items;
    private ProgressDialog progressDialog;
    private void parseProvince(List<Province> provinces){
        if(provinces != null && options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
            options3Items = new ArrayList<>();
            MissionImpl.getInstance().parseProvince(provinces, options1Items, options2Items, options3Items);
        }
        DialogUtil.showPikerView(root.getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = String.format("%s-%s-%s", options1Items.get(options1),
                        options2Items.get(options1).get(options2),
                        options3Items.get(options1).get(options2).get(options3));
                locationTv.setText(tx);
                location = tx;
            }
        }, options1Items, options2Items, options3Items);
    }


}
