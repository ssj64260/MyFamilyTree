package com.cxb.myfamilytree.utils;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        return dependency instanceof ImageButton;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        final int margin;
        if (dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
            margin = layoutParams.topMargin;
        } else {
            margin = 0;
        }

        final int top = dependency.getTop() - child.getHeight() - margin;
        child.setY(top);
        return true;
    }
}
