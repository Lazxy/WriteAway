package com.work.lazxy.writeaway.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lenovo on 2016/12/31.
 */

public class PermissionUtils {
    private static PermissionListener mListener;
    public static final int PERMISSION_REQUEST_CODE=0x1;
    public static final int OVERLAY_PERMISSION_REQ_CODE=0x10;
    public static final int USAGE_STATS_REQ_CODE=0x100;
    public static boolean requestPermission(Context mContext, String[] permissions, PermissionListener listener){
        mListener=listener;
        List<String> deniedPermissions=new ArrayList<String>();
        for(int i=0;i<permissions.length;i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if(deniedPermissions.size()!=0){
            ActivityCompat.requestPermissions((Activity) mContext,deniedPermissions.toArray(new String[deniedPermissions.size()]),PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    public static void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==PERMISSION_REQUEST_CODE&&mListener!=null){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]== PackageManager.PERMISSION_DENIED)
                    mListener.onDenied(permissions[i]);
                else
                    mListener.onGranted();
            }
        }
    }
    public static AlertDialog.Builder showMissingPermissionDialog(final Context mContext, final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("权限提示");
        builder.setMessage("权限申请失败，请点击确定按钮再次申请该权限或前往设置手动开启该权限");

        builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mContext instanceof Activity){
                    ((Activity)mContext).finish();
                }
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissionWithJudge(mContext,permission);
            }
        });

        return builder;
    }
    private static void requestPermissionWithJudge(Context mContext, String permission) {
        if(ActivityCompat.checkSelfPermission(mContext,permission)== PackageManager.PERMISSION_DENIED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale((Activity)mContext,permission)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:"+ mContext.getPackageName()));
                mContext.startActivity(intent);
            }else{
                requestPermission(mContext,new String[]{permission},mListener);
            }
        }

    }
    public static boolean  getAppUsageAccessPermission(final Activity activity){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager)
                    activity.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),activity.getPackageName());
            if(mode!= AppOpsManager.MODE_ALLOWED){
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setMessage("使用该应用需要开启相关权限，请点击\"确定\"在设置中手动开启，开启后可双击返回键返回此应用")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),USAGE_STATS_REQ_CODE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity,
                                        "需要开启该权限时请到设置->安全->有权查看使用情况的应用 中手动开启", Toast.LENGTH_SHORT);
                            }
                        }).show();
                return false;
            }
        }
        return true;
    }
    public static void setDrawOverFlow(Activity activity){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
    }
    public static boolean needToRequestPermission(){
        return Build.VERSION.SDK_INT>=23;
    }
    public interface PermissionListener{
        public void onGranted();
        public void onDenied(String permission);
    }
}
