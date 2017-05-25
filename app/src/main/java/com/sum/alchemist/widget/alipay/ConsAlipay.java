package com.sum.alchemist.widget.alipay;

public class ConsAlipay {
	
	public static final String DEFAULT_CODE = "UTF-8";
	
	public static final String PARTNER = "2088011377982002";
	public static final String SELLER = "2088011377982002";
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANUYKW7qJMIxjCVHUyoMAa9s0QTVvhyQTfeZBbtER9uLx9oaJz5e6GF+PgUIlwzvTvLEud7+1/4uhD2Z+e+tnCDoStSZZf64rOal7BsaBC+MuFZwkH8FRMD+o+LeTKuJIySaBpFig6AR33a7S/AFO/771bSR2g0WIdGqPKAuh/LRAgMBAAECgYEA1Qktyvv8OcRtFovC4cNVH/sKz+mcAVRnDgSGjVxK9ns9FpeU6i7Z5TXfbQrUcBMC+4OfSwe+ChiZDjjUfUA0V4Kp9Hmt4mnGZrZNX31k0Iy91+H8z9qOor+W31Ta39M8qE43Gm+nw2fCTBvn/lu8XbRL6o8zm60GFfHFiU0+Io0CQQDuTttU4GUqUAfyFsl0Dt+kAxrZhMz2mykgPytoKYG5sGrMQ9BO4lxwFJiYnxp9DwuVAHspzXaBQi8BoDhTSGS7AkEA5Oob8TC004Olylm6pioFdzVNeGb3RuPWblKnV1bpkRIZ2EQLAsfyI9vNZFwX59qUfd4iJGLtcAJtADC9oipT4wJAD4FB8/vFmUMrwSyKXuq40ksBbDWfwEvIGTkC8fwBXLuFt1SSMQ+5Q/GTHBr99jUMY91sJgQgObi8pjZuc7y6EQJAS3tOn2c6GQyt3F1eCHCOwNIYq3z4SK3UBSjH+Sq1dZqMXx2Aq/gWRfCyd0dOsqsGj8Jxa6IbPPXvz0JDJXfjlQJAJR7Evq1iSJUDiSh4zD1SlPI69mrL+ZKloPGQaTtM4/e3yM7+MLYEaAQqKbCPNty2qixzkT2rq7MMl2/UZ58Auw==";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// KEY
	public static final String ORDER_INFO_KEY_PARTNER = "partner";
	public static final String ORDER_INFO_KEY_SELLER = "seller_id";
	public static final String ORDER_INFO_KEY_OUT_TRADE_NO = "out_trade_no"; //out_trade_no
	public static final String ORDER_INFO_KEY_SUBJECT = "subject";//subject
	public static final String ORDER_INFO_KEY_BODY = "body";//body
	public static final String ORDER_INFO_KEY_TOTAL_FEE = "total_fee";//total_fee
	public static final String ORDER_INFO_KEY_NOTIFY_URL = "notify_url";//notify_url
	public static final String ORDER_INFO_KEY_PAYMENT_TYPE = "payment_type";//payment_type
	public static final String ORDER_INFO_KEY_INPUT_CHARSET = "_input_charset";//_input_charset
	public static final String ORDER_INFO_KEY_IT_B_PAY = "it_b_pay";//it_b_pay
	public static final String ORDER_INFO_KEY_RETURN_URL = "return_url";//return_url
	public static final String ORDER_INFO_KEY_PAYMETHOD = "paymethod";//paymethod
	public static final String ORDER_INFO_KEY_SERVICE = "service";//service
	//VALUE

	// TODO
	public static final String ORDER_INFO_TRACK_NOTIFY_URL = "/pay/alipay/brainwaves/notify.do";
	public static final String ORDER_INFO_SUBSCRIBE_NOTIFY_URL = "/pay/alipay/subscription/notify.do";
	public static final String ORDER_INFO_SERVICE = "mobile.securitypay.pay";
	public static final String ORDER_INFO_PAYMENT_TYPE = "1";
	public static final String ORDER_INFO_INPUT_CHARSET = "utf-8";
	public static final String ORDER_INFO_IT_B_PAY = "30m";
	public static final String ORDER_INFO_RETURN_URL = "m.alipay.com";
	public static final String ORDER_INFO_PAY_METHOD = "expressGateway";
	
	public static final String PAY_INFO_KEY_SIGN = "sign";
	public static final String PAY_INFO_SIGN_TYPE= "sign_type=\"RSA\"";
	
	
	public static final String RESULT_OK = "9000";//为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
	public static final String RESULT_CHECKING = "8000";//“8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
	public static final String RESULT_CANCEL = "6001";//“6001” 代表用户取消支付

}

