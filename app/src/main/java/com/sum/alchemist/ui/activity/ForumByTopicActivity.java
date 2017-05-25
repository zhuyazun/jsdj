package com.sum.alchemist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.ui.fragment.ForumAllFragment;

public class ForumByTopicActivity extends BaseActivity {

    public static final String TOPIC = "topic";
    public static final String TOPIC_ID = "topic_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int id = getIntent().getIntExtra(TOPIC_ID, 0);
        Fragment fragment = ForumAllFragment.newInstance(getIntent().getStringExtra(TOPIC));
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).show(fragment).commit();
        findView(R.id.floating_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForumByTopicActivity.this, SendForumActivity.class).putExtra(TOPIC_ID, id));
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_forum_by_topic;
    }

    @Override
    protected void initTitle() {

        Toolbar toolbar = findView(R.id.toolbar);
        TextView titleTv = findView(R.id.toolbar_title_tv);
        titleTv.setText("话题");
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
}
