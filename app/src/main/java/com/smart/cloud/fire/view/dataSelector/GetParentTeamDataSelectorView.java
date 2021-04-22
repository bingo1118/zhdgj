package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smart.cloud.fire.activity.AccountManage.AllAreaEntity;
import com.smart.cloud.fire.activity.AccountManage.OwnParentAreaListAdapter;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.T;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;

public class GetParentTeamDataSelectorView extends DataSelectorView {


    public GetParentTeamDataSelectorView(Context context) {
        super(context);
    }

    public GetParentTeamDataSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetParentTeamDataSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void getdata() {
        getPlaceTypeId();
    }

    public void setCheckedModel(BingoViewModel checkedModel) {
        this.checkedModel = checkedModel;
        changeStatus(Status.CHECKED,checkedModel);
    }


    public void getPlaceTypeId() {
        Observable mObservable = BasePresenter.apiStores1.getOwnParentAreaList(MyApp.getUserID(),MyApp.getPrivilege()+"");

        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<AllAreaEntity>() {
            @Override
            public void onSuccess(AllAreaEntity model) {
                int result = model.getErrorCode();
                if (result == 0) {
                    dataList=new ArrayList<>();
                    for(int i=0;i<model.getList().size();i++){
                        dataList.add(model.getList().get(i));
                    }
                    getDataSuccesss();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getDataFail();
            }

            @Override
            public void onCompleted() {

            }
        }));
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        editText.setHint("请选择一级区域");
    }
}

