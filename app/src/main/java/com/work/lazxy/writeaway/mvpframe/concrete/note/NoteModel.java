package com.work.lazxy.writeaway.mvpframe.concrete.note;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.work.lazxy.writeaway.db.NoteDataHandler;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.utils.FileUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Lazxy on 2017/2/25.
 */

public class NoteModel implements NoteContract.Model {

    @Override
    public Observable<String> getContent(final String path) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(FileUtils.readTextFormFile(path));
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<NoteEntity> saveAll(@Nullable final String path, @Nullable final String content, @Nullable final String title, @Nullable final String preview, final long editTime) {
        return Observable.create(new ObservableOnSubscribe<NoteEntity>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<NoteEntity> e) throws Exception {
                if (content != null) {
                    FileUtils.writeTextToFile(path, content);
                }
                if (title != null && preview != null) {
                    NoteDataHandler.getInstance().saveData(title, path, preview, editTime);
                }
                e.onNext(new NoteEntity(title, preview, path, editTime));
            }
        });
    }

    @Override
    public Observable<Bitmap> shareAsPhoto(String content) {
        return null;
    }
}
