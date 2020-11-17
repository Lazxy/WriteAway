package com.work.lazxy.writeaway.ui.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.ConfigManager
import com.work.lazxy.writeaway.common.Constant
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameActivity
import com.work.lazxy.writeaway.mvpframe.concrete.note.NoteContract
import com.work.lazxy.writeaway.mvpframe.concrete.note.NoteModel
import com.work.lazxy.writeaway.mvpframe.concrete.note.NotePresenter
import com.work.lazxy.writeaway.ui.filter.EditLengthInputFilter
import com.work.lazxy.writeaway.ui.filter.LineBreakInputFilter
import com.work.lazxy.writeaway.ui.filter.backstack.Action
import com.work.lazxy.writeaway.ui.widget.ProgressDialog
import com.work.lazxy.writeaway.utils.CalendarUtils
import com.work.lazxy.writeaway.utils.FileUtils
import com.work.lazxy.writeaway.utils.StringUtils
import com.work.lazxy.writeaway.utils.UIUtils

import kotlinx.android.synthetic.main.activity_note.*

/**
 * Created by Lazxy on 2017/4/27.
 */

class NoteActivity : BaseFrameActivity<NotePresenter, NoteModel>(), NoteContract.View {
    private val mProgress: ProgressDialog by lazy { ProgressDialog(this) }
    private var mNote: NoteEntity? = null
    private var mOldLength: Int = 0
    private var mCanRevoke = false
    private var mShouldQuit = false
    private var mIsNewNote = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
    }

    override fun initData() {
        mNote = intent.getSerializableExtra(Constant.Extra.EXTRA_NOTE) as NoteEntity?
        if (mNote == null) {
            mIsNewNote = true
            //新建笔记的情况
            mNote = NoteEntity("", "", FileUtils.createFileWithTime(ConfigManager.fileSavedPath),
                    System.currentTimeMillis())
        }
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!TextUtils.isEmpty(mNote?.title))
            etNoteTitle.setText(mNote?.title)
        else
            etNoteTitle.hint = resources.getString(R.string.no_title)
        etNoteTitle.clearFocus()
        tvEditTime?.text = CalendarUtils.getTimeFromTimestamp(mNote!!.lastEditTime)
        setCanRevoke(false)
    }

    override fun initListener() {
        toolbar?.setNavigationOnClickListener { this@NoteActivity.onBackPressed() }

        padNoteContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                setCanRevoke(padNoteContent.isCanRevoke)
            }
        })

        etNoteTitle.filters = arrayOf<InputFilter>(EditLengthInputFilter(32))
    }

    override fun initLoad() {
        mPresenter.getContent(mNote!!.filePath)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (mCanRevoke) {
            menu.findItem(R.id.menu_note_revoke).isVisible = true
            menu.findItem(R.id.menu_note_save).isVisible = true
        } else {
            menu.findItem(R.id.menu_note_revoke).isVisible = false
            menu.findItem(R.id.menu_note_save).isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_revoke -> padNoteContent.revoke()
            R.id.menu_note_clear -> UIUtils.showSimpleAlertDialog(this, null, "确认清空所有内容吗？", "清空", "取消",
                    DialogInterface.OnClickListener { _, _ ->
                        //这里会被认为是一个Add操作，而不是替换，所以没办法将其加入回退栈
                        padNoteContent.setText(Action.REVOKED_SIGN + LineBreakInputFilter.INDENT + Action.REVOKED_SIGN)
                        padNoteContent.setSelection(padNoteContent.length())
                        padNoteContent.clearRevokeStack()
                        setCanRevoke(false)
                    }, null)
            R.id.menu_note_save -> {
                val content = padNoteContent.text.toString()
                mPresenter.saveAll(mNote!!.filePath, content, etNoteTitle.text.toString(), StringUtils.getPreview(content))
                mShouldQuit = false
                padNoteContent.clearFocus()
                mOldLength = padNoteContent.length()
                setCanRevoke(false)
                UIUtils.hideInputMethod(this, padNoteContent)//隐藏输入法键盘
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //这里对文本修改的判断有一种情况下会失效，即用户修改了超过20次内容后又正好回退到回退栈清空时，页面不会出现保存标记，且退出保存不会被触发
        if (mCanRevoke || mOldLength != padNoteContent.length()) {
            UIUtils.showSimpleAlertDialog(this, null, "文本有修改，需要保存吗？", "保存", "取消", { dialog, which ->
                val content = padNoteContent.text.toString()
                mPresenter.saveAll(mNote!!.filePath, content, etNoteTitle.text.toString(), StringUtils.getPreview(content))
                mShouldQuit = true
            }) { _, _ -> super@NoteActivity.onBackPressed() }
        } else {
            //正文无改动时
            if (etNoteTitle.text.toString() != mNote?.title) {
                val content = if (mIsNewNote) {
                    "" //这里强迫生成一个空白的新文本文件
                } else {
                    null //默认为是已存在文本的无修改情况
                }
                //只更改标题信息
                mPresenter.saveAll(mNote!!.filePath, content, etNoteTitle.text.toString(), mNote!!.preview)
                mShouldQuit = true
                return
            }
            super.onBackPressed()
        }
    }

    override fun onRequestStart() {
        mProgress.show()
    }

    override fun onRequestError(msg: String) {
        mProgress.dismiss()
        showShortToast(msg)
        etNoteTitle.clearFocus()
    }

    override fun onRequestEnd() {
        mProgress.dismiss()
    }

    override fun setContent(content: String) {
        padNoteContent.setText(content)
        if (padNoteContent.text.toString() == LineBreakInputFilter.INDENT) {
            padNoteContent.setSelection(padNoteContent.length())
            padNoteContent.requestFocus()
        }
        mOldLength = padNoteContent.length()
    }

    override fun onSaveSuccessful(note: NoteEntity) {
        showShortToast("保存成功")
        setResponse(note)
        if (mShouldQuit) {
            finish()
        }
    }

    private fun setCanRevoke(canRevoke: Boolean) {
        mCanRevoke = canRevoke
        invalidateOptionsMenu()
    }

    private fun setResponse(note: NoteEntity) {
        val intent = Intent()
        intent.putExtra(Constant.Extra.EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intent)
        mNote = note
    }
}
