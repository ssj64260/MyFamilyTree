package com.cxb.myfamilytree.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.app.BaseAppCompatActivity;
import com.cxb.myfamilytree.config.Config;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.utils.DateTimeUtils;
import com.cxb.myfamilytree.widget.FamilyDBHelper;
import com.cxb.myfamilytree.widget.dialog.DateTimePickerDialog;
import com.cxb.myfamilytree.widget.dialog.DialogListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_FEMALE;
import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 添加家庭成员
 */

public class AddFamilyActivity extends BaseAppCompatActivity {

    public static final String ADD_TYPE = "add_type";//添加类型
    public static final String FAMILY_INFO = "family_info";//家人信息

    private Toolbar mToolBar;
    private EditText mEditName;
    private EditText mEditCall;
    private EditText mEditBirthday;
    private RadioGroup mGenderGroup;

    private DateTimePickerDialog mDatePicker;

    private FamilyBean mSelectFamily;
    private String mAddType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);

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

    private void doAddFamily() {
        final String name = mEditName.getText().toString();
        final String call = mEditCall.getText().toString();
        final String birthday = mEditBirthday.getText().toString();
        final String gender = mGenderGroup.getCheckedRadioButtonId() == R.id.rb_female ? SEX_FEMALE : SEX_MALE;
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(mEditName, "真实姓名不能为空", Snackbar.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(call)) {
            Snackbar.make(mEditCall, "称呼不能为空", Snackbar.LENGTH_LONG).show();
        } else {
            FamilyBean family = new FamilyBean();
            family.setMemberId(String.valueOf(System.currentTimeMillis()));
            family.setMemberName(name);
            family.setCall(call);
            family.setBirthday(birthday);
            family.setSex(gender);
            saveToDatabase(family);
        }
    }

    private void saveToDatabase(FamilyBean family) {
        if (Config.TYPE_ADD_SPOUSE.equals(mAddType)) {
            final String selectFamilyId = mSelectFamily.getMemberId();
            final String selectFamilySex = mSelectFamily.getSex();
            final String familyId = family.getMemberId();
            final String familySex = family.getSex();
            if (familySex.equals(selectFamilySex)) {
                Snackbar.make(mEditCall, "不允许同性配偶", Snackbar.LENGTH_LONG).show();
            } else {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
                family.setSpouseId(selectFamilyId);
                mSelectFamily.setSpouseId(familyId);
                dbHelper.save(family);
                dbHelper.save(mSelectFamily);
                final List<FamilyBean> children = dbHelper.getChildren(mSelectFamily, selectFamilyId);
                for (FamilyBean child : children) {
                    if (SEX_MALE.equals(familySex)) {
                        child.setFatherId(familyId);
                    } else {
                        child.setMotherId(familyId);
                    }
                }
                dbHelper.save(children);
                dbHelper.closeDB();
                setResult(RESULT_OK);
                onBackPressed();
            }
        } else if (Config.TYPE_ADD_PARENT.equals(mAddType)) {
            final String familyId = family.getMemberId();
            final String familySex = family.getSex();
            final String fatherId = mSelectFamily.getFatherId();
            final String motherId = mSelectFamily.getMotherId();
            final boolean isAddMale = SEX_MALE.equals(familySex);
            if (isAddMale && !TextUtils.isEmpty(fatherId)) {
                Snackbar.make(mEditCall, "已有父亲", Snackbar.LENGTH_LONG).show();
            } else if (!isAddMale && !TextUtils.isEmpty(motherId)) {
                Snackbar.make(mEditCall, "已有母亲", Snackbar.LENGTH_LONG).show();
            } else {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
                final String parentId;
                final List<FamilyBean> children;
                if (isAddMale) {
                    parentId = motherId;
                    mSelectFamily.setFatherId(familyId);
                    children = dbHelper.findFamiliesByMotherId(motherId, "");
                    for (FamilyBean child : children) {
                        child.setFatherId(familyId);
                    }
                } else {
                    parentId = fatherId;
                    mSelectFamily.setMotherId(familyId);
                    children = dbHelper.findFamiliesByFatherId(fatherId, "");
                    for (FamilyBean child : children) {
                        child.setMotherId(familyId);
                    }
                }
                family.setSpouseId(parentId);
                dbHelper.save(family);
                dbHelper.save(mSelectFamily);

                FamilyBean parent = dbHelper.findFamilyById(parentId);
                if (parent != null) {
                    parent.setSpouseId(familyId);
                    dbHelper.save(parent);
                }

                dbHelper.save(children);
                dbHelper.closeDB();
                setResult(RESULT_OK);
                onBackPressed();
            }
        } else if (Config.TYPE_ADD_CHILD.equals(mAddType)) {
            final String selectFamilySex = mSelectFamily.getSex();
            if (SEX_MALE.equals(selectFamilySex)) {
                family.setFatherId(mSelectFamily.getMemberId());
                family.setMotherId(mSelectFamily.getSpouseId());
            } else {
                family.setFatherId(mSelectFamily.getSpouseId());
                family.setMotherId(mSelectFamily.getMemberId());
            }
            final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
            dbHelper.save(family);
            dbHelper.closeDB();
            setResult(RESULT_OK);
            onBackPressed();
        } else if (Config.TYPE_ADD_BROTHERS_AND_SISTERS.equals(mAddType)) {
            family.setFatherId(mSelectFamily.getFatherId());
            family.setMotherId(mSelectFamily.getMotherId());
            final FamilyDBHelper dbHelper = new FamilyDBHelper(this);
            dbHelper.save(family);
            dbHelper.closeDB();
            setResult(RESULT_OK);
            onBackPressed();
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
                doAddFamily();
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
