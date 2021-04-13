package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;


import java.util.ArrayList;

public class GetGradeTypeView extends DataSelectorView{

    public GetGradeTypeView(Context context) {
        super(context);
    }

    public GetGradeTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetGradeTypeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        dataList=new ArrayList<>();
        dataList.add(new DeviceState(1,"一级账号"));
        dataList.add(new DeviceState(2,"二级账号"));
        dataList.add(new DeviceState(3,"三级账号"));
        dataList.add(new DeviceState(4,"个人账号"));
        getDataSuccesss();
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("报警");
    }
}

