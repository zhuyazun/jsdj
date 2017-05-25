package com.sum.alchemist.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.JsonObject;
import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.impl.ForumImpl;

import java.util.Calendar;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by TUS on 2016/5/26.
 */
public class DialogUtil {

    private static final String TAG = "DialogUtil";

    /**
     * 退出对话框
     */
    public static Dialog showExitDialog(Context context, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.app_name);
        builder.setMessage("确定要退出程序么?");

        builder.setPositiveButton(android.R.string.ok, onClickListener);
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.show();


    }

    public static Dialog showContectDialog(Context context, String contact){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.app_name);
        builder.setMessage(contact);

        builder.setPositiveButton(android.R.string.ok, null);

        return builder.show();


    }
    private static Subscription subscription;

    /**
     * 修改信息弹框
     */
    public static Dialog showEditDialog(Context context, String title, final Action1<String> action1){

        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(R.layout.dialog_edit).show();

        final TextInputLayout inputLayout = (TextInputLayout) dialog.findViewById(R.id.input_layout);

        dialog.findViewById(R.id.submit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = inputLayout.getEditText().getText().toString();
                if(TextUtils.isEmpty(string)){
                    inputLayout.setError("信息不能为空");
                }else if(action1 != null){
                    action1.call(string);
                    dialog.dismiss();
                }
            }
        });



        return dialog;

    }

    public static Dialog showCommentDialog(Context context, final int tid, final Action1<String> action1){
        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("评论")
                .setView(R.layout.dialog_comment).show();

        final TextInputLayout inputLayout = (TextInputLayout) dialog.findViewById(R.id.input_layout);
        final View progress = dialog.findViewById(R.id.progress);

        dialog.findViewById(R.id.submit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String string = inputLayout.getEditText().getText().toString();
                if(TextUtils.isEmpty(string)){
                    inputLayout.setError("评论不能为空");
                }else{
                    subscription = ForumImpl.getInstance().putForumComment(tid, string).subscribeOn(Schedulers.io())
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    inputLayout.setVisibility(View.INVISIBLE);
                                    progress.setVisibility(View.VISIBLE);
                                }
                            })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<JsonObject>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    inputLayout.setError(RetrofitHelper.getHttpErrorMessage(e));
                                    inputLayout.setVisibility(View.VISIBLE);
                                    progress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onNext(JsonObject s) {
                                    if(action1 != null)
                                        action1.call(string);
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(subscription != null){
                    subscription.unsubscribe();
                    subscription = null;
                }
            }
        });


        return dialog;

    }

    /**
     * 选择时间弹框
     */
    public static Dialog showDateDialog(Context context, DatePickerDialog.OnDateSetListener onDateSetListener){
        DatePickerDialog dialog = new DatePickerDialog(context, onDateSetListener, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dialog.show();
        return dialog;
    }

    public static void showPikerView(Context context, OptionsPickerView.OnOptionsSelectListener optionsSelectListener, List<String> options1Items,
                                     List<List<String>> options2Items, List<List<List<String>>> options3Items){
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(context, optionsSelectListener)
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();
        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }



}
