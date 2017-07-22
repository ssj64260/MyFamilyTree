package com.cxb.myfamilytree.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseAppCompatActivity;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.utils.DateTimeUtils;
import com.cxb.myfamilytree.widget.FamilyDBHelper;
import com.cxb.myfamilytree.widget.dialog.DateTimePickerDialog;
import com.cxb.myfamilytree.widget.dialog.DialogListener;

import java.util.Calendar;
import java.util.Date;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_FEMALE;
import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 家庭成员信息
 */

public class FamilyInfoActivity extends BaseAppCompatActivity {

    public static final String FAMILY_INFO = "family_info";//家人信息

    private Toolbar mToolBar;
    private EditText mEditName;
    private EditText mEditCall;
    private EditText mEditBirthday;
    private RadioGroup mGenderGroup;

    private DateTimePickerDialog mDatePicker;
    private AlertDialog mAlertDialog;

    private FamilyBean mSelectFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_info);

        initView();
        setData();

    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mEditName = (EditText) findViewById(R.id.et_name);
        mEditCall = (EditText) findViewById(R.id.et_call);
        mEditBirthday = (EditText) findViewById(R.id.et_birthday);
        mGenderGroup = (RadioGroup) findViewById(R.id.rg_gender);
    }

    private void setData() {
        mSelectFamily = getIntent().getParcelableExtra(FAMILY_INFO);

        mEditName.setText(mSelectFamily.getMemberName());
        mEditCall.setText(mSelectFamily.getCall());
        mEditBirthday.setText(mSelectFamily.getBirthday());

        final int count = mGenderGroup.getChildCount();
        if (count == 2) {
            final RadioButton maleButton = (RadioButton) mGenderGroup.getChildAt(0);
            final RadioButton femaleButton = (RadioButton) mGenderGroup.getChildAt(1);
            if (SEX_MALE.equals(mSelectFamily.getSex())) {
                maleButton.setChecked(true);
                femaleButton.setChecked(false);
            } else {
                maleButton.setChecked(false);
                femaleButton.setChecked(true);
            }
        }

        mToolBar.setTitle("亲人信息");
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEditBirthday.setOnClickListener(click);
        mEditBirthday.setLongClickable(false);
    }

    private void showDateDialog(int tag, String dateText) {
        if (mDatePicker == null) {
            mDatePicker = new DateTimePickerDialog(this);
            mDatePicker.setDialogClickListener(new DialogListener() {
                @Override
                public void onConfirmListener(int tag, String content) {
                    if (tag == R.id.et_birthday) {
                        mEditBirthday.setText(content);
                    }
                }
            });
        }

        final Calendar calendar = Calendar.getInstance();
        if (TextUtils.isEmpty(dateText)) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(DateTimeUtils.StringToDateIgnoreTime(dateText));
        }

        mDatePicker.setDate(tag, calendar, false);
        mDatePicker.show();
    }

    private void saveFamilyInfo() {
        final String name = mEditName.getText().toString();
        final String call = mEditCall.getText().toString();
        final String birthday = mEditBirthday.getText().toString();
        final String gender = mGenderGroup.getCheckedRadioButtonId() == R.id.rb_female ? SEX_FEMALE : SEX_MALE;
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(mEditName, "真实姓名不能为空", Snackbar.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(call)) {
            Snackbar.make(mEditCall, "称呼不能为空", Snackbar.LENGTH_LONG).show();
        } else {
            if (mAlertDialog == null) {
                mAlertDialog = new AlertDialog.Builder(this).create();
                mAlertDialog.setCanceledOnTouchOutside(false);
                mAlertDialog.setCancelable(true);
                mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectFamily.setMemberName(name);
                        mSelectFamily.setCall(call);
                        mSelectFamily.setBirthday(birthday);
                        mSelectFamily.setSex(gender);
                        final FamilyDBHelper dbHelper = new FamilyDBHelper(FamilyInfoActivity.this);
                        dbHelper.save(mSelectFamily);
                        final String spouseId = mSelectFamily.getSpouseId();
                        if (!TextUtils.isEmpty(spouseId)) {
                            final FamilyBean spouseInfo = dbHelper.findFamilyById(spouseId);
                            final String spouseGender = SEX_MALE.equals(gender) ? SEX_FEMALE : SEX_MALE;
                            spouseInfo.setSex(spouseGender);
                            dbHelper.save(spouseInfo);
                        }

                        dbHelper.closeDB();
                        setResult(RESULT_OK);
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }

            if (TextUtils.isEmpty(mSelectFamily.getSpouseId()) || gender.equals(mSelectFamily.getSex())) {
                mAlertDialog.setMessage("是否要修改该亲人的信息？");
            } else {
                mAlertDialog.setMessage("更改性别后，配偶的性别也相应更改，是否继续修改该亲人的信息？");
            }
            mAlertDialog.show();
        }
    }

    //点击监听
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_birthday:
                    final String dateText = mEditBirthday.getText().toString();
                    showDateDialog(R.id.et_birthday, dateText);
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_confirm:
                hideKeyboard();
                saveFamilyInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
