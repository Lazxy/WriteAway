package com.work.lazxy.writeaway.common;


import com.work.lazxy.writeaway.WriteAway;
import com.work.lazxy.writeaway.utils.SPUtils;

/**
 * Created by lenovo on 2016/11/25.
 */

public class ConfigManager {
    private static final String NORMAL_CONFIG="normalConfig";

    public static final String DEFAULT="null";

    public static final String FILE_PATH = "filePath";
    public static class AppInfo{
        private static final String QUERY_TYPE="queryType";

        public static final String QUERY_BY_DAY = "queryByDay";

        public static final String QUERY_BY_WEEK = "queryByWeek";

        public static final String QUERY_BY_MONTH = "queryByMonth";

        public static void setQueryType(String queryType){
            SPUtils.set(WriteAway.appContext,NORMAL_CONFIG,QUERY_TYPE, queryType);
        }

        public String getQueryType(){
            return SPUtils.get(WriteAway.appContext, NORMAL_CONFIG,QUERY_TYPE,QUERY_BY_DAY);
        }
    }

    public static String getFileSavedPath(){
        return SPUtils.get(WriteAway.appContext,NORMAL_CONFIG,FILE_PATH, WriteAway.appContext.getFilesDir().getPath()+"/");
    }

    public static void setFileSavedPath(String filePath){
        SPUtils.set(WriteAway.appContext,NORMAL_CONFIG,FILE_PATH,filePath);
    }
}
