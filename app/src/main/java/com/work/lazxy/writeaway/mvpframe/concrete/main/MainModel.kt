package com.work.lazxy.writeaway.mvpframe.concrete.main

import com.work.lazxy.writeaway.db.NoteDataHandler
import com.work.lazxy.writeaway.db.PlanningDataHandler
import io.reactivex.Observable

/**
 * Created by Lazxy on 2017/5/30.
 */
class MainModel : MainContract.Model {
    override fun getDataCount(): Observable<IntArray> {
        return Observable.create { e ->
            val noteCount = NoteDataHandler.instance.getNotesCount()
            val planningCount = PlanningDataHandler.instance.planningsCount
            e.onNext(intArrayOf(noteCount, planningCount))
            e.onComplete()
        }
    }
}