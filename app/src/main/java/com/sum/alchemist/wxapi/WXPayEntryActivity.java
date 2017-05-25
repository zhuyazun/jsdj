package com.sum.alchemist.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sum.alchemist.Config;
import com.sum.alchemist.R;
import com.sum.alchemist.utils.EventParams;
import com.sum.xlog.core.XLog;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "WXPayEntryActivity";


	private static final int WECHAT_PAY_SUCCESS_CODE = 0;
	private static final int WECHAT_PAY_ERROR_CODE = -1;
	private static final int WECHAT_PAY_CANCEL = -2;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_purchase);

    	api = WXAPIFactory.createWXAPI(this, Config.Common.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }


    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq baseReq) {

	}

	@Override
	public void onResp(BaseResp baseResp) {

		try{
			XLog.d(TAG, "onPayFinish, errCode = %s, errMsg = %s, transaction = %s, openid = %s",
					baseResp.errCode, baseResp.errStr, baseResp.transaction, baseResp.openId);
			if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

				switch (baseResp.errCode){
					case WECHAT_PAY_CANCEL:
                        showToastMsg("取消支付");
						break;
					case WECHAT_PAY_SUCCESS_CODE:
                        showToastMsg("支付成功");

						EventBus.getDefault().post(new EventParams(EventParams.PURCHASE_RESPONSE));
						break;
					case WECHAT_PAY_ERROR_CODE:
                        showToastMsg("支付失败,请稍后重试");
						break;
				}
			}

		}catch (Exception e){
			XLog.e(TAG, "=== 支付回调异常 ===", e);
            showToastMsg("支付失败,请稍后重试");
		}


		finish();

	}

	public void showToastMsg(String msg){
		if(!TextUtils.isEmpty(msg)) {
			Toast.makeText(this, msg, msg.length() > 20?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
		}
	}
}