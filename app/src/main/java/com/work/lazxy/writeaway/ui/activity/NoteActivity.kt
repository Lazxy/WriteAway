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
import com.work.lazxy.writeaway.ui.widget.ProgressDialog
import com.work.lazxy.writeaway.utils.*

import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Lazxy on 2017/4/27.
 */

class NoteActivity : BaseFrameActivity<NotePresenter, NoteModel>(), NoteContract.View {
    private val mProgress: ProgressDialog by lazy { ProgressDialog(this) }
    private lateinit var mStandardNote: NoteEntity
    private var mOldLength: Int = 0
    private var mCanRevoke = false
    private var mShouldQuit = false
    private var mIsNewNote = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
    }

    override fun initData() {
        var note = intent.getSerializableExtra(Constant.Extra.EXTRA_NOTE) as NoteEntity?
        if (note == null) {
            //新建笔记的情况
            note = createNewNote()
            mIsNewNote = true
        }
        mStandardNote = note
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!TextUtils.isEmpty(mStandardNote.title)) {
            etNoteTitle.setText(mStandardNote.title)
        } else {
            etNoteTitle.hint = resources.getString(R.string.no_title)
        }
        etNoteTitle.clearFocus()
        tvEditTime?.text = CalendarUtils.getTimeFromTimestamp(mStandardNote.lastEditTime)
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
        mPresenter.getContent(mStandardNote.filePath)
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
            R.id.menu_note_clear -> clearNote()
            R.id.menu_note_save -> saveNote()
            R.id.menu_note_save_as_pic -> saveAsPicture()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (checkNoteChanged()) {
            //当文本有改动时，弹窗提示，确认后保存文本
            UIUtils.showSimpleAlertDialog(this, null, "文本有修改，需要保存吗？", "保存", "取消", { dialog, which ->
                val content = padNoteContent.text.toString()
                saveNoteInfo(mStandardNote.filePath, content, etNoteTitle.text.toString(), StringUtils.getPreview(content), true)
            }) { _, _ -> super@NoteActivity.onBackPressed() }
        } else {
            //正文无改动时，判断是否保存标题信息，然后直接退出当前页面
            if (etNoteTitle.text.toString() != mStandardNote.title) {
                //为一个新笔记时强迫生成一个空白的新文本文件 否则只保存标题
                val content = if (mIsNewNote) "" else null
                //只更改标题信息
                saveNoteInfo(mStandardNote.filePath, content, etNoteTitle.text.toString(), mStandardNote.preview, true)
                return
            }
            super.onBackPressed()
        }
    }

    private fun clearNote() {
        UIUtils.showSimpleAlertDialog(this, null, "确认清空所有内容吗？", "清空", "取消",
                DialogInterface.OnClickListener { _, _ ->
                    //这里会被认为是一个Add操作，而不是替换，所以没办法将其加入回退栈
                    padNoteContent.setContent(LineBreakInputFilter.INDENT)
                    padNoteContent.setSelection(padNoteContent.length())
                    padNoteContent.clearRevokeStack()
                    setCanRevoke(false)
                }, null)
    }

    private fun saveNote() {
        val content = padNoteContent.text.toString()
        saveNoteInfo(mStandardNote.filePath, content, etNoteTitle.text.toString(), StringUtils.getPreview(content), false)
        setCanRevoke(false)
        padNoteContent.clearFocus()
        UIUtils.hideInputMethod(this, padNoteContent)//隐藏输入法键盘
    }

    private fun saveAsPicture() {
        UIUtils.showSimpleAlertDialog(this, "提示", "要根据当前内容生成长图吗？", "确定", "", { _, _ ->
            saveToBitmap()
        }, { _, _ -> })
    }

    /**
     * 校验文本是否有改动
     * @return true：文本经过修改 false：文本未经修改
     */
    private fun checkNoteChanged(): Boolean {
        //这里对文本修改的判断有一种情况下会失效，即用户修改了超过内容次数达到上限后又正好回退到回退栈清空时，页面不会出现保存标记，
        // 若此时文本长度恰好等于初始文本长度时，则且退出保存不会被触发
        return mCanRevoke || mOldLength != padNoteContent.length()
    }

    private fun saveNoteInfo(path: String, content: String?,
                             title: String, preview: String, shouldQuit: Boolean) {
        mPresenter.saveAll(path, content, title, preview)
        mShouldQuit = shouldQuit
        mStandardNote = NoteEntity(title, preview, path, System.currentTimeMillis())
        mIsNewNote = false
        mOldLength = content?.length ?: 0
    }

    private fun createNewNote():NoteEntity{
        return NoteEntity("", "", FileUtils.createFileWithTime(ConfigManager.fileSavedPath),
                System.currentTimeMillis())
    }

    override fun setContent(content: String) {
        padNoteContent.setContent(content)
        if (padNoteContent.text.toString() == LineBreakInputFilter.INDENT) {
            padNoteContent.setSelection(padNoteContent.length())
            padNoteContent.requestFocus()
        }
        mOldLength = padNoteContent.length()
    }

    override fun onSaveSuccessful(note: NoteEntity) {
        showShortToast("保存成功")
        val intent = Intent()
        intent.putExtra(Constant.Extra.EXTRA_NOTE, note)
        setResult(Activity.RESULT_OK, intent)
        mStandardNote = note
        if (mShouldQuit) {
            finish()
        }
    }

    private fun setCanRevoke(canRevoke: Boolean) {
        mCanRevoke = canRevoke
        invalidateOptionsMenu()
    }

    private fun saveToBitmap() {
        GlobalScope.launch(Dispatchers.Main) {
            val progress = ProgressDialog(this@NoteActivity)
            progress.show()
            val result = async {
                generateBitmap()
            }
            if (result.await()) {
                UIUtils.showSimpleAlertDialog(this@NoteActivity, "提示", "图片生成成功！", "确认",
                        null, null, null)
            } else {
                UIUtils.showSimpleAlertDialog(this@NoteActivity, "提示", "图片生成失败，请重试！", "确认",
                        null, null, null)
            }
            progress.hide()
        }
    }

    private suspend fun generateBitmap() = suspendCoroutine<Boolean> {
        CoroutineScope(Dispatchers.IO).launch {
            val builder = NoteSnapshotGenerator.Builder()
                    .setText(padNoteContent.text.toString())
                    .setTitle(etNoteTitle.text.toString())
            it.resume(builder.build(this@NoteActivity).generate())
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
}
