package com.cxb.myfamilytree.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import com.cxb.myfamilytree.config.Config;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.presenter.AddFamilyPresenter;
import com.cxb.myfamilytree.presenter.IAddFamilyPresenter;
import com.cxb.myfamilytree.utils.DateTimeUtils;
import com.cxb.myfamilytree.view.IAddFamilyView;
import com.cxb.myfamilytree.widget.dialog.DateTimePickerDialog;
import com.cxb.myfamilytree.widget.dialog.DialogListener;

import java.util.Calendar;
import java.util.Date;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_FEMALE;
import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 添加家庭成员
 */

public class AddFamilyActivity extends BaseAppCompatActivity implements IAddFamilyView {

    public static final String ADD_TYPE = "add_type";//添加类型
    public static final String FAMILY_INFO = "family_info";//家人信息

    private CoordinatorLayout mRootView;
    private Toolbar mToolBar;
    private EditText mEditName;
    private EditText mEditCall;
    private EditText mEditBirthday;
    private RadioGroup mGenderGroup;

    private DateTimePickerDialog mDatePicker;
    private AlertDialog mAlertDialog;

    private FamilyBean mSelectFamily;
    private String mAddType;

    private IAddFamilyPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);

        initView();
        setData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (mDatePicker != null) {
            mDatePicker.dismiss();
        }
    }

    private void initView() {
        mPresenter = new AddFamilyPresenter();
        mPresenter.attachView(this);

        mRootView = (CoordinatorLayout) findViewById(R.id.rootview);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mEditName = (EditText) findViewById(R.id.et_name);
        mEditCall = (EditText) findViewById(R.id.et_call);
        mEditBirthday = (EditText) findViewById(R.id.et_birthday);
        mGenderGroup = (RadioGroup) findViewById(R.id.rg_gender);
    }

    private void setData() {
        final Intent intent = getIntent();
        mAddType = intent.getStringExtra(ADD_TYPE);
        mSelectFamily = intent.getParcelableExtra(FAMILY_INFO);
        final String familyName = mSelectFamily.getMemberName();
        if (Config.TYPE_ADD_SPOUSE.equals(mAddType)) {
            mToolBar.setTitle("添加配偶");
            mToolBar.setSubtitle("添加" + familyName + "的配偶");
        } else if (Config.TYPE_ADD_PARENT.equals(mAddType)) {
            mToolBar.setTitle("添加父母");
            mToolBar.setSubtitle("添加" + familyName + "的父母");
        } else if (Config.TYPE_ADD_CHILD.equals(mAddType)) {
            mToolBar.setTitle("添加子女");
            mToolBar.setSubtitle("添加" + familyName + "的子女");
        } else if (Config.TYPE_ADD_BROTHERS_AND_SISTERS.equals(mAddType)) {
            mToolBar.setTitle("添加兄弟姐妹");
            mToolBar.setSubtitle("添加" + familyName + "的兄弟姐妹");
        } else {
            mToolBar.setTitle("亲人信息");
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
        }

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

    private void doConfirm() {
        final String name = mEditName.getText().toString();
        final String call = mEditCall.getText().toString();
        final String birthday = mEditBirthday.getText().toString();
        final String gender = mGenderGroup.getCheckedRadioButtonId() == R.id.rb_female ? SEX_FEMALE : SEX_MALE;
        if (TextUtils.isEmpty(name)) {
            showToast("真实姓名不能为空");
        } else if (TextUtils.isEmpty(call)) {
            showToast("称呼不能为空");
        } else {
            if (TextUtils.isEmpty(mAddType)) {
                if (mAlertDialog == null) {
                    mAlertDialog = new AlertDialog.Builder(this).create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    mAlertDialog.setCancelable(true);
                    mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final boolean isChangeGender = !gender.equals(mSelectFamily.getSex());
                            mSelectFamily.setMemberName(name);
                            mSelectFamily.setCall(call);
                            mSelectFamily.setBirthday(birthday);
                            mSelectFamily.setSex(gender);
                            mPresenter.updateFamilyInfo(mSelectFamily, isChangeGender);
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
            } else {
                final FamilyBean family = new FamilyBean();
                family.setMemberId(String.valueOf(System.currentTimeMillis()));
                family.setMemberName(name);
                family.setCall(call);
                family.setBirthday(birthday);
                family.setSex(gender);
                if (Config.TYPE_ADD_SPOUSE.equals(mAddType)) {
                    mPresenter.addSpouse(mSelectFamily, family);
                } else if (Config.TYPE_ADD_PARENT.equals(mAddType)) {
                    mPresenter.addParent(mSelectFamily, family);
                } else if (Config.TYPE_ADD_CHILD.equals(mAddType)) {
                    mPresenter.addChild(mSelectFamily, family);
                } else if (Config.TYPE_ADD_BROTHERS_AND_SISTERS.equals(mAddType)) {
                    mPresenter.addBrothersAndSisters(mSelectFamily, family);
                }
            }
        }
    }

    private final View.OnClickListener click = new View.OnClickListener() {
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
                doConfirm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setResultAndFinish() {
        Intent intent = new Intent();
        intent.putExtra(FAMILY_INFO, mSelectFamily.getMemberId());
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void showToast(String toast) {
        Snackbar.make(mRootView, toast, Snackbar.LENGTH_LONG).show();
    }
}
