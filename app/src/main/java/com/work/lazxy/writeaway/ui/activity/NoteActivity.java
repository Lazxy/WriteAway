package com.work.lazxy.writeaway.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.common.ConfigManager;
import com.work.lazxy.writeaway.common.Constant;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameActivity;
import com.work.lazxy.writeaway.mvpframe.concrete.note.NoteContract;
import com.work.lazxy.writeaway.mvpframe.concrete.note.NoteModel;
import com.work.lazxy.writeaway.mvpframe.concrete.note.NotePresenter;
import com.work.lazxy.writeaway.ui.filter.EditLengthInputFilter;
import com.work.lazxy.writeaway.ui.filter.LineBreakInputFilter;
import com.work.lazxy.writeaway.ui.filter.backstack.Action;
import com.work.lazxy.writeaway.ui.widget.NotePad;
import com.work.lazxy.writeaway.ui.widget.ProgressDialog;
import com.work.lazxy.writeaway.utils.CalendarUtils;
import com.work.lazxy.writeaway.utils.FileUtils;
import com.work.lazxy.writeaway.utils.StringUtils;
import com.work.lazxy.writeaway.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lazxy on 2017/4/27.
 */

public class NoteActivity extends BaseFrameActivity<NotePresenter,NoteModel> implements NoteContract.View{
    @BindView(R.id.toolbar_note)
    Toolbar toolbar;
    @BindView(R.id.et_note_title)
    EditText etTitle;
    @BindView(R.id.tv_note_edit_time)
    TextView tvEditTime;
    @BindView(R.id.note_content)
    NotePad notePad;

    private ProgressDialog mProgress;
    private NoteEntity mNote;
    private int mOldLength;
    private boolean mCanRevoke = false;
    private boolean mShouldQuit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
    }

    @Override
    public void initData(){
        mProgress = new ProgressDialog(this);
        mNote = (NoteEntity) getIntent().getSerializableExtra(Constant.Extra.EXTRA_NOTE);
        if(mNote == null){
            //新建笔记的情况
            mNote = new NoteEntity("","",FileUtils.createFileWithTime(ConfigManager.getFileSavedPath()),
                    System.currentTimeMillis());
        }
    }

    @Override
    public void initView(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!TextUtils.isEmpty(mNote.getTitle()))
            etTitle.setText(mNote.getTitle());
        else
            etTitle.setHint(getResources().getString(R.string.no_title));
        etTitle.clearFocus();
        tvEditTime.setText(CalendarUtils.getTimeFromTimestamp(mNote.getLastEditTime()));
        setCanRevoke(false);
    }

    @Override
    public void initListener(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteActivity.this.onBackPressed();
            }
        });

        notePad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCanRevoke(notePad.isCanRevoke());
            }
        });

        etTitle.setFilters(new InputFilter[]{new EditLengthInputFilter(32)});
    }

    @Override
    public void initLoad(){
        mPresenter.getContent(mNote.getFilePath());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_note,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(mCanRevoke){
            menu.findItem(R.id.menu_note_revoke).setVisible(true);
            menu.findItem(R.id.menu_note_save).setVisible(true);
        }else{
            menu.findItem(R.id.menu_note_revoke).setVisible(false);
            menu.findItem(R.id.menu_note_save).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_note_revoke:
                notePad.revoke();
                break;
            case R.id.menu_note_clear:
                UIUtils.showSimpleAlertDialog(this, null, "确认清空所有内容吗？", "清空", "取消",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //这里会被认为是一个Add操作，而不是替换，所以没办法将其加入回退栈
                        notePad.setText(Action.REVOKED_SIGN+LineBreakInputFilter.INDENT+Action.REVOKED_SIGN);
                        notePad.setSelection(notePad.length());
                        notePad.clearRevokeStack();
                        setCanRevoke(false);
                    }
                },null);
                break;
            case R.id.menu_note_save:
                String content = notePad.getText().toString();
                mPresenter.saveAll(mNote.getFilePath(),content,etTitle.getText().toString(),StringUtils.getPreview(content));
                mShouldQuit = false;
                notePad.clearFocus();
                mOldLength = notePad.length();
                setCanRevoke(false);
                UIUtils.hideInputMethod(this,notePad);//隐藏输入法键盘
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        //这里对文本修改的判断有一种情况下会失效，即用户修改了超过20次内容后又正好回退到回退栈清空时，页面不会出现保存标记，且退出保存不会被触发
        if(mCanRevoke || mOldLength!=notePad.length()){
            UIUtils.showSimpleAlertDialog(this, null, "文本有修改，需要保存吗？", "保存", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String content = notePad.getText().toString();
                    mPresenter.saveAll(mNote.getFilePath(),content,etTitle.getText().toString(), StringUtils.getPreview(content));
                    mShouldQuit = true;
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NoteActivity.super.onBackPressed();
                }
            });
        }else{
            //正文无改动时
            if(!etTitle.getText().toString().equals(mNote.getTitle())){
                //只更改标题信息
                mPresenter.saveAll(mNote.getFilePath(),null,etTitle.getText().toString(),mNote.getPreview());
                mShouldQuit = true;
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestStart() {
        mProgress.show();
    }

    @Override
    public void onRequestError(String msg) {
        mProgress.dismiss();
        showShortToast(msg);
        etTitle.clearFocus();
    }

    @Override
    public void onRequestEnd() {
        mProgress.dismiss();
    }

    @Override
    public void setContent(String content) {
        notePad.setText(content);
        if(notePad.getText().toString().equals(LineBreakInputFilter.INDENT)) {
            notePad.setSelection(notePad.length());
            notePad.requestFocus();
        }
        mOldLength = notePad.length();
    }

    @Override
    public void onSaveSuccessful(NoteEntity note) {
        showShortToast("保存成功");
        setResponse(note);
        if(mShouldQuit) {
            finish();
        }
    }

    private void setCanRevoke(boolean canRevoke){
        mCanRevoke = canRevoke;
        invalidateOptionsMenu();
    }

    private void setResponse(NoteEntity note){
        Intent intent = new Intent();
        intent.putExtra(Constant.Extra.EXTRA_NOTE,note);
        setResult(RESULT_OK,intent);
        mNote = note;
    }
}
