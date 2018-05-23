package com.work.lazxy.writeaway.mvpframe.concrete.main;

import com.work.lazxy.writeaway.mvpframe.base.BaseObserver;

/**
 * Created by Lazxy on 2017/5/30.
 */

public class MainPresenter extends MainContract.Presenter {
    @Override
    public void getDataCount() {
        observe(mModel.getDataCount(), new BaseObserver<int[]>() {
            @Override
            protected void onSuccess(int[] data) {
                mView.onUpdateData(data[0],data[1]);
            }

            @Override
            public void onError(Throwable t){
                mView.onRequestError("数据好像不见了");
            }
        });

    }
}
