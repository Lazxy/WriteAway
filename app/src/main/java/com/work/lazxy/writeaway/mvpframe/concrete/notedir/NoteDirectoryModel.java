package com.work.lazxy.writeaway.mvpframe.concrete.notedir;


import com.work.lazxy.writeaway.db.NoteDataHandler;
import com.work.lazxy.writeaway.entity.NoteEntity;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Lazxy on 2017/2/25.
 */

public class NoteDirectoryModel implements NoteDirectoryContract.Model {

    @Override
    public Observable<List<NoteEntity>> loadData(final int limitIndex, final int num) {
        return Observable.create(new ObservableOnSubscribe<List<NoteEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NoteEntity>> emitter) throws Exception {
                List<NoteEntity> notes = NoteDataHandler.getInstance().getData(limitIndex, num);
                emitter.onNext(notes);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<Boolean> deleteData(final String path) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                File file = new File(path);
                NoteDataHandler.getInstance().deleteData(path);
                boolean isSuccessful = file.delete();
                e.onNext(isSuccessful);
                e.onComplete();
            }
        });
    }
}
