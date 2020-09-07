package com.work.lazxy.writeaway.entity;

/**
 * Created by Lazxy on 2017/6/1.
 */

public class PlanningEntity {
    private int mPriority;
    private String mGoal;

    private long   mId;

    public PlanningEntity(int priority, String goal) {
        mPriority = priority;
        mGoal = goal;
        // 为了列表项能被独立标识的临时性方案，之后这个Id应该被唯一地放在数据库里
        mId = this.hashCode();
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

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }
}
