package com.work.lazxy.writeaway.mvpframe.concrete.planning;


import com.work.lazxy.writeaway.entity.PlanningEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseModel;
import com.work.lazxy.writeaway.mvpframe.base.BasePresenter;
import com.work.lazxy.writeaway.mvpframe.base.BaseView;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Lazxy on 2017/2/22.
 */

public interface PlanningContract {
    interface Model extends BaseModel {
        Observable<List<PlanningEntity>> getPlanning();
        Observable<String> savePlanning(List<PlanningEntity> plannings);
        Observable<String> addPlanning(PlanningEntity planning);
    }
    interface View extends BaseView {
        void setPlanning(List<PlanningEntity> plannings);
    }
    abstract class Presenter extends BasePresenter<Model,View> {
        public abstract void getPlanning();
        public abstract void savePlanning(List<PlanningEntity> plannings);
        public abstract void addPlanning(PlanningEntity planning);
    }
}
