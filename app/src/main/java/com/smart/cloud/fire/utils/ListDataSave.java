package com.smart.cloud.fire.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 2019/5/9.
 */
public class ListDataSave {
    private static final String PREFERENCENAME="bingo";
    private static final String tag="list";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static ListDataSave mInstance;

    public static ListDataSave getInstance(Context mContext){
        if(mInstance==null){
            synchronized (ListDataSave.class){
                if(mInstance==null){
                    mInstance=new ListDataSave(mContext);
                }
            }
        }
        return mInstance;
    }

    public ListDataSave(Context mContext) {
        preferences = mContext.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public  void setDataList(String addRepeaterMac) {
        if(addRepeaterMac.length()==0){
            return;
        }
        List<String> temp = getDataList();
        if(!temp.contains(addRepeaterMac)){
            temp.add(0,addRepeaterMac);
            temp=temp.subList(0,temp.size()>3?3:temp.size());
            setDataList("list",temp);
        }else if(temp.indexOf(addRepeaterMac)!=0){
            temp.remove(addRepeaterMac);
            temp.add(0,addRepeaterMac);
            temp=temp.subList(0,temp.size()>3?3:temp.size());
            setDataList("list",temp);
        }
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0){
            editor.remove(tag);
            editor.commit();
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * 获取List
     * @param
     * @return
     */
    public <T> List<T> getDataList() {
        List<T> datalist=new ArrayList<T>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }
}