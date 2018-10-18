package com.cxb.myfamilytree.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.LinearLayout;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseActivity;
import com.cxb.myfamilytree.config.Constants;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.presenter.FamilyPresenter;
import com.cxb.myfamilytree.utils.FastClick;
import com.cxb.myfamilytree.view.IFamilyView;
import com.cxb.myfamilytree.widget.familytree.FamilyTreeView;
import com.cxb.myfamilytree.widget.familytree.OnFamilyClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * 仿亲友+
 */

public class FamilyTreeActivity extends BaseActivity implements IFamilyView {

    private static final int REQUEST_CHANGE_FAMILY = 1001;
    private static final int REQUEST_CODE_THEME = 1002;

    private FamilyTreeView mFamilyTree;

    private View mBackground;
    private LinearLayout mButtons;
    private FloatingActionButton btnAdd;
    private Button mAddSpouse;
    private Button mAddParent;
    private Button mAddChild;
    private Button mAddBrothers;

    private FamilyBean mSelectFamily;
    private boolean mMenuIsOpen = false;

    private RotateAnimation mOpenRotation;
    private RotateAnimation mCloseRotation;
    private ScaleAnimation mOpenScale;
    private ScaleAnimation mCloseScale;
    private AlphaAnimation mOpenAlpha;
    private AlphaAnimation mCloseAlpha;

    private FamilyPresenter mPresenter;

    public static void show(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, FamilyTreeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_family_tree;
    }

    @Override
    protected void initData() {
        super.initData();
        initAnimation();
        mPresenter = new FamilyPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mPresenter.attachView(this);

        setToolbarTitle(R.string.app_name);

        mFamilyTree = findViewById(R.id.ftv_tree);
        mBackground = findViewById(R.id.view_background);
        mButtons = findViewById(R.id.ll_buttons);
        btnAdd = findViewById(R.id.btn_add);
        mAddSpouse = findViewById(R.id.btn_spouse);
        mAddParent = findViewById(R.id.btn_parent);
        mAddChild = findViewById(R.id.btn_child);
        mAddBrothers = findViewById(R.id.btn_brothers);

        mBackground.setOnClickListener(click);
        btnAdd.setOnClickListener(click);
        mAddSpouse.setOnClickListener(click);
        mAddParent.setOnClickListener(click);
        mAddChild.setOnClickListener(click);
        mAddBrothers.setOnClickListener(click);

        mFamilyTree.setShowBottomSpouse(false);
        mFamilyTree.setOnFamilyClickListener(familyClick);

        mPresenter.getFamily(Constants.MY_ID);
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
        if (id == R.id.action_scroll_to_center) {
            mFamilyTree.scrollToCenter();
        } else if (id == R.id.action_show) {
            boolean isShow = mFamilyTree.isShowBottomSpouse();
            if (isShow) {
                item.setTitle(getString(R.string.show_bottom_spouse));
            } else {
                item.setTitle(getString(R.string.not_show_bottom_spouse));
            }
            mFamilyTree.setShowBottomSpouse(!isShow);
            showFamilyTree(mSelectFamily);
            return true;
        } else if (id == R.id.action_choose_theme) {
            ThemeListActivity.show(this, REQUEST_CODE_THEME);
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
                mPresenter.getFamily(Constants.MY_ID);
            }
        } else if (requestCode == REQUEST_CODE_THEME) {
            if (resultCode == RESULT_OK) {
                recreate();
            }
        }
    }

    private void toAddFamily(String type) {
        AddFamilyActivity.show(this, REQUEST_CODE_THEME, mSelectFamily, type);

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

        btnAdd.startAnimation(mOpenRotation);
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

        btnAdd.startAnimation(mCloseRotation);
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
        public void onFamilySelect(View view, FamilyBean family) {
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
                case R.id.btn_add:
                    if (mMenuIsOpen) {
                        closeFloatingMenu();
                    } else {
                        openFloatingMenu();
                    }
                    break;
                case R.id.btn_spouse:
                    if (TextUtils.isEmpty(mSelectFamily.getSpouseId())) {
                        toAddFamily(Constants.TYPE_ADD_SPOUSE);
                    } else {
                        Snackbar.make(mRootView, "已有配偶", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btn_parent:
                    if (TextUtils.isEmpty(mSelectFamily.getFatherId()) || TextUtils.isEmpty(mSelectFamily.getMotherId())) {
                        toAddFamily(Constants.TYPE_ADD_PARENT);
                    } else {
                        Snackbar.make(mRootView, "已有父亲和母亲", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btn_child:
                    toAddFamily(Constants.TYPE_ADD_CHILD);
                    break;
                case R.id.btn_brothers:
                    final String fatherId = mSelectFamily.getFatherId();
                    final String motherId = mSelectFamily.getMotherId();
                    final String name = mSelectFamily.getMemberName();
                    if (TextUtils.isEmpty(fatherId) && TextUtils.isEmpty(motherId)) {
                        Snackbar.make(mRootView, "请先添加“" + name + "”的父母", Snackbar.LENGTH_LONG).show();
                    } else {
                        toAddFamily(Constants.TYPE_ADD_BROTHERS_AND_SISTERS);
                    }
                    break;
            }
        }
    };

    @Override
    public void showFamilyTree(FamilyBean family) {
        mSelectFamily = family;
        mFamilyTree.drawFamilyTree(mSelectFamily);
    }
}
