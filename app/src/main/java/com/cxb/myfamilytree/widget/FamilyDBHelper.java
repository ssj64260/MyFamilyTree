package com.cxb.myfamilytree.widget;

import android.content.Context;
import android.text.TextUtils;

import com.cxb.myfamilytree.model.FamilyBean;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 家庭成员数据库帮助类
 */

public class FamilyDBHelper {

    private final String DB_NAME = "MyFamilyTree.db";
    private final boolean DEBUGGABLE = true; // 是否输出log

    private LiteOrm liteOrm;

    private boolean mInquirySpouse = true;//是否查询配偶

    public FamilyDBHelper(Context context) {
        liteOrm = LiteOrm.newSingleInstance(context, DB_NAME);
        liteOrm.setDebugged(DEBUGGABLE);
    }

    public void setmInquirySpouse(boolean mInquirySpouse) {
        this.mInquirySpouse = mInquirySpouse;
    }

    public boolean ismInquirySpouse() {
        return mInquirySpouse;
    }

    public FamilyBean findFamilyById(String familyId) {
        if (!TextUtils.isEmpty(familyId)) {
            List<FamilyBean> families = liteOrm.query(new QueryBuilder<>(FamilyBean.class).where("memberId = ?", familyId));
            if (families.size() > 0) {
                return families.get(0);
            }
        }

        return null;
    }

    public List<FamilyBean> findFamiliesByFatherId(String fatherId, String ignoreId) {
        if (!TextUtils.isEmpty(fatherId)) {
            final String sql = "fatherId = ? and memberId != ?";
            return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                    .appendOrderAscBy("birthday")
                    .where(sql, fatherId, ignoreId));
        } else {
            return new ArrayList<>();
        }
    }

    public List<FamilyBean> findFamiliesByMotherId(String motherId, String ignoreId) {
        if (!TextUtils.isEmpty(motherId)) {
            final String sql = "motherId = ? and memberId != ?";
            return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                    .appendOrderAscBy("birthday")
                    .where(sql, motherId, ignoreId));
        } else {
            return new ArrayList<>();
        }
    }

    public List<FamilyBean> findMyBrothersByParentId(String fatherId, String motherId, String ignoreId, String birthday, boolean isLittle) {
        final String parentId;
        String sql;
        if (!TextUtils.isEmpty(fatherId)) {
            sql = "fatherId = ? and memberId != ?";
            parentId = fatherId;
        } else if (!TextUtils.isEmpty(motherId)) {
            sql = "motherId = ? and memberId != ?";
            parentId = motherId;
        } else {
            return new ArrayList<>();
        }

        if (isLittle) {
            sql += " and birthday > ?";
        } else {
            sql += " and birthday <= ?";
        }

        return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                .appendOrderAscBy("birthday")
                .where(sql, parentId, ignoreId, birthday));
    }

    public long save(List<FamilyBean> families) {
        return liteOrm.save(families);
    }

    public long save(FamilyBean family) {
        return liteOrm.save(family);
    }

    private void setChildren(List<FamilyBean> famliyList) {
        if (famliyList != null) {
            for (FamilyBean family : famliyList) {
                final List<FamilyBean> childrenList = getChildren(family, "");
                if (childrenList != null && mInquirySpouse) {
                    setSpouse(childrenList);
                }
                family.setChildren(childrenList);
            }
        }
    }

    private void setSpouse(List<FamilyBean> famliyList) {
        for (FamilyBean family : famliyList) {
            setSpouse(family);
        }
    }

    public void setSpouse(FamilyBean family) {
        final String spouseId = family.getSpouseId();
        family.setSpouse(findFamilyById(spouseId));
    }

    public int deleteTable() {
        return liteOrm.delete(FamilyBean.class);
    }

    public void closeDB() {
        if (liteOrm != null) {
            liteOrm.close();
        }
    }

    public FamilyBean getCouple(String maleId, String femaleId) {
        final FamilyBean male = findFamilyById(maleId);
        final FamilyBean female = findFamilyById(femaleId);
        if (male != null) {
            male.setSpouse(female);
            return male;
        } else if (female != null) {
            return female;
        }
        return null;
    }

    public List<FamilyBean> getChildren(FamilyBean parentInfo, String ignoreId) {
        final String parentId = parentInfo.getMemberId();
        final String parentSex = parentInfo.getSex();
        final List<FamilyBean> childrenList;
        if (SEX_MALE.equals(parentSex)) {
            childrenList = findFamiliesByFatherId(parentId, ignoreId);
        } else {
            childrenList = findFamiliesByMotherId(parentId, ignoreId);
        }
        return childrenList;
    }

    public List<FamilyBean> getChildrenAndGrandChildren(FamilyBean parentInfo, String ignoreId) {

        final List<FamilyBean> childrenList = getChildren(parentInfo, ignoreId);
        setSpouse(childrenList);
        setChildren(childrenList);

        return childrenList;
    }

    public List<FamilyBean> getMyBrothers(FamilyBean myInfo, boolean isLittle) {
        final String myId = myInfo.getMemberId();
        final String myBirthday = myInfo.getBirthday();
        final String myFatherId = myInfo.getFatherId();
        final String myMotherId = myInfo.getMotherId();

        final List<FamilyBean> brotherList = findMyBrothersByParentId(myFatherId, myMotherId, myId, myBirthday, isLittle);
        setSpouse(brotherList);
        setChildren(brotherList);
        return brotherList;
    }
}
