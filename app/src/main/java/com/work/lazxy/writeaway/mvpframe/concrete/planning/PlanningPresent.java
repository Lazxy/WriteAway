package com.work.lazxy.writeaway.mvpframe.concrete.planning;


import android.util.Log;

import com.work.lazxy.writeaway.entity.PlanningEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver;

import java.util.List;

/**
 * Created by Lazxy on 2017/2/22.
 */

public class PlanningPresent extends PlanningContract.Presenter {
    @Override
    public void getPlanning() {
        observe(mModel.getPlanning(), new BaseObserver<List<PlanningEntity>>(mView) {
            @Override
            protected void onSuccess(List<PlanningEntity> plannings) {
                if(plannings!=null){
                    mView.setPlanning(plannings);
                }
            }
        });
    }

    @Override
    public void savePlanning(List<PlanningEntity> plannings) {
        observe(mModel.savePlanning(plannings), new BaseObserver<String>(mView) {
            @Override
            protected void onSuccess(String s) {
                Log.i("DEBUG","saved success");
            }
        });
    }

    @Override
    public void addPlanning(PlanningEntity planning) {
        observe(mModel.addPlanning(planning), new BaseObserver<String>() {
            @Override
            protected void onSuccess(String s) {
                Log.i("DEBUG","saved success");
            }
        });
    }
}
