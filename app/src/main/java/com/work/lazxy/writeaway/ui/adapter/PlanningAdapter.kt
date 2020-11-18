package com.work.lazxy.writeaway.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.entity.PlanningEntity
import com.work.lazxy.writeaway.ui.adapter.PlanningAdapter.PlanningViewHolder
import com.work.lazxy.writeaway.ui.listener.PlanningItemTouchListener

/**
 * Created by Lazxy on 2017/6/1.
 */
class PlanningAdapter(private var mData: MutableList<PlanningEntity>) : BaseSwipeDraggableAdapter<PlanningViewHolder>() {
    private var mListener: PlanningItemTouchListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_planning, parent, false)
        return PlanningViewHolder(root)
    }

    override fun onBindViewHolder(holder: PlanningViewHolder, position: Int) {
        val item = mData[position]
        holder.planningNo.text = item.priority.toString() + "."
        holder.planningContent.text = item.goal
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (mListener != null) { //触发调换位置的监听，由上层控制是否调用notifyDataChanged
            mListener!!.onMove(fromPosition, toPosition)
        }
    }

    override fun getItemId(position: Int): Long { // requires static value, it means need to keep the same value
// even if the item position has been changed.
        return mData[position].id
    }

    override fun onGetSwipeReactionType(holder: PlanningViewHolder, position: Int, x: Int, y: Int): Int { //设置滑动方向 这里是右滑触发滑动事件
        return SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT or SwipeableItemConstants.REACTION_MASK_START_SWIPE_LEFT
    }

    override fun onSwipeItem(holder: PlanningViewHolder, position: Int, result: Int): SwipeResultAction {
        return if (result == SwipeableItemConstants.RESULT_SWIPED_RIGHT) { //滑动方向为右滑时 触发滑动监听回调，并让控件返回原位置
            object : SwipeResultActionRemoveItem() {
                override fun onPerformAction() {
                    val removeItem = mData[position]
                    mData.removeAt(position)
                    if (mListener != null) {
                        mListener!!.onSwipe(removeItem, position)
                    }
                }
            }
        } else {
            SwipeResultActionDoNothing()
        }
    }

    protected override fun getDraggableItem(holder: PlanningViewHolder): View {
        return holder.planningNo
    }

    val data: MutableList<PlanningEntity>
        get() = mData

    fun addData(newItem: PlanningEntity) {
        mData.add(newItem)
    }

    fun setNewData(data: MutableList<PlanningEntity>) {
        mData = data
        notifyDataSetChanged()
    }

    fun setItemTouchListener(listener: PlanningItemTouchListener?) {
        mListener = listener
    }

    class PlanningViewHolder(itemView: View) : ViewHolder(itemView) {
        private val containerView: View
        val planningNo: TextView
        val planningContent: TextView
        override fun getSwipeableContainerView(): View? {
            return containerView
        }

        init {
            containerView = itemView.findViewById(R.id.layout_planning_item)
            planningNo = itemView.findViewById(R.id.tv_item_planning_no)
            planningContent = itemView.findViewById(R.id.tv_item_planning_goal)
        }
    }

    init {
        setHasStableIds(true)
    }
}