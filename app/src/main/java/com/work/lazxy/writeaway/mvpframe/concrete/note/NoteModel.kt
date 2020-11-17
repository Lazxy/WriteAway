package com.work.lazxy.writeaway.mvpframe.concrete.note

import com.work.lazxy.writeaway.db.NoteDataHandler.Companion.instance
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.utils.FileUtils
import io.reactivex.Observable

/**
 * Created by Lazxy on 2017/2/25.
 */
class NoteModel : NoteContract.Model {
    override fun getContent(path: String): Observable<String> {
        return Observable.create { e ->
            e.onNext(FileUtils.readTextFormFile(path))
            e.onComplete()
        }
    }

    override fun saveAll(path: String, content: String?, title: String, preview: String, editTime: Long): Observable<NoteEntity> {
        return Observable.create { e ->
            if (content != null) {
                FileUtils.writeTextToFile(path, content)
            }
            instance.saveData(title, path, preview, editTime)
            e.onNext(NoteEntity(title, preview, path, editTime))
        }
    }

//    override fun shareAsPhoto(content: String): Observable<Bitmap> {
//    }
}