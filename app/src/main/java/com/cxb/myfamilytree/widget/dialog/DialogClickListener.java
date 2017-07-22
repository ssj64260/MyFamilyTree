package com.cxb.myfamilytree.widget.dialog;

/**
 * 对话框回调
 */

public interface DialogClickListener {

    void onConfirmListener();

    void onConfirmListener(String content);

    void onConfirmListener(int tag, String content);

    void onCancelListener();

}
