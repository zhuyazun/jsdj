package com.sum.alchemist.widget.share;

/**
 * 分享参数
 */
public class ShareParamsFactory {
//    public static final String SHARE_IMAGE_DIR = Config.DownloadConfig.getFilePath();
//    public static final String SHARE_IMAGE_NAME = "logo.png";
//    public static final String SHARE_IMAGE_PATH = SHARE_IMAGE_DIR + SHARE_IMAGE_NAME;
//
//    private static final String SHARE_URL = "http://vu5s4g.b.rrxiu.net";
//
//    public static Platform.ShareParams getShareParams(String platform) {
//        return getShareParams(platform, null);
//    }
//
//    public static Platform.ShareParams getShareParams(String platform, String shareText){
//        if(SinaWeibo.NAME.equals(platform)){
//            return getWeiboShareParams(shareText);
//        }else if(WechatMoments.NAME.equals(platform) || Wechat.NAME.equals(platform)){
//            return getWechatShareParams(shareText);
//        }
//
//        return getDefaultShareParams(shareText);
//    }
//
//    private static Platform.ShareParams getWeiboShareParams(String shareText){
//        Platform.ShareParams shareParams = new Platform.ShareParams();
//
//        shareParams.setImagePath(SHARE_IMAGE_PATH);
//        if(TextUtils.isEmpty(shareText))
//            shareText = MyApp.getInstance().getString(R.string.share_weibo_text);
//        shareParams.setText(shareText + getShareUrl());
//
//        return shareParams;
//    }
//
//    private static Platform.ShareParams getQzoneShareParams(String shareText){
//        Platform.ShareParams shareParams = new Platform.ShareParams();
//        shareParams.setTitle(MyApp.getInstance().getString(R.string.app_name));
//        shareParams.setSite(MyApp.getInstance().getString(R.string.app_name));
//        shareParams.setImageUrl("http://image3.weplus.me/2016/08/19/1471587850713.png@!300x300");
//        if(TextUtils.isEmpty(shareText))
//            shareText = MyApp.getInstance().getString(R.string.share_tencent_text);
//        shareParams.setText(shareText);
//        shareParams.setTitleUrl(getShareUrl());
//        shareParams.setSiteUrl(getShareUrl());
//
//        return shareParams;
//    }
//
//    private static Platform.ShareParams getWechatShareParams(String shareText){
//        Platform.ShareParams shareParams = new Platform.ShareParams();
//
//        if(TextUtils.isEmpty(shareText))
//            shareText = MyApp.getInstance().getString(R.string.morning_share_text, SpUtil.getInstance().getIntValue(Constant.SP_KEY_MORNING_DAY, 1) - 1);
//        shareParams.setTitle(shareText);
//        shareParams.setImagePath(SHARE_IMAGE_PATH);
//        shareParams.setShareType(Platform.SHARE_WEBPAGE);
//        shareParams.setText(shareText);
//        shareParams.setUrl(getShareUrl());
//        return shareParams;
//    }
//
//    private static Platform.ShareParams getDefaultShareParams(String shareText){
//        Platform.ShareParams shareParams = new Platform.ShareParams();
//
//        if(TextUtils.isEmpty(shareText))
//            shareText = MyApp.getInstance().getString(R.string.share_tencent_text);
//
//        shareParams.setTitle(MyApp.getInstance().getString(R.string.app_name));
//        shareParams.setTitleUrl(getShareUrl());
//        shareParams.setSite(MyApp.getInstance().getString(R.string.app_name));
//        shareParams.setSiteUrl(getShareUrl());
//        shareParams.setImagePath(SHARE_IMAGE_PATH);
//        shareParams.setImageUrl("http://image3.weplus.me/2016/08/19/1471587850713.png@!300x300");
//        shareParams.setShareType(Platform.SHARE_WEBPAGE);
//        shareParams.setText(shareText);
//        shareParams.setUrl(getShareUrl());
//        return shareParams;
//    }
//
//    private static String getShareUrl(){
//        return SpUtil.getInstance().getStringValue(Constant.SP_KEY_SHARE_URL, SHARE_URL);
//    }
}
