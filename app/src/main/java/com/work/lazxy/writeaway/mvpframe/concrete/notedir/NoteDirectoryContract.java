package com.work.lazxy.writeaway.mvpframe.concrete.notedir;

import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseModel;
import com.work.lazxy.writeaway.mvpframe.base.BasePresenter;
import com.work.lazxy.writeaway.mvpframe.base.BaseView;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Lazxy on 2017/2/25.
 */

public interface NoteDirectoryContract {
    interface Model extends BaseModel {
        Observable<List<NoteEntity>> loadData(int limitIndex, int num);
        Observable<Boolean> deleteData(String path);
    }

    interface View extends BaseView {
        void onLoadSuccessful(List<NoteEntity> notes);
        void onDeleteSuccessful(String path);
    }

    abstract class Presenter extends BasePresenter<Model,View> {
        public abstract void loadData(int limitIndex, int num);
        public abstract void deleteData(String path);
    }
}
