package com.cxb.myfamilytree.presenter;

import com.cxb.myfamilytree.config.Constants;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.model.FamilyModel;
import com.cxb.myfamilytree.model.IFamilyModel;
import com.cxb.myfamilytree.view.ILaunchView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 启动页Presenter实现
 */

public class LaunchPresenter implements IBasePresenter<ILaunchView> {

    private ILaunchView mView;
    private IFamilyModel mModel;

    private CompositeDisposable mDisposable;

    public LaunchPresenter() {
        mDisposable = new CompositeDisposable();
        mModel = new FamilyModel();
    }

    public void getFamily(String familyId) {
        mDisposable.add(
                mModel.findFamilyById(familyId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Consumer<FamilyBean>() {
                                    @Override
                                    public void accept(@NonNull FamilyBean family) throws Exception {
                                        if (isActive()) {
                                            mView.startMainActivity();
                                        }
                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        if (isActive()) {
                                            addFamily();
                                        }
                                    }
                                }));
    }

    private void addFamily() {
        FamilyBean family = new FamilyBean();
        family.setMemberId(Constants.MY_ID);
        family.setMemberName("我的姓名");
        family.setCall("我");
        family.setSex(SEX_MALE);
        family.setBirthday("");

        mDisposable.add(
                mModel.saveFamily(family)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (isActive()) {
                                    mView.startMainActivity();
                                }
                            }
                        })
                        .subscribe());
    }

    private boolean isActive() {
        return mView != null;
    }

    @Override
    public void attachView(ILaunchView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mView = null;
    }
}
