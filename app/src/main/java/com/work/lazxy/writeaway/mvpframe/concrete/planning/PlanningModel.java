package com.work.lazxy.writeaway.mvpframe.concrete.planning;


import android.util.SparseArray;

import com.work.lazxy.writeaway.db.PlanningDataHandler;
import com.work.lazxy.writeaway.entity.PlanningEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Lazxy on 2017/2/22.
 */

public class PlanningModel implements PlanningContract.Model {

    @Override
    public Observable<List<PlanningEntity>> getPlanning() {
        return Observable.create(new ObservableOnSubscribe<List<PlanningEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PlanningEntity>> e) throws Exception {
                List<PlanningEntity> plannings = new ArrayList<>();
                SparseArray<String> rawData = PlanningDataHandler.getInstance().getPlannings();
                if(rawData!=null&&rawData.size()>0){
                    for(int i=1;i<=rawData.size();i++){
                        plannings.add(new PlanningEntity(i,rawData.get(i)));
                    }
                }
                e.onNext(plannings);
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<String> savePlanning(final List<PlanningEntity> plannings) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                PlanningDataHandler.getInstance().savePlannings(plannings);
                e.onNext("");
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<String> addPlanning(final PlanningEntity planning) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                PlanningDataHandler.getInstance().addPlanning(planning);
                e.onNext("");
                e.onComplete();
            }
        });
    }
}
