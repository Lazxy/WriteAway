package com.work.lazxy.writeaway.event;

/**
 * Created by Lazxy on 2017/4/29.
 */

public class EventDeleteNote {

    public String mNotePath;

    public EventDeleteNote(String path){
        this.mNotePath = path;
    }
}
