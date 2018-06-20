package com.cxb.myfamilytree.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseActivity;
import com.cxb.myfamilytree.config.Constants;

/**
 * Created on 2017/10/30 13:26:25
 * Copied from Copyright (C) 2017 Kosh.
 * Modified by Copyright (C) 2017 ThirtyDegreesRay.
 */

public class ThemeHelper {

    public static void apply(@NonNull BaseActivity activity) {
        String theme = PrefUtils.getTheme();
        activity.setTheme(getTheme(theme));
    }

    @StyleRes
    public static int getTheme(String theme) {
        switch (theme) {
            case Constants.GREEN:
                return R.style.ThemeGreen;
            case Constants.RED:
                return R.style.ThemeRed;
            case Constants.PINK:
                return R.style.ThemePink;
            case Constants.INDIGO:
                return R.style.ThemeIndigo;
            case Constants.TEAL:
                return R.style.ThemeTeal;
            case Constants.ORANGE:
                return R.style.ThemeOrange;
            case Constants.PURPLE:
                return R.style.ThemePurple;
            case Constants.BLUE:
                return R.style.ThemeBlue;
            case Constants.BROWN:
                return R.style.ThemeBrown;
            case Constants.GREY:
                return R.style.ThemeGrey;
            case Constants.WHITE:
                return R.style.ThemeWhite;
            case Constants.BLACK:
                return R.style.ThemeBlack;
            case Constants.DARK:
                return R.style.ThemeDark;
        }
        return R.style.ThemeDark;
    }
}
