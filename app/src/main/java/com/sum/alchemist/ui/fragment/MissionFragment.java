package com.sum.alchemist.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.ui.activity.MissionDetailActivity;
import com.sum.alchemist.ui.adapter.AdapterItemListener;
import com.sum.alchemist.ui.adapter.ProvisionAdapter;
import com.sum.alchemist.ui.adapter.RequirementAdapter;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.ui.activity.MissionDetailActivity.ID_KEY;
import static com.sum.alchemist.ui.activity.MissionDetailActivity.MISSION_TYPE_KEY;

/**
 * Created by Qiu on 2016/10/16.
 */
public class MissionFragment extends BaseFragment {

    private RadioGroup radioGroup;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ProvisionAdapter provisionAdapter;
    private RequirementAdapter requirementAdapter;

    private EditText searchEdit;
    private View emptyView;
    private DrawerLayout drawerLayout;
    private FilterDrawer filterDrawer;
    private CategoryDrawer categoryDrawer;


    private int refreshType = PROVISION;
    public static final int PROVISION = 0;
    public static final int REQUIREMENT = 1;
    private int lastVisibleItem = 0;

    private static final String TAG = "MissionFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provisionAdapter = new ProvisionAdapter();
        requirementAdapter = new RequirementAdapter();

        provisionAdapter.setAdapterItemListener(adapterItemListener);
        requirementAdapter.setAdapterItemListener(adapterItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_mission, null);

        filterDrawer = new FilterDrawer(view, drawerInterface);
        categoryDrawer = new CategoryDrawer(view, drawerInterface);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        emptyView = view.findViewById(R.id.empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchEdit = (EditText) view.findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    refreshLayout.setRefreshing(true);
                    onRefreshData();
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.search).setOnClickListener(listener);
        view.findViewById(R.id.filter).setOnClickListener(listener);
        view.findViewById(R.id.menu).setOnClickListener(listener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_sell:
                        refreshType = PROVISION;
                        if (provisionAdapter.getItemCount() == 0) {
                            if (!refreshLayout.isRefreshing())
                                refreshLayout.setRefreshing(true);
                            onRefreshData();
                        }else{
                            recyclerView.setAdapter(provisionAdapter);
                        }
                        break;
                    case R.id.radio_purchase:
                        refreshType = REQUIREMENT;
                        if (requirementAdapter.getItemCount() == 0) {
                            if (!refreshLayout.isRefreshing())
                                refreshLayout.setRefreshing(true);
                            onRefreshData();
                        }else{
                            recyclerView.setAdapter(requirementAdapter);
                        }
                        break;
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && lastVisibleItem >= recyclerView.getAdapter().getItemCount() - 1
                        && lastVisibleItem >= 9) {
                    loadMoreDate();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });

        forceFreshData();

        return view;
    }

    FilterDrawer.DrawerInterface drawerInterface = new FilterDrawer.DrawerInterface() {
        @Override
        public void save(int index) {
            refreshLayout.setRefreshing(true);
            onRefreshData();
            closeDrawer(index);
        }

        @Override
        public void closeDrawer(int index) {
            drawerLayout.closeDrawer(index);
        }
    };

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.filter:
                    if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                    }else{
                        drawerLayout.openDrawer(Gravity.RIGHT);
                    }

                    break;
                case R.id.menu:
                    if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                        drawerLayout.closeDrawer(Gravity.LEFT);
                    }else{
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                    break;

                case R.id.search:
                    refreshLayout.setRefreshing(true);
                    onRefreshData();
                    break;
            }
        }
    };

    private int requirement_page = 0;
    private int provision_page = 0;
    private boolean isLoad = false;

    AdapterItemListener<Object> adapterItemListener = new AdapterItemListener<Object>() {
        @Override
        public void onItemClickListener(Object data, int position, int id) {
            if(data instanceof Provision && id == 0){
                Provision pro = (Provision) data;

                startActivity(new Intent(getActivity(), MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, PROVISION).putExtra(ID_KEY, pro.id));
            }else if(data instanceof Requirement && id == 0){
                Requirement req = (Requirement) data;

                startActivity(new Intent(getActivity(), MissionDetailActivity.class)
                        .putExtra(MISSION_TYPE_KEY, REQUIREMENT).putExtra(ID_KEY, req.id));
            }
        }
    };

    /**
     * 刷新列表数据
     */
    public void onRefreshData() {

        Subscription subscription;
        if (refreshType == REQUIREMENT) {
            subscription = MissionImpl.getInstance().searchRequirement(categoryDrawer.getCategory(), filterDrawer.getSendDate(),
                    filterDrawer.getLocation(), filterDrawer.getCompanySize(), filterDrawer.getMoney(), filterDrawer.getCompany(), searchEdit.getText().toString(),
                    0, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Requirement>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            refreshLayout.setRefreshing(false);
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(List<Requirement> requirements) {
                            refreshLayout.setRefreshing(false);
                            requirement_page = 0;
                            requirementAdapter.setDatas(requirements);
                            recyclerView.setAdapter(requirementAdapter);
                            emptyView.setVisibility(requirementAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            subscription = MissionImpl.getInstance().searchProvision(categoryDrawer.getCategory(), filterDrawer.getSendDate(),
                    filterDrawer.getLocation(), filterDrawer.getCompanySize(), filterDrawer.getMoney(), filterDrawer.getCompany(), searchEdit.getText().toString(),
                    0, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Provision>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            refreshLayout.setRefreshing(false);
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        }

                        @Override
                        public void onNext(List<Provision> provisions) {
                            refreshLayout.setRefreshing(false);
                            provision_page = 0;
                            provisionAdapter.setDatas(provisions);
                            recyclerView.setAdapter(provisionAdapter);
                            emptyView.setVisibility(provisionAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                        }
                    });
        }
        compositeSubscription.add(subscription);


    }

    public void loadMoreDate() {
        if (isLoad)
            return;

        isLoad = true;
        Subscription subscription;
        if (refreshType == REQUIREMENT) {
            subscription = MissionImpl.getInstance().searchRequirement(categoryDrawer.getCategory(), filterDrawer.getSendDate(),
                    filterDrawer.getLocation(), filterDrawer.getCompanySize(), filterDrawer.getMoney(), filterDrawer.getCompany(), searchEdit.getText().toString(), (requirement_page+1)*10, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Requirement>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            refreshLayout.setRefreshing(false);
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));

                            isLoad = false;
                        }

                        @Override
                        public void onNext(List<Requirement> requirements) {
                            refreshLayout.setRefreshing(false);
                            requirement_page++;
                            requirementAdapter.addDatas(requirements);
                            requirementAdapter.notifyDataSetChanged();
                            isLoad = false;
                        }
                    });
        } else {
            subscription = MissionImpl.getInstance().searchProvision(categoryDrawer.getCategory(), filterDrawer.getSendDate(),
                    filterDrawer.getLocation(), filterDrawer.getCompanySize(), filterDrawer.getMoney(), filterDrawer.getCompany(), searchEdit.getText().toString(), (provision_page+1)*10, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Provision>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            refreshLayout.setRefreshing(false);
                            showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                            isLoad = false;
                        }

                        @Override
                        public void onNext(List<Provision> provisions) {
                            refreshLayout.setRefreshing(false);
                            provision_page++;
                            provisionAdapter.addDatas(provisions);
                            provisionAdapter.notifyDataSetChanged();
                            isLoad = false;
                        }
                    });
        }

        compositeSubscription.add(subscription);
    }

    /**
     * 带动画强制刷新
     */
    public void forceFreshData() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    onRefreshData();
                }
            });
        }
    }


}
