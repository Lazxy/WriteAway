package com.work.lazxy.writeaway.mvpframe.concrete.note;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.mvpframe.base.BaseModel;
import com.work.lazxy.writeaway.mvpframe.base.BasePresenter;
import com.work.lazxy.writeaway.mvpframe.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Lazxy on 2017/2/25.
 */

public interface NoteContract {
    interface Model extends BaseModel {
        Observable<String> getContent(String path);
        Observable<NoteEntity> saveAll(String path, @Nullable String content,String title,String preview,long editTime);
        Observable<Bitmap> shareAsPhoto(String content);
    }
    interface View extends BaseView {
        void setContent(String content);
        void onSaveSuccessful(NoteEntity note);
    }
    abstract class Presenter extends BasePresenter<Model,View> {
        public abstract void getContent(String path);
        public abstract void saveAll(String path, @Nullable String content, String title,String preview);
        public abstract void shareAsPhoto(String content);
    }
}
