package com.cxb.myfamilytree.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxb.myfamilytree.R;


public class ProgressDialogFragment extends DialogFragment {

    private View mRootView;

    private String mMessage;

    private OnDismissListener mDismissListener;

    public ProgressDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.dialog_fragment_progress, container, false);
        initView();

        return mRootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    private void initView() {
        final TextView tvMessage = mRootView.findViewById(R.id.tv_message);
        tvMessage.setText(mMessage);
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setDismissListener(OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
