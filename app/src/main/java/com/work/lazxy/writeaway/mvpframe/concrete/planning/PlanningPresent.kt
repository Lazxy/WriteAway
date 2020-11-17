package com.work.lazxy.writeaway.mvpframe.concrete.planning

import android.util.Log
import com.work.lazxy.writeaway.entity.PlanningEntity
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver

/**
 * Created by Lazxy on 2017/2/22.
 */
class PlanningPresent : PlanningContract.Presenter() {
    override fun getPlanning() {
        observe(mModel.planning, object : BaseObserver<List<PlanningEntity>>(mView) {
            override fun onSuccess(plannings: List<PlanningEntity>) {
                mView.setPlanning(plannings)
            }
        })
    }

    override fun savePlanning(plannings: List<PlanningEntity>) {
        observe(mModel.savePlanning(plannings), object : BaseObserver<String>(mView) {
            override fun onSuccess(s: String) {
                Log.i("DEBUG", "saved success")
            }
        })
    }

    override fun addPlanning(planning: PlanningEntity) {
        observe(mModel.addPlanning(planning), object : BaseObserver<String>() {
            override fun onSuccess(s: String) {
                Log.i("DEBUG", "saved success")
            }
        })
    }
}