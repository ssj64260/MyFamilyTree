package com.cxb.myfamilytree.presenter;

import com.cxb.myfamilytree.view.ILaunchView;

/**
 * 启动页Presenter接口
 */

public interface ILaunchPresenter extends IBasePresenter<ILaunchView> {

    void getFamily(String familyId);

}
