package com.cxb.myfamilytree.presenter;

/**
 * presenter 基础部分
 */

public interface IBasePresenter<V> {

    void attachView(V view);

    void detachView();

}
