package com.work.lazxy.writeaway.mvpframe.concrete.note;


import android.support.annotation.Nullable;

import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver;
import com.work.lazxy.writeaway.ui.filter.backstack.Action;

/**
 * Created by Lazxy on 2017/2/25.
 */

public class NotePresenter extends NoteContract.Presenter {

    @Override
    public void getContent(String path) {
        observe(mModel.getContent(path), new BaseObserver<String>(mView) {
            @Override
            protected void onSuccess(String s) {
                mView.setContent(Action.REVOKED_SIGN+s+Action.REVOKED_SIGN);
            }

            @Override
            public void onError(Throwable e ){
                mView.onRequestError("文本好像走丢了~");
            }
        });
    }

    @Override
    public void saveAll(@Nullable String path, @Nullable String content,
                        @Nullable String title, @Nullable String preview) {
        observe(mModel.saveAll(path, content, title, preview, System.currentTimeMillis()), new BaseObserver<NoteEntity>() {
            @Override
            protected void onSuccess(NoteEntity note) {
                if(note != null){
                    mView.onSaveSuccessful(note);
                }
            }
        });
    }


    @Override
    public void shareAsPhoto(String content) {

    }
}
