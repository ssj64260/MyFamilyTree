package com.cxb.myfamilytree.presenter;

import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.view.IAddFamilyView;

/**
 * 添加亲人Presenter接口
 */

public interface IAddFamilyPresenter extends IBasePresenter<IAddFamilyView> {

    void addSpouse(FamilyBean selectFamily, FamilyBean addFamily);

    void addParent(FamilyBean selectFamily, FamilyBean addFamily);

    void addChild(FamilyBean selectFamily, FamilyBean addFamily);

    void addBrothersAndSisters(FamilyBean selectFamily, FamilyBean addFamily);

    void updateFamilyInfo(FamilyBean family, boolean isChangeGender);

}
