package com.cxb.myfamilytree.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.config.Constants;
import com.cxb.myfamilytree.presenter.LaunchPresenter;
import com.cxb.myfamilytree.utils.AppManager;
import com.cxb.myfamilytree.view.ILaunchView;

import java.util.ArrayList;
import java.util.List;

import static com.cxb.myfamilytree.config.Constants.REQUEST_TO_SETTING;


/**
 * 启动页面
 */

public class LaunchActivity extends AppCompatActivity implements ILaunchView {

    private int permissionPosition = 0;//当前请求权限位置
    private String[] permissions;
    private String[] errorTips;

    private AlertDialog mAlertDialog;

    private LaunchPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new LaunchPresenter();
        mPresenter.attachView(this);

        checkPermission();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initData() {
        mPresenter.getFamily(Constants.MY_ID);
    }

    private void checkPermission() {
        final String appName = getString(R.string.app_name);
        permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        errorTips = new String[]{
                String.format(getString(R.string.text_storage_permission_message), appName),
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

    @Override
    public void startMainActivity() {
        FamilyTreeActivity.show(LaunchActivity.this);
        finish();
    }
}
