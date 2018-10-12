package com.cxb.myfamilytree.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {

    private Context mContext;

    private String mTitle;
    private String mMessage;
    private String mConfirmButtonText;
    private String mCancelButtonText;
    private DialogInterface.OnClickListener mConfirmClick;
    private DialogInterface.OnClickListener mCancelClick;

    public AlertDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);
        builder.setPositiveButton(mConfirmButtonText, mConfirmClick);
        builder.setNegativeButton(mCancelButtonText, mCancelClick);
        return builder.create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setConfirmButton(String buttonText) {
        mConfirmButtonText = buttonText;
        mConfirmClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
    }

    public void setConfirmButton(String buttonText, DialogInterface.OnClickListener confirmClick) {
        mConfirmButtonText = buttonText;
        mConfirmClick = confirmClick;
    }

    public void setCancelButton(String buttonText) {
        mCancelButtonText = buttonText;
        mCancelClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
    }

    public void setCancelButton(String buttonText, DialogInterface.OnClickListener cancelClick) {
        mCancelButtonText = buttonText;
        mCancelClick = cancelClick;
    }
}
