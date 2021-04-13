package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;


import java.util.ArrayList;

public class GetAlarmDealStateView extends DataSelectorView{

    public GetAlarmDealStateView(Context context) {
        super(context);
    }

    public GetAlarmDealStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetAlarmDealStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        dataList=new ArrayList<>();
        dataList.add(new DeviceState(0,"未处理"));
        dataList.add(new DeviceState(1,"已处理"));
        getDataSuccesss();
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("状态");
    }
}



