package com.sum.alchemist.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.sum.alchemist.R;
import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.impl.MissionImpl;
import com.sum.alchemist.model.impl.UserImpl;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.sum.alchemist.R.id.cancelBtn;
import static com.sum.alchemist.R.id.pickPhotoBtn;
import static com.sum.alchemist.R.id.takePhotoBtn;

/**
 * Created by QIU on 2015/10/31.
 */
public class UImageActivity extends BaseActivity {

    private static final String TAG = "UImageActivity";
    public static final String IMAGE_URL_KEY = "image_url";

    public static final String ACTION_TYPE_KEY = "action_type";
    public static final int UP_IMAGE_TYPE = 1002;
    public static final int UP_AVATAR_TYPE = 1001;
    private static int action_type;

    private static final String IMAGE_FILE_NAME = "/avatarImage.jpg";// 头像文件名称
    public static final int UPLOAD_IMAGE = 101;
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private ProgressDialog mProgressDialog;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + IMAGE_FILE_NAME;

        action_type = getIntent().getIntExtra(ACTION_TYPE_KEY, UP_IMAGE_TYPE);


    }

    @Override
    protected int getContentView() {
        return R.layout.activity_uimage;
    }

    @Override
    protected void initViewAndListener() {
        findViewById(takePhotoBtn).setOnClickListener(listener);
        findViewById(pickPhotoBtn).setOnClickListener(listener);
        findViewById(cancelBtn).setOnClickListener(listener);

        findViewById(R.id.root_layout).setOnClickListener(listener);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.root_layout:
                    finish();
                    break;
                // 拍照
                case takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                case cancelBtn:
                    setResult(RESULT_CANCELED, null);
                    finish();
                    break;
                default:
                    break;

            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    upImage();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Uri path = Uri.fromFile(new File(imagePath));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void upImage() {

        Observable<String> upload = MissionImpl.getInstance().uploadFile(new File(imagePath))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog = showProgressDialog("正在上传", false);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());

        if(action_type == UP_AVATAR_TYPE){
            upload.flatMap(new Func1<String, Observable<String>>() {
                @Override
                public Observable<String> call(String s) {
                    return UserImpl.getInstance().putUserInfo(s, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null);
                }
            });
        }

        addSubscrebe(upload.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        showToastMsg(RetrofitHelper.getHttpErrorMessage(e));
                        finish();
                    }

                    @Override
                 public void onNext(String url) {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        try {
                            Intent intent = new Intent();
                            intent.putExtra(IMAGE_URL_KEY, url);
                            setResult(RESULT_OK, intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finish();
                    }
                }));
    }

}
