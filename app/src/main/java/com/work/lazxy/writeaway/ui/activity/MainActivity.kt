package com.work.lazxy.writeaway.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.view.View
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.Constant
import com.work.lazxy.writeaway.common.base.BaseFragment
import com.work.lazxy.writeaway.common.base.BaseTabPagerAdapter
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.event.EventChangeNote
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameActivity
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainContract
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainModel
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainPresenter
import com.work.lazxy.writeaway.service.ExportNoteService
import com.work.lazxy.writeaway.ui.adapter.MainPagerAdapter
import com.work.lazxy.writeaway.ui.fragment.NoteDirFragment
import com.work.lazxy.writeaway.ui.fragment.PlanningFragment
import com.work.lazxy.writeaway.utils.PermissionUtils

import org.greenrobot.eventbus.EventBus

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_menu_main.*

class MainActivity : BaseFrameActivity<MainPresenter, MainModel>(), MainContract.View, View.OnClickListener, ViewPager.OnPageChangeListener, PermissionUtils.PermissionListener {

    private var mAdapter: BaseTabPagerAdapter<*>? = null
    private var mExitTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initData() {
        val fragments = ArrayList<BaseFragment>()
        fragments.add(PlanningFragment())
        fragments.add(NoteDirFragment())
        mAdapter = MainPagerAdapter(fragmentManager, fragments, null)
    }

    override fun initView() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = resources.getString(R.string.planning_toolbar_title)
        fab.visibility = View.INVISIBLE
        pager.adapter = mAdapter
        navigationView.itemIconTintList = null
    }

    override fun initListener() {
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        pager.addOnPageChangeListener(this)
        layoutPlanningCount.setOnClickListener(this)
        layoutNoteCount.setOnClickListener(this)
        tvExportNotes.setOnClickListener(this)
        tvAboutApp.setOnClickListener(this)
        fab.setOnClickListener {
            /*添加新的笔记*/
            startActivityForResult(Intent(this@MainActivity, NoteActivity::class.java),
                    Constant.Common.REQUEST_CODE_NEW_NOTE)
        }
    }

    public override fun onResume() {
        super.onResume()
        mPresenter.getDataCount()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Snackbar.make(fab!!, "再次点击退出应用", Snackbar.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.layoutPlanningCount -> pager.currentItem = 0
            R.id.layoutNoteCount -> pager.currentItem = 1
            R.id.tvExportNotes ->
                //先检查是否有日记存在
                if (Integer.parseInt(tvNoteCount.text.toString()) > 0) {
                    /*这里需要检查一下读入权限*/
                    if (PermissionUtils.requestPermission(this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), this)) {
                        val intent = Intent(this, ExportNoteService::class.java)
                        startService(intent)
                    }
                }
            R.id.tvAboutApp -> openActivity(AboutAppActivity::class.java)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        when (position) {
            1 -> {
                supportActionBar?.title = resources.getString(R.string.note_toolbar_title)
                fab.visibility = View.VISIBLE
            }
            0 -> {
                supportActionBar?.title = resources.getString(R.string.planning_toolbar_title)
                fab.visibility = View.GONE
            }
            else -> {
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.Common.REQUEST_CODE_NEW_NOTE) {
                EventBus.getDefault().post(EventChangeNote(true, data?.getSerializableExtra(Constant.Extra.EXTRA_NOTE) as NoteEntity))
            } else if (requestCode == Constant.Common.REQUEST_CODE_UPDATE_NOTE) {
                EventBus.getDefault().post(EventChangeNote(false, data?.getSerializableExtra(Constant.Extra.EXTRA_NOTE) as NoteEntity))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        PermissionUtils.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onGranted() {
        val intent = Intent(this, ExportNoteService::class.java)
        startService(intent)
    }

    override fun onDenied(permission: String) {
        showShortToast(resources.getString(R.string.alert_permission_needed))
    }

    override fun onRequestStart() {

    }

    override fun onRequestError(msg: String) {

    }

    override fun onRequestEnd() {

    }

    override fun onUpdateData(noteCount: Int, planningCount: Int) {
        tvNoteCount.text = noteCount.toString() + ""
        tvPlanningCount.text = planningCount.toString() + ""
    }

    fun updatePlanningCount(isAdd: Boolean) {
        val count = Integer.decode(tvPlanningCount.text.toString())!!
        if (isAdd)
            tvPlanningCount.text = (count + 1).toString() + ""
        else
            tvPlanningCount.text = (count - 1).toString() + ""
    }
}
