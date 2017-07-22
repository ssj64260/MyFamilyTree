package com.cxb.myfamilytree.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.util.List;

/**
 * 家庭成员
 */
@Table("FamilyBean")
public class FamilyBean implements Parcelable {

    public static final String SEX_MALE = "1";//1为男性
    public static final String SEX_FEMALE = "2";//2为女性

    @PrimaryKey(AssignType.BY_MYSELF)
    private String memberId;//人员ID
    private String memberName;//姓名
    private String call;//称呼
    private String memberImg;//头像
    private String sex;//性别：1男，2女
    private String birthday;//生日

    private String fatherId;//父亲ID
    private String motherId;//母亲ID
    private String spouseId;//配偶ID

    private String mothersId;//养母ID
    private String fathersId;//养父ID

    @Ignore
    private FamilyBean spouse;//配偶
    @Ignore
    private List<FamilyBean> children;//儿女
    @Ignore
    private boolean isSelect = false;//是否选中

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memberId);
        dest.writeString(this.memberName);
        dest.writeString(this.call);
        dest.writeString(this.memberImg);
        dest.writeString(this.sex);
        dest.writeString(this.birthday);
        dest.writeString(this.fatherId);
        dest.writeString(this.motherId);
        dest.writeString(this.spouseId);
        dest.writeString(this.mothersId);
        dest.writeString(this.fathersId);
        dest.writeParcelable(this.spouse, flags);
        dest.writeTypedList(this.children);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }

    public FamilyBean() {
    }

    protected FamilyBean(Parcel in) {
        this.memberId = in.readString();
        this.memberName = in.readString();
        this.call = in.readString();
        this.memberImg = in.readString();
        this.sex = in.readString();
        this.birthday = in.readString();
        this.fatherId = in.readString();
        this.motherId = in.readString();
        this.spouseId = in.readString();
        this.mothersId = in.readString();
        this.fathersId = in.readString();
        this.spouse = in.readParcelable(FamilyBean.class.getClassLoader());
        this.children = in.createTypedArrayList(FamilyBean.CREATOR);
        this.isSelect = in.readByte() != 0;
    }

    public static final Creator<FamilyBean> CREATOR = new Creator<FamilyBean>() {
        @Override
        public FamilyBean createFromParcel(Parcel source) {
            return new FamilyBean(source);
        }

        @Override
        public FamilyBean[] newArray(int size) {
            return new FamilyBean[size];
        }
    };

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(String spouseId) {
        this.spouseId = spouseId;
    }

    public String getMothersId() {
        return mothersId;
    }

    public void setMothersId(String mothersId) {
        this.mothersId = mothersId;
    }

    public String getFathersId() {
        return fathersId;
    }

    public void setFathersId(String fathersId) {
        this.fathersId = fathersId;
    }

    public FamilyBean getSpouse() {
        return spouse;
    }

    public void setSpouse(FamilyBean spouse) {
        this.spouse = spouse;
    }

    public List<FamilyBean> getChildren() {
        return children;
    }

    public void setChildren(List<FamilyBean> children) {
        this.children = children;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
