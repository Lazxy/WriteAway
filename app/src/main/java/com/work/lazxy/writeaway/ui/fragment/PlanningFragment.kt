package com.work.lazxy.writeaway.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputFilter
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.entity.PlanningEntity
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameFragment
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningContract
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningModel
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningPresent
import com.work.lazxy.writeaway.ui.activity.MainActivity
import com.work.lazxy.writeaway.ui.adapter.PlanningAdapter
import com.work.lazxy.writeaway.ui.filter.EditLengthInputFilter
import com.work.lazxy.writeaway.ui.listener.PlanningItemTouchListener
import com.work.lazxy.writeaway.ui.widget.ProgressDialog
import com.work.lazxy.writeaway.utils.UIUtils
import kotlinx.android.synthetic.main.fragment_planning.view.*

import java.util.ArrayList
import java.util.Collections

/**
 * Created by Lazxy on 2017/4/27.
 */

class PlanningFragment : BaseFrameFragment<PlanningPresent, PlanningModel>(), PlanningContract.View {
    private var mAdapter: PlanningAdapter? = null
    private var mListener: PlanningItemTouchListener? = null
    private var mDialog: ProgressDialog? = null
    private var mIsEditing: Boolean = false
    private var mHasChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_planning)
    }

    override fun initData() {
        mDialog = ProgressDialog(activity)
        mAdapter = PlanningAdapter(R.layout.item_planning, ArrayList())
        mIsEditing = false
        mHasChanged = false
    }

    override fun initView() {
        contentView.rvPlanning.adapter = mAdapter
        contentView.rvPlanning.layoutManager = LinearLayoutManager(activity)
        contentView.etNewPlanning.filters = arrayOf<InputFilter>(EditLengthInputFilter(resources.getInteger(R.integer.planning_length) * 2))
    }

    override fun initListener() {
        contentView.rvPlanning.setOnTouchListener { v, event ->
            if (mHasChanged) {
                //在交换或者结束时存储一下信息
                if (event.action == MotionEvent.ACTION_UP) {
                    mPresenter.savePlanning(mAdapter!!.data)
                    mHasChanged = false
                }
            }
            contentView.rvPlanning.onTouchEvent(event)
        }
        mListener = PlanningItemTouchListener(object : PlanningItemTouchListener.ItemTouchListener {
            override fun onMove(oldPosition: Int, newPosition: Int) {
                mHasChanged = true
                val plannings = mAdapter!!.data
                //交换一下两者的位置和信息
                plannings[oldPosition].priority = newPosition + 1
                plannings[newPosition].priority = oldPosition + 1
                Collections.swap(plannings, oldPosition, newPosition)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onSwipe(position: Int) {
                UIUtils.showSimpleAlertDialog(activity, false, null, getString(R.string.planning_delete_prompt), "删除", "取消", { dialog, which ->
                    val plannings = mAdapter!!.data
                    plannings.removeAt(position)
                    for (i in position until plannings.size) {
                        plannings[i].priority = i + 1
                    }
                    mPresenter.savePlanning(plannings)
                    (activity as MainActivity).updatePlanningCount(false)
                    mAdapter!!.notifyDataSetChanged()
                }) { dialog, which -> mAdapter!!.notifyDataSetChanged() }
            }
        })
        val helper = ItemTouchHelper(mListener)
        helper.attachToRecyclerView(contentView.rvPlanning)

        contentView.ivPlanningAdd.setOnClickListener {
            if (mIsEditing) {
                //编辑状态 终态指向保存
                val newPlanning = contentView.etNewPlanning.getText().toString()
                if (!TextUtils.isEmpty(newPlanning)) {
                    val planning = PlanningEntity(mAdapter!!.itemCount + 1, newPlanning)
                    mAdapter!!.addData(planning)
                    mAdapter!!.notifyDataSetChanged()
                    mPresenter.addPlanning(planning)

                    UIUtils.hideInputMethod(activity, contentView.etNewPlanning)
                    mIsEditing = false
                    //这里得告诉一下MainActivity数量变化
                    (activity as MainActivity).updatePlanningCount(true)
                    contentView.ivPlanningAdd.setImageResource(R.drawable.iv_planning_add_36dp_amber)
                    contentView.ivPlanningAdd.setBackgroundResource(R.drawable.selector_planning_add)
                    contentView.layoutPlanningEdit.visibility = View.GONE
                } else {
                    Toast.makeText(activity, getString(R.string.planning_void_prompt), Toast.LENGTH_SHORT).show()
                }
            } else {
                //未编辑状态 终态指向编辑
                //展示下滑动画
                mIsEditing = true
                contentView.layoutPlanningEdit.visibility = View.VISIBLE
                contentView.etNewPlanning.setText("")
                contentView.etNewPlanning.requestFocus()
                UIUtils.showInputMethod(activity, contentView.etNewPlanning)//输入法弹出设置有问题
                contentView.ivPlanningAdd.setImageResource(R.drawable.iv_planning_confirm_36dp)
                contentView.ivPlanningAdd.setBackgroundResource(R.drawable.select_planning_confirm)
            }
        }

        contentView.ivPlanningCancelAdd.setOnClickListener {
            contentView.layoutPlanningEdit.visibility = View.GONE
            UIUtils.hideInputMethod(activity, contentView.etNewPlanning)
            contentView.ivPlanningAdd.setImageResource(R.drawable.iv_planning_add_36dp_amber)
            contentView.ivPlanningAdd.setBackgroundResource(R.drawable.selector_planning_add)
            mIsEditing = false
        }
    }

    override fun initLoad() {
        mPresenter.getPlanning()
    }


    override fun setPlanning(plannings: List<PlanningEntity>) {
        mAdapter!!.setNewData(plannings)
    }

    override fun onRequestStart() {
        mDialog!!.show()
    }

    override fun onRequestError(msg: String) {
        mDialog!!.dismiss()
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestEnd() {
        mDialog!!.dismiss()
    }
}
