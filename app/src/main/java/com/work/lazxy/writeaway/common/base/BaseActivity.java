package com.work.lazxy.writeaway.common.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Created by Lazxy on 2017/2/21.
 */

public class BaseActivity extends AppCompatActivity implements BaseFunc{

    public FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mFragmentManager=getFragmentManager();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        initData();
        initView();
        initListener();
        initLoad();
    }

    public void openActivity(Class<? extends BaseActivity> toActivity) {
        openActivity(toActivity, null);
    }

    public void openActivity(Class<? extends BaseActivity> toActivity, Bundle parameter) {
        Intent intent = new Intent(this, toActivity);
        if (parameter != null) {
            intent.putExtras(parameter);
        }
        startActivity(intent);

    }
    protected void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }


    public void initLoad() {

    }




    public <T extends BaseFragment> T findFragment(int containerId){
        T fragment=(T)mFragmentManager.findFragmentById(containerId);
        return fragment;
    }

    public boolean isFragmentAdded(int containerId){
        return findFragment(containerId) != null;
    }

    public boolean isFragmentShowing( int containerId){
        Fragment fragment=findFragment(containerId);
        return fragment!=null&&!findFragment(containerId).isHidden();
    }

    public <T extends BaseFragment>void showFragment(T fragment,int containerId){
        if(fragment.isAdded()&&fragment.isHidden()) {
            mFragmentManager.beginTransaction().show(fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }else if(!fragment.isAdded()){
            addFragment(fragment,containerId);
        }
    }

    public <T extends BaseFragment>void hideFragment(T fragment) {
        mFragmentManager.beginTransaction().hide(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
    public <T extends BaseFragment> void addFragment(T fragment, int containerId) {
        mFragmentManager.beginTransaction().replace(containerId,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
}
