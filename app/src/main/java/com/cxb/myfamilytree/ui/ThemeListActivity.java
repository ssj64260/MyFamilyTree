package com.cxb.myfamilytree.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseActivity;
import com.cxb.myfamilytree.model.ThemeBean;
import com.cxb.myfamilytree.presenter.ThemeListPresenter;
import com.cxb.myfamilytree.ui.adapter.OnListClickListener;
import com.cxb.myfamilytree.ui.adapter.ThemeListAdapter;
import com.cxb.myfamilytree.utils.PrefUtils;
import com.cxb.myfamilytree.view.IThemeListView;
import com.cxb.myfamilytree.widget.RecyclerViewDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题列表
 */

public class ThemeListActivity extends BaseActivity implements IThemeListView {

    private RecyclerView rvThemeList;

    private List<ThemeBean> mThemeList;
    private ThemeListAdapter mThemeAdapter;

    private ThemeListPresenter mPresenter;

    public static void show(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, ThemeListActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_theme_list;
    }

    @Override
    protected void initData() {
        super.initData();
        mThemeList = new ArrayList<>();
        mThemeAdapter = new ThemeListAdapter(this, mThemeList);
        mThemeAdapter.setListClick(mListClick);

        mPresenter = new ThemeListPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mPresenter.attachView(this);

        setToolbarBackEnable();
        setToolbarTitle(R.string.choose_theme);

        final int lineColor = PrefUtils.isDarkTheme() ? R.color.dark_line : R.color.material_light_white;

        rvThemeList = findViewById(R.id.rv_theme_list);
        rvThemeList.setLayoutManager(new LinearLayoutManager(this));
        rvThemeList.addItemDecoration(new RecyclerViewDecoration(this, 1, lineColor));
        rvThemeList.setAdapter(mThemeAdapter);

        mPresenter.getThemeList(getResources());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mPresenter = null;
    }

    @Override
    public void showThemeList(List<ThemeBean> themeList) {
        mThemeList.clear();
        if (themeList != null) {
            mThemeList.addAll(themeList);
        }
        mThemeAdapter.notifyDataSetChanged();
    }

    @Override
    public void recreateActivity() {
        recreate();
    }

    private OnListClickListener mListClick = new OnListClickListener() {
        @Override
        public void onItemClick(int position) {
            final ThemeBean theme = mThemeList.get(position);
            final String name = theme.getName();
            final String selectTheme = PrefUtils.getTheme();
            final boolean isSelect = selectTheme.equals(name);

            if (!isSelect) {
                mPresenter.saveTheme(name);
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onTagClick(int tag, int position) {

        }
    };
}
