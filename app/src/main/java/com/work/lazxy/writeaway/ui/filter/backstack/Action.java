package com.work.lazxy.writeaway.ui.filter.backstack;

/**
 * Created by Lazxy on 2017/5/23.
 */

public interface Action {
    String REVOKED_SIGN = "/re";

    String revoke(String src);

    int getOriginPosition();
}
