package com.work.lazxy.writeaway.mvpframe.concrete.note

import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver
import com.work.lazxy.writeaway.ui.filter.backstack.Action

/**
 * Created by Lazxy on 2017/2/25.
 */
class NotePresenter : NoteContract.Presenter() {
    override fun getContent(path: String) {
        observe(mModel.getContent(path), object : BaseObserver<String>(mView) {
            protected override fun onSuccess(s: String) {
                mView.setContent(Action.REVOKED_SIGN + s + Action.REVOKED_SIGN)
            }

            override fun onError(e: Throwable) {
                mView.onRequestError("文本好像走丢了~")
            }
        })
    }

    override fun saveAll(path: String, content: String?,
                         title: String, preview: String) {
        observe(mModel.saveAll(path, content, title, preview, System.currentTimeMillis()), object : BaseObserver<NoteEntity>() {
            protected override fun onSuccess(note: NoteEntity) {
                mView.onSaveSuccessful(note)
            }
        })
    }

//    fun shareAsPhoto(content: String?) {}
}