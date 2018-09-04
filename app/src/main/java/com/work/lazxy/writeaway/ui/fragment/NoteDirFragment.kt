package com.work.lazxy.writeaway.ui.fragment

import android.os.Bundle

import com.chad.library.adapter.base.BaseQuickAdapter
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.event.EventChangeNote
import com.work.lazxy.writeaway.event.EventDeleteNote
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameListFragment
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryContract
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryModel
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryPresenter
import com.work.lazxy.writeaway.ui.adapter.NoteDirAdapter
import kotlinx.android.synthetic.main.fragment_note_dir.view.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList

/**
 * Created by Lazxy on 2017/4/27.
 */

class NoteDirFragment : BaseFrameListFragment<NoteDirectoryPresenter, NoteDirectoryModel>(), NoteDirectoryContract.View {
    private var mCurrentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_note_dir)
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        mCurrentIndex = 0
        setList(contentView.refreshLayoutNoteDir, contentView.rvNoteDir, NoteDirAdapter(R.layout.item_note_dir, ArrayList()))
    }

    override fun initLoad() {
        requestData(true)
    }

    public override fun requestData(isRefresh: Boolean) {
        super.requestData(isRefresh)
        if (isRefresh) {
            mCurrentIndex = 0
        } else {
            mCurrentIndex = mAdapter.itemCount - 1
        }
        mPresenter.loadData(mCurrentIndex, itemPerPage)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeleteNote(event: EventDeleteNote) {
        mPresenter.deleteData(event.mNotePath)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeNote(event: EventChangeNote) {
        requestData(event.mShouldRefresh)
    }

    override fun onLoadSuccessful(notes: List<NoteEntity>) {
        setData(notes)
    }

    override fun onDeleteSuccessful(path: String) {
        val notes = mAdapter.data as MutableList<NoteEntity>
        for (note: NoteEntity in notes) {
            if (note.filePath == path) {
                notes.remove(note)
                if (notes.isEmpty()) {
                    mAdapter.emptyView = mEmptyView
                }
                mAdapter.notifyDataSetChanged()
                break
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
