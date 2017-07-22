package com.cxb.myfamilytree.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseAppCompatActivity;
import com.cxb.myfamilytree.config.Config;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.utils.FastClick;
import com.cxb.myfamilytree.widget.FamilyDBHelper;
import com.cxb.myfamilytree.widget.FamilyTreeView;
import com.cxb.myfamilytree.widget.OnFamilyClickListener;

/**
 * 仿亲友+
 */

public class FamilyTreeActivity extends BaseAppCompatActivity {

    private static final int REQUEST_CHANGE_FAMILY = 1001;

    private CoordinatorLayout mRootView;
    private FamilyTreeView mFamilyTree;
    private Toolbar mToolbar;

    private View mBackground;
    private LinearLayout mButtons;
    private FloatingActionButton mFloatingButton;
    private CardView mAddSpouse;
    private CardView mAddParent;
    private CardView mAddChild;
    private CardView mAddBrothers;

    private FamilyBean mSelectFamily;
    private boolean mMenuIsOpen = false;

    private RotateAnimation mOpenRotation;
    private RotateAnimation mCloseRotation;
    private ScaleAnimation mOpenScale;
    private ScaleAnimation mCloseScale;
    private AlphaAnimation mOpenAlpha;
    private AlphaAnimation mCloseAlpha;

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
            boolean isShow = mFamilyTree.isBottomNeedSpouse();
            if (isShow) {
                item.setTitle(getString(R.string.text_do_show_spouse));
            } else {
                item.setTitle(getString(R.string.text_do_not_show_spouse));
            }
            mFamilyTree.setBottomNeedSpouse(!isShow);
            mFamilyTree.drawFamilyTree(mSelectFamily);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_FAMILY) {
            if (resultCode == RESULT_OK) {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
                mSelectFamily = dbHelper.findFamilyById(Config.MY_ID);
                dbHelper.closeDB();
                mFamilyTree.drawFamilyTree(mSelectFamily);
            }
        }
    }

    private void initView() {
        mRootView = (CoordinatorLayout) findViewById(R.id.rootview);
        mFamilyTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackground = findViewById(R.id.view_background);
        mButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        mFloatingButton = (FloatingActionButton) findViewById(R.id.fab);
        mAddSpouse = (CardView) findViewById(R.id.cv_spouse);
        mAddParent = (CardView) findViewById(R.id.cv_parent);
        mAddChild = (CardView) findViewById(R.id.cv_child);
        mAddBrothers = (CardView) findViewById(R.id.cv_brothers);
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

//        String json = AssetsUtil.getAssetsTxtByName(this, Config.FAMILY_LIST_DATA_FILE_NAME);
//        List<FamilyBean> mList = JSONObject.parseArray(json, FamilyBean.class);
//
//        mFamilyTree.saveData(mList);
        final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
        mSelectFamily = dbHelper.findFamilyById(Config.MY_ID);
        dbHelper.closeDB();

        mFamilyTree.drawFamilyTree(mSelectFamily);
        mFamilyTree.setOnFamilyClickListener(familyClick);
    }

    private void toAddFamily(String type) {
        Intent intent = new Intent();
        intent.setClass(this, AddFamilyActivity.class);
        intent.putExtra(AddFamilyActivity.ADD_TYPE, type);
        intent.putExtra(AddFamilyActivity.FAMILY_INFO, mSelectFamily);
        startActivityForResult(intent, REQUEST_CHANGE_FAMILY);
        closeFloatingMenu();
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
                Intent intent = new Intent();
                intent.setClass(FamilyTreeActivity.this, FamilyInfoActivity.class);
                intent.putExtra(FamilyInfoActivity.FAMILY_INFO, mSelectFamily);
                startActivityForResult(intent, REQUEST_CHANGE_FAMILY);
            } else {
                mSelectFamily = family;
                mFamilyTree.drawFamilyTree(mSelectFamily);
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
                case R.id.fab:
                    if (mMenuIsOpen) {
                        closeFloatingMenu();
                    } else {
                        openFloatingMenu();
                    }
                    break;
                case R.id.cv_spouse:
                    if (TextUtils.isEmpty(mSelectFamily.getSpouseId())) {
                        toAddFamily(Config.TYPE_ADD_SPOUSE);
                    } else {
                        Snackbar.make(mRootView, "已有配偶", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.cv_parent:
                    if (TextUtils.isEmpty(mSelectFamily.getFatherId()) || TextUtils.isEmpty(mSelectFamily.getMotherId())) {
                        toAddFamily(Config.TYPE_ADD_PARENT);
                    } else {
                        Snackbar.make(mRootView, "已有父亲和母亲", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.cv_child:
                    toAddFamily(Config.TYPE_ADD_CHILD);
                    break;
                case R.id.cv_brothers:
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
}
