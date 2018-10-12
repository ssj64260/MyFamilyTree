package com.cxb.myfamilytree.ui;

import android.content.Intent;
import android.os.Bundle;

import com.cxb.myfamilytree.config.Constants;
import com.cxb.myfamilytree.presenter.LaunchPresenter;
import com.cxb.myfamilytree.utils.PermissionsHelper;
import com.cxb.myfamilytree.view.ILaunchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/**
 * 启动页面
 */

public class LaunchActivity extends AppCompatActivity implements ILaunchView {

    private LaunchPresenter mPresenter;

    private PermissionsHelper mPermissionsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new LaunchPresenter();
        mPresenter.attachView(this);

        mPermissionsHelper = new PermissionsHelper.Builder()
                .writeExternalStorage()
                .readExternalStorage()
                .setPermissionsResult(mPermissionsResult)
                .bulid();
        mPermissionsHelper.requestPermissions(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initData() {
        mPresenter.getFamily(Constants.MY_ID);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsHelper.requestPermissionsResult(this, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionsHelper.activityResult(this, requestCode);
    }

    @Override
    public void startMainActivity() {
        FamilyTreeActivity.show(LaunchActivity.this);
        finish();
    }

    private PermissionsHelper.OnPermissionsResult mPermissionsResult = new PermissionsHelper.OnPermissionsResult() {
        @Override
        public void allPermissionGranted() {
            initData();
        }

        @Override
        public void cancelToSettings() {
            finish();
        }
    };
}
