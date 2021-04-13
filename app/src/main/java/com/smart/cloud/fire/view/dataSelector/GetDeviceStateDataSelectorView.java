package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;


import java.util.ArrayList;

public class GetDeviceStateDataSelectorView extends DataSelectorView{
    public GetDeviceStateDataSelectorView(Context context) {
        super(context);
    }

    public GetDeviceStateDataSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetDeviceStateDataSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        dataList=new ArrayList<>();
        dataList.add(new DeviceState(1,"在线设备"));
        dataList.add(new DeviceState(0,"离线设备"));
        dataList.add(new DeviceState(2,"报警设备"));
        dataList.add(new DeviceState(3,"故障设备"));
        dataList.add(new DeviceState(4,"低电设备"));
        getDataSuccesss();
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("状态");
    }
}
