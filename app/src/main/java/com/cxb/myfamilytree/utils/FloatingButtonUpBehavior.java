package com.cxb.myfamilytree.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 跟随FloatingButton移动
 */

public class FloatingButtonUpBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    public FloatingButtonUpBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof FloatingActionButton;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        int top = dependency.getTop() - child.getHeight();
        child.setY(top);
        return true;
    }
}
