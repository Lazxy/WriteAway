package com.work.lazxy.writeaway.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.event.EventChangeNote;
import com.work.lazxy.writeaway.event.EventDeleteNote;
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameListFragment;
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryContract;
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryModel;
import com.work.lazxy.writeaway.mvpframe.concrete.notedir.NoteDirectoryPresenter;
import com.work.lazxy.writeaway.ui.adapter.NoteDirAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lazxy on 2017/4/27.
 */

public class NoteDirFragment extends BaseFrameListFragment<NoteDirectoryPresenter, NoteDirectoryModel>
        implements NoteDirectoryContract.View {
    @BindView(R.id.layout_note_dir)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.rv_note_dir)
    RecyclerView       recyclerView;

    private int        mCurrentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_note_dir);
        ButterKnife.bind(this, getContentView());
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        mCurrentIndex = 0;
        setList(refreshLayout, recyclerView, new NoteDirAdapter(R.layout.item_note_dir, new ArrayList<NoteEntity>()));
    }

    @Override
    public void initView(){
        super.initView();
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mAdapter.isFirstOnly(true);
    }

    @Override
    public void initLoad() {
        requestData(true);
    }

    @Override
    public void requestData(boolean isRefresh) {
        super.requestData(isRefresh);
        if (isRefresh) {
            mCurrentIndex = 0;
        } else {
            mCurrentIndex = mAdapter.getItemCount() - 1;
        }
        mPresenter.loadData(mCurrentIndex, getItemPerPage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteNote(EventDeleteNote event) {
        mPresenter.deleteData(event.mNotePath);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeNote(EventChangeNote event) {
        requestData(event.mShouldRefresh);
    }

    @Override
    public void onLoadSuccessful(List<NoteEntity> notes) {
        setData(notes);
    }

    @Override
    public void onDeleteSuccessful(String path) {
        List<NoteEntity> notes = mAdapter.getData();
        for (NoteEntity note : notes) {
            if (note.getFilePath().equals(path)) {
                notes.remove(note);
                if (notes.isEmpty()) {
                    mAdapter.setEmptyView(mEmptyView);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
