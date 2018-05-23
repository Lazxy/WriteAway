package com.work.lazxy.writeaway.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.common.Constant;
import com.work.lazxy.writeaway.common.base.BaseFragment;
import com.work.lazxy.writeaway.common.base.BaseTabPagerAdapter;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.event.EventChangeNote;
import com.work.lazxy.writeaway.mvpframe.base.BaseFrameActivity;
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainContract;
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainModel;
import com.work.lazxy.writeaway.mvpframe.concrete.main.MainPresenter;
import com.work.lazxy.writeaway.service.ExportNoteService;
import com.work.lazxy.writeaway.ui.adapter.MainPagerAdapter;
import com.work.lazxy.writeaway.ui.fragment.NoteDirFragment;
import com.work.lazxy.writeaway.ui.fragment.PlanningFragment;
import com.work.lazxy.writeaway.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseFrameActivity<MainPresenter, MainModel>
        implements MainContract.View, View.OnClickListener, ViewPager.OnPageChangeListener, PermissionUtils.PermissionListener {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.fab_main)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout_main)
    DrawerLayout drawer;
    @BindView(R.id.vp_main)
    ViewPager pager;
    @BindView(R.id.nav_view_main)
    NavigationView navigationView;

    private TextView tvNoteCount;
    private TextView tvPlanningCount;
    private TextView tvExportNotes;
    private TextView tvAboutApp;
    private View layoutNoteCount;
    private View layoutPlanningCount;

    private BaseTabPagerAdapter mAdapter;
    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new PlanningFragment());
        fragments.add(new NoteDirFragment());
        mAdapter = new MainPagerAdapter(getFragmentManager(), fragments, null);
    }

    @Override
    public void initView() {
        tvNoteCount = (TextView) navigationView.findViewById(R.id.tv_nav_note_count);
        tvPlanningCount = (TextView) navigationView.findViewById(R.id.tv_nav_planning_count);
        tvExportNotes = (TextView) navigationView.findViewById(R.id.tv_nav_export_note);
        tvAboutApp = (TextView) navigationView.findViewById(R.id.tv_nav_about_app);
        layoutNoteCount = navigationView.findViewById(R.id.nav_item_note_count);
        layoutPlanningCount = navigationView.findViewById(R.id.nav_item_planning_count);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.planning_toolbar_title));
        fab.setVisibility(View.INVISIBLE);
        pager.setAdapter(mAdapter);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void initListener() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        pager.addOnPageChangeListener(this);
        layoutPlanningCount.setOnClickListener(this);
        layoutNoteCount.setOnClickListener(this);
        tvExportNotes.setOnClickListener(this);
        tvAboutApp.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*添加新的笔记*/
                startActivityForResult(new Intent(MainActivity.this, NoteActivity.class),
                        Constant.Common.REQUEST_CODE_NEW_NOTE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getDataCount();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Snackbar.make(fab, "再次点击退出应用", Snackbar.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.nav_item_planning_count:
                pager.setCurrentItem(0);
                break;
            case R.id.nav_item_note_count:
                pager.setCurrentItem(1);
                break;
            case R.id.tv_nav_export_note:
                //先检查是否有日记存在
                if (Integer.parseInt(tvNoteCount.getText().toString()) > 0) {
                    /*这里需要检查一下读入权限*/
                    if (PermissionUtils.requestPermission(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, this)) {
                        Intent intent = new Intent(this, ExportNoteService.class);
                        startService(intent);
                    }
                }
                break;
            case R.id.tv_nav_about_app:
                openActivity(AboutAppActivity.class);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 1:
                getSupportActionBar().setTitle(getResources().getString(R.string.note_toolbar_title));
                fab.setVisibility(View.VISIBLE);
                break;
            case 0:
                getSupportActionBar().setTitle(getResources().getString(R.string.planning_toolbar_title));
                fab.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.Common.REQUEST_CODE_NEW_NOTE) {
                EventBus.getDefault().post(new EventChangeNote(true, (NoteEntity) data.getSerializableExtra(Constant.Extra.EXTRA_NOTE)));
            } else if (requestCode == Constant.Common.REQUEST_CODE_UPDATE_NOTE) {
                EventBus.getDefault().post(new EventChangeNote(false, (NoteEntity) data.getSerializableExtra(Constant.Extra.EXTRA_NOTE)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onGranted() {
        Intent intent = new Intent(this, ExportNoteService.class);
        startService(intent);
    }

    @Override
    public void onDenied(String permission) {
        showShortToast(getResources().getString(R.string.alert_permission_needed));
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

    }

    @Override
    public void onUpdateData(int noteCount, int planningCount) {
        tvNoteCount.setText(noteCount + "");
        tvPlanningCount.setText(planningCount + "");
    }

    public void updatePlanningCount(boolean isAdd){
        int count = Integer.decode(tvPlanningCount.getText().toString());
        if(isAdd)
            tvPlanningCount.setText(count+1+"");
        else
            tvPlanningCount.setText(count-1+"");
    }
}
