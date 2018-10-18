package com.cxb.myfamilytree.model;

import io.reactivex.Observable;

/**
 * 家谱树model接口
 */

public interface IFamilyModel {

    Observable<FamilyBean> findFamilyById(String familyId);

    Observable saveFamily(FamilyBean family);

    Observable updateSpouseIdEach(String currentId, String spouseId);

    Observable updateParentId(String fatherId, String motherId);

    Observable exchangeParentId(String afterChangeFatherId, String afterChangeMotherId);

    Observable updateGender(String familyId, String gender);

    Observable deleteFamily(FamilyBean family);
}
