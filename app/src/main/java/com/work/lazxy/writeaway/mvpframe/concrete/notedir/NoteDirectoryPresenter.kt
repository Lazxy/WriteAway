package com.work.lazxy.writeaway.mvpframe.concrete.notedir

import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver

/**
 * Created by Lazxy on 2017/2/25.
 */
class NoteDirectoryPresenter : NoteDirectoryContract.Presenter() {
    override fun loadData(limitIndex: Int, num: Int) {
        observe(mModel.loadData(limitIndex, num), object : BaseObserver<List<NoteEntity>>(mView) {
            override fun onSuccess(noteEntities: List<NoteEntity>) {
                mView.onLoadSuccessful(noteEntities)
            }
        })
    }

    override fun deleteData(path: String) {
        observe(mModel.deleteData(path), object : BaseObserver<Boolean>() {
            override fun onSuccess(isDelete: Boolean) {
                if (isDelete) mView.onDeleteSuccessful(path) else mView.onRequestError("好像出了些问题...")
            }
        })
    }
}