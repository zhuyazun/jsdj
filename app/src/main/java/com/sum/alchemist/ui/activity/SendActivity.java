package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.github.mr5.icarus.Callback;
import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.button.Button;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Province;
import com.sum.alchemist.model.entity.WebContent;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.fragment.MissionFragment.PROVISION;
import static com.sum.alchemist.ui.fragment.MissionFragment.REQUIREMENT;


/**
 * Created by Qiu on 2016/11/5.
 */

public class SendActivity extends BaseActivity{

    private TextInputLayout titleInputLayout;
    private TextInputEditText priceEdit;
    private TextInputEditText contactEdit;
    private WebView webView;
    private Icarus icarus;
    private RadioGroup radioGroup;
    private Spinner categorySp;
    private TextView locationTv;
    private Spinner companySp;
    private Spinner moneySp;
    private Spinner companSizeSp;
    private String location;
    private View view;

    private int sendType = PROVISION;

    @Override
    protected int getContentView() {
        return R.layout.activity_send;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        titleInputLayout = findView(R.id.input_layout);
        priceEdit = findView(R.id.price_edit);
        contactEdit = findView(R.id.contact_edit);
        webView = findView(R.id.web_view);
        radioGroup = findView(R.id.radio_group);
        categorySp = findView(R.id.spinner_category);
        locationTv = findView(R.id.location_tv);
        companySp = findView(R.id.spinner_company);
        moneySp = findView(R.id.spinner_money);
        companSizeSp = findView(R.id.spinner_company_size);
        view = findView(R.id.choose_layout);
        CheckBox moreCb = findView(R.id.more_cb);
        moreCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });


        locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(options1Items != null){
                    parseProvince(null);
                    return;
                }
                addSubscrebe(MissionImpl.getInstance().getProvince(v.getContext())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                showProgressDialog(getString(R.string.loading), false);
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
                                hideProgressDialog();
                            }

                            @Override
                            public void onNext(List<Province> provinces) {
                                hideProgressDialog();
                                parseProvince(provinces);
                            }
                        }));
            }
        });


//供应、求购选择监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.provision:
                        sendType = PROVISION;
                        break;
                    case R.id.requirement:
                        sendType = REQUIREMENT;
                        break;
                }
            }
        });

        initWebView();
    }

    private List<String> options1Items;
    private List<List<String>> options2Items;
    private List<List<List<String>>> options3Items;
    private void parseProvince(List<Province> provinces){
        if(provinces != null && options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
            options3Items = new ArrayList<>();
            MissionImpl.getInstance().parseProvince(provinces, options1Items, options2Items, options3Items);
        }
        DialogUtil.showPikerView(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                location = String.format("%s-%s-%s", options1Items.get(options1),
                        options2Items.get(options1).get(options2),
                        options3Items.get(options1).get(options2).get(options3));
                String displayStr = options2Items.get(options1).get(options2);
                if("全省".equals(displayStr) || "全市".equals(displayStr))
                    displayStr = options1Items.get(options1);
                locationTv.setText(displayStr);
            }
        }, options1Items, options2Items, options3Items);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("发布");
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

    private void initWebView(){
        TextViewToolbar toolbar = new TextViewToolbar();
        Options options = new Options();
        options.setPlaceholder("简单描述一下...");
        options.addAllowedAttributes("img", Arrays.asList("data-type", "data-id", "class", "src", "alt", "width", "height", "data-non-image"));
        options.addAllowedAttributes("iframe", Arrays.asList("data-type", "data-id", "class", "src", "width", "height"));
        options.addAllowedAttributes("a", Arrays.asList("data-type", "data-id", "class", "href", "target", "title"));

        icarus = new Icarus(toolbar, options, webView);
        prepareToolbar(toolbar, icarus);
        icarus.loadCSS("file:///android_asset/editor.css");
        icarus.render();

    }

    private com.github.mr5.icarus.Toolbar prepareToolbar(TextViewToolbar toolbar, Icarus icarus) {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "Simditor.ttf");
        HashMap<String, Integer> generalButtons = new HashMap<>();
        generalButtons.put(Button.NAME_BOLD, R.id.button_bold);
        generalButtons.put(Button.NAME_ITALIC, R.id.button_italic);
        generalButtons.put(Button.NAME_UNDERLINE, R.id.button_underline);
        generalButtons.put(Button.NAME_ALIGN_LEFT, R.id.button_align_left);
        generalButtons.put(Button.NAME_ALIGN_CENTER, R.id.button_align_center);
        generalButtons.put(Button.NAME_ALIGN_RIGHT, R.id.button_align_right);
//        generalButtons.put(Button.NAME_OL, R.id.button_list_ol);
//        generalButtons.put(Button.NAME_CODE, R.id.button_math);
//        generalButtons.put(Button.NAME_BLOCKQUOTE, R.id.button_blockquote);
//        generalButtons.put(Button.NAME_HR, R.id.button_hr);
//        generalButtons.put(Button.NAME_UL, R.id.button_list_ul);
//        generalButtons.put(Button.NAME_INDENT, R.id.button_indent);
//        generalButtons.put(Button.NAME_OUTDENT, R.id.button_outdent);
//        generalButtons.put(Button.NAME_STRIKETHROUGH, R.id.button_strike_through);

        for (String name : generalButtons.keySet()) {
            TextView textView = findView(generalButtons.get(name));
            if (textView == null) {
                continue;
            }
            textView.setTypeface(iconfont);
            TextViewButton button = new TextViewButton(textView, icarus);
            button.setName(name);
            toolbar.addButton(button);
        }

        TextView imageTv = findView(R.id.button_image);
        imageTv.setTypeface(iconfont);
        imageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendActivity.this, UImageActivity.class), UImageActivity.UPLOAD_IMAGE);
            }
        });

        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send){
            icarus.getContent(new Callback() {
                @Override
                public void run(String params) {
                    send(params);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send(String sendContext){

        WebContent webContent = new Gson().fromJson(sendContext, WebContent.class);

        if(webContent == null || TextUtils.isEmpty(webContent.content)){
            showToastMsg("请输入描述");
            return;
        }else{
            sendContext = webContent.content;
        }

        String type = (String) categorySp.getSelectedItem();
        if("类别".equals(type)){
            showToastMsg("请选择一个分类");
            return;
        }
        String location = locationTv.getText().toString();
        if("所在地区".equals(location)){
            showToastMsg("请选择所在地区");
            return;
        }
        String company = (String) companySp.getSelectedItem();
        if("企业性质".equals(company)){
            showToastMsg("请选择企业性质");
            return;
        }
        String money = (String) moneySp.getSelectedItem();
        if("金额范围".equals(money)){
            showToastMsg("请选择金额范围");
            return;
        }
        String companySize = (String) companSizeSp.getSelectedItem();
        if("企业规模".equals(companySize)){
            showToastMsg("请选择企业规模");
            return;
        }
        String contact = contactEdit.getText().toString();
        if(TextUtils.isEmpty(contact)){
            showToastMsg("请填写联系方式");
            return;
        }
        String title = titleInputLayout.getEditText().getText().toString();
        if(TextUtils.isEmpty(title)){
            titleInputLayout.setError("请输入标题");
            return;
        }

        String price = priceEdit.getText().toString();
        if(TextUtils.isEmpty(price)){
            priceEdit.setError("请输入价格");
            return;
        }


        Observable<JsonObject> sendObservable;
        if(sendType == PROVISION){
            sendObservable = MissionImpl.getInstance().putProvision(type, this.location, company, money, companySize, title, price, sendContext, contact);
        }else{
            sendObservable = MissionImpl.getInstance().putRequirement(type, this.location, company, money, companySize, title, price, sendContext, contact);
        }
        addSubscrebe(sendObservable.subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog(getString(R.string.loading), false);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        hideProgressDialog();
                        showToastMsg("发布成功");
                        finish();
                    }
                }));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UImageActivity.UPLOAD_IMAGE && resultCode == RESULT_OK && data != null){
            String url = data.getStringExtra(UImageActivity.IMAGE_URL_KEY);
            url = String.format("<img src=\"%s\" style=\"width:100%%\">", url);
            icarus.insertHtml(url);
        }
    }
}
