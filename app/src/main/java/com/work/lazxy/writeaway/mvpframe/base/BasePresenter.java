package com.work.lazxy.writeaway.mvpframe.base;

import android.content.Context;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Lazxy on 2017/2/21.
 * MVP Presenter基类
 */

public abstract class BasePresenter<M, V> {
    public M mModel;
    public V mView;
    private WeakReference<V> mViewRef;

    public void setVM(V view, M model) {
        //联系View 与 Model，用弱引用防止内存泄漏
        mViewRef = new WeakReference<V>(view);
        mModel = model;
        mView = mViewRef.get();
    }

    public Context getContext() {
        if (mView instanceof BaseFrameActivity) {
            return (Context) mView;
        } else if (mView instanceof BaseFrameFragment) {
            return ((BaseFrameFragment) mView).getActivity();
        }
        return null;
    }

    public void onStartRequest() {
    }

    public void onEndRequest() {
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

//    protected <T> void subscribe(Observable<T> observable, Subscriber<T> subscriber) {
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
//    }
    protected <T> void observe(Observable<T> observable, Observer<T> observer){
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }
}
