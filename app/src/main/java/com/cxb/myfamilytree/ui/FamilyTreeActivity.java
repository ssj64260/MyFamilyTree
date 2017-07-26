package com.cxb.myfamilytree.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseAppCompatActivity;
import com.cxb.myfamilytree.config.Config;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.presenter.FamilyPresenter;
import com.cxb.myfamilytree.utils.FastClick;
import com.cxb.myfamilytree.view.IFamilyView;
import com.cxb.myfamilytree.widget.familytree.FamilyTreeView;
import com.cxb.myfamilytree.widget.familytree.OnFamilyClickListener;

/**
 * 仿亲友+
 */

public class FamilyTreeActivity extends BaseAppCompatActivity implements IFamilyView {

    private static final int REQUEST_CHANGE_FAMILY = 1001;

    private CoordinatorLayout mRootView;
    private FamilyTreeView mFamilyTree;
    private Toolbar mToolbar;

    private View mBackground;
    private LinearLayout mButtons;
    private ImageButton mFloatingButton;
    private TextView mAddSpouse;
    private TextView mAddParent;
    private TextView mAddChild;
    private TextView mAddBrothers;

    private FamilyBean mSelectFamily;
    private boolean mMenuIsOpen = false;

    private RotateAnimation mOpenRotation;
    private RotateAnimation mCloseRotation;
    private ScaleAnimation mOpenScale;
    private ScaleAnimation mCloseScale;
    private AlphaAnimation mOpenAlpha;
    private AlphaAnimation mCloseAlpha;

    private FamilyPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        initView();
        setData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFamilyTree.destroyView();
        mPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show) {
            boolean isShow = mFamilyTree.isShowBottomSpouse();
            if (isShow) {
                item.setTitle(getString(R.string.text_do_show_spouse));
            } else {
                item.setTitle(getString(R.string.text_do_not_show_spouse));
            }
            mFamilyTree.setShowBottomSpouse(!isShow);
            showFamilyTree(mSelectFamily);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_FAMILY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final String id = data.getStringExtra(AddFamilyActivity.FAMILY_INFO);
                    if (!TextUtils.isEmpty(id)) {
                        mPresenter.getFamily(id);
                        return;
                    }
                }
                mPresenter.getFamily(Config.MY_ID);
            }
        }
    }

    private void initView() {
        mPresenter = new FamilyPresenter();
        mPresenter.attachView(this);

        mRootView = (CoordinatorLayout) findViewById(R.id.rootview);
        mFamilyTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackground = findViewById(R.id.view_background);
        mButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        mFloatingButton = (ImageButton) findViewById(R.id.ib_add);
        mAddSpouse = (TextView) findViewById(R.id.tv_spouse);
        mAddParent = (TextView) findViewById(R.id.tv_parent);
        mAddChild = (TextView) findViewById(R.id.tv_child);
        mAddBrothers = (TextView) findViewById(R.id.tv_brothers);
    }

    private void setData() {
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);

        initAnimation();
        mBackground.setOnClickListener(click);
        mFloatingButton.setOnClickListener(click);
        mAddSpouse.setOnClickListener(click);
        mAddParent.setOnClickListener(click);
        mAddChild.setOnClickListener(click);
        mAddBrothers.setOnClickListener(click);

        mFamilyTree.setShowBottomSpouse(false);
        mFamilyTree.setOnFamilyClickListener(familyClick);

        mPresenter.getFamily(Config.MY_ID);
    }

    private void toAddFamily(String type) {
        Intent intent = new Intent();
        intent.setClass(this, AddFamilyActivity.class);
        intent.putExtra(AddFamilyActivity.ADD_TYPE, type);
        intent.putExtra(AddFamilyActivity.FAMILY_INFO, mSelectFamily);
        startActivityForResult(intent, REQUEST_CHANGE_FAMILY);
        if (!TextUtils.isEmpty(type)) {
            closeFloatingMenu();
        }
    }

    private void initAnimation() {
        mOpenRotation = new RotateAnimation(0f, -45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mOpenRotation.setInterpolator(new AnticipateOvershootInterpolator(5f));
        mOpenRotation.setFillAfter(true);
        mOpenRotation.setDuration(200);
        mCloseRotation = new RotateAnimation(-45f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mCloseRotation.setInterpolator(new AnticipateOvershootInterpolator(5f));
        mOpenRotation.setFillAfter(true);
        mCloseRotation.setDuration(200);

        mOpenAlpha = new AlphaAnimation(0f, 1f);
        mOpenRotation.setFillAfter(true);
        mOpenAlpha.setDuration(200);
        mCloseAlpha = new AlphaAnimation(1f, 0f);
        mOpenRotation.setFillAfter(true);
        mCloseAlpha.setDuration(200);

        mOpenScale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mOpenScale.setInterpolator(new OvershootInterpolator());
        mOpenScale.setFillAfter(true);
        mOpenScale.setDuration(200);
        mCloseScale = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mCloseScale.setInterpolator(new OvershootInterpolator());
        mCloseScale.setFillAfter(true);
        mCloseScale.setDuration(200);
    }

    private void openFloatingMenu() {
        mMenuIsOpen = true;
        mOpenRotation.cancel();
        mCloseRotation.cancel();
        mOpenScale.cancel();
        mCloseScale.cancel();
        mOpenAlpha.cancel();
        mCloseAlpha.cancel();

        mBackground.setVisibility(View.VISIBLE);
        mButtons.setVisibility(View.VISIBLE);

        mFloatingButton.startAnimation(mOpenRotation);
        mBackground.startAnimation(mOpenAlpha);
        mButtons.startAnimation(mOpenAlpha);
        mAddSpouse.startAnimation(mOpenScale);
        mAddParent.startAnimation(mOpenScale);
        mAddChild.startAnimation(mOpenScale);
        mAddBrothers.startAnimation(mOpenScale);
    }

    private void closeFloatingMenu() {
        mMenuIsOpen = false;
        mOpenRotation.cancel();
        mCloseRotation.cancel();
        mOpenScale.cancel();
        mCloseScale.cancel();
        mOpenAlpha.cancel();
        mCloseAlpha.cancel();

        mFloatingButton.startAnimation(mCloseRotation);
        mAddSpouse.startAnimation(mCloseScale);
        mAddParent.startAnimation(mCloseScale);
        mAddChild.startAnimation(mCloseScale);
        mAddBrothers.startAnimation(mCloseScale);
        mBackground.startAnimation(mCloseAlpha);
        mButtons.startAnimation(mCloseAlpha);

        mBackground.setVisibility(View.GONE);
        mButtons.setVisibility(View.INVISIBLE);
    }

    private final OnFamilyClickListener familyClick = new OnFamilyClickListener() {
        @Override
        public void onFamilySelect(FamilyBean family) {
            if (family.isSelect()) {
                toAddFamily("");
            } else {
                showFamilyTree(family);
            }
        }
    };

    private final View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.view_background:
                    if (!FastClick.isFastClick()) {
                        closeFloatingMenu();
                    }
                    break;
                case R.id.ib_add:
                    if (mMenuIsOpen) {
                        closeFloatingMenu();
                    } else {
                        openFloatingMenu();
                    }
                    break;
                case R.id.tv_spouse:
                    if (TextUtils.isEmpty(mSelectFamily.getSpouseId())) {
                        toAddFamily(Config.TYPE_ADD_SPOUSE);
                    } else {
                        Snackbar.make(mRootView, "已有配偶", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.tv_parent:
                    if (TextUtils.isEmpty(mSelectFamily.getFatherId()) || TextUtils.isEmpty(mSelectFamily.getMotherId())) {
                        toAddFamily(Config.TYPE_ADD_PARENT);
                    } else {
                        Snackbar.make(mRootView, "已有父亲和母亲", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.tv_child:
                    toAddFamily(Config.TYPE_ADD_CHILD);
                    break;
                case R.id.tv_brothers:
                    final String fatherId = mSelectFamily.getFatherId();
                    final String motherId = mSelectFamily.getMotherId();
                    final String name = mSelectFamily.getMemberName();
                    if (TextUtils.isEmpty(fatherId) && TextUtils.isEmpty(motherId)) {
                        Snackbar.make(mRootView, "请先添加“" + name + "”的父母", Snackbar.LENGTH_LONG).show();
                    } else {
                        toAddFamily(Config.TYPE_ADD_BROTHERS_AND_SISTERS);
                    }
                    break;
            }
        }
    };

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showFamilyTree(FamilyBean family) {
        mSelectFamily = family;
        mFamilyTree.drawFamilyTree(mSelectFamily);
    }
}
