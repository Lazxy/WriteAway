package com.work.lazxy.writeaway.ui.listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Lazxy on 2017/6/4.
 */

public class PlanningItemTouchListener extends ItemTouchHelper.Callback {
    private ItemTouchListener mListener;
    public PlanningItemTouchListener(ItemTouchListener listener){
        mListener = listener;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //设置上下拖动和向右滑动有回调事件
        return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mListener.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onSwipe(viewHolder.getAdapterPosition());
    }

    public interface ItemTouchListener{
        void onMove(int oldPosition ,int newPosition);
        void onSwipe(int position);
    }
}
