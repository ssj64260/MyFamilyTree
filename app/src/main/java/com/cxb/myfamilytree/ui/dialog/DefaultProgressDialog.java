package com.cxb.myfamilytree.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 自定义loading对话框
 */
public class DefaultProgressDialog {

    private OnDismissListener mOnDismissListener;

    private ProgressDialog progressDialog;

    public DefaultProgressDialog(Context ctx) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ctx);
        }
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.OnDismissListener();
                }
            }
        });
    }

    public void setCancelable(boolean cancelable){
        progressDialog.setCancelable(cancelable);
    }

    public void setMessage(String msg){
        progressDialog.setMessage(msg);
    }

    public void showDialog(){
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //设置取消按钮点击后的回调事件
    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    //对话框返回键监听
    public void setOnkeyListener(DialogInterface.OnKeyListener onkeyListener){
        progressDialog.setOnKeyListener(onkeyListener);
    }

    public interface OnDismissListener {
        void OnDismissListener();
    }
}
