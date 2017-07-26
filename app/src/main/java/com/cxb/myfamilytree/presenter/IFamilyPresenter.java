package com.cxb.myfamilytree.presenter;

import com.cxb.myfamilytree.view.IFamilyView;

/**
 * 家谱树presenter接口
 */

public interface IFamilyPresenter extends IBasePresenter<IFamilyView> {

    void getFamily(String familyId);

}
