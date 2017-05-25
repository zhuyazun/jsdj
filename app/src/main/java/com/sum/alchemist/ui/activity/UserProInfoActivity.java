package com.sum.alchemist.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.impl.UserImpl;
import com.sum.alchemist.utils.DialogUtil;
import com.sum.alchemist.utils.EventParams;

import org.greenrobot.eventbus.EventBus;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserProInfoActivity extends BaseActivity {



    private TextView userName;
    private TextView userNickname;
    private TextView userVip;
    private TextView userCardNumber;
    private TextView userContact;
    private TextView userQq;
    private TextView userWechat;
    private TextView userCompany;
    private TextView userJobDuties;
    private TextView userJobTitle;
    private TextView userGraduatedSchool;
    private TextView userEducation;
    private TextView userProfession;
    private TextView userResearchAreas;
    private TextView userPaperWorks;
    private TextView userPatentedSoft;
    private TextView userCompletedTechnologyProjects;
    private TextView user_doing_technology_projects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUserInfo();
    }

    private void initUserInfo(){
        User user = UserImpl.getInstance().getUserDao().queryLoginUser();
        if(user != null) {
            userName.setText(user.full_name);
            userNickname.setText(user.nickname);
            userVip.setText(user.age);
            userCardNumber.setText(user.card_number);
            userContact.setText(user.contact);
            userQq.setText(user.qq);
            userWechat.setText(user.wechat);
            userCompany.setText(user.company);
            userJobDuties.setText(user.job_duties);
            userJobTitle.setText(user.job_title);
            userGraduatedSchool.setText(user.graduated_school);
            userEducation.setText(user.education);
            userProfession.setText(user.profession);
            userResearchAreas.setText(user.research_areas);
            userPaperWorks.setText(user.paper_works);
            userPatentedSoft.setText(user.patented_soft);
            userCompletedTechnologyProjects.setText(user.completed_technology_projects);
            user_doing_technology_projects.setText(user.doing_technology_projects);
        }

    }


    @Override
    protected void initViewAndListener() {
        super.initViewAndListener();
        userName = (TextView) findViewById(R.id.user_name);
        userNickname = (TextView) findViewById(R.id.user_nickname);
        userVip = (TextView) findViewById(R.id.user_vip);
        userCardNumber = (TextView) findViewById(R.id.user_card_number);
        userContact = (TextView) findViewById(R.id.user_contact);
        userQq = (TextView) findViewById(R.id.user_qq);
        userWechat = (TextView) findViewById(R.id.user_wechat);
        userCompany = (TextView) findViewById(R.id.user_company);
        userJobDuties = (TextView) findViewById(R.id.user_job_duties);
        userJobTitle = (TextView) findViewById(R.id.user_job_title);
        userGraduatedSchool = (TextView) findViewById(R.id.user_graduated_school);
        userEducation = (TextView) findViewById(R.id.user_education);
        userProfession = (TextView) findViewById(R.id.user_profession);
        userResearchAreas = (TextView) findViewById(R.id.user_research_areas);
        userPaperWorks = (TextView) findViewById(R.id.user_paper_works);
        userPatentedSoft = (TextView) findViewById(R.id.user_patented_soft);
        userCompletedTechnologyProjects = (TextView) findViewById(R.id.user_completed_technology_projects);
        user_doing_technology_projects = (TextView) findViewById(R.id.user_doing_technology_projects);
        findViewById(R.id.user_nickname_layout).setOnClickListener(listener);
        findViewById(R.id.user_vip_layout).setOnClickListener(listener);
        findViewById(R.id.user_card_number_layout).setOnClickListener(listener);
        findViewById(R.id.user_contact_layout).setOnClickListener(listener);
        findViewById(R.id.user_name_layout).setOnClickListener(listener);
        findViewById(R.id.user_qq_layout).setOnClickListener(listener);
        findViewById(R.id.user_wechat_layout).setOnClickListener(listener);
        findViewById(R.id.user_company_layout).setOnClickListener(listener);
        findViewById(R.id.user_job_duties_layout).setOnClickListener(listener);
        findViewById(R.id.user_job_title_layout).setOnClickListener(listener);
        findViewById(R.id.user_graduated_school_layout).setOnClickListener(listener);
        findViewById(R.id.user_education_layout).setOnClickListener(listener);
        findViewById(R.id.user_profession_layout).setOnClickListener(listener);
        findViewById(R.id.user_research_areas_layout).setOnClickListener(listener);
        findViewById(R.id.user_paper_works_layout).setOnClickListener(listener);
        findViewById(R.id.user_patented_soft_layout).setOnClickListener(listener);
        findViewById(R.id.user_completed_technology_projects_layout).setOnClickListener(listener);
        findViewById(R.id.user_doing_technology_projects_layout).setOnClickListener(listener);

        findViewById(R.id.submit_bt).setOnClickListener(listener);
        TextView cancel = (TextView) findViewById(R.id.cancel_bt);
        if(getIntent().getBooleanExtra("isFirst", false)){
            cancel.setText("跳过");
        }
        cancel.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.user_name_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改姓名", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userName.setText(s);
                        }
                    });
                    break;
                case R.id.user_nickname_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改昵称", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userNickname.setText(s);
                        }
                    });
                    break;
                case R.id.user_vip_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改会员号", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userVip.setText(s);
                        }
                    });
                    break;
                case R.id.user_card_number_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改身份证号", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userCardNumber.setText(s);
                        }
                    });
                    break;
                case R.id.user_contact_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改联系方式", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userContact.setText(s);
                        }
                    });
                    break;
                case R.id.user_qq_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改QQ号", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userQq.setText(s);
                        }
                    });
                    break;
                case R.id.user_wechat_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改微信号", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userWechat.setText(s);
                        }
                    });
                    break;
                case R.id.user_company_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改单位信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userCompany.setText(s);
                        }
                    });
                    break;
                case R.id.user_job_duties_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改职务信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userJobDuties.setText(s);
                        }
                    });
                    break;
                case R.id.user_job_title_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改职称信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userJobTitle.setText(s);
                        }
                    });
                    break;
                case R.id.user_graduated_school_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改毕业院校信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userGraduatedSchool.setText(s);
                        }
                    });
                    break;
                case R.id.user_education_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改学历信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userEducation.setText(s);
                        }
                    });
                    break;
                case R.id.user_profession_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改专业信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userProfession.setText(s);
                        }
                    });
                    break;
                case R.id.user_research_areas_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改研究领域信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userResearchAreas.setText(s);
                        }
                    });
                    break;
                case R.id.user_paper_works_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改论文著作信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userPaperWorks.setText(s);
                        }
                    });
                    break;
                case R.id.user_patented_soft_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改专利软著信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userPatentedSoft.setText(s);
                        }
                    });
                    break;
                case R.id.user_completed_technology_projects_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改已完成的科技项目信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            userCompletedTechnologyProjects.setText(s);
                        }
                    });
                    break;
                case R.id.user_doing_technology_projects_layout:
                    DialogUtil.showEditDialog(UserProInfoActivity.this, "修改正在承担的科技项目信息", new Action1<String>() {
                        @Override
                        public void call(String s) {
                            user_doing_technology_projects.setText(s);
                        }
                    });
                    break;
                case R.id.submit_bt:
                    submitUserProInfo();
                    break;
                case R.id.cancel_bt:
                    finish();
                    break;

            }
        }
    };

    private void submitUserProInfo(){
        Subscription subscription = UserImpl.getInstance().putUserInfo(null, userNickname.getText().toString(),
                userName.getText().toString(), null, userVip.getText().toString(), userCardNumber.getText().toString(),
                userContact.getText().toString(), userQq.getText().toString(), userWechat.getText().toString(), userCompany.getText().toString(),
                userJobDuties.getText().toString(), userJobTitle.getText().toString(), userGraduatedSchool.getText().toString(), userEducation.getText().toString(),
                userProfession.getText().toString(), userResearchAreas.getText().toString(), userPaperWorks.getText().toString(), userPatentedSoft.getText().toString(),
                userCompletedTechnologyProjects.getText().toString(), user_doing_technology_projects.getText().toString()).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog("正在提交...", true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        hideProgressDialog();
                    }

                    @Override
                    public void onNext(String s) {
                        hideProgressDialog();
                        showToastMsg("修改成功");
                        EventBus.getDefault().post(new EventParams(EventParams.USER_INFO_CHANGE));
                        finish();
                    }
                });
        addSubscrebe(subscription);
    }



    @Override
    protected void initTitle() {
        super.initTitle();
        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("详细信息");
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
        return R.layout.activity_user_pro_info;
    }
}
