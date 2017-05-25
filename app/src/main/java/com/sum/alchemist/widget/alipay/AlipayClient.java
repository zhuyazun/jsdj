package com.sum.alchemist.widget.alipay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.sum.alchemist.MyApp;
import com.sum.alchemist.utils.EventParams;
import com.sum.xlog.core.XLog;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlipayClient {
	
	public static final String TAG = "AlipayClient";
	
	private PayTask mPayTask;
	private String mCurrentPayProductId;
	private String mCurrentPayProductInfo;
	private String mCurrentPayPrice;
	

	public AlipayClient(Activity activity) {
		mPayTask = new PayTask(activity);
	}
	
	/**
	 * 
	 * 此接口为发起支付的主要接口
	 * @param productID 产品id
	 * @param productPrice 产品价格，这个是float型，后面保留2个小数点，然后再转成字符串
	 * @param productInfo 产品描述 可以为null 或者空字符串
	 *
	 */
	public void pay(String productID,String productInfo,String productPrice){
		
		if(TextUtils.isEmpty(productID) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productInfo) ){
			XLog.e(TAG,"=== 调用支付宝付款接口传入的参数错误 ===");
			return ;
		}

		mCurrentPayProductId = productID;
		mCurrentPayProductInfo = productInfo;
		mCurrentPayPrice = productPrice;
		
		new AlipayAsyncTask(AlipayAsyncTask.EVENT_PAY).execute(new Void[]{});
	}

	

	private String getOrderInfo(String id, String body, String price) {


		StringBuilder orderInfoBuilder = new StringBuilder();
		
		/** 合作者身份ID **/
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_PARTNER);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.PARTNER));
		/** 卖家支付宝账号 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_SELLER);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.SELLER)); 
		/** 商户网站唯一订单号 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_OUT_TRADE_NO); 
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(id));
		/** 商品名称 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_SUBJECT);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(body));
		/** 商品详情 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_BODY);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(body));
		/** 商品金额 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_TOTAL_FEE);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(price));
		/** 服务器异步通知页面路径 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_NOTIFY_URL);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.ORDER_INFO_TRACK_NOTIFY_URL));
		/**  接口名称， 固定值 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_SERVICE);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.ORDER_INFO_SERVICE));
		/**  支付类型， 固定值 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_PAYMENT_TYPE);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString("1"));
		/**  支付类型， 固定值 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_INPUT_CHARSET);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.ORDER_INFO_INPUT_CHARSET));
		/**  未付款交易的超时时间 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_IT_B_PAY);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.ORDER_INFO_IT_B_PAY));
		/**  未付款交易的超时时间 **/
		orderInfoBuilder.append("&");
		orderInfoBuilder.append(ConsAlipay.ORDER_INFO_KEY_RETURN_URL);
		orderInfoBuilder.append("=");
		orderInfoBuilder.append(convString(ConsAlipay.ORDER_INFO_RETURN_URL));

		return orderInfoBuilder.toString();
	}

	private String getOutTradeNo() {
		
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	
	public String sign(String content) {
		return SignUtils.sign(content, ConsAlipay.RSA_PRIVATE);
	}
		
	private String getPayInfo(String orderInfo,String sign){
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(orderInfo);
		stringBuilder.append("&");
		stringBuilder.append(ConsAlipay.PAY_INFO_KEY_SIGN);
		stringBuilder.append("=");
		stringBuilder.append(convString(sign));
		stringBuilder.append("&");
		stringBuilder.append(ConsAlipay.PAY_INFO_SIGN_TYPE);
		return stringBuilder.toString();
		
	}
	
	private String convString(String string){
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("\"");
		stringBuilder.append(string);
		stringBuilder.append("\"");
		
		return stringBuilder.toString();
	}
	
	private class AlipayAsyncTask extends AsyncTask<Void, Void, Object>{
		
		public static final int EVENT_PAY = 0x00001;
		public static final int EVENT_CHECK_ACCOUNT = 0x00002;
		
		private int processEvent;
		
		public AlipayAsyncTask(int event) {
			processEvent = event;
		}
		
		@Override
		protected Object doInBackground(Void... params) {
			
			if(processEvent == EVENT_PAY){
				try {
					String orderInfo = getOrderInfo(mCurrentPayProductId, mCurrentPayProductInfo, mCurrentPayPrice);
					String sign = sign(orderInfo);
					sign = URLEncoder.encode(sign, ConsAlipay.DEFAULT_CODE);
					XLog.d(TAG, "=== 订单信息 : " + orderInfo + " ===");
					final String payInfo = getPayInfo(orderInfo, sign);
					String result = mPayTask.pay(payInfo);
					if(TextUtils.isEmpty(result)){
						XLog.e(TAG, "=== 调用支付宝付款接口，返回的结果为null或空字符串 ===");
					}else {
						XLog.d(TAG, "=== 支付结果 : "+ result + " ===");
					}
					return result;
				} catch (Exception e) {
					XLog.e(TAG, "=== 调用支付宝付款接口发生异常 ===",e);
				}
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if(processEvent == EVENT_PAY){
				if(result == null){
					return ;
				}
				Result resultObj = new Result(String.valueOf(result));
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, ConsAlipay.RESULT_OK)) {
					Toast.makeText(MyApp.getInstance(), "支付成功", Toast.LENGTH_LONG).show();
					EventBus.getDefault().post(new EventParams(EventParams.PURCHASE_RESPONSE));
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, ConsAlipay.RESULT_CHECKING)) {
						Toast.makeText(MyApp.getInstance(), "支付结果确认中", Toast.LENGTH_LONG).show();
					} else if(TextUtils.equals(resultStatus, ConsAlipay.RESULT_CANCEL)){
						Toast.makeText(MyApp.getInstance(), "支付取消", Toast.LENGTH_LONG).show();
					}else {
						Toast.makeText(MyApp.getInstance(), "支付失败", Toast.LENGTH_LONG).show();
					}
				}

			}
			
		}
		
	}

	public static AlipayClient Create(Activity activity) {
		return new AlipayClient(activity);
	}
	
}
