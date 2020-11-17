package com.work.lazxy.writeaway.ui.adapter

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.Constant
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.event.EventDeleteNote
import com.work.lazxy.writeaway.ui.activity.NoteActivity
import com.work.lazxy.writeaway.utils.CalendarUtils
import com.work.lazxy.writeaway.utils.UIUtils
import org.greenrobot.eventbus.EventBus

/**
 * Created by Lazxy on 2017/4/29.
 */
class NoteDirAdapter(layoutResId: Int, data: List<NoteEntity?>?) : BaseQuickAdapter<NoteEntity, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: NoteEntity) {
        val title = if (TextUtils.isEmpty(item.title)) mContext.resources.getString(R.string.no_title) else item.title
        helper.setText(R.id.tv_note_dir_title, title)
        helper.setText(R.id.tv_note_dir_preview, item.preview)
        helper.setText(R.id.tv_note_dir_last_edit, CalendarUtils.getTimeFromTimestamp(item.lastEditTime))
        val options = helper.getView<View>(R.id.iv_note_dir_options)
        options.tag = item
        options.setOnClickListener(mOptionsListener)
        val card = helper.getView<View>(R.id.card_item_note)
        card.tag = item
        card.setOnClickListener(mCardListener)
    }

    private val mOptionsListener = View.OnClickListener { v -> /*显示一个PopupMenu*/
        val popupMenu = PopupMenu(v.context, v, Gravity.BOTTOM)
        popupMenu.inflate(R.menu.popup_note_dir)
        UIUtils.setIconEnable(popupMenu.menu, true)
        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            when (id) {
                R.id.menu_note_dir_delete -> UIUtils.showSimpleAlertDialog(v.context,
                        null, "确认删除吗？", "删除", "取消",
                        DialogInterface.OnClickListener { dialog, which ->
                            val path = (v.tag as NoteEntity).filePath
                            EventBus.getDefault().post(EventDeleteNote(path))
                        }, null)
                else -> {
                }
            }
            true
        }
        popupMenu.show()
    }
    private val mCardListener = View.OnClickListener { v ->
        val intent = Intent(mContext, NoteActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NOTE, v.tag as NoteEntity)
        (mContext as Activity).startActivityForResult(intent, Constant.Common.REQUEST_CODE_UPDATE_NOTE)
    }
}