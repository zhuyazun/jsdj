package com.sum.alchemist.ui.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {

    CompositeSubscription compositeSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(compositeSubscription != null){
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    public <T extends View> T findView(View view, int id){
        return (T)view.findViewById(id);
    }

    public void showToastMsg(int resource){
        showToastMsg(getString(resource));
    }

    public void showToastMsg(String msg){
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, msg.length() > 20?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
        }
    }

    private ProgressDialog mProgressDialog;
    public ProgressDialog showProgressDialog(String msg, boolean isCancel){
        hideProgressDialog();
        mProgressDialog = ProgressDialog.show(getActivity(), null, msg, true, isCancel);
        return mProgressDialog;
    }

    public void hideProgressDialog(){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
