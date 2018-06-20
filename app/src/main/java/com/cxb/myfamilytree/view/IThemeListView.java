package com.cxb.myfamilytree.view;

import com.cxb.myfamilytree.model.ThemeBean;

import java.util.List;

/**
 * 主题View
 */

public interface IThemeListView {

    void showThemeList(List<ThemeBean> themeList);

    void recreateActivity();

}
