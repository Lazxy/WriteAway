package com.work.lazxy.writeaway.event;

import com.work.lazxy.writeaway.entity.NoteEntity;

/**
 * Created by Lazxy on 2017/5/28.
 */

public class EventChangeNote {
    public boolean mIsNewNote;
    public NoteEntity mNote;

    public EventChangeNote(boolean isNewNote, NoteEntity note) {
        mIsNewNote = isNewNote;
        mNote = note;
    }
}
