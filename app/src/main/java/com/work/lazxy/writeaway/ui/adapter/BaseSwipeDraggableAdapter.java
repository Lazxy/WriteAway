package com.work.lazxy.writeaway.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

/**
 * 简单地对实现拖拽和滑动删除相关功能的接口进行封装，屏蔽没有必要实现的接口
 * @author Lazxy
 * @date 2020/9/7
 */
public abstract class BaseSwipeDraggableAdapter<T extends BaseSwipeDraggableAdapter.ViewHolder>
        extends RecyclerView.Adapter<T> implements DraggableItemAdapter<T>, SwipeableItemAdapter<T> {
    @Override
    public boolean onCheckCanStartDrag(T holder, int position, int x, int y) {
        View dragHandle = getDraggableItem(holder);

        int handleWidth = dragHandle.getWidth();
        int handleHeight = dragHandle.getHeight();
        int handleLeft = dragHandle.getLeft();
        int handleTop = dragHandle.getTop();

        // 扩大拖拽触发区域的大小
        return (x >= (handleLeft - handleWidth / 2)) && (x < handleLeft + handleWidth * 1.5)
                && (y >= handleTop - handleWidth / 2) && (y < handleTop + handleHeight * 1.5);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(T holder, int position) {
        // 返回列表中可拖拽子项的范围，默认为整个列表
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        // this method is not used unless calling
        // `RecyclerViewDragDropManager.setCheckCanDropEnabled(true)` explicitly.
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
    }

    @Override
    public void onSwipeItemStarted(T holder, int position) {

    }

    @Override
    public void onSetSwipeBackground(T holder, int position, int type) {

    }

    /**
     * 返回可拖拽的触发点控件
     * @param holder
     * @return
     */
    protected abstract View getDraggableItem(T holder);

    public static abstract class ViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
