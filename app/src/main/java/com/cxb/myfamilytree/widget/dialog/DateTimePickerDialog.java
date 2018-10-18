package com.cxb.myfamilytree.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.cxb.myfamilytree.R;

import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;

/**
 * 日期选择
 */

public class DateTimePickerDialog extends AlertDialog {

    private DialogClickListener mDialogClickListener;

    private DatePicker dpDate;
    private TimePicker tpTime;
    private Button btnConfrim;

    private int mTag;//标记点击的控件
    private Calendar mCalendar;

    private boolean mIsTimePicker = false;//是否时间选择器

    public DateTimePickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCalendar != null) {
            if (mIsTimePicker) {
                final int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                final int minute = mCalendar.get(Calendar.MINUTE);
                tpTime.setCurrentHour(hour);
                tpTime.setCurrentMinute(minute);
                tpTime.setVisibility(View.VISIBLE);
                tpTime.setIs24HourView(true);
                dpDate.setVisibility(View.GONE);
            } else {
                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                dpDate.init(year, month, day, null);
                tpTime.setVisibility(View.GONE);
                dpDate.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTag = 0;
        mCalendar = null;
        mIsTimePicker = false;
    }

    private void initView() {
        dpDate = findViewById(R.id.dp_date);
        tpTime = findViewById(R.id.tp_time);
        btnConfrim = findViewById(R.id.btn_confirm);
        btnConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_confirm:
                        final String content;
                        if (mIsTimePicker) {
                            content = tpTime.getCurrentHour() + ":" + tpTime.getCurrentMinute() + ":00";
                        } else {
                            final String month = String.valueOf(dpDate.getMonth() + 1);
                            final String day = String.valueOf(dpDate.getDayOfMonth());

                            content = dpDate.getYear()
                                    + "-" + (month.length() < 2 ? "0" + month : month)
                                    + "-" + (day.length() < 2 ? "0" + day : day);
                        }
                        if (mDialogClickListener != null) {
                            mDialogClickListener.onConfirmListener(mTag, content);
                        }
                        dismiss();
                        break;
                }
            }
        });
    }

    public void setDate(int tag, Calendar calendar, boolean isTimePicker) {
        this.mTag = tag;
        this.mCalendar = calendar;
        this.mIsTimePicker = isTimePicker;
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        this.mDialogClickListener = dialogClickListener;
    }
}
