package com.work.lazxy.writeaway.common;

/**
 * Created by Lazxy on 2017/5/24.
 */

public interface Constant {
    interface Common {
        int PREVIEW_MAX_LENGTH = 100;

        int REQUEST_CODE_NEW_NOTE = 0x16;

        int REQUEST_CODE_UPDATE_NOTE = 0x32;

        int REQUEST_CODE_IMPORT_NOTE = 0x64;
    }
    interface Extra{
        String EXTRA_NOTE = "extraNote";

        String EXTRA_IMPORT_PATH = "paths";
    }
}
