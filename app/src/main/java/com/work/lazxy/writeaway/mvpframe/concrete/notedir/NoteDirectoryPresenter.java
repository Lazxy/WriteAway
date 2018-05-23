package com.work.lazxy.writeaway.mvpframe.concrete.notedir;


import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseObserver;

import java.util.List;


/**
 * Created by Lazxy on 2017/2/25.
 */

public class NoteDirectoryPresenter extends NoteDirectoryContract.Presenter {

    @Override
    public void loadData(int limitIndex, int num) {
        observe(mModel.loadData(limitIndex, num), new BaseObserver<List<NoteEntity>>(mView) {
            @Override
            protected void onSuccess(List<NoteEntity> noteEntities) {
                mView.onLoadSuccessful(noteEntities);
            }
        });
    }

    @Override
    public void deleteData(final String path) {
        observe(mModel.deleteData(path), new BaseObserver<Boolean>() {
            @Override
            protected void onSuccess(Boolean isDelete) {
                if(isDelete)
                    mView.onDeleteSuccessful(path);
                else
                    mView.onRequestError("好像出了些问题...");
            }
        });
    }
}
