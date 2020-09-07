package com.work.lazxy.writeaway.ui.listener;

import com.work.lazxy.writeaway.entity.PlanningEntity;

/**
 * Created by Lazxy on 2017/6/4.
 */

public interface PlanningItemTouchListener {
    void onMove(int oldPosition, int newPosition);

    void onSwipe(PlanningEntity entity, int position);
}
