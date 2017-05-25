package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.City;
import com.sum.alchemist.model.impl.ForumImpl;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.ImageViewAdapter;
import com.sum.alchemist.widget.MyGridView;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.sum.alchemist.ui.activity.ForumByTopicActivity.TOPIC_ID;

/**
 * Created by Qiu on 2016/11/10.
 */

public class SendForumActivity extends BaseActivity {

    public static final String add_image = "drawable://" + R.drawable.add_image;

    private TextInputLayout textInputLayout;
    private EditText contentTv;
    private MyGridView gridView;
    private TextView cityTv;
    private ImageViewAdapter adapter;
    private int topic_id;

    private List<String> uploadList;
    private String city;
    private String title;
    private String content;

    @Override
    protected int getContentView() {
        return R.layout.activity_send_forum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic_id = getIntent().getIntExtra(TOPIC_ID, 0);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();

        textInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout);
        contentTv = (EditText) findViewById(R.id.content);
        gridView = (MyGridView) findViewById(R.id.grid_view);
        cityTv = findView(R.id.city);
        adapter = new ImageViewAdapter();
        adapter.setItemListener(itemListener);

        cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCityInfo();
            }
        });

        ArrayList<String> data = new ArrayList<>();
        data.add(add_image);
        adapter.setData(data);

        gridView.setAdapter(adapter);
    }

    private AdapterItemListener<String> itemListener = new AdapterItemListener<String>() {
        @Override
        public void onItemClickListener(String data, int position, int id) {
            if (add_image.equals(data)) {
                // 添加照片
                new Picker.Builder(SendForumActivity.this, pickListener, R.style.MIP_theme)
                        .setPickMode(Picker.PickMode.MULTIPLE_IMAGES)
                        .setLimit(10 - adapter.getCount())
                        .build()
                        .startActivity();
            } else {
                //删除或者浏览
            }
        }
    };

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("发贴");
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

    Picker.PickListener pickListener = new Picker.PickListener() {

        @Override
        public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
            for (ImageEntry image : images) {
                if (adapter.getCount() < 9) {
                    adapter.insetData(image.path);
                } else if (adapter.getCount() == 9) {
                    adapter.removeData(8);
                    adapter.insetData(image.path);
                } else {
                    break;
                }
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancel() {

        }
    };

    private void getCityInfo(){
        showProgressDialog("正在定位...", true);
        try {
            Call call = RetrofitHelper.getInstance().okHttpClient.newCall(
                    new Request.Builder().url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json").get().build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            showToastMsg("定位失败");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    City city = new Gson().fromJson(response.body().string(), City.class);
                    if(city != null) {
                        SendForumActivity.this.city = city.province + city.city;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                                cityTv.setText(SendForumActivity.this.city);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            showToastMsg("定位失败");
            hideProgressDialog();
        }
    }

    private void send() {
        title = textInputLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(title)) {
            textInputLayout.setError("标题不能为空!");
            return;
        }

        content = contentTv.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToastMsg("内容不能为空!");
            return;
        }

        if (TextUtils.isEmpty(city)) {
            showToastMsg("为了方便沟通建议您使用定位!");
            city = "未知城市";
            return;
        }
        uploadImage();

    }
    Subscription subscription;
    private void uploadImage() {
        //发送图片
        adapter.getLists().remove(add_image);
        if (adapter.getLists().size() > 1) {
            uploadList = new ArrayList<>();
            showProgressDialog("正在上传", false);
            subscription = Observable.from(adapter.getLists())
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<String, Observable<File>>() {
                        @Override
                        public Observable<File> call(String s) {
                            return Luban.get(SendForumActivity.this)
                                    .load(new File(s))
                                    .putGear(Luban.THIRD_GEAR)
                                    .asObservable();
                        }
                    })
                    .flatMap(new Func1<File, Observable<String>>() {
                        @Override
                        public Observable<String> call(File file) {
                            return MissionImpl.getInstance().uploadFile(file);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            uploadForum();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                            subscription.unsubscribe();
                        }

                        @Override
                        public void onNext(String s) {
                            uploadList.add(s);
                        }
                 });

            addSubscrebe(subscription);

        }else{
            uploadForum();
        }
    }

    private void uploadForum() {
        String img = null;
        if(uploadList != null && uploadList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : uploadList) {
                stringBuilder.append(string).append("|");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            img = stringBuilder.toString();
        }
        addSubscrebe(ForumImpl.getInstance().putForum(title, city, content, topic_id, img).subscribeOn(Schedulers.io())
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
                        showToastMsg("发帖成功");
                        finish();
                    }
                }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            send();
        }
        return super.onOptionsItemSelected(item);
    }
}
