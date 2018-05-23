package com.work.lazxy.writeaway.mvpframe.concrete.main;

import com.work.lazxy.writeaway.db.NoteDataHandler;
import com.work.lazxy.writeaway.db.PlanningDataHandler;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Lazxy on 2017/5/30.
 */

public class MainModel implements MainContract.Model {
    @Override
    public Observable<int[]> getDataCount() {
        return Observable.create(new ObservableOnSubscribe<int[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<int[]> e) throws Exception {
                int noteCount = NoteDataHandler.getInstance().getNotesCount();
                int planningCount = PlanningDataHandler.getInstance().getPlanningsCount();
                e.onNext(new int[]{noteCount,planningCount});
                e.onComplete();
            }
        });
    }
}
