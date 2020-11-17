package com.work.lazxy.writeaway.mvpframe.concrete.planning

import com.work.lazxy.writeaway.db.PlanningDataHandler
import com.work.lazxy.writeaway.entity.PlanningEntity
import io.reactivex.Observable
import java.util.*

/**
 * Created by Lazxy on 2017/2/22.
 */
class PlanningModel : PlanningContract.Model {
    override fun getPlanning(): Observable<List<PlanningEntity>> {
        return Observable.create { e ->
            val plannings: MutableList<PlanningEntity> = ArrayList()
            val rawData = PlanningDataHandler.instance.getPlannings()
            if (rawData.size() > 0) {
                for (i in 1..rawData.size()) {
                    plannings.add(PlanningEntity(i, rawData[i]))
                }
            }
            e.onNext(plannings)
            e.onComplete()
        }
    }

    override fun savePlanning(plannings: List<PlanningEntity>): Observable<String> {
        return Observable.create { e ->
            PlanningDataHandler.instance.savePlannings(plannings)
            e.onNext("")
            e.onComplete()
        }
    }

    override fun addPlanning(planning: PlanningEntity): Observable<String> {
        return Observable.create { e ->
            PlanningDataHandler.instance.addPlanning(planning)
            e.onNext("")
            e.onComplete()
        }
    }
}