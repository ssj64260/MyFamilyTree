package com.cxb.myfamilytree.model;

import android.content.res.Resources;

import java.util.List;

import io.reactivex.Observable;

/**
 * 主题model接口
 */

public interface IThemeModel {

    Observable<List<ThemeBean>> getThemeList(Resources resources);

    Observable saveTheme(String theme);

}
