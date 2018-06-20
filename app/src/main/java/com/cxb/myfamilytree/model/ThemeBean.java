package com.cxb.myfamilytree.model;

import android.support.annotation.ColorRes;

/**
 * 主题
 */

public class ThemeBean {

    private String name;
    private int color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(@ColorRes int color) {
        this.color = color;
    }
}
