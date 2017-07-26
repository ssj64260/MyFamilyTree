package com.cxb.myfamilytree.presenter;

import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.model.FamilyModel;
import com.cxb.myfamilytree.view.IFamilyView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 家谱树界面presenter实现
 */

public class FamilyPresenter implements IFamilyPresenter {

    private FamilyModel mModel;
    private IFamilyView mView;

    public FamilyPresenter() {
        mModel = new FamilyModel();
    }

    @Override
    public void getFamily(String familyId) {
        mModel.findFamilyById(familyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<FamilyBean>() {
                            @Override
                            public void accept(@NonNull FamilyBean family) throws Exception {
                                if (isActive()) {
                                    mView.showFamilyTree(family);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
    }

    private boolean isActive() {
        return mView != null;
    }

    @Override
    public void attachView(IFamilyView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
