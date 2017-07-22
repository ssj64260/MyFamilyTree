package com.cxb.myfamilytree.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseAppCompatActivity;
import com.cxb.myfamilytree.config.Config;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.utils.AppManager;
import com.cxb.myfamilytree.widget.FamilyDBHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.cxb.myfamilytree.config.Config.REQUEST_TO_SETTING;
import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;


/**
 * 启动页面
 */

public class LaunchActivity extends BaseAppCompatActivity {

    private int permissionPosition = 0;//当前请求权限位置
    private String[] permissions;
    private String[] errorTips;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();

    }

    private void initData() {
        final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
        Observable
                .create(new ObservableOnSubscribe<FamilyBean>() {
                    @Override
                    public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<FamilyBean> e) throws Exception {
                        FamilyBean myInfo = dbHelper.findFamilyById(Config.MY_ID);
                        if (myInfo == null) {
                            myInfo = new FamilyBean();
                            myInfo.setMemberId(Config.MY_ID);
                            myInfo.setMemberName("我的姓名");
                            myInfo.setCall("我");
                            myInfo.setSex(SEX_MALE);
                            myInfo.setBirthday("");
                        }
                        dbHelper.save(myInfo);
                        e.onNext(myInfo);
                        dbHelper.closeDB();
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FamilyBean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull FamilyBean familyBean) throws Exception {
                        startActivity(new Intent(LaunchActivity.this, FamilyTreeActivity.class));
                        finish();
                    }
                });
    }

    private void checkPermission() {
        final String appName = getString(R.string.app_name);
        permissions = new String[]{
                Config.PERMISSION_STORAGE
        };
        errorTips = new String[]{
                String.format(getString(R.string.text_storage_permission_message), appName)
        };

        final List<String> requestList = new ArrayList<>();
        final List<String> errorTipsList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                String tips = this.errorTips[i];
                requestList.add(permission);
                errorTipsList.add(tips);
            }
        }
        permissions = requestList.toArray(new String[requestList.size()]);
        errorTips = errorTipsList.toArray(new String[errorTipsList.size()]);
        requestPermission();
    }

    private void requestPermission() {
        if (permissionPosition < permissions.length) {
            ActivityCompat.requestPermissions(this, new String[]{permissions[permissionPosition]}, permissionPosition);
        } else {
            initData();
        }
    }

    private void showPermissionTipsDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this).create();
            mAlertDialog.setTitle(getString(R.string.permission_dialog_title));
            mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.permission_dialog_btn_setting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    AppManager.showInstalledAppDetails(LaunchActivity.this, getPackageName(), REQUEST_TO_SETTING);
                }
            });
            mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.permission_dialog_btn_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        mAlertDialog.setMessage(errorTips[permissionPosition]);
        mAlertDialog.show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (permissionPosition < errorTips.length) {
                    showPermissionTipsDialog();
                } else {
                    finish();
                }
            } else {
                permissionPosition++;
                requestPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_TO_SETTING == requestCode) {
            if (permissionPosition < permissions.length) {
                if (ContextCompat.checkSelfPermission(this, permissions[permissionPosition]) != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    permissionPosition++;
                    requestPermission();
                }
            }
        }
    }

}
