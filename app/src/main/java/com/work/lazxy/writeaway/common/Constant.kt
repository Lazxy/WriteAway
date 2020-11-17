package com.work.lazxy.writeaway.common

/**
 * Created by Lazxy on 2017/5/24.
 */
interface Constant {
    class Common {
        companion object {
            const val PREVIEW_MAX_LENGTH = 100
            const val REQUEST_CODE_NEW_NOTE = 0x16
            const val REQUEST_CODE_UPDATE_NOTE = 0x32
            const val REQUEST_CODE_IMPORT_NOTE = 0x64
        }
    }

    class Extra {
        companion object {
            const val EXTRA_NOTE = "extraNote"
            const val EXTRA_IMPORT_PATH = "paths"
        }
    }
}