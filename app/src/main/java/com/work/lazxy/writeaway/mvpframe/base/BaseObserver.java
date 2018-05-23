package com.work.lazxy.writeaway.mvpframe.base;

//import com.google.firebase.crash.FirebaseCrash;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Lazxy on 2017/5/20.
 */

public abstract class BaseObserver<T> implements Observer<T> {
    protected Disposable mDisposable;
    private BaseView mView;

    public BaseObserver(){

    }
    public BaseObserver(BaseView view){
        mView = view;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if(mView != null)
            mView.onRequestStart();
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        if(mView!=null)
            mView.onRequestError(e.getMessage());
//        FirebaseCrash.report(e);
    }

    @Override
    public void onComplete() {
        if(mView!=null)
            mView.onRequestEnd();
    }

    protected abstract void onSuccess(T t);
}
