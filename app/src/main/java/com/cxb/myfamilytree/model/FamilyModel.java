package com.cxb.myfamilytree.model;

import com.cxb.myfamilytree.app.APP;
import com.cxb.myfamilytree.widget.familytree.FamilyDBHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * 家谱树界面model实现
 */

public class FamilyModel implements IFamilyModel {
    @Override
    public Observable<FamilyBean> findFamilyById(final String familyId) {
        return Observable
                .create(new ObservableOnSubscribe<FamilyBean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<FamilyBean> e) throws Exception {
                        final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                        FamilyBean family = dbHelper.findFamilyById(familyId);
                        dbHelper.closeDB();

                        if (family != null) {
                            e.onNext(family);
                            e.onComplete();
                        } else {
                            e.onError(new Throwable("can not find family"));
                        }
                    }
                });
    }

    @Override
    public Observable saveFamily(final FamilyBean family) {
        return Observable
                .create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                        dbHelper.save(family);
                        dbHelper.closeDB();

                        e.onComplete();
                    }
                });
    }

    @Override
    public Observable updateSpouseIdEach(final String currentId, final String spouseId) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                dbHelper.updateSpouseId(currentId, spouseId);
                dbHelper.updateSpouseId(spouseId, currentId);
                dbHelper.closeDB();

                e.onComplete();
            }
        });
    }

    @Override
    public Observable updateParentId(final String fatherId, final String motherId) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                dbHelper.updateParentId(fatherId, motherId);
                dbHelper.closeDB();

                e.onComplete();
            }
        });
    }

    @Override
    public Observable exchangeParentId(final String afterChangeFatherId, final String afterChangeMotherId) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                dbHelper.exchangeParentId(afterChangeFatherId, afterChangeMotherId);
                dbHelper.closeDB();

                e.onComplete();
            }
        });
    }

    @Override
    public Observable updateGender(final String familyId, final String gender) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                dbHelper.updateGender(familyId, gender);
                dbHelper.closeDB();

                e.onComplete();
            }
        });
    }

    @Override
    public Observable deleteFamily(final FamilyBean family) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                final FamilyDBHelper dbHelper = new FamilyDBHelper(APP.get());
                dbHelper.deleteFamily(family);
                dbHelper.closeDB();

                e.onComplete();
            }
        });
    }
}
