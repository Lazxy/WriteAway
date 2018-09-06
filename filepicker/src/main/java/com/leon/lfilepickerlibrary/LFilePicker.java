package com.leon.lfilepickerlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.leon.lfilepickerlibrary.consts.ExtraConsts;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.ui.LFilePickerActivity;

/**
 * 作者：Leon
 * 时间：2017/3/20 16:57
 */
public class LFilePicker {
    private Activity mActivity;
    private Fragment mFragment;
    private android.support.v4.app.Fragment mSupportFragment;
    private String mPickerTitle;
    private int mPickerTheme = R.style.LFileTheme;
    private int mTitleStyle = R.style.LFileToolbarTextStyle;
    private int mBackIcon;
    private int mRequestCode;
    private boolean mMultiMode = true;
    private boolean mChooseMode = true;
    private String mMultiModeConfirmText;
    private int mFileIcon;
    private int mFolderIcon;
    private String[] mFileTypes;
    private String mNotFoundTips;
    private int mMaxNum;
    private String mStartPath;
    private boolean mIsGreater = true;
    private long mStandardFileSize;

    /**
     * 绑定Activity
     */
    public LFilePicker withActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * 绑定Fragment
     */
    public LFilePicker withFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    /**
     * 绑定v4包Fragment
     */
    public LFilePicker withSupportFragment(android.support.v4.app.Fragment supportFragment) {
        this.mSupportFragment = supportFragment;
        return this;
    }


    /**
     * 设置Picker的标题
     */
    public LFilePicker withTitle(String title) {
        this.mPickerTitle = title;
        return this;
    }

    /**
     * 设置页面主题，主要影响colorPrimary/colorPrimaryDark/colorAccent
     */
    public LFilePicker withTheme(@StyleRes int theme) {
        this.mPickerTheme = theme;
        return this;
    }

    /**
     * 设置标题的字体样式
     */
    public LFilePicker withTitleStyle(@StyleRes int style) {
        this.mTitleStyle = style;
        return this;
    }

    /**
     * 请求码，标识返回结果的来源
     */
    public LFilePicker withRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置返回图标
     */
    public void withBackIcon(int backIcon) {
        this.mBackIcon = backIcon;
    }

    /**
     * 设置选择模式，默认为true,多选；false为单选
     */
    public LFilePicker withMultiMode(boolean isMultiMode) {
        this.mMultiMode = isMultiMode;
        return this;
    }

    /**
     * 设置多选时确认按钮文字
     */
    public LFilePicker withMultiModeConfirmText(String text) {
        this.mMultiModeConfirmText = text;
        return this;
    }

    /**
     * 设置文件夹图标风格
     */
    public LFilePicker withFileIcon(int resId) {
        this.mFileIcon = resId;
        return this;
    }

    public LFilePicker withFolderIcon(int resId) {
        this.mFolderIcon = resId;
        return this;
    }

    public LFilePicker withFileFilter(String[] types) {
        this.mFileTypes = types;
        return this;
    }

    /**
     * 没有选中文件时的提示信息
     */
    public LFilePicker withNotFoundTips(String tip) {
        this.mNotFoundTips = tip;
        return this;
    }

    /**
     * 设置最大文件选中数量
     */
    public LFilePicker withMaxNum(int num) {
        this.mMaxNum = num;
        return this;
    }

    /**
     * 设置目录初始路径
     */
    public LFilePicker withStartPath(String path) {
        this.mStartPath = path;
        return this;
    }

    /**
     * 设置选择模式，true为文件选择模式，false为文件夹选择模式，默认为true
     */
    public LFilePicker withChooseMode(boolean chooseMode) {
        this.mChooseMode = chooseMode;
        return this;
    }

    /**
     * 设置文件大小过滤方式：大于\小于指定大小,需要和withFileSize(long fileSize)共同使用
     *
     * @param isGreater true：大于 ；false：小于，同时包含指定大小在内
     * @return
     */
    public LFilePicker withShouldLarger(boolean isGreater) {
        this.mIsGreater = isGreater;
        return this;
    }

    /**
     * 设置过滤文件大小
     */
    public LFilePicker withStandardFileSize(long fileSize) {
        this.mStandardFileSize = fileSize;
        return this;
    }

    public void start() {
        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity or Fragment by withActivity or withFragment/withSupportFragment method");
        }
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);

        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }


    private Intent initIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, LFilePickerActivity.class);
        } else if (mFragment != null) {
            intent = new Intent(mFragment.getActivity(), LFilePickerActivity.class);
        } else {
            intent = new Intent(mSupportFragment.getActivity(), LFilePickerActivity.class);
        }
        return intent;
    }

    @NonNull
    private Bundle getBundle() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.setPickerTitle(mPickerTitle);
        paramEntity.setPickerTheme(mPickerTheme);
        paramEntity.setTitleStyle(mTitleStyle);
        paramEntity.setBackIcon(mBackIcon);
        paramEntity.setMultiMode(mMultiMode);
        paramEntity.setMultiModeConfirmText(mMultiModeConfirmText);
        paramEntity.setFileIcon(mFileIcon);
        paramEntity.setFolderIcon(mFolderIcon);
        paramEntity.setFileTypes(mFileTypes);
        paramEntity.setNotFoundTips(mNotFoundTips);
        paramEntity.setMaxNum(mMaxNum);
        paramEntity.setChooseFileMode(mChooseMode);
        paramEntity.setStartPath(mStartPath);
        paramEntity.setStandardFileSize(mStandardFileSize);
        paramEntity.setGreater(mIsGreater);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ExtraConsts.EXTRA_FILE_PARAM, paramEntity);
        return bundle;
    }
}
