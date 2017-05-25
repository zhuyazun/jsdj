package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sum.alchemist.R;
import com.sum.alchemist.model.db.DaoManager;


//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        init();
//        startService(new Intent(WelcomeActivity.this, LazyService.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if(!SpUtil.getInstance().getBooleanValue("guide_state", false)) {
//                    startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
//                }else{
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//                }
                finish();
            }
        }, 3000);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_welcome;
    }

    private void init(){
        DaoManager.getInstance();

    }
}
