package com.cxb.myfamilytree.model;

import android.content.res.Resources;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.config.Constants;
import com.cxb.myfamilytree.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * 主题model实现
 */

public class ThemeModel implements IThemeModel {

    private int[] colors = {
        R.color.material_green_500,
        R.color.material_red_500,
        R.color.bilibili_pink_500,
        R.color.material_indigo_500,
        R.color.material_teal_500,
        R.color.material_orange_500,
        R.color.material_deep_purple_500,
        R.color.material_blue_500,
        R.color.material_brown_500,
        R.color.material_blue_grey_500/*,
        R.color.material_blue_grey_100,
        R.color.material_light_black,
        R.color.material_black_700*/
    };

    @Override
    public Observable<List<ThemeBean>> getThemeList(final Resources resources) {
        return Observable
                .create(new ObservableOnSubscribe<List<ThemeBean>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<ThemeBean>> e) throws Exception {
                        final String[] names = resources.getStringArray(R.array.theme_list);
                        final List<ThemeBean> themeList = new ArrayList<>();
                        for (int i = 0; i < names.length; i++) {
                            if (i < colors.length) {
                                final ThemeBean theme = new ThemeBean();
                                theme.setName(names[i]);
                                theme.setColor(colors[i]);
                                themeList.add(theme);
                            }
                        }

                        e.onNext(themeList);
                        e.onComplete();
                    }
                });
    }

    @Override
    public Observable saveTheme(final String theme) {
        return Observable
                .create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        PrefUtils.set(Constants.THEME, theme);
                        e.onComplete();
                    }
                });
    }
}
