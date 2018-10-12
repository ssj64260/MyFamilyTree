package com.cxb.myfamilytree.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cxb.myfamilytree.R;

/**
 * Loading对话框
 */

public class ProgressDialog {

    private AlertDialog alertDialog;
    private TextView tvMessage;

    private OnDismissListener mOnDismissListener;

    public ProgressDialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.dialog_progress, null);
        tvMessage = rootView.findViewById(R.id.tv_message);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(rootView);
        builder.setCancelable(true);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismissListener();
                }
            }
        });

        alertDialog = builder.create();
    }

    public void setCancelable(boolean cancelable) {
        alertDialog.setCancelable(cancelable);
        alertDialog.setCanceledOnTouchOutside(cancelable);
    }

    public void show() {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void setMessage(String message) {
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
    }

    //设置取消按钮点击后的回调事件
    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    //对话框返回键监听
    public void setOnkeyListener(DialogInterface.OnKeyListener onkeyListener){
        alertDialog.setOnKeyListener(onkeyListener);
    }

    public interface OnDismissListener {
        void onDismissListener();
    }
}
