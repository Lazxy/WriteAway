package com.work.lazxy.writeaway.mvpframe.concrete.notedir

import com.work.lazxy.writeaway.db.NoteDataHandler
import com.work.lazxy.writeaway.entity.NoteEntity
import io.reactivex.Observable
import java.io.File

/**
 * Created by Lazxy on 2017/2/25.
 */
class NoteDirectoryModel : NoteDirectoryContract.Model {
    override fun loadData(limitIndex: Int, num: Int): Observable<List<NoteEntity>> {
        return Observable.create<List<NoteEntity>> { emitter ->
            val notes: List<NoteEntity> = NoteDataHandler.instance.getData(limitIndex, num)
            emitter.onNext(notes)
            emitter.onComplete()
        }
    }

    override fun deleteData(path: String): Observable<Boolean> {
        return Observable.create { e ->
            val file = File(path)
            NoteDataHandler.instance.deleteData(path)
            val isSuccessful = file.delete()
            e.onNext(isSuccessful)
            e.onComplete()
        }
    }
}