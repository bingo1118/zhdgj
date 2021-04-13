package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;


import java.util.ArrayList;

public class GetAlarmdeviceStateView extends DataSelectorView{

    public GetAlarmdeviceStateView(Context context) {
        super(context);
    }

    public GetAlarmdeviceStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetAlarmdeviceStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        dataList=new ArrayList<>();
        dataList.add(new DeviceState(1,"误报"));
        dataList.add(new DeviceState(2,"火警"));
        getDataSuccesss();
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("请选择");
    }
}


