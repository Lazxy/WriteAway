package com.work.lazxy.writeaway.entity;

/**
 * Created by Lazxy on 2017/6/1.
 */

public class PlanningEntity {
    private int mPriority;
    private String mGoal;

    public PlanningEntity(int priority, String goal) {
        mPriority = priority;
        mGoal = goal;
    }

    public String getGoal() {
        return mGoal;
    }

    public void setGoal(String goal) {
        this.mGoal = goal;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }
}
