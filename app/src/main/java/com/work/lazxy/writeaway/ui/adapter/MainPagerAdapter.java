package com.work.lazxy.writeaway.ui.adapter;

import android.app.FragmentManager;

import com.work.lazxy.writeaway.common.base.BaseFragment;
import com.work.lazxy.writeaway.common.base.BaseTabPagerAdapter;

import java.util.List;

/**
 * Created by Lazxy on 2017/4/27.
 */

public class MainPagerAdapter extends BaseTabPagerAdapter<BaseFragment> {
    public MainPagerAdapter(FragmentManager manager, List<BaseFragment> fragments, String[] title) {
        super(manager, fragments, title);
    }
}
