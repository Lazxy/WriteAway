package com.work.lazxy.writeaway.mvpframe.concrete.main

import com.work.lazxy.writeaway.mvpframe.base.BaseObserver

/**
 * Created by Lazxy on 2017/5/30.
 */
class MainPresenter : MainContract.Presenter() {
    override fun getDataCount() {
        observe(mModel.dataCount, object : BaseObserver<IntArray>() {
            protected override fun onSuccess(data: IntArray) {
                mView.onUpdateData(data[0], data[1])
            }

            override fun onError(t: Throwable) {
                mView.onRequestError("数据好像不见了")
            }
        })
    }
}