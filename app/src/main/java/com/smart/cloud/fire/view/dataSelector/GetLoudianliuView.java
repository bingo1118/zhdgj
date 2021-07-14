package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class GetLoudianliuView extends DataSelectorView{

    public GetLoudianliuView(Context context) {
        super(context);
    }

    public GetLoudianliuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetLoudianliuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        dataList=new ArrayList<>();
        dataList.add(new DeviceState(0,"30"));
        dataList.add(new DeviceState(1,"50"));
        dataList.add(new DeviceState(2,"75"));
        dataList.add(new DeviceState(3,"100"));
        dataList.add(new DeviceState(4,"200"));
        dataList.add(new DeviceState(5,"300"));
        dataList.add(new DeviceState(6,"500"));
        dataList.add(new DeviceState(7,"800"));
        dataList.add(new DeviceState(8,"1000"));
        getDataSuccesss();
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("请选择阈值档位");
        setRootView(this);
    }
}
