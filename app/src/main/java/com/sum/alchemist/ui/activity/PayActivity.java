package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sum.alchemist.Config;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.GoldPackage;
import com.sum.alchemist.model.entity.Order;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.PayImpl;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.PayAdapter;
import com.sum.alchemist.utils.CommonUtil;
import com.sum.alchemist.utils.EventParams;
import com.sum.alchemist.widget.alipay.AlipayClient;
import com.sum.xlog.util.DateUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.utils.EventParams.PURCHASE_RESPONSE;
import static com.sum.alchemist.utils.EventParams.USER_INFO_CHANGE;

/**
 * Created by Qiu on 2016/11/24.
 */

public class PayActivity extends BaseActivity{

    private View alipayType;
    private AppCompatCheckBox alipayCb;
    private View wechatType;
    private AppCompatCheckBox wechatCb;
    private RecyclerView recyclerView;
    private PayAdapter adapter;
    private IWXAPI wxAPI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxAPI = WXAPIFactory.createWXAPI(this, Config.Common.WX_APP_ID);
        wxAPI.registerApp(Config.Common.WX_APP_ID);
        doLoadGoalPackage();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        alipayType = findViewById(R.id.alipay_type);
        alipayCb = (AppCompatCheckBox) findViewById(R.id.alipay_cb);
        wechatType = findViewById(R.id.wechat_type);
        wechatCb = (AppCompatCheckBox) findViewById(R.id.wechat_cb);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


        adapter = new PayAdapter();
        adapter.setAdapterItemListener(itemListener);
        recyclerView.setAdapter(adapter);

        alipayType.setOnClickListener(onClickListener);
        wechatType.setOnClickListener(onClickListener);
        findViewById(R.id.pay).setOnClickListener(onClickListener);
    }

    AdapterItemListener<GoldPackage> itemListener = new AdapterItemListener<GoldPackage>() {
        @Override
        public void onItemClickListener(GoldPackage data, int position, int id) {
            adapter.selectId = data.id;
            adapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.alipay_type:
                    alipayCb.setChecked(true);
                    wechatCb.setChecked(false);
                    break;
                case R.id.wechat_type:
                    alipayCb.setChecked(false);
                    wechatCb.setChecked(true);
                    break;
                case R.id.pay:
                    doPay();
                    break;
            }
        }
    };

    @Override
    protected void initTitle() {
        super.initTitle();

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("充值");
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
        return R.layout.activity_pay;
    }

    private void doPay(){
        if(adapter.selectId == -1)
            return;
        showProgressDialog("创建订单中...", false);
        addSubscrebe(PayImpl.getInstance().createOrder(alipayCb.isChecked()?"zhifubao":"weixin", String.valueOf(adapter.selectId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Order>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(Order order) {
                        hideProgressDialog();
                        if(!TextUtils.isEmpty(order.prepayid)) {
                            doPay(order.prepayid);
                            return;
                        }
                        if(order.amount > 0) {
                            doPay(String.valueOf(order.amount), order.order_no, order.body);
                        }
                    }
                }));
    }

    private void doLoadGoalPackage(){
        addSubscrebe(PayImpl.getInstance().getPayProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GoldPackage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                    }

                    @Override
                    public void onNext(List<GoldPackage> goldPackages) {
                        adapter.setDatas(goldPackages);
                        adapter.notifyDataSetChanged();
                    }
                }));
    }

    private void doPay(String pid){
        if(wxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
            wxAPI.sendReq(createPayReq(pid));
        } else {
            showToastMsg("你的系统版本不支持微信支付");
        }
    }

    private void doPay(String price, String id, String info){
        AlipayClient.Create(this).pay(id, info, price);
    }

    public PayReq createPayReq(String prepayId){
        PayReq request = new PayReq();
        request.appId = Config.Common.WX_APP_ID;
        request.partnerId = Config.Common.WX_PARTNER_ID;
        request.prepayId= prepayId;
        request.packageValue = "Sign=WXPay";
        // 生成随机字符串
        request.nonceStr= String.valueOf((int)(Math.random() * Math.pow(10, 20)));
        request.timeStamp= String.valueOf(DateUtil.getCurrentTimeSeconds());
        // 生成签名
        request.sign= CommonUtil.getWechatSign(request);

        return request;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadUserGold(EventParams eventParams){
        if(eventParams.getCode() == PURCHASE_RESPONSE){
            showProgressDialog("正在同步信息", false);
            addSubscrebe(UserImpl.getInstance().getUserInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(User user) {
                            hideProgressDialog();
                            EventBus.getDefault().post(new EventParams(USER_INFO_CHANGE));
                            showToastMsg("同步成功");
                            finish();
                        }
                    }));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
