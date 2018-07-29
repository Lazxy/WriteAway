package com.work.lazxy.writeaway.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.entity.PlanningEntity;
import com.work.lazxy.writeaway.event.EventPlanningChanged;
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameFragment;
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningContract;
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningModel;
import com.work.lazxy.writeaway.mvpframe.concrete.planning.PlanningPresent;
import com.work.lazxy.writeaway.ui.activity.MainActivity;
import com.work.lazxy.writeaway.ui.adapter.PlanningAdapter;
import com.work.lazxy.writeaway.ui.filter.EditLengthInputFilter;
import com.work.lazxy.writeaway.ui.listener.PlanningItemTouchListener;
import com.work.lazxy.writeaway.ui.widget.ProgressDialog;
import com.work.lazxy.writeaway.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lazxy on 2017/4/27.
 */

public class PlanningFragment extends BaseFrameFragment<PlanningPresent,PlanningModel> implements PlanningContract.View{
    @BindView(R.id.rv_planning)
    RecyclerView rvPlanningList;
    @BindView(R.id.layout_planning_edit)
    View layoutEditPlanning;
    @BindView(R.id.iv_planning_add)
    ImageView ivAddPlanning;
    @BindView(R.id.iv_planning_cancel_add)
    ImageView ivCancelAdd;
    @BindView(R.id.et_planning_new)
    EditText etNewPlanning;

    private PlanningAdapter mAdapter;
    private PlanningItemTouchListener mListener;
    private ProgressDialog mDialog;
    private boolean mIsEditing;
    private boolean mHasChanged;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_planning);
        ButterKnife.bind(this,getContentView());
    }

    @Override
    public void initData(){
        mDialog = new ProgressDialog(getActivity());
        mAdapter = new PlanningAdapter(R.layout.item_planning,new ArrayList<PlanningEntity>());
        mIsEditing = false;
        mHasChanged = false;
    }

    @Override
    public void initView(){
        rvPlanningList.setAdapter(mAdapter);
        rvPlanningList.setLayoutManager(new LinearLayoutManager(getActivity()));
        etNewPlanning.setFilters(new InputFilter[]{new EditLengthInputFilter(getResources().getInteger(R.integer.planning_length)*2)});
    }

    @Override
    public void initListener(){
        rvPlanningList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mHasChanged){
                    //在交换或者结束时存储一下信息
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        mPresenter.savePlanning(mAdapter.getData());
                        mHasChanged = false;
                    }
                }
                return rvPlanningList.onTouchEvent(event);
            }
        });
        mListener = new PlanningItemTouchListener(new PlanningItemTouchListener.ItemTouchListener() {
            @Override
            public void onMove(int oldPosition, int newPosition) {
                mHasChanged = true;
                List<PlanningEntity> plannings = mAdapter.getData();
                //交换一下两者的位置和信息
                plannings.get(oldPosition).setPriority(newPosition+1);
                plannings.get(newPosition).setPriority(oldPosition+1);
                Collections.swap(plannings,oldPosition,newPosition);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwipe(final int position) {
                UIUtils.showSimpleAlertDialog(getActivity(), false, null, getString(R.string.planning_delete_prompt)
                        , "删除", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<PlanningEntity> plannings = mAdapter.getData();
                                plannings.remove(position);
                                for (int i = position; i < plannings.size(); i++) {
                                    plannings.get(i).setPriority(i+1);
                                }
                                mPresenter.savePlanning(plannings);
                                ((MainActivity)getActivity()).updatePlanningCount(false);
                                mAdapter.notifyDataSetChanged();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(mListener);
        helper.attachToRecyclerView(rvPlanningList);

        ivAddPlanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsEditing){
                    String newPlanning = etNewPlanning.getText().toString();
                    if(!TextUtils.isEmpty(newPlanning)){
                        PlanningEntity planning = new PlanningEntity(mAdapter.getItemCount()+1,newPlanning);
                        mAdapter.addData(planning);
                        mAdapter.notifyDataSetChanged();
                        mPresenter.addPlanning(planning);
                        /*这里得告诉一下MainActivity数量变化*/
                        ((MainActivity)getActivity()).updatePlanningCount(true);
                        ivAddPlanning.setImageResource(R.drawable.iv_planning_add_36dp_amber);
                        ivAddPlanning.setBackgroundResource(R.drawable.selector_planning_add);
                        layoutEditPlanning.setVisibility(View.GONE);
                        UIUtils.hideInputMethod(getActivity(),etNewPlanning);
                        mIsEditing = false;
                    }else{
                        Toast.makeText(getActivity(),getString(R.string.planning_void_prompt),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mIsEditing = true;
                    layoutEditPlanning.setVisibility(View.VISIBLE);
                    etNewPlanning.setText("");
                    etNewPlanning.requestFocus();
                    UIUtils.showInputMethod(getActivity(),etNewPlanning);//输入法弹出设置有问题
                    ivAddPlanning.setImageResource(R.drawable.iv_planning_confirm_36dp_green);
                    ivAddPlanning.setBackgroundResource(R.drawable.select_planning_confirm);
                }
            }
        });

        ivCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditPlanning.setVisibility(View.GONE);
                UIUtils.hideInputMethod(getActivity(),etNewPlanning);
                ivAddPlanning.setImageResource(R.drawable.iv_planning_add_36dp_amber);
                ivAddPlanning.setBackgroundResource(R.drawable.selector_planning_add);
                mIsEditing = false;
            }
        });
    }

    @Override
    public void initLoad(){
        mPresenter.getPlanning();
    }


    @Override
    public void setPlanning(List<PlanningEntity> plannings) {
        mAdapter.setNewData(plannings);
    }

    @Override
    public void onRequestStart() {
        mDialog.show();
    }

    @Override
    public void onRequestError(String msg) {
        mDialog.dismiss();
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestEnd() {
        mDialog.dismiss();
    }
}
