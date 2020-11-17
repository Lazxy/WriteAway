package com.work.lazxy.writeaway.entity

/**
 * Created by Lazxy on 2017/6/1.
 */
class PlanningEntity(var priority: Int, var goal: String) {
    // 为了列表项能被独立标识的临时性方案，之后这个Id应该被唯一地放在数据库里
    var id: Long = hashCode().toLong()
}