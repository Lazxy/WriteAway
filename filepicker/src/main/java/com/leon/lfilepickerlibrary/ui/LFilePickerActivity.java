package com.leon.lfilepickerlibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.R;
import com.leon.lfilepickerlibrary.adapter.PathAdapter;
import com.leon.lfilepickerlibrary.consts.ExtraConsts;
import com.leon.lfilepickerlibrary.filter.LFileFilter;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.leon.lfilepickerlibrary.utils.StringUtils;
import com.leon.lfilepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LFilePickerActivity extends AppCompatActivity {

    private final String TAG = "FilePickerLeon";
    private EmptyRecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mTvPath;
    private TextView mTvBack;
    private Button mBtnConfirmSelected;
    private String mPath;
    private List<File> mAvailableFileList;
    private ArrayList<String> mSelectedFiles = new ArrayList<>();
    private PathAdapter mPathAdapter;
    private Toolbar mToolbar;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private boolean mIsAllSelected = false;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mParamEntity = (ParamEntity) getIntent().getExtras().getSerializable(ExtraConsts.EXTRA_FILE_PARAM);
        if (mParamEntity != null && mParamEntity.getPickerTheme() != 0) {
            setTheme(mParamEntity.getPickerTheme());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        if (!checkSDCardState()) {
            Toast.makeText(this, R.string.lfile_not_found_path, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = mParamEntity.getStartPath();
        if (StringUtils.isEmpty(mPath)) {
            //如果没有指定路径，则使用默认路径
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mAvailableFileList = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getStandardFileSize());
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_file_list);
        mTvPath = findViewById(R.id.tv_path);
        mTvBack = findViewById(R.id.tv_back);
        mBtnConfirmSelected = findViewById(R.id.btn_confirm_selected);
        mEmptyView = findViewById(R.id.empty_view);
        mToolbar = findViewById(R.id.toolbar);
        initToolbar();
        initSelectedButton();
        mTvPath.setText(mPath);
        mPathAdapter = new PathAdapter(mAvailableFileList, this, mParamEntity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mPathAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
    }

    private void initListener() {
        // 返回目录上一级
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null) {
                    return;
                }
                mPath = tempPath;
                mAvailableFileList = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getStandardFileSize());
                mPathAdapter.setListData(mAvailableFileList);
                mPathAdapter.updateAllSelected(false);
                mIsAllSelected = false;
                updateMenuTitle();
                mBtnConfirmSelected.setText(getString(R.string.lfile_selected));
                mRecyclerView.scrollToPosition(0);
                mTvPath.setText(mPath);
                //清除添加集合中数据
                mSelectedFiles.clear();
            }
        });
        mPathAdapter.setOnItemClickListener(new PathAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (mParamEntity.isMultiMode()) {
                    actionInMultiMode(position);
                } else {
                    actionInSingleMode(position);
                }
            }
        });

        mBtnConfirmSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParamEntity.isChooseFileMode() && mSelectedFiles.size() < 1) {
                    String info = mParamEntity.getNotFoundTips();
                    if (TextUtils.isEmpty(info)) {
                        Toast.makeText(LFilePickerActivity.this, R.string.lfile_not_found_books, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LFilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //返回
                    commitSelection();
                }
            }
        });
    }

    private void actionInMultiMode(int position) {
        if (mAvailableFileList.get(position).isDirectory()) {
            //如果当前是目录，则进入继续查看目录
            checkInDirectory(position);
            mPathAdapter.updateAllSelected(false);
            mIsAllSelected = false;
            updateMenuTitle();
            mBtnConfirmSelected.setText(getString(R.string.lfile_selected));
        } else {
            //如果已经选择则取消，否则添加进来
            if (mSelectedFiles.contains(mAvailableFileList.get(position).getAbsolutePath())) {
                mSelectedFiles.remove(mAvailableFileList.get(position).getAbsolutePath());
            } else {
                mSelectedFiles.add(mAvailableFileList.get(position).getAbsolutePath());
            }
            updateCounterText();
            //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
            if (mParamEntity.getMaxNum() > 0 && mSelectedFiles.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_out_size, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void actionInSingleMode(int position) {
        if (mParamEntity.isChooseFileMode()) {
            if (mAvailableFileList.get(position).isDirectory()) {
                checkInDirectory(position);
            } else {
                mSelectedFiles.add(mAvailableFileList.get(position).getAbsolutePath());
                commitSelection();
            }
        } else {
            if (mAvailableFileList.get(position).isDirectory()) {
                mSelectedFiles.add(mAvailableFileList.get(position).getAbsolutePath());
                commitSelection();
            } else {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_choose_folder_tip, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkInDirectory(int position) {
        mPath = mAvailableFileList.get(position).getAbsolutePath();
        mTvPath.setText(mPath);
        //更新数据源
        mAvailableFileList = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getStandardFileSize());
        mPathAdapter.setListData(mAvailableFileList);
        mPathAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 完成提交
     */
    private void commitSelection() {
        //判断是否数量符合要求
        if (mParamEntity.isChooseFileMode()) {
            if (mParamEntity.getMaxNum() > 0 && mSelectedFiles.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(LFilePickerActivity.this, R.string.lfile_out_size, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ExtraConsts.EXTRA_FILE_PATHS, mSelectedFiles);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    private void initToolbar() {
        if (mParamEntity.getPickerTitle() != null) {
            mToolbar.setTitle(mParamEntity.getPickerTitle());
        }
        if (mParamEntity.getTitleStyle() != 0) {
            mToolbar.setTitleTextAppearance(this, mParamEntity.getTitleStyle());
        }
        if (mParamEntity.getBackIcon() != 0) {
            mToolbar.setNavigationIcon(mParamEntity.getBackIcon());
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSelectedButton() {
        if (!mParamEntity.isMultiMode()) {
            mBtnConfirmSelected.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseFileMode()) {
            mBtnConfirmSelected.setVisibility(View.VISIBLE);
            mBtnConfirmSelected.setText(getString(R.string.lfile_OK));
            //文件夹模式默认为单选模式
            mParamEntity.setMultiMode(false);
        }
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDCardState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        this.mMenu = menu;
        mMenu.findItem(R.id.action_selecteall_cancel).setVisible(mParamEntity.isMultiMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            //将当前目录下所有文件选中或者取消
            mPathAdapter.updateAllSelected(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
            if (mIsAllSelected) {
                for (File mListFile : mAvailableFileList) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mSelectedFiles.contains(mListFile.getAbsolutePath())) {
                        mSelectedFiles.add(mListFile.getAbsolutePath());
                    }
                    updateCounterText();
                }
            } else {
                mSelectedFiles.clear();
                mBtnConfirmSelected.setText(getString(R.string.lfile_selected));
            }
            updateMenuTitle();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /**
     * 更新选项菜单文字
     */
    public void updateMenuTitle() {

        if (mIsAllSelected) {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_cancel));
        } else {
            mMenu.getItem(0).setTitle(getString(R.string.lfile_select_all));
        }
    }

    private void updateCounterText() {
        if (mParamEntity.getMultiModeConfirmText() != null) {
            mBtnConfirmSelected.setText(mParamEntity.getMultiModeConfirmText() + "( " + mSelectedFiles.size() + " )");
        } else {
            mBtnConfirmSelected.setText(getString(R.string.lfile_selected) + "( " + mSelectedFiles.size() + " )");
        }
    }

}
