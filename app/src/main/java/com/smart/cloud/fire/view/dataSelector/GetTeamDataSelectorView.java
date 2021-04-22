package com.smart.cloud.fire.view.dataSelector;

import android.content.Context;
import android.util.AttributeSet;

import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;

public class GetTeamDataSelectorView extends DataSelectorView {


    public GetTeamDataSelectorView(Context context) {
        super(context);
    }

    public GetTeamDataSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetTeamDataSelectorView(Context context, AttributeSet attrs, int defStyle) {
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
        Observable  mObservable = BasePresenter.apiStores1.getAreaId(MyApp.getUserID(),MyApp.getPrivilege()+"","").map(new Func1<HttpAreaResult,ArrayList<Area>>() {
            @Override
            public ArrayList<Area> call(HttpAreaResult o) {
                return o.getSmoke();
            }
        });


        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<ArrayList<Area>>() {
            @Override
            public void onSuccess(ArrayList<Area> model) {
                if(model!=null&&model.size()!=0){
                    dataList=new ArrayList<>();
                    for(int i=0;i<model.size();i++){
                        dataList.add(model.get(i));
                    }
                    getDataSuccesss();
                }else{
                    getDataFail();
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
        editText.setHint("请选择需要添加的区域");
    }
}
