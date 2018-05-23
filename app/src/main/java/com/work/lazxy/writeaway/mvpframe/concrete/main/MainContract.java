package com.work.lazxy.writeaway.mvpframe.concrete.main;

import com.work.lazxy.writeaway.mvpframe.base.BaseModel;
import com.work.lazxy.writeaway.mvpframe.base.BasePresenter;
import com.work.lazxy.writeaway.mvpframe.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Lazxy on 2017/5/30.
 */

public interface MainContract {
    interface Model extends BaseModel {
        Observable<int[]> getDataCount();
    }

    interface View extends BaseView {
        void onUpdateData(int noteCount,int planningCount);
    }

    abstract class Presenter extends BasePresenter<Model,View> {
        public abstract void getDataCount();
    }
}
