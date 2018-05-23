package com.work.lazxy.writeaway.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.entity.PlanningEntity;

import java.util.List;

/**
 * Created by Lazxy on 2017/6/1.
 */

public class PlanningAdapter extends BaseQuickAdapter<PlanningEntity,BaseViewHolder>{

    public PlanningAdapter(int resId ,List<PlanningEntity> data) {
        super(resId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PlanningEntity item) {
        helper.setText(R.id.tv_item_planning_no,item.getPriority()+".");
        helper.setText(R.id.tv_item_planning_goal,item.getGoal());
        /*这里得设置一下触摸监听*/
    }
}
