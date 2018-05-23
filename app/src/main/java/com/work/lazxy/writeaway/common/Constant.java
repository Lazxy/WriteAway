package com.work.lazxy.writeaway.common;

/**
 * Created by Lazxy on 2017/5/24.
 */

public interface Constant {
    interface Common {
        int PREVIEW_MAX_LENGTH = 30;

        int REQUEST_CODE_NEW_NOTE = 0x16;

        int REQUEST_CODE_UPDATE_NOTE = 0x32;
    }
    interface Extra{
        String EXTRA_NOTE = "extraNote";
    }
}
