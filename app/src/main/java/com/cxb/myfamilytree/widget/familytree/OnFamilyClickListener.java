package com.cxb.myfamilytree.widget.familytree;


import android.view.View;

import com.cxb.myfamilytree.model.FamilyBean;

/**
 * 家庭成员选中回调
 */

public interface OnFamilyClickListener {
    void onFamilySelect(View view, FamilyBean family);
}
